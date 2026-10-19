package com.fishing.dao;

import com.fishing.model.Customer;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class CustomerDAO {
    public Customer findByEmail (String email){
        EntityManager em = JPAUtil.createEntityManager();
        
        try{
            return em.createQuery(
            "SELECT c FROM Customer c WHERE c.email = :email",
                    Customer.class
            )
            .setParameter("email", email)
            .getSingleResult();    
            
        }   catch (NoResultException e){
            return null;
            
        }   finally {
            em.close();
        }
    }
    public boolean updatePassword(String email, String hashedPassword){
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            int updated = em.createQuery(
                    "UPDATE Customer c SET c.password = :password "
                    + "WHERE c.email = :email"
            )
            .setParameter("password", hashedPassword)
            .setParameter("email", email)
            .executeUpdate();
            
            transaction.commit();
            return updated >0;
        }   catch (Exception e){
            if (transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}