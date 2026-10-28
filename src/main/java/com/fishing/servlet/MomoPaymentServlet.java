package com.fishing.servlet;

import com.fishing.dao.OrderDAO;
import com.fishing.dao.PaymentDAO;
import com.fishing.enums.OrderStatus;
import com.fishing.enums.PaymentMethod;
import com.fishing.enums.PaymentStatus;
import com.fishing.model.Order;
import com.fishing.model.Payment;
import com.fishing.util.AuthUtil;
import com.fishing.util.BusinessException;
import com.fishing.util.MomoUtil;
import com.fishing.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.RoundingMode;

/** GET /payment/momo?orderId=12 -> chuyển khách sang trang MoMo có mã QR. */
@WebServlet("/payment/momo")
public class MomoPaymentServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long orderId = WebUtil.parseId(req.getParameter("orderId"));
        Order order = orderId == null ? null : orderDAO.findById(orderId, AuthUtil.getCustomerId(req));
        if (order == null) {
            WebUtil.flash(req, "error", "Không tìm thấy đơn hàng.");
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }
        String back = req.getContextPath() + "/orders?id=" + orderId;

        Payment payment = paymentDAO.findByOrder(orderId);
        if (payment == null || payment.getMethod() != PaymentMethod.MOMO
                || payment.getStatus() != PaymentStatus.PENDING
                || order.getStatus() == OrderStatus.CANCELLED) {
            WebUtil.flash(req, "error", "Đơn hàng không còn chờ thanh toán MoMo.");
            resp.sendRedirect(back);
            return;
        }

        long amount = payment.getAmount().setScale(0, RoundingMode.HALF_UP).longValue();
        // Mỗi lần gửi MoMo phải có mã khác nhau nên ghép thêm thời gian
        String momoOrderId = order.getCode() + "-" + System.currentTimeMillis();
        String base = MomoUtil.baseUrl(req);
        try {
            String payUrl = MomoUtil.createPayment(momoOrderId, amount,
                    "Thanh toan don hang " + order.getCode(),
                    base + "/payment/momo-return", base + "/payment/momo-ipn");
            resp.sendRedirect(payUrl);
        } catch (BusinessException e) {
            WebUtil.flash(req, "error", e.getMessage());
            resp.sendRedirect(back);
        }
    }
}
