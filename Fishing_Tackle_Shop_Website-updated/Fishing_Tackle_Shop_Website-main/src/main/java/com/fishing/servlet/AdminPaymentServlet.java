package com.fishing.servlet;

import com.fishing.dao.PaymentDAO;
import com.fishing.enums.PaymentMethod;
import com.fishing.enums.PaymentStatus;
import com.fishing.util.BusinessException;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Quản lý thanh toán (Admin).
 * GET : /admin/payments                    -> danh sách (method, status để lọc)
 * POST: action=confirm  orderId, transactionRef
 *       -> xác nhận đã nhận tiền chuyển khoản, quay về trang chi tiết đơn
 */
@WebServlet("/admin/payments")
public class AdminPaymentServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/admin/payment/list.jsp";
    // Mã giao dịch ngân hàng: chữ, số, dấu gạch; 4-50 ký tự
    private static final Pattern REF = Pattern.compile("^[A-Za-z0-9\\-_.]{4,50}$");

    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PaymentMethod method = parseEnum(PaymentMethod.class, req.getParameter("method"));
        PaymentStatus status = parseEnum(PaymentStatus.class, req.getParameter("status"));
        req.setAttribute("payments", paymentDAO.search(method, status));
        req.setAttribute("methods", PaymentMethod.values());
        req.setAttribute("statuses", PaymentStatus.values());
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long orderId = WebUtil.parseId(req.getParameter("orderId"));
        if (orderId == null || !"confirm".equals(req.getParameter("action"))) {
            resp.sendRedirect(req.getContextPath() + "/admin/payments");
            return;
        }

        String ref = WebUtil.trim(req.getParameter("transactionRef"));
        if (!REF.matcher(ref).matches()) {
            WebUtil.flash(req, "error", "Mã giao dịch không hợp lệ (4–50 ký tự: chữ, số, - _ .).");
        } else {
            try {
                paymentDAO.confirmTransfer(orderId, ref);
                WebUtil.flash(req, "success", "Đã xác nhận nhận tiền chuyển khoản.");
            } catch (BusinessException e) {
                WebUtil.flash(req, "error", e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/orders?id=" + orderId);
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, s.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
