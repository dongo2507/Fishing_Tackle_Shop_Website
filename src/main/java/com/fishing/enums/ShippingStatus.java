package com.fishing.enums;

/**
 * Trạng thái vận chuyển. Đi theo trạng thái đơn hàng:
 *   Order PENDING/CONFIRMED -> PREPARING
 *   Order SHIPPING          -> SHIPPED   (ghi shippedDate)
 *   Order SUCCESS           -> DELIVERED (ghi deliveredDate)
 *   Order CANCELLED         -> CANCELLED
 */
public enum ShippingStatus {
    PREPARING("Chờ lấy hàng"),
    SHIPPED("Đang vận chuyển"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã hủy");

    private final String label;

    ShippingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
