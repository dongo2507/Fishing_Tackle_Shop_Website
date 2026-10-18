
package com.fishing.util;

import jakarta.persistence.EntityManager;

public class DatabaseConnectionTest {

    public static void main(String[] args) {
        EntityManager em = null;

        try {
            em = JPAUtil.createEntityManager();

            Object result = em.createNativeQuery(
                "SELECT current_database()"
            ).getSingleResult();

            System.out.println("Kết nối database thành công!");
            System.out.println("Database: " + result);

        } catch (Exception e) {
            System.out.println("Kết nối database thất bại!");
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            JPAUtil.close();
        }
    }
}
