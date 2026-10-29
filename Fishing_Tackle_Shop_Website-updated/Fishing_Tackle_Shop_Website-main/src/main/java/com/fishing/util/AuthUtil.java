package com.fishing.util;

import com.fishing.model.Admin;
import com.fishing.model.Customer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Đọc/ghi thông tin đăng nhập trong session.
 * Các thuộc tính đặt khi đăng nhập thành công:
 *   role ("CUSTOMER" | "ADMIN"), currentUserName, customerId (Long) hoặc adminId (Long).
 */
public final class AuthUtil {

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_ADMIN = "ADMIN";

    private AuthUtil() {}

    /** Id khách đang đăng nhập; null nếu chưa đăng nhập hoặc đang là admin. */
    public static Long getCustomerId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || !ROLE_CUSTOMER.equals(session.getAttribute("role"))) {
            return null;
        }
        Object v = session.getAttribute("customerId");
        return v instanceof Long ? (Long) v : null;
    }

    /** Id admin đang đăng nhập; null nếu chưa đăng nhập hoặc đang là khách. */
    public static Long getAdminId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || !ROLE_ADMIN.equals(session.getAttribute("role"))) {
            return null;
        }
        Object v = session.getAttribute("adminId");
        return v instanceof Long ? (Long) v : null;
    }

    public static boolean isCustomer(HttpServletRequest req) {
        return getCustomerId(req) != null;
    }

    public static boolean isAdmin(HttpServletRequest req) {
        return getAdminId(req) != null;
    }

    /** Đăng nhập khách: đổi session id (chống session fixation) và xóa dữ liệu của phiên trước. */
    public static void loginCustomer(HttpServletRequest req, Customer c) {
        HttpSession session = freshSession(req);
        session.setAttribute("role", ROLE_CUSTOMER);
        session.setAttribute("customerId", c.getId());
        session.setAttribute("currentUserName", c.getFullName());
    }

    public static void loginAdmin(HttpServletRequest req, Admin a) {
        HttpSession session = freshSession(req);
        session.setAttribute("role", ROLE_ADMIN);
        session.setAttribute("adminId", a.getId());
        session.setAttribute("currentUserName", a.getName());
    }

    private static HttpSession freshSession(HttpServletRequest req) {
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();   // bỏ giỏ đếm, thông tin reset mật khẩu, flash... của phiên cũ
        }
        return req.getSession(true);
    }

    /** Chỉ cho phép quay lại đường dẫn nội bộ của web (chống open redirect). */
    public static String safeNext(HttpServletRequest req, String next) {
        String ctx = req.getContextPath();
        if (next == null || next.isBlank()) {
            return null;
        }
        if (!next.startsWith(ctx + "/") || next.startsWith("//") || next.contains("\\")
                || next.contains("\r") || next.contains("\n")) {
            return null;
        }
        return next;
    }
}