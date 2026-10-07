package com.fishing.dao;

import com.fishing.model.Brand;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BrandDAO {

    public List<Brand> findAll() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Brand b ORDER BY b.name", Brand.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Brand findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.find(Brand.class, id);
        } finally {
            em.close();
        }
    }

    public boolean existsByName(String name, Long excludeId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            String jpql = "SELECT COUNT(b) FROM Brand b WHERE LOWER(b.name) = LOWER(:name)"
                    + (excludeId != null ? " AND b.id <> :excludeId" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("name", name);
            if (excludeId != null) {
                q.setParameter("excludeId", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public long countProducts(Long brandId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Product p WHERE p.brand.id = :id", Long.class)
                    .setParameter("id", brandId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /** id == null: thêm mới (persist); ngược lại: cập nhật (merge). */
    public Brand save(Brand brand) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Brand saved;
            if (brand.getId() == null) {
                em.persist(brand);
                saved = brand;
            } else {
                saved = em.merge(brand);
            }
            em.getTransaction().commit();
            return saved;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Brand b = em.find(Brand.class, id);
            if (b != null) {
                em.remove(b);
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
}
