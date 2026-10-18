package com.fishing.servlet;

import com.fishing.dao.OrderDAO;
import com.fishing.dao.PaymentDAO;
import com.fishing.dao.ShippingDAO;
import com.fishing.model.Order;
import com.fishing.util.BusinessException;
import com.fishing.util.ShopConfig;
import com.fishing.util.TempAuth;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Đơn hàng của khách.
 * GET : /orders        -> lịch sử đơn hàng
 *       /orders?id=1   -> chi tiết 1 đơn
 * POST: action=cancel  id, reason -> khách tự hủy đơn (chỉ khi còn "Chờ xác nhận")
 */
@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/customer/order/list.jsp";
    private static final String DETAIL_VIEW = "/WEB-INF/views/customer/order/detail.jsp";

    private final OrderDAO orderDAO = new OrderDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ShippingDAO shippingDAO = new ShippingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long customerId = TempAuth.getCustomerId(req);
        String idParam = req.getParameter("id");

        if (idParam == null) {
            req.setAttribute("orders", orderDAO.findByCustomer(customerId));
            req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
            return;
        }

        Long id = WebUtil.parseId(idParam);
        Order order = id == null ? null : orderDAO.findById(id, customerId);
        if (order == null) {
            WebUtil.flash(req, "error", "Không tìm thấy đơn hàng.");
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }
        req.setAttribute("order", order);
        // Thanh toán & vận chuyển (phần của Lộc)
        req.setAttribute("payment", paymentDAO.findByOrder(id));
        req.setAttribute("shipping", shippingDAO.findByOrder(id));
        req.setAttribute("bankName", ShopConfig.BANK_NAME);
        req.setAttribute("bankAccountNo", ShopConfig.BANK_ACCOUNT_NO);
        req.setAttribute("bankAccountName", ShopConfig.BANK_ACCOUNT_NAME);
        req.getRequestDispatcher(DETAIL_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = WebUtil.parseId(req.getParameter("id"));
        if (!"cancel".equals(req.getParameter("action")) || id == null) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        String reason = WebUtil.trim(req.getParameter("reason"));
        if (reason.length() > 255) {
            reason = reason.substring(0, 255);
        }
        try {
            orderDAO.cancelOrder(id, TempAuth.getCustomerId(req),
                    reason.isEmpty() ? "Khách hàng hủy" : "Khách hàng hủy: " + reason);
            WebUtil.flash(req, "success", "Đã hủy đơn hàng.");
        } catch (BusinessException e) {
            WebUtil.flash(req, "error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/orders?id=" + id);
    }
}
