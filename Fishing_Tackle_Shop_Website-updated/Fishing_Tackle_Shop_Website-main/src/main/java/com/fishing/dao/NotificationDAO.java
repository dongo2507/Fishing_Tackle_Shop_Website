package com.fishing.dao;

import com.fishing.enums.NotificationType;
import com.fishing.model.Notification;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class NotificationDAO {

    /**
     * Tạo thông báo trong CÙNG transaction của nghiệp vụ gọi nó (đặt hàng, hủy đơn...):
     * nghiệp vụ rollback thì thông báo cũng không được lưu.
     */
    public void create(EntityManager em, Long recipientId, NotificationType type, String title, String content) {
        if (recipientId == null) {
            return;
        }
        em.persist(new Notification(recipientId, type, title, content));
    }

    /** Thông báo của một khách, mới nhất trước. */
    public List<Notification> findByRecipient(Long customerId, int limit) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT n FROM Notification n WHERE n.recipientId = :cid "
                                    + "ORDER BY n.sentAt DESC, n.id DESC", Notification.class)
                    .setParameter("cid", customerId)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countUnread(Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(n) FROM Notification n WHERE n.recipientId = :cid AND n.isRead = false",
                            Long.class)
                    .setParameter("cid", customerId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Chỉ đánh dấu được thông báo của chính khách đó. */
    public void markRead(Long id, Long customerId) {
        run("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.recipientId = :cid",
                id, customerId);
    }

    public void markAllRead(Long customerId) {
        run("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :cid AND n.isRead = false",
                null, customerId);
    }

    private void run(String jpql, Long id, Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            var q = em.createQuery(jpql).setParameter("cid", customerId);
            if (id != null) {
                q.setParameter("id", id);
            }
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}