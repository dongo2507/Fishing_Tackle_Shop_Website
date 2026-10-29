package com.fishing.dao;

import com.fishing.model.Address;
import com.fishing.model.Customer;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.function.Consumer;

/**
 * Sổ địa chỉ giao hàng của khách (phần của Dũng).
 * Mọi hàm đều nhận customerId của khách đang đăng nhập: khách chỉ đụng được địa chỉ của chính mình.
 */
public class AddressDAO {

    /** Địa chỉ của khách, địa chỉ mặc định đứng đầu. */
    public List<Address> findByCustomer(Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Address a WHERE a.customer.id = :cid "
                                    + "ORDER BY a.isDefault DESC, a.id ASC", Address.class)
                    .setParameter("cid", customerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** null nếu không có hoặc không phải của khách này. */
    public Address findById(Long id, Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Address a WHERE a.id = :id AND a.customer.id = :cid", Address.class)
                    .setParameter("id", id)
                    .setParameter("cid", customerId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    /** Thêm mới (id null) hoặc cập nhật. Địa chỉ đầu tiên của khách tự thành mặc định. */
    public void save(Address input, Long customerId) {
        inTransaction(em -> {
            Address a;
            if (input.getId() == null) {
                a = new Address();
                a.setCustomer(em.getReference(Customer.class, customerId));
            } else {
                a = em.createQuery(
                                "SELECT a FROM Address a WHERE a.id = :id AND a.customer.id = :cid", Address.class)
                        .setParameter("id", input.getId())
                        .setParameter("cid", customerId)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                if (a == null) {
                    return;
                }
            }
            a.setReceiverName(input.getReceiverName());
            a.setReceiverPhone(input.getReceiverPhone());
            a.setStreet(input.getStreet());
            a.setWard(input.getWard());
            a.setDistrict(input.getDistrict());
            a.setCity(input.getCity());

            long count = em.createQuery("SELECT COUNT(a) FROM Address a WHERE a.customer.id = :cid", Long.class)
                    .setParameter("cid", customerId)
                    .getSingleResult();
            boolean makeDefault = input.isDefault() || (input.getId() == null && count == 0);
            if (makeDefault) {
                clearDefault(em, customerId);
            }
            a.setDefault(makeDefault || (input.getId() != null && a.isDefault()));
            if (input.getId() == null) {
                em.persist(a);
            }
        });
    }

    public void setDefault(Long id, Long customerId) {
        inTransaction(em -> {
            Address a = em.find(Address.class, id);
            if (a != null && a.getCustomer().getId().equals(customerId)) {
                clearDefault(em, customerId);
                a.setDefault(true);
            }
        });
    }

    /** Xóa địa chỉ mặc định thì địa chỉ còn lại đầu tiên thành mặc định. */
    public void delete(Long id, Long customerId) {
        inTransaction(em -> {
            Address a = em.find(Address.class, id);
            if (a == null || !a.getCustomer().getId().equals(customerId)) {
                return;
            }
            boolean wasDefault = a.isDefault();
            em.remove(a);
            em.flush();
            if (wasDefault) {
                em.createQuery("SELECT a FROM Address a WHERE a.customer.id = :cid ORDER BY a.id", Address.class)
                        .setParameter("cid", customerId)
                        .setMaxResults(1)
                        .getResultStream()
                        .findFirst()
                        .ifPresent(next -> next.setDefault(true));
            }
        });
    }

    private void clearDefault(EntityManager em, Long customerId) {
        em.createQuery("UPDATE Address a SET a.isDefault = false WHERE a.customer.id = :cid")
                .setParameter("cid", customerId)
                .executeUpdate();
    }

    private void inTransaction(Consumer<EntityManager> work) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            work.accept(em);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
