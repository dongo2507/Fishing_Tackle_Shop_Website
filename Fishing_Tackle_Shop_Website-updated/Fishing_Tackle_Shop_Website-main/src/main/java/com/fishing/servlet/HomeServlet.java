package com.fishing.servlet;

import com.fishing.dao.BrandDAO;
import com.fishing.dao.CategoryDAO;
import com.fishing.dao.HomeDAO;
import com.fishing.dao.ProductCatalogDAO;
import com.fishing.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Trang chủ khách hàng: GET /home (index.jsp chuyển tiếp tới đây). */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/customer/home.jsp";

    private final ProductCatalogDAO catalogDAO = new ProductCatalogDAO();
    private final HomeDAO homeDAO = new HomeDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Product> newest = catalogDAO.search(null, null, null, "newest", 1, 8);
        List<Product> topRated = homeDAO.topRated(4);

        // Lấy điểm đánh giá của cả hai nhóm sản phẩm bằng một câu truy vấn
        List<Long> ids = new ArrayList<>();
        for (Product p : newest) {
            ids.add(p.getId());
        }
        for (Product p : topRated) {
            if (!ids.contains(p.getId())) {
                ids.add(p.getId());
            }
        }

        req.setAttribute("newest", newest);
        req.setAttribute("topRated", topRated);
        req.setAttribute("ratings", catalogDAO.getRatings(ids));
        req.setAttribute("categories", categoryDAO.findAll(true));   // chỉ danh mục đang hiện
        req.setAttribute("brands", brandDAO.findAll());
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
