
package com.fishing.dao;

import com.fishing.model.Address;
import com.fishing.model.Customer;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.UUID;

public class AddressDAO {

    // Lấy danh sách địa chỉ của khách hàng
    public List<Address> findByCustomer(String customerId) {
        EntityManager em = JPAUtil.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT a FROM Address a "
                    + "WHERE a.customer.customerId = :customerId "
                    + "ORDER BY a.isDefault DESC, a.id ASC",
                    Address.class)
                    .setParameter("customerId", customerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Thêm địa chỉ
    public void add(Address address, String customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Customer customer = em.find(Customer.class, customerId);
            if (customer == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy khách hàng.");
            }

            Long count = em.createQuery(
                    "SELECT COUNT(a) FROM Address a "
                    + "WHERE a.customer.customerId = :customerId",
                    Long.class)
                    .setParameter("customerId", customerId)
                    .getSingleResult();

            address.setId(UUID.randomUUID().toString());
            address.setCustomer(customer);

            if (count == 0 || address.isDefault()) {
                clearDefault(em, customerId);
                address.setDefault(true);
            }

            em.persist(address);
            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Sửa địa chỉ, chỉ khi địa chỉ thuộc khách hàng
    public boolean update(Address input, String customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Address address = em.createQuery(
                    "SELECT a FROM Address a "
                    + "WHERE a.id = :id "
                    + "AND a.customer.customerId = :customerId",
                    Address.class)
                    .setParameter("id", input.getId())
                    .setParameter("customerId", customerId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (address == null) {
                tx.rollback();
                return false;
            }
            address.setReceiverName(input.getReceiverName());
            address.setReceiverPhone(input.getReceiverPhone());
            address.setAddressLabel(input.getAddressLabel());
            address.setStreet(input.getStreet());
            address.setWard(input.getWard());
            address.setDistrict(input.getDistrict());
            address.setCity(input.getCity());

            if (input.isDefault()) {
                clearDefault(em, customerId);
                address.setDefault(true);
            }

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    // Xóa địa chỉ thuộc khách hàng
    public boolean delete(String addressId, String customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Address address = em.createQuery(
                    "SELECT a FROM Address a "
                    + "WHERE a.id = :id "
                    + "AND a.customer.customerId = :customerId",
                    Address.class)
                    .setParameter("id", addressId)
                    .setParameter("customerId", customerId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (address == null) {
                tx.rollback();
                return false;
            }
            boolean wasDefault = address.isDefault();
            em.remove(address);
            em.flush();

            // Nếu xóa địa chỉ mặc định, chọn một địa chỉ khác
            if (wasDefault) {
                Address next = em.createQuery(
                        "SELECT a FROM Address a "
                        + "WHERE a.customer.customerId = :customerId "
                        + "ORDER BY a.id ASC",
                        Address.class)
                        .setParameter("customerId", customerId)
                        .setMaxResults(1)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                if (next != null) {
                    next.setDefault(true);
                }
            }
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    // Chọn địa chỉ mặc định
    public boolean setDefault(String addressId, String customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Address address = em.createQuery(
                    "SELECT a FROM Address a "
                    + "WHERE a.id = :id "
                    + "AND a.customer.customerId = :customerId",
                    Address.class)
                    .setParameter("id", addressId)
                    .setParameter("customerId", customerId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (address == null) {
                tx.rollback();
                return false;
            }
            clearDefault(em, customerId);
            address.setDefault(true);
            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    private void clearDefault(EntityManager em, String customerId) {
        em.createQuery(
                "UPDATE Address a SET a.isDefault = false "
                + "WHERE a.customer.customerId = :customerId")
                .setParameter("customerId", customerId)
                .executeUpdate();
    }
}
