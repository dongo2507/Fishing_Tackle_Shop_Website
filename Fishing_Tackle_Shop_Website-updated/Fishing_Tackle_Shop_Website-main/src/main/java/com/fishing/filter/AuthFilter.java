package com.fishing.filter;

import com.fishing.util.AuthUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Phân quyền:
 *  - /admin/*  : chỉ ADMIN.
 *  - giỏ hàng, đặt hàng, đơn hàng, đánh giá, thông báo, sổ địa chỉ: chỉ khách (CUSTOMER) đã đăng nhập.
 * Chưa đăng nhập thì chuyển tới /login (kèm đường dẫn để quay lại sau khi đăng nhập).
 * Đã đăng nhập nhưng sai vai trò thì trả 403.
 */
@WebFilter(urlPatterns = {"/admin/*", "/cart", "/checkout", "/orders", "/feedback",
        "/notifications", "/account/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        boolean adminArea = path.startsWith("/admin");
        boolean allowed = adminArea ? AuthUtil.isAdmin(req) : AuthUtil.isCustomer(req);

        if (allowed) {
            // Trang cần đăng nhập thì không để trình duyệt lưu cache (bấm Back sau khi đăng xuất vẫn không xem lại được)
            resp.setHeader("Cache-Control", "no-store");
            chain.doFilter(request, response);
            return;
        }

        boolean loggedInOtherRole = AuthUtil.isAdmin(req) || AuthUtil.isCustomer(req);
        if (loggedInOtherRole) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    adminArea ? "Chỉ quản trị viên mới vào được trang này."
                            : "Trang này dành cho tài khoản khách hàng.");
            return;
        }

        String target = req.getContextPath() + "/login";
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String uri = req.getRequestURI();
            if (req.getQueryString() != null) {
                uri += "?" + req.getQueryString();
            }
            target += "?next=" + URLEncoder.encode(uri, StandardCharsets.UTF_8);
        }
        resp.sendRedirect(target);
    }
}