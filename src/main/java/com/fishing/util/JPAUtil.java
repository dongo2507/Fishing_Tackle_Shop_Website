package com.fishing.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Tạo một lần cho cả ứng dụng
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("fishingPU");

    private JPAUtil() {}

    // EntityManager không thread-safe: mỗi phương thức DAO tự tạo và tự đóng
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
