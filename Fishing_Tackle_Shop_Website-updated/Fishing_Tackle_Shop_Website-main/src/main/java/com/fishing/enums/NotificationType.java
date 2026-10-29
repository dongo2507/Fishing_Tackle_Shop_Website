package com.fishing.enums;

public enum NotificationType {
    ORDER_CREATED("🛒"),
    ORDER_CANCELLED("❌"),
    PAYMENT_SUCCESS("💳"),
    SHIPPING_UPDATE("🚚");

    private final String icon;

    NotificationType(String icon) {
        this.icon = icon;
    }

    /** Biểu tượng hiển thị cạnh thông báo. */
    public String getIcon() {
        return icon;
    }
}
