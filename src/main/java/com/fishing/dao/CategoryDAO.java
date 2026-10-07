package com.fishing.dao;

import com.fishing.model.Category;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CategoryDAO {

    public List<Category> findAll(boolean onlyVisible) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            String jpql = "SELECT c FROM Category c"
                    + (onlyVisible ? " WHERE c.visible = :visible" : "")
                    + " ORDER BY c.id";
            TypedQuery<Category> q = em.createQuery(jpql, Category.class);
            if (onlyVisible) {
                q.setParameter("visible", true);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Category findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public boolean existsByName(String name, Long excludeId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Category c WHERE LOWER(c.name) = LOWER(:name)"
                    + (excludeId != null ? " AND c.id <> :excludeId" : "");
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

    public long countProducts(Long categoryId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Product p WHERE p.category.id = :id", Long.class)
                    .setParameter("id", categoryId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Category save(Category category) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Category saved;
            if (category.getId() == null) {
                em.persist(category);
                saved = category;
            } else {
                saved = em.merge(category);
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
            Category c = em.find(Category.class, id);
            if (c != null) {
                em.remove(c);
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

    public void toggleVisible(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Category c = em.find(Category.class, id);
            if (c != null) {
                c.setVisible(!c.isVisible());
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
