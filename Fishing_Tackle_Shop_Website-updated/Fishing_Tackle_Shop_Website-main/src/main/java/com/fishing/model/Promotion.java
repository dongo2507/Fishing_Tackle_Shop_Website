package com.fishing.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Mã giảm giá: giảm theo %, có trần giảm tối đa và thời gian hiệu lực (tính cả ngày bắt đầu và ngày kết thúc). */
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã khách nhập, luôn lưu IN HOA. */
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.TEN;

    /** Giảm tối đa bao nhiêu tiền; null = không giới hạn. */
    @Column(name = "max_discount", precision = 15, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** Admin có thể tắt mã bất cứ lúc nào. */
    @Column(nullable = false)
    private boolean enabled = true;

    public Promotion() {}

    /** Mã đang dùng được vào ngày today không. */
    public boolean isActiveOn(LocalDate today) {
        return enabled && startDate != null && endDate != null
                && !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isActive() {
        return isActiveOn(LocalDate.now());
    }

    /** Số tiền được giảm cho đơn có tạm tính subtotal (làm tròn xuống đồng, không vượt quá subtotal). */
    public BigDecimal computeDiscount(BigDecimal subtotal) {
        BigDecimal discount = subtotal.multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
        if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }

    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
