package com.fishing.servlet;

import com.fishing.dao.OrderDAO;
import com.fishing.dao.PaymentDAO;
import com.fishing.dao.ShippingDAO;
import com.fishing.enums.OrderStatus;
import com.fishing.model.Order;
import com.fishing.util.BusinessException;
import com.fishing.util.ShopConfig;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Quản lý đơn hàng (Admin).
 * GET : /admin/orders                     -> danh sách (status, q để lọc/tìm)
 *       /admin/orders?id=1                -> chi tiết đơn
 * POST: action=next    id                 -> chuyển sang trạng thái kế tiếp
 *       action=cancel  id, reason         -> hủy đơn + hoàn kho
 *       back=detail                       -> xong thì quay về trang chi tiết
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/admin/order/list.jsp";
    private static final String DETAIL_VIEW = "/WEB-INF/views/admin/order/detail.jsp";

    private final OrderDAO orderDAO = new OrderDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ShippingDAO shippingDAO = new ShippingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Long id = WebUtil.parseId(idParam);
            Order order = id == null ? null : orderDAO.findById(id, null);
            if (order == null) {
                WebUtil.flash(req, "error", "Không tìm thấy đơn hàng.");
                resp.sendRedirect(req.getContextPath() + "/admin/orders");
                return;
            }
            req.setAttribute("order", order);
            // Thanh toán & vận chuyển (phần của Lộc)
            req.setAttribute("payment", paymentDAO.findByOrder(id));
            req.setAttribute("shipping", shippingDAO.findByOrder(id));
            req.setAttribute("carriers", ShopConfig.CARRIERS);
            req.getRequestDispatcher(DETAIL_VIEW).forward(req, resp);
            return;
        }

        OrderStatus status = parseStatus(req.getParameter("status"));
        String keyword = WebUtil.trim(req.getParameter("q"));
        req.setAttribute("orders", orderDAO.search(status, keyword));
        req.setAttribute("counts", orderDAO.countByStatus());
        req.setAttribute("statuses", OrderStatus.values());
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        Long id = WebUtil.parseId(req.getParameter("id"));

        if (id == null) {
            WebUtil.flash(req, "error", "Dữ liệu không hợp lệ.");
        } else {
            try {
                if ("next".equals(action)) {
                    OrderStatus s = orderDAO.advanceStatus(id);
                    WebUtil.flash(req, "success", "Đơn " + String.format("DH%06d", id)
                            + " đã chuyển sang \"" + s.getLabel() + "\".");
                } else if ("cancel".equals(action)) {
                    String reason = WebUtil.trim(req.getParameter("reason"));
                    if (reason.length() > 255) {
                        reason = reason.substring(0, 255);
                    }
                    orderDAO.cancelOrder(id, null,
                            reason.isEmpty() ? "Cửa hàng hủy" : "Cửa hàng hủy: " + reason);
                    WebUtil.flash(req, "success", "Đã hủy đơn " + String.format("DH%06d", id)
                            + " và hoàn trả số lượng về kho.");
                }
            } catch (BusinessException e) {
                WebUtil.flash(req, "error", e.getMessage());
            }
        }

        if ("detail".equals(req.getParameter("back")) && id != null) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders?id=" + id);
        } else {
            // Giữ lại bộ lọc đang dùng
            String status = WebUtil.trim(req.getParameter("filterStatus"));
            resp.sendRedirect(req.getContextPath() + "/admin/orders"
                    + (parseStatus(status) != null ? "?status=" + status : ""));
        }
    }

    private OrderStatus parseStatus(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(s.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
