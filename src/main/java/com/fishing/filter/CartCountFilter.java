package com.fishing.filter;

import com.fishing.dao.CartDAO;
import com.fishing.util.TempAuth;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Đặt số lượng trong giỏ ("cartCount") vào session cho các trang của khách,
 * để thanh menu hiện "Giỏ hàng (3)". Chỉ đọc DB 1 lần mỗi phiên;
 * sau đó CartServlet / CheckoutServlet tự cập nhật khi giỏ thay đổi.
 */
@WebFilter(urlPatterns = {"/product", "/cart", "/checkout", "/orders"})
public class CartCountFilter implements Filter {

    public static final String ATTR = "cartCount";

    private final CartDAO cartDAO = new CartDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpSession session = httpReq.getSession();
        if (session.getAttribute(ATTR) == null) {
            refresh(httpReq, cartDAO);
        }
        chain.doFilter(req, res);
    }

    public static void refresh(HttpServletRequest req, CartDAO dao) {
        req.getSession().setAttribute(ATTR, dao.countQuantity(TempAuth.getCustomerId(req)));
    }
}
