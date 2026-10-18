package com.fishing.model;

import com.fishing.enums.ShippingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thông tin vận chuyển của 1 đơn hàng (mỗi đơn đúng 1 Shipping).
 * Tạo cùng lúc với đơn; Admin bổ sung đơn vị vận chuyển, mã vận đơn, ngày giao dự kiến.
 */
@Entity
@Table(name = "shipping")
public class Shipping {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // Địa chỉ giao (copy từ đơn hàng; sau này lấy từ Address của Dũng)
    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @Column(name = "carrier_name", length = 50)
    private String carrierName;

    @Column(name = "tracking_number", length = 30)
    private String trackingNumber;

    @Column(name = "shipping_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShippingStatus status = ShippingStatus.PREPARING;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    public Shipping() {}

    /** Đủ thông tin để giao cho đơn vị vận chuyển chưa. */
    public boolean isReadyToShip() {
        return carrierName != null && !carrierName.isBlank()
                && trackingNumber != null && !trackingNumber.isBlank();
    }

    /** Admin còn sửa được thông tin vận chuyển không. */
    public boolean isEditable() {
        return status == ShippingStatus.PREPARING || status == ShippingStatus.SHIPPED;
    }

    public String getExpectedDeliveryDateText() {
        return expectedDeliveryDate == null ? "" : expectedDeliveryDate.format(DATE_FMT);
    }

    public String getShippedDateText() {
        return shippedDate == null ? "" : shippedDate.format(DATETIME_FMT);
    }

    public String getDeliveredDateText() {
        return deliveredDate == null ? "" : deliveredDate.format(DATETIME_FMT);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public ShippingStatus getStatus() { return status; }
    public void setStatus(ShippingStatus status) { this.status = status; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public LocalDateTime getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDateTime shippedDate) { this.shippedDate = shippedDate; }

    public LocalDateTime getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(LocalDateTime deliveredDate) { this.deliveredDate = deliveredDate; }
}
