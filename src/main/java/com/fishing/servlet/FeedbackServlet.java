package com.fishing.servlet;

import com.fishing.dao.FeedbackDAO;
import com.fishing.dao.ProductDAO;
import com.fishing.model.Product;
import com.fishing.util.ProductRules;
import com.fishing.util.TempAuth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Khách gửi đánh giá: POST /feedback (productId, rating 1-5, comment). */
@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    private static final int MAX_COMMENT = 2000;

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long productId = parseId(req.getParameter("productId"));
        Product p = productId == null ? null : productDAO.findById(productId);
        if (!ProductRules.isVisibleToCustomer(p)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Integer rating = parseRating(req.getParameter("rating"));
        String comment = trim(req.getParameter("comment"));

        // Kiểm tra dữ liệu ở tầng Java (không dựa vào database)
        if (rating == null) {
            flash(req, "error", "Vui lòng chọn số sao từ 1 đến 5.");
        } else if (comment.length() > MAX_COMMENT) {
            flash(req, "error", "Bình luận tối đa " + MAX_COMMENT + " ký tự.");
        } else {
            // TODO: sau khi có đăng nhập, bắt buộc phải đăng nhập (và đã mua hàng) mới được gửi
            feedbackDAO.add(productId, TempAuth.getCustomerId(req), rating,
                    comment.isEmpty() ? null : comment);
            flash(req, "success", "Cảm ơn bạn đã đánh giá sản phẩm!");
        }
        resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "#feedback");
    }

    private Integer parseRating(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            int v = Integer.parseInt(s.trim());
            return (v >= 1 && v <= 5) ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
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
