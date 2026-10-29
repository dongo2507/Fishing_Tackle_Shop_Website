package com.fishing.servlet;

import com.fishing.dao.CustomerDAO;
import com.fishing.model.Customer;
import com.fishing.util.AuthUtil;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Trang thông tin cá nhân của khách: GET/POST /account/profile.
 * Đã được AuthFilter bảo vệ (/account/*): chỉ khách đã đăng nhập mới vào được.
 */
@WebServlet("/account/profile")
public class ProfileServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/customer/account/profile.jsp";
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s().]{8,15}$");

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Customer c = customerDAO.findById(AuthUtil.getCustomerId(req));
        if (c == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }
        req.setAttribute("customer", c);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long id = AuthUtil.getCustomerId(req);
        Customer c = customerDAO.findById(id);
        if (c == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        String fullName = WebUtil.trim(req.getParameter("fullName"));
        String phone = WebUtil.trim(req.getParameter("phone"));

        String error = null;
        if (fullName.isEmpty()) {
            error = "Họ tên không được để trống.";
        } else if (fullName.length() > 100) {
            error = "Họ tên tối đa 100 ký tự.";
        } else if (!phone.isEmpty() && !PHONE.matcher(phone).matches()) {
            error = "Số điện thoại không hợp lệ (8-15 ký tự, chỉ gồm số).";
        }

        if (error != null) {
            // Giữ lại những gì người dùng vừa nhập để họ sửa tiếp
            c.setFullName(fullName);
            c.setPhone(phone);
            req.setAttribute("customer", c);
            req.setAttribute("error", error);
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }

        customerDAO.updateProfile(id, fullName, WebUtil.emptyToNull(phone));
        // Cập nhật tên hiển thị "Xin chào, ..." trên header
        req.getSession().setAttribute("currentUserName", fullName);
        WebUtil.flash(req, "success", "Đã cập nhật thông tin cá nhân.");
        resp.sendRedirect(req.getContextPath() + "/account/profile");
    }
}
