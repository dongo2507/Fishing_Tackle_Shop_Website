package com.fishing.servlet;

import com.fishing.dao.PaymentDAO;
import com.fishing.util.BusinessException;
import com.fishing.util.MomoUtil;
import com.fishing.util.WebUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MoMo báo kết quả thanh toán:
 * GET  /payment/momo-return : trình duyệt của khách quay về sau khi thanh toán
 * POST /payment/momo-ipn    : server MoMo gọi về (chỉ chạy khi web đã deploy)
 */
@WebServlet(urlPatterns = {"/payment/momo-return", "/payment/momo-ipn"})
public class MomoCallbackServlet extends HttpServlet {

    private static final String[] FIELDS = {"partnerCode", "orderId", "requestId", "amount", "orderInfo",
            "orderType", "transId", "resultCode", "message", "payType", "responseTime", "extraData", "signature"};

    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> p = new HashMap<>();
        for (String f : FIELDS) {
            p.put(f, req.getParameter(f));
        }
        Long orderId = parseOrderId(p.get("orderId"));
        if (orderId == null || !MomoUtil.verify(p)) {
            WebUtil.flash(req, "error", "Kết quả thanh toán không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        if ("0".equals(p.get("resultCode"))) {
            try {
                paymentDAO.confirmOnline(orderId, Long.parseLong(p.get("amount")), p.get("transId"));
                WebUtil.flash(req, "success", "Thanh toán MoMo thành công.");
            } catch (BusinessException e) {
                WebUtil.flash(req, "error", e.getMessage());
            }
        } else {
            WebUtil.flash(req, "error", "Thanh toán MoMo chưa thành công: " + p.get("message"));
        }
        resp.sendRedirect(req.getContextPath() + "/orders?id=" + orderId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            Map<String, String> p = new HashMap<>();
            for (String f : FIELDS) {
                JsonElement e = json.get(f);
                p.put(f, e == null || e.isJsonNull() ? null : e.getAsString());
            }
            Long orderId = parseOrderId(p.get("orderId"));
            if (orderId != null && MomoUtil.verify(p) && "0".equals(p.get("resultCode"))) {
                paymentDAO.confirmOnline(orderId, Long.parseLong(p.get("amount")), p.get("transId"));
            }
        } catch (RuntimeException e) {
            log("MoMo IPN lỗi: " + e.getMessage());
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /** "DH000012-1730000000000" -> 12 */
    private Long parseOrderId(String momoOrderId) {
        if (momoOrderId == null || !momoOrderId.matches("DH\\d{1,18}-\\d+")) {
            return null;
        }
        return Long.valueOf(momoOrderId.substring(2, momoOrderId.indexOf('-')));
    }
}
