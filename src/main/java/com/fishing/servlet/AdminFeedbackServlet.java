package com.fishing.servlet;

import com.fishing.dao.FeedbackDAO;
import com.fishing.dao.ProductDAO;
import com.fishing.util.TempAuth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin xem đánh giá và trả lời (SubFeedback).
 * GET : /admin/feedbacks?productId=1&unanswered=true
 * POST: action=reply (feedbackId, content) | deleteReply (subId) | delete (feedbackId)
 *       Các trường filterProductId, filterUnanswered để giữ bộ lọc sau khi thao tác.
 */
@WebServlet("/admin/feedbacks")
public class AdminFeedbackServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/admin/feedback/list.jsp";
    private static final int MAX_REPLY = 2000;

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long productId = parseId(req.getParameter("productId"));
        boolean unanswered = "true".equals(req.getParameter("unanswered"));

        req.setAttribute("feedbacks", feedbackDAO.search(productId, unanswered));
        req.setAttribute("products", productDAO.search(null, null, null));
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        if ("reply".equals(action)) {
            reply(req);
        } else if ("deleteReply".equals(action)) {
            deleteReply(req);
        } else if ("delete".equals(action)) {
            delete(req);
        }
        resp.sendRedirect(listUrl(req));
    }

    private void reply(HttpServletRequest req) {
        Long feedbackId = parseId(req.getParameter("feedbackId"));
        String content = trim(req.getParameter("content"));

        if (feedbackId == null || feedbackDAO.findById(feedbackId) == null) {
            flash(req, "error", "Không tìm thấy đánh giá.");
        } else if (content.isEmpty()) {
            flash(req, "error", "Nội dung trả lời không được để trống.");
        } else if (content.length() > MAX_REPLY) {
            flash(req, "error", "Nội dung trả lời tối đa " + MAX_REPLY + " ký tự.");
        } else {
            feedbackDAO.addReply(feedbackId, TempAuth.getAdminId(req), content);
            flash(req, "success", "Đã gửi câu trả lời.");
        }
    }

    private void deleteReply(HttpServletRequest req) {
        Long subId = parseId(req.getParameter("subId"));
        if (subId == null) {
            flash(req, "error", "Không tìm thấy câu trả lời.");
        } else {
            feedbackDAO.deleteReply(subId);
            flash(req, "success", "Đã xóa câu trả lời.");
        }
    }

    private void delete(HttpServletRequest req) {
        Long feedbackId = parseId(req.getParameter("feedbackId"));
        if (feedbackId == null) {
            flash(req, "error", "Không tìm thấy đánh giá.");
        } else {
            feedbackDAO.delete(feedbackId);
            flash(req, "success", "Đã xóa đánh giá.");
        }
    }

    /** Dựng lại URL danh sách từ giá trị đã được phân tích (không redirect theo chuỗi người dùng gửi). */
    private String listUrl(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder(req.getContextPath()).append("/admin/feedbacks");
        String sep = "?";
        Long pid = parseId(req.getParameter("filterProductId"));
        if (pid != null) {
            sb.append(sep).append("productId=").append(pid);
            sep = "&";
        }
        if ("true".equals(req.getParameter("filterUnanswered"))) {
            sb.append(sep).append("unanswered=true");
        }
        return sb.toString();
    }

    private Long parseId(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMessage", message);
    }
}
