package com.fishing.dao;

import com.fishing.model.Customer;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
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
}