package com.fishing.servlet;

import com.fishing.dao.ShippingDAO;
import com.fishing.enums.ShippingStatus;
import com.fishing.util.BusinessException;
import com.fishing.util.ShopConfig;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Quản lý vận chuyển (Admin).
 * GET : /admin/shippings                   -> danh sách (status, q = mã vận đơn / mã đơn)
 * POST: action=save  orderId, carrierName, trackingNumber, expectedDate (yyyy-MM-dd)
 *       -> cập nhật thông tin vận chuyển, quay về trang chi tiết đơn
 * Trạng thái vận chuyển tự đổi theo trạng thái đơn (xem OrderDAO.advanceStatus).
 */
@WebServlet("/admin/shippings")
public class AdminShippingServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/admin/shipping/list.jsp";
    // Mã vận đơn: chữ in hoa + số + dấu gạch, 6-30 ký tự
    private static final Pattern TRACKING = Pattern.compile("^[A-Z0-9\\-]{6,30}$");

    private final ShippingDAO shippingDAO = new ShippingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ShippingStatus status = parseStatus(req.getParameter("status"));
        String keyword = WebUtil.trim(req.getParameter("q"));
        req.setAttribute("shippings", shippingDAO.search(status, keyword));
        req.setAttribute("statuses", ShippingStatus.values());
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long orderId = WebUtil.parseId(req.getParameter("orderId"));
        if (orderId == null || !"save".equals(req.getParameter("action"))) {
            resp.sendRedirect(req.getContextPath() + "/admin/shippings");
            return;
        }

        String carrier = WebUtil.trim(req.getParameter("carrierName"));
        String tracking = WebUtil.trim(req.getParameter("trackingNumber")).toUpperCase();
        LocalDate expected = parseDate(req.getParameter("expectedDate"));

        String error = null;
        if (!carrier.isEmpty() && !ShopConfig.CARRIERS.contains(carrier)) {
            error = "Đơn vị vận chuyển không hợp lệ.";
        } else if (!tracking.isEmpty() && !TRACKING.matcher(tracking).matches()) {
            error = "Mã vận đơn phải gồm 6–30 ký tự chữ, số hoặc dấu gạch ngang.";
        } else if (!tracking.isEmpty() && carrier.isEmpty()) {
            error = "Vui lòng chọn đơn vị vận chuyển cho mã vận đơn.";
        } else if (expected == null) {
            error = "Ngày giao dự kiến không hợp lệ.";
        } else if (expected.isBefore(LocalDate.now().minusDays(30))
                || expected.isAfter(LocalDate.now().plusDays(60))) {
            error = "Ngày giao dự kiến không hợp lý.";
        }

        if (error != null) {
            WebUtil.flash(req, "error", error);
        } else {
            try {
                shippingDAO.updateInfo(orderId,
                        WebUtil.emptyToNull(carrier), WebUtil.emptyToNull(tracking), expected);
                WebUtil.flash(req, "success", "Đã cập nhật thông tin vận chuyển.");
            } catch (BusinessException e) {
                WebUtil.flash(req, "error", e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/orders?id=" + orderId);
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(s.trim());   // input type="date" gửi dạng yyyy-MM-dd
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private ShippingStatus parseStatus(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return ShippingStatus.valueOf(s.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
