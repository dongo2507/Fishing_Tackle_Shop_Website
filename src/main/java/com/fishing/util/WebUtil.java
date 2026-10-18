package com.fishing.util;

import jakarta.servlet.http.HttpServletRequest;

/** Các hàm nhỏ dùng chung cho Servlet giỏ hàng / đơn hàng. */
public final class WebUtil {

    private WebUtil() {}

    public static Long parseId(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            long v = Long.parseLong(s.trim());
            return v > 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Đọc số nguyên; sai định dạng thì trả về giá trị mặc định. */
    public static int parseInt(String s, int defaultValue) {
        if (s == null || s.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static String emptyToNull(String s) {
        return s == null || s.isEmpty() ? null : s;
    }

    /** Thông báo hiển thị 1 lần ở trang kế tiếp (sau redirect). */
    public static void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMessage", message);
    }
}
