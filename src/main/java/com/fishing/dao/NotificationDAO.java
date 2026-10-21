package com.fishing.dao;

import com.fishing.model.Notification;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.UUID;

public class NotificationDAO {

    // Lấy toàn bộ thông báo của một tài khoản, mới nhất trước
    public List<Notification> findByRecipient(String recipientId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT n FROM Notification n "
                    + "WHERE n.recipientId = :recipientId "
                    + "ORDER BY n.sentAt DESC",
                    Notification.class)
                    .setParameter("recipientId", recipientId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy 5 thông báo mới nhất cho chuông thông báo
    public List<Notification> findLatestByRecipient(
            String recipientId, int limit) {

        EntityManager em = JPAUtil.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT n FROM Notification n "
                    + "WHERE n.recipientId = :recipientId "
                    + "ORDER BY n.sentAt DESC",
                    Notification.class)
                    .setParameter("recipientId", recipientId)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    // Đếm số thông báo chưa đọc của riêng tài khoản
    public long countUnreadByRecipient(String recipientId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(n) FROM Notification n "
                    + "WHERE n.recipientId = :recipientId "
                    + "AND n.isRead = false",
                    Long.class)
                    .setParameter("recipientId", recipientId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // Lưu thông báo mới
    public boolean save(Notification notification) {
        if (notification == null || !notification.send()) {
            return false;
        }
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (notification.getNotificationId() == null
                    || notification.getNotificationId().isBlank()) {
                notification.setNotificationId(
                        UUID.randomUUID().toString());
            }
            em.persist(notification);
            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    // Đánh dấu đã đọc, chỉ cho phép đọc thông báo thuộc tài khoản đó
    public boolean markAsRead(
            String notificationId, String recipientId) {

        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            List<Notification> results = em.createQuery(
                    "SELECT n FROM Notification n "
                    + "WHERE n.notificationId = :notificationId "
                    + "AND n.recipientId = :recipientId",
                    Notification.class)
                    .setParameter("notificationId", notificationId)
                    .setParameter("recipientId", recipientId)
                    .setMaxResults(1)
                    .getResultList();

            if (results.isEmpty()) {
                tx.rollback();
                return false;
            }

            Notification notification = results.get(0);

            if (!notification.isRead()) {
                notification.markAsRead();
            }

            tx.commit();
            return true;

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
