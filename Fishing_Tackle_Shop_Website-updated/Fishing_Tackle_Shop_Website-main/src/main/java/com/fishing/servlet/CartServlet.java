package com.fishing.servlet;

import com.fishing.dao.CartDAO;
import com.fishing.filter.CartCountFilter;
import com.fishing.util.BusinessException;
import com.fishing.util.AuthUtil;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Giỏ hàng của khách.
 * GET : /cart                                  -> xem giỏ
 * POST: action=add     productId, quantity, buyNow (tùy chọn)
 *       action=update  productId, quantity     (0 = xóa)
 *       action=remove  productId
 *       action=clear
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/customer/cart/view.jsp";

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long customerId = AuthUtil.getCustomerId(req);
        req.setAttribute("cart", cartDAO.findByCustomer(customerId));
        req.setAttribute("maxQty", CartDAO.MAX_QTY_PER_ITEM);
        CartCountFilter.refresh(req, cartDAO);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long customerId = AuthUtil.getCustomerId(req);
        String action = req.getParameter("action");
        Long productId = WebUtil.parseId(req.getParameter("productId"));

        String redirect = req.getContextPath() + "/cart";
        try {
            if ("add".equals(action)) {
                if (productId == null) {
                    throw new BusinessException("Sản phẩm không hợp lệ.");
                }
                int qty = WebUtil.parseInt(req.getParameter("quantity"), 1);
                cartDAO.addItem(customerId, productId, qty);
                if (req.getParameter("buyNow") != null) {
                    redirect = req.getContextPath() + "/checkout";
                } else {
                    WebUtil.flash(req, "success", "Đã thêm vào giỏ hàng.");
                    redirect = req.getContextPath() + "/product?id=" + productId;
                }
            } else if ("update".equals(action)) {
                int qty = WebUtil.parseInt(req.getParameter("quantity"), -1);
                if (productId == null || qty < 0) {
                    throw new BusinessException("Số lượng không hợp lệ.");
                }
                cartDAO.updateQuantity(customerId, productId, qty);
                WebUtil.flash(req, "success", qty == 0 ? "Đã xóa sản phẩm khỏi giỏ." : "Đã cập nhật số lượng.");
            } else if ("remove".equals(action)) {
                if (productId != null) {
                    cartDAO.removeItem(customerId, productId);
                    WebUtil.flash(req, "success", "Đã xóa sản phẩm khỏi giỏ.");
                }
            } else if ("clear".equals(action)) {
                cartDAO.clear(customerId);
                WebUtil.flash(req, "success", "Đã xóa toàn bộ giỏ hàng.");
            }
        } catch (BusinessException e) {
            WebUtil.flash(req, "error", e.getMessage());
            // Thêm từ trang sản phẩm bị lỗi thì quay lại trang sản phẩm
            if ("add".equals(action) && productId != null && req.getParameter("buyNow") == null) {
                redirect = req.getContextPath() + "/product?id=" + productId;
            }
        }

        CartCountFilter.refresh(req, cartDAO);
        resp.sendRedirect(redirect);
    }
}
