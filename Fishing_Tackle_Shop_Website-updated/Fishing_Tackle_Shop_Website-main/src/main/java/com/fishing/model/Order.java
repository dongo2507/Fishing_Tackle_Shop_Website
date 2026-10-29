package com.fishing.model;

import com.fishing.enums.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Đơn hàng. Tên bảng là "orders" vì ORDER là từ khóa SQL.
 * Thông tin người nhận được lưu thẳng vào đơn (snapshot) để sau này khách
 * sửa/xóa địa chỉ thì đơn cũ vẫn giữ đúng địa chỉ đã giao.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tạm dùng số, sau này đổi thành @ManyToOne Customer (phần của Dũng)
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    // ----- Người nhận (sau này lấy từ Address của Dũng) -----
    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 15)
    private String receiverPhone;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @Column(length = 500)
    private String note;

    // ----- Tiền -----
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    // Mã giảm giá đã dùng (lưu lại mã để sau này Promotion bị sửa/xóa đơn cũ vẫn đúng)
    @Column(name = "promotion_code", length = 30)
    private String promotionCode;

    // Giảm giá từ Promotion
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    // Phí vận chuyển từ Shipping (phần của Lộc), hiện = 0
    @Column(name = "shipping_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // ----- Hủy đơn -----
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<LineItem> items = new ArrayList<>();

    public Order() {}

    public void addItem(LineItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /** Tính lại tạm tính và tổng cộng từ các LineItem. */
    public void recalculate() {
        BigDecimal sum = BigDecimal.ZERO;
        for (LineItem li : items) {
            sum = sum.add(li.getSubtotal());
        }
        subtotal = sum;
        BigDecimal total = subtotal.subtract(discountAmount).add(shippingFee);
        totalAmount = total.signum() < 0 ? BigDecimal.ZERO : total;
    }

    public int getTotalQuantity() {
        int sum = 0;
        for (LineItem li : items) {
            sum += li.getQuantity();
        }
        return sum;
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Ngày đặt dạng chữ cho JSP (JSTL fmt không định dạng được LocalDateTime). */
    public String getOrderDateText() {
        return orderDate == null ? "" : orderDate.format(DATE_FMT);
    }

    public String getCancelledAtText() {
        return cancelledAt == null ? "" : cancelledAt.format(DATE_FMT);
    }

    /** Mã đơn hiển thị cho đẹp, ví dụ DH000012. */
    public String getCode() {
        return id == null ? "" : String.format("DH%06d", id);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getPromotionCode() { return promotionCode; }
    public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public List<LineItem> getItems() { return items; }
    public void setItems(List<LineItem> items) { this.items = items; }
}
