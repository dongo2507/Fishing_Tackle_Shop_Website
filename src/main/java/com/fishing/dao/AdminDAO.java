package com.fishing.dao;

import com.fishing.model.Admin;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
public class AdminDAO {
    public Admin findUsername(String username){
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Admin a WHERE a.username = username",
                    Admin.class
            )
                    .setParameter("username",username)
                    .getSingleResult();
        }   catch (NoResultException e){
            return null;
        }   finally {
            em.close();
        }
    }
}
