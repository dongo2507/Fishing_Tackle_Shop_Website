
package com.fishing.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static final EntityManagerFactory emf = createFactory();

    private static EntityManagerFactory createFactory() {
        String host = System.getenv("DB_HOST");
        String database = System.getenv("DB_NAME");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        String sslMode = System.getenv("DB_SSL_MODE");

        if (host == null || host.isBlank()
                || database == null || database.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Thieu cau hinh ket noi Neon. "
                    + "Can DB_HOST, DB_NAME, DB_USER, DB_PASSWORD."
            );
        }

        if (sslMode == null || sslMode.isBlank()) {
            sslMode = "require";
        }

        Map<String, Object> properties = new HashMap<>();

        properties.put(
                "jakarta.persistence.jdbc.url",
                "jdbc:postgresql://" + host + "/" + database
                        + "?sslmode=" + sslMode
        );
        properties.put("jakarta.persistence.jdbc.user", username);
        properties.put("jakarta.persistence.jdbc.password", password);

        return Persistence.createEntityManagerFactory(
                "fishingPU", properties
        );
    }

    private JPAUtil() {
    }

    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
