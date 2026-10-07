package com.fishing.listener;

import com.fishing.model.Brand;
import com.fishing.model.Category;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                    .getSingleResult();
            if (count == 0) {
                em.getTransaction().begin();
                for (String name : new String[]{"Cần câu", "Máy câu", "Dây câu", "Mồi câu"}) {
                    Category c = new Category();
                    c.setName(name);
                    em.persist(c);
                }
                for (String name : new String[]{"Shimano", "Daiwa", "Penn"}) {
                    Brand b = new Brand();
                    b.setName(name);
                    em.persist(b);
                }
                em.getTransaction().commit();
            }
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
    }
}
