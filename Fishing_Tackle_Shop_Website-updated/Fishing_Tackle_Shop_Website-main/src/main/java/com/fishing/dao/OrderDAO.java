package com.fishing.dao;

import com.fishing.enums.OrderStatus;
import com.fishing.enums.PaymentMethod;
import com.fishing.enums.ProductStatus;
import com.fishing.model.Cart;
import com.fishing.model.CartItem;
import com.fishing.model.LineItem;
import com.fishing.model.Order;
import com.fishing.model.Product;
import com.fishing.util.BusinessException;
import com.fishing.util.JPAUtil;
import com.fishing.util.ProductRules;
import com.fishing.util.ShopConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
public class OrderDAO {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ShippingDAO shippingDAO = new ShippingDAO();

    /** Dữ liệu người nhận từ form Checkout. */
    public static class ShippingInfo implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public String receiverName;
        public String receiverPhone;
        public String shippingAddress;
        public String note;
        public PaymentMethod paymentMethod = PaymentMethod.COD;

        public String getReceiverName() { return receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public String getShippingAddress() { return shippingAddress; }
        public String getNote() { return note; }
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
    }

    public Order placeOrder(Long customerId, ShippingInfo info) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Khóa giỏ hàng
            Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.customerId = :cid", Cart.class)
                    .setParameter("cid", customerId)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            List<Long> productIds = cart == null ? List.of() : em.createQuery(
                            "SELECT i.product.id FROM CartItem i WHERE i.cart.id = :cartId", Long.class)
                    .setParameter("cartId", cart.getId())
                    .getResultList();
            if (productIds.isEmpty()) {
                throw new BusinessException("Giỏ hàng đang trống.");
            }

            // 2. Khóa sản phẩm theo thứ tự id tăng dần (tránh deadlock) và nạp dữ liệu mới nhất
            em.createQuery("SELECT p FROM Product p WHERE p.id IN :ids ORDER BY p.id", Product.class)
                    .setParameter("ids", productIds)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

            Order order = new Order();
            order.setCustomerId(customerId);
            order.setReceiverName(info.receiverName);
            order.setReceiverPhone(info.receiverPhone);
            order.setShippingAddress(info.shippingAddress);
            order.setNote(info.note);

            // 3. Kiểm tra từng món, tạo LineItem, trừ kho
            for (CartItem ci : cart.getItems()) {
                Product p = ci.getProduct();
                if (!ProductRules.isVisibleToCustomer(p) || p.getStatus() != ProductStatus.ACTIVE) {
                    throw new BusinessException("\"" + p.getName() + "\" hiện không còn bán. "
                            + "Vui lòng xóa khỏi giỏ hàng.");
                }
                if (p.getStockQuantity() < ci.getQuantity()) {
                    throw new BusinessException("\"" + p.getName() + "\" chỉ còn "
                            + p.getStockQuantity() + " sản phẩm. Vui lòng giảm số lượng.");
                }

                order.addItem(LineItem.fromCartItem(ci));

                p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
                if (p.getStockQuantity() == 0) {
                    p.setStatus(ProductStatus.OUT_OF_STOCK);
                }
            }

            // 4. Tính tiền, lưu đơn, xóa giỏ
            order.recalculate();                                        // tính tạm tính
            order.setShippingFee(ShopConfig.calculateShippingFee(order.getSubtotal()));
            order.recalculate();                                        // tính lại tổng có phí ship
            em.persist(order);              // cascade lưu luôn các LineItem

            // Tạo thanh toán (PENDING) và vận chuyển (Chờ lấy hàng) cho đơn – phần của Lộc
            paymentDAO.createForOrder(em, order, info.paymentMethod);
            shippingDAO.createForOrder(em, order);

            cart.getItems().clear();        // orphanRemoval xóa các CartItem
            cart.setUpdatedAt(LocalDateTime.now());

            em.getTransaction().commit();
            return order;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // =====================================================================
    //  XEM ĐƠN HÀNG
    // =====================================================================

    /** Lịch sử đơn của 1 khách, mới nhất trước. */
    public List<Order> findByCustomer(Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items "
                                    + "WHERE o.customerId = :cid ORDER BY o.id DESC", Order.class)
                    .setParameter("cid", customerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Chi tiết đơn. customerId != null thì chỉ trả về nếu đơn thuộc khách đó. */
    public Order findById(Long id, Long customerId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Order o = em.createQuery(
                            "SELECT DISTINCT o FROM Order o "
                                    + "LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product "
                                    + "WHERE o.id = :id", Order.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (o != null && customerId != null && !o.getCustomerId().equals(customerId)) {
                return null;    // không cho xem đơn của người khác
            }
            return o;
        } finally {
            em.close();
        }
    }

    /**
     * Admin tìm đơn. keyword: mã đơn (12 hoặc DH000012), số điện thoại hoặc tên người nhận.
     * Tham số nào null/rỗng thì bỏ qua.
     */
    public List<Order> search(OrderStatus status, String keyword) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            boolean hasKeyword = keyword != null && !keyword.isBlank();
            Long idFromKeyword = hasKeyword ? parseOrderCode(keyword) : null;

            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE 1 = 1");
            if (status != null) {
                jpql.append(" AND o.status = :status");
            }
            if (hasKeyword) {
                jpql.append(" AND (LOWER(o.receiverName) LIKE LOWER(:kw) ESCAPE '!'"
                        + " OR o.receiverPhone LIKE :kw ESCAPE '!'");
                if (idFromKeyword != null) {
                    jpql.append(" OR o.id = :oid");
                }
                jpql.append(")");
            }
            jpql.append(" ORDER BY o.id DESC");

            TypedQuery<Order> q = em.createQuery(jpql.toString(), Order.class);
            if (status != null) {
                q.setParameter("status", status);
            }
            if (hasKeyword) {
                q.setParameter("kw", "%" + escapeLike(keyword.trim()) + "%");
                if (idFromKeyword != null) {
                    q.setParameter("oid", idFromKeyword);
                }
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    public Map<OrderStatus, Long> countByStatus() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Map<OrderStatus, Long> result = new EnumMap<>(OrderStatus.class);
            for (OrderStatus s : OrderStatus.values()) {
                result.put(s, 0L);
            }
            List<Object[]> rows = em.createQuery(
                            "SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status", Object[].class)
                    .getResultList();
            for (Object[] r : rows) {
                result.put((OrderStatus) r[0], (Long) r[1]);
            }
            return result;
        } finally {
            em.close();
        }
    }

    public OrderStatus advanceStatus(Long orderId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Order o = em.find(Order.class, orderId, LockModeType.PESSIMISTIC_WRITE);
            if (o == null) {
                throw new BusinessException("Không tìm thấy đơn hàng.");
            }
            OrderStatus next = o.getStatus().next();
            if (next == null) {
                throw new BusinessException("Đơn " + o.getCode() + " đã ở trạng thái \""
                        + o.getStatus().getLabel() + "\", không thể chuyển tiếp.");
            }
            // Đồng bộ thanh toán & vận chuyển (lỗi thì rollback, đơn giữ trạng thái cũ)
            if (next == OrderStatus.SHIPPING) {
                paymentDAO.checkCanShip(em, orderId);
                shippingDAO.onOrderShipping(em, orderId);
            } else if (next == OrderStatus.SUCCESS) {
                shippingDAO.onOrderDelivered(em, orderId);
                paymentDAO.onOrderDelivered(em, orderId);
            }
            o.setStatus(next);
            em.getTransaction().commit();
            return next;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    public void cancelOrder(Long orderId, Long customerId, String reason) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Order o = em.find(Order.class, orderId, LockModeType.PESSIMISTIC_WRITE);
            if (o == null || (customerId != null && !o.getCustomerId().equals(customerId))) {
                throw new BusinessException("Không tìm thấy đơn hàng.");
            }
            if (customerId != null && o.getStatus() != OrderStatus.PENDING) {
                throw new BusinessException("Đơn " + o.getCode() + " đã được cửa hàng xác nhận, "
                        + "bạn không thể tự hủy. Vui lòng liên hệ cửa hàng.");
            }
            if (!o.getStatus().isCancellable()) {
                throw new BusinessException("Đơn " + o.getCode() + " đang ở trạng thái \""
                        + o.getStatus().getLabel() + "\", không thể hủy.");
            }
            List<Long> productIds = em.createQuery(
                            "SELECT li.product.id FROM LineItem li WHERE li.order.id = :oid", Long.class)
                    .setParameter("oid", orderId)
                    .getResultList();
            em.createQuery("SELECT p FROM Product p WHERE p.id IN :ids ORDER BY p.id", Product.class)
                    .setParameter("ids", productIds)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

            for (LineItem li : o.getItems()) {
                Product p = li.getProduct();
                p.setStockQuantity(p.getStockQuantity() + li.getQuantity());
                if (p.getStatus() == ProductStatus.OUT_OF_STOCK && p.getStockQuantity() > 0) {
                    p.setStatus(ProductStatus.ACTIVE);
                }
            }

            paymentDAO.onOrderCancelled(em, orderId);
            shippingDAO.onOrderCancelled(em, orderId);

            o.setStatus(OrderStatus.CANCELLED);
            o.setCancelledAt(LocalDateTime.now());
            o.setCancelReason(reason);
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

    private String escapeLike(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}