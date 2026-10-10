package com.fishing.dao;

import com.fishing.model.Feedback;
import com.fishing.model.Product;
import com.fishing.model.SubFeedback;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class FeedbackDAO {

    /** Đánh giá của một sản phẩm (mới nhất trước), kèm các câu trả lời (cũ trước). */
    public List<Feedback> findByProduct(Long productId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT f FROM Feedback f LEFT JOIN FETCH f.subFeedbacks s "
                  + "WHERE f.product.id = :pid "
                  + "ORDER BY f.createdAt DESC, s.createdAt ASC", Feedback.class)
                    .setParameter("pid", productId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Điểm trung bình và số lượt đánh giá. Chưa có đánh giá thì count = 0. */
    public RatingSummary getSummary(Long productId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Object[] row = em.createQuery(
                    "SELECT AVG(f.rating), COUNT(f) FROM Feedback f WHERE f.product.id = :pid",
                    Object[].class)
                    .setParameter("pid", productId)
                    .getSingleResult();
            double avg = row[0] == null ? 0 : ((Number) row[0]).doubleValue();
            long count = ((Number) row[1]).longValue();
            return new RatingSummary(avg, count);
        } finally {
            em.close();
        }
    }

    /** Dùng cho trang quản trị: lọc theo sản phẩm và/hoặc chỉ lấy đánh giá chưa được trả lời. */
    public List<Feedback> search(Long productId, boolean onlyUnanswered) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT f FROM Feedback f JOIN FETCH f.product "
                  + "LEFT JOIN FETCH f.subFeedbacks s WHERE 1 = 1");
            if (productId != null) {
                jpql.append(" AND f.product.id = :pid");
            }
            if (onlyUnanswered) {
                jpql.append(" AND NOT EXISTS (SELECT 1 FROM SubFeedback x WHERE x.feedback.id = f.id)");
            }
            jpql.append(" ORDER BY f.createdAt DESC, s.createdAt ASC");

            TypedQuery<Feedback> q = em.createQuery(jpql.toString(), Feedback.class);
            if (productId != null) {
                q.setParameter("pid", productId);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Feedback findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.find(Feedback.class, id);
        } finally {
            em.close();
        }
    }

    /** Khách gửi đánh giá. comment có thể null. */
    public Feedback add(Long productId, Long customerId, int rating, String comment) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Feedback f = new Feedback();
            f.setRating(rating);
            f.setComment(comment);
            f.setCustomerId(customerId);
            f.setProduct(em.getReference(Product.class, productId));
            em.persist(f);
            em.getTransaction().commit();
            return f;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Xóa đánh giá (cascade xóa luôn các câu trả lời). */
    public void delete(Long feedbackId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Feedback f = em.find(Feedback.class, feedbackId);
            if (f != null) {
                em.remove(f);
            }
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

    // ---------- Câu trả lời (SubFeedback) ----------

    public SubFeedback addReply(Long feedbackId, Long adminId, String content) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Feedback f = em.find(Feedback.class, feedbackId);
            if (f == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Không tìm thấy đánh giá.");
            }
            SubFeedback sf = new SubFeedback();
            sf.setContent(content);
            sf.setAdminId(adminId);
            sf.setFeedback(f);
            em.persist(sf);
            em.getTransaction().commit();
            return sf;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteReply(Long subFeedbackId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            SubFeedback sf = em.find(SubFeedback.class, subFeedbackId);
            if (sf != null) {
                em.remove(sf);
            }
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
