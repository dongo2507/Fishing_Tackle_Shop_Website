package com.fishing.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * TẠM THỜI: chưa có đăng nhập (phần của Dũng) nên giả lập người dùng hiện tại.
 * Khi Dũng làm xong, chỉ cần sửa HAI hàm này để lấy id từ session đăng nhập thật,
 * các nơi khác không phải đổi gì.
 */
public final class TempAuth {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_ADMIN_ID = 1L;

    private TempAuth() {}

    public static Long getCustomerId(HttpServletRequest req) {
        Object v = req.getSession().getAttribute("customerId");
        return v instanceof Long ? (Long) v : DEFAULT_CUSTOMER_ID;
    }

    public static Long getAdminId(HttpServletRequest req) {
        Object v = req.getSession().getAttribute("adminId");
        return v instanceof Long ? (Long) v : DEFAULT_ADMIN_ID;
    }
}
