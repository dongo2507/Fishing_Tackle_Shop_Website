package com.fishing.listener;

import com.fishing.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Tạm thời không khởi tạo Hibernate.
        // Chỉ phục vụ quá trình làm giao diện.
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Chỉ đóng JPA nếu đã được khởi tạo.
        JPAUtil.close();
    }
}
