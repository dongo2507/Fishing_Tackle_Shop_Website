package com.fishing.dao;

import com.fishing.enums.OrderStatus;
import com.fishing.enums.PaymentMethod;
import com.fishing.enums.PaymentStatus;
import com.fishing.model.Order;
import com.fishing.model.Payment;
import com.fishing.util.BusinessException;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Thanh toán.
 * Các hàm có tham số EntityManager được gọi BÊN TRONG transaction của OrderDAO
 * (đặt hàng, chuyển trạng thái, hủy đơn) để mọi thay đổi cùng thành công hoặc cùng rollback.
 */
public class PaymentDAO {

    // =====================================================================
    //  Gọi từ OrderDAO (trong transaction có sẵn)
    // =====================================================================

    /** Tạo Payment PENDING cho đơn mới. */
    public void createForOrder(EntityManager em, Order order, PaymentMethod method) {
        Payment p = new Payment();
        p.setOrder(order);
        p.setMethod(method);
        p.setAmount(order.getTotalAmount());
        p.setStatus(PaymentStatus.PENDING);
        em.persist(p);
    }

    /** Trước khi giao hàng: đơn chuyển khoản phải nhận được tiền rồi. */
    public void checkCanShip(EntityManager em, Long orderId) {
        Payment p = findLocked(em, orderId);
        if (p != null && p.getMethod() != PaymentMethod.COD && p.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Đơn thanh toán bằng " + p.getMethod().getLabel()
                    + " chưa nhận được tiền. Hãy xác nhận thanh toán trước khi giao hàng.");
        }
    }

    /** Giao thành công: đơn COD coi như đã thu tiền. */
    public void onOrderDelivered(EntityManager em, Long orderId) {
        Payment p = findLocked(em, orderId);
        if (p != null && p.getMethod() == PaymentMethod.COD && p.getStatus() == PaymentStatus.PENDING) {
            p.setStatus(PaymentStatus.SUCCESS);
            p.setPaymentDate(LocalDateTime.now());
            p.setTransactionRef("COD");
        }
    }

    /** Hủy đơn: chưa trả tiền -> FAILED; đã trả tiền -> REFUNDED (cửa hàng hoàn tiền thủ công). */
    public void onOrderCancelled(EntityManager em, Long orderId) {
        Payment p = findLocked(em, orderId);
        if (p == null) {
            return;
        }
        if (p.getStatus() == PaymentStatus.PENDING) {
            p.setStatus(PaymentStatus.FAILED);
        } else if (p.getStatus() == PaymentStatus.SUCCESS) {
            p.setStatus(PaymentStatus.REFUNDED);
        }
    }

    // =====================================================================
    //  Dùng riêng (tự mở transaction)
    // =====================================================================

    /** Admin xác nhận đã nhận được tiền chuyển khoản. */
    public void confirmTransfer(Long orderId, String transactionRef) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Payment p = findLocked(em, orderId);
            if (p == null) {
                throw new BusinessException("Đơn hàng chưa có thông tin thanh toán.");
            }
            if (p.getMethod() == PaymentMethod.COD) {
                throw new BusinessException("Đơn COD sẽ tự ghi nhận đã thanh toán khi giao thành công.");
            }
            if (p.getStatus() != PaymentStatus.PENDING) {
                throw new BusinessException("Thanh toán đang ở trạng thái \"" + p.getStatus().getLabel()
                        + "\", không thể xác nhận.");
            }
            if (p.getOrder().getStatus() == OrderStatus.CANCELLED) {
                throw new BusinessException("Đơn hàng đã hủy.");
            }
            p.setStatus(PaymentStatus.SUCCESS);
            p.setPaymentDate(LocalDateTime.now());
            p.setTransactionRef(transactionRef);
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

    public Payment findByOrder(Long orderId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Payment p WHERE p.order.id = :oid", Payment.class)
                    .setParameter("oid", orderId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    /** Admin xem danh sách thanh toán, lọc theo phương thức / trạng thái (null = bỏ qua). */
    public List<Payment> search(PaymentMethod method, PaymentStatus status) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT p FROM Payment p JOIN FETCH p.order WHERE 1 = 1");
            if (method != null) {
                jpql.append(" AND p.method = :method");
            }
            if (status != null) {
                jpql.append(" AND p.status = :status");
            }
            jpql.append(" ORDER BY p.id DESC");
            TypedQuery<Payment> q = em.createQuery(jpql.toString(), Payment.class);
            if (method != null) {
                q.setParameter("method", method);
            }
            if (status != null) {
                q.setParameter("status", status);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    private Payment findLocked(EntityManager em, Long orderId) {
        return em.createQuery("SELECT p FROM Payment p WHERE p.order.id = :oid", Payment.class)
                .setParameter("oid", orderId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
