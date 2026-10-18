package com.fishing.dao;

import com.fishing.enums.ShippingStatus;
import com.fishing.model.Order;
import com.fishing.model.Shipping;
import com.fishing.util.BusinessException;
import com.fishing.util.JPAUtil;
import com.fishing.util.ShopConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vận chuyển.
 * Các hàm có tham số EntityManager được gọi BÊN TRONG transaction của OrderDAO.
 */
public class ShippingDAO {

    // =====================================================================
    //  Gọi từ OrderDAO (trong transaction có sẵn)
    // =====================================================================

    /** Tạo thông tin vận chuyển cho đơn mới: copy địa chỉ, phí ship, ngày giao dự kiến. */
    public void createForOrder(EntityManager em, Order order) {
        Shipping s = new Shipping();
        s.setOrder(order);
        s.setShippingAddress(order.getShippingAddress());
        s.setShippingFee(order.getShippingFee());
        s.setStatus(ShippingStatus.PREPARING);
        s.setExpectedDeliveryDate(LocalDate.now().plusDays(ShopConfig.DEFAULT_DELIVERY_DAYS));
        em.persist(s);
    }

    /** Đơn chuyển sang "Đang giao": bắt buộc đã có đơn vị vận chuyển + mã vận đơn. */
    public void onOrderShipping(EntityManager em, Long orderId) {
        Shipping s = findLocked(em, orderId);
        if (s == null) {
            return;     // đơn cũ tạo trước khi có mô-đun vận chuyển
        }
        if (!s.isReadyToShip()) {
            throw new BusinessException("Chưa nhập đơn vị vận chuyển và mã vận đơn. "
                    + "Hãy cập nhật ở mục \"Vận chuyển\" trước khi giao hàng.");
        }
        s.setStatus(ShippingStatus.SHIPPED);
        s.setShippedDate(LocalDateTime.now());
    }

    /** Đơn giao thành công: ghi ngày giao thực tế. */
    public void onOrderDelivered(EntityManager em, Long orderId) {
        Shipping s = findLocked(em, orderId);
        if (s != null) {
            s.setStatus(ShippingStatus.DELIVERED);
            s.setDeliveredDate(LocalDateTime.now());
        }
    }

    public void onOrderCancelled(EntityManager em, Long orderId) {
        Shipping s = findLocked(em, orderId);
        if (s != null) {
            s.setStatus(ShippingStatus.CANCELLED);
        }
    }

    // =====================================================================
    //  Dùng riêng (tự mở transaction)
    // =====================================================================

    /** Admin cập nhật đơn vị vận chuyển, mã vận đơn, ngày giao dự kiến. */
    public void updateInfo(Long orderId, String carrierName, String trackingNumber, LocalDate expectedDate) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Shipping s = findLocked(em, orderId);
            if (s == null) {
                throw new BusinessException("Đơn hàng chưa có thông tin vận chuyển.");
            }
            if (!s.isEditable()) {
                throw new BusinessException("Vận chuyển đang ở trạng thái \"" + s.getStatus().getLabel()
                        + "\", không thể sửa.");
            }
            // Mã vận đơn không được trùng với đơn khác
            if (trackingNumber != null) {
                Long dup = em.createQuery(
                                "SELECT COUNT(s) FROM Shipping s WHERE s.trackingNumber = :t AND s.id <> :id",
                                Long.class)
                        .setParameter("t", trackingNumber)
                        .setParameter("id", s.getId())
                        .getSingleResult();
                if (dup > 0) {
                    throw new BusinessException("Mã vận đơn " + trackingNumber + " đã dùng cho đơn khác.");
                }
            }
            s.setCarrierName(carrierName);
            s.setTrackingNumber(trackingNumber);
            s.setExpectedDeliveryDate(expectedDate);
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

    public Shipping findByOrder(Long orderId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Shipping s WHERE s.order.id = :oid", Shipping.class)
                    .setParameter("oid", orderId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    /** Admin xem danh sách vận chuyển. keyword: mã vận đơn hoặc mã đơn hàng (DH000012). */
    public List<Shipping> search(ShippingStatus status, String keyword) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            boolean hasKeyword = keyword != null && !keyword.isBlank();
            Long orderId = hasKeyword ? parseOrderCode(keyword) : null;

            StringBuilder jpql = new StringBuilder("SELECT s FROM Shipping s JOIN FETCH s.order WHERE 1 = 1");
            if (status != null) {
                jpql.append(" AND s.status = :status");
            }
            if (hasKeyword) {
                jpql.append(" AND (UPPER(s.trackingNumber) = :kw");
                if (orderId != null) {
                    jpql.append(" OR s.order.id = :oid");
                }
                jpql.append(")");
            }
            jpql.append(" ORDER BY s.id DESC");

            TypedQuery<Shipping> q = em.createQuery(jpql.toString(), Shipping.class);
            if (status != null) {
                q.setParameter("status", status);
            }
            if (hasKeyword) {
                q.setParameter("kw", keyword.trim().toUpperCase());
                if (orderId != null) {
                    q.setParameter("oid", orderId);
                }
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    private Shipping findLocked(EntityManager em, Long orderId) {
        return em.createQuery("SELECT s FROM Shipping s WHERE s.order.id = :oid", Shipping.class)
                .setParameter("oid", orderId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /** "DH000012", "#12", "12" -> 12; không phải mã đơn thì null. */
    private Long parseOrderCode(String s) {
        String t = s.trim().toUpperCase();
        if (t.startsWith("#")) {
            t = t.substring(1);
        } else if (t.startsWith("DH")) {
            t = t.substring(2);
        }
        if (t.isEmpty() || t.length() > 18 || !t.chars().allMatch(Character::isDigit)) {
            return null;
        }
        return Long.valueOf(t);
    }
}
