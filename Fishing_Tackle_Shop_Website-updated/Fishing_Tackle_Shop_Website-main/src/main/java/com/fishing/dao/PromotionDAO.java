package com.fishing.dao;

import com.fishing.model.Promotion;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.List;

public class PromotionDAO {

    public List<Promotion> findAll() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Promotion p ORDER BY p.id DESC", Promotion.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Promotion findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.find(Promotion.class, id);
        } finally {
            em.close();
        }
    }

    /** Mã đang dùng được hôm nay (đã bật và còn trong thời gian hiệu lực); không có thì null. */
    public Promotion findActiveByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Promotion p = findByCode(em, code, false);
            return p != null && p.isActiveOn(LocalDate.now()) ? p : null;
        } finally {
            em.close();
        }
    }

    /** Dùng trong transaction đặt hàng: tìm mã dùng được và khóa dòng để kiểm tra chắc chắn. */
    public Promotion findActiveByCode(EntityManager em, String code) {
        Promotion p = findByCode(em, code, true);
        return p != null && p.isActiveOn(LocalDate.now()) ? p : null;
    }

    public boolean existsByCode(String code, Long excludeId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM Promotion p WHERE p.code = :code"
                    + (excludeId != null ? " AND p.id <> :id" : "");
            var q = em.createQuery(jpql, Long.class).setParameter("code", code.trim().toUpperCase());
            if (excludeId != null) {
                q.setParameter("id", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public void save(Promotion p) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            if (p.getId() == null) {
                em.persist(p);
            } else {
                em.merge(p);
            }
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

    public void toggleEnabled(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Promotion p = em.find(Promotion.class, id);
            if (p != null) {
                p.setEnabled(!p.isEnabled());
            }
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

    /** Đơn cũ chỉ lưu chuỗi mã nên xóa mã không ảnh hưởng đơn đã đặt. */
    public void delete(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Promotion p = em.find(Promotion.class, id);
            if (p != null) {
                em.remove(p);
            }
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

    private Promotion findByCode(EntityManager em, String code, boolean lock) {
        var q = em.createQuery("SELECT p FROM Promotion p WHERE p.code = :code", Promotion.class)
                .setParameter("code", code.trim().toUpperCase());
        if (lock) {
            q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return q.getResultStream().findFirst().orElse(null);
    }
}
