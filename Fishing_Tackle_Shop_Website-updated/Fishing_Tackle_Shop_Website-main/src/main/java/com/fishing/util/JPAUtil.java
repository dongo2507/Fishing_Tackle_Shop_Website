package com.fishing.util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil{
    private static final EntityManagerFactory emf = createFactory();
    private static EntityManagerFactory createFactory(){
        String host = System.getenv("DB_HOST");
        //Chạy trên Render: sử dụng Neon
        if(host != null && !host.trim().isEmpty()){
            String database = System.getenv("DB_NAME");
            String username = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            String sslMode = System.getenv("DB_SSL_MODE");
            if(database ==null || database.isBlank()
                    || username == null || username.isBlank()
                    || password == null || password.isBlank()){
                throw new IllegalStateException("Thiếu biến môi trường để kết nối database trên Render");
            }
            if (sslMode == null || sslMode.isBlank()){
                sslMode = "require";
            }
            Map<String, Object> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.url",
                    "jdbc:postgresql://" + host + "/" + database
                            + "?sslmode=" + sslMode);
            properties.put("jakarta.persistence.jdbc.user",username);
            properties.put("jakarta.persistence.jdbc.password",password);
            return Persistence.createEntityManagerFactory(
                    "fishingPU", properties);
        }
        return Persistence.createEntityManagerFactory("fishingPU");
    }
    private JPAUtil(){
    }
    public static EntityManager createEntityManager(){
        return emf.createEntityManager();
    }
    public static void close(){
        if (emf != null && emf.isOpen()){
            emf.close();
        }
    }
}