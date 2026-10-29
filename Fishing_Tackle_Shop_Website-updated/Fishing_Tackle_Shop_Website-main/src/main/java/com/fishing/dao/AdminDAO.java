package com.fishing.dao;

import com.fishing.model.Admin;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class AdminDAO {

    public Admin findByUsername(String username) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM Admin a", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public void save(Admin admin) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(admin);
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
}