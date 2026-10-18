package com.fishing.enums;

public enum PaymentStatus {
    PENDING("Chờ thanh toán"),
    SUCCESS("Đã thanh toán"),
    FAILED("Thất bại / đã hủy"),
    REFUNDED("Đã hoàn tiền");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
