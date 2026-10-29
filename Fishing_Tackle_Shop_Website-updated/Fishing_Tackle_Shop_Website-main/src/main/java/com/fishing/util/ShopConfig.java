package com.fishing.util;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cấu hình thanh toán & vận chuyển (phần của Lộc).
 * Đổi số liệu ở đây là cả web cập nhật theo.
 */
public final class ShopConfig {

    private ShopConfig() {}

    // ----- Phí vận chuyển: đồng giá, miễn phí cho đơn từ ngưỡng trở lên -----
    public static final BigDecimal SHIPPING_FEE = new BigDecimal("30000");
    public static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("500000");

    /** Phí ship theo tạm tính của đơn. */
    public static BigDecimal calculateShippingFee(BigDecimal subtotal) {
        return subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_FEE;
    }

    /** Số ngày giao dự kiến mặc định tính từ lúc đặt. */
    public static final int DEFAULT_DELIVERY_DAYS = 3;

    /** Đơn vị vận chuyển Admin được chọn. */
    public static final List<String> CARRIERS = List.of(
            "Giao Hàng Nhanh (GHN)",
            "Giao Hàng Tiết Kiệm (GHTK)",
            "Viettel Post",
            "J&T Express",
            "Shop tự giao");

    // ----- Tài khoản nhận chuyển khoản (THAY bằng tài khoản thật trước khi demo) -----
    public static final String BANK_NAME = "Vietcombank";
    public static final String BANK_ACCOUNT_NO = "0123456789";
    public static final String BANK_ACCOUNT_NAME = "FISHING TACKLE SHOP";
}
