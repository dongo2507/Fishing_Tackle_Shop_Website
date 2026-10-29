package com.fishing.enums;

/** Phương thức thanh toán. VNPAY, MOMO để dành cho phần nâng cao (cổng thanh toán). */
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng (COD)", true),
    BANK_TRANSFER("Chuyển khoản ngân hàng", true),
    VNPAY("Ví VNPay", false),
    MOMO("Ví MoMo", false);

    private final String label;
    private final boolean enabled;

    PaymentMethod(String label, boolean enabled) {
        this.label = label;
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    /** Khách có được chọn ở trang đặt hàng không (VNPay/MoMo chưa tích hợp). */
    public boolean isEnabled() {
        return enabled;
    }
}
