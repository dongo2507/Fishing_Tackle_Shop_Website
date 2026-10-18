package com.fishing.servlet;

import com.fishing.dao.BrandDAO;
import com.fishing.dao.CategoryDAO;
import com.fishing.dao.ProductCatalogDAO;
import com.fishing.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Trang danh sách sản phẩm cho khách hàng.
 * GET /products?q=...&categoryId=..&brandId=..&sort=newest|price_asc|price_desc&page=1
 */
@WebServlet("/products")
public class ProductListServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/customer/product/list.jsp";
    private static final int PAGE_SIZE = 12;

    private final ProductCatalogDAO catalogDAO = new ProductCatalogDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = trim(req.getParameter("q"));
        Long brandId = parseId(req.getParameter("brandId"));
        Long categoryId = parseId(req.getParameter("categoryId"));
        String sort = normalizeSort(req.getParameter("sort"));

        long total = catalogDAO.count(q, brandId, categoryId);
        int totalPages = (int) Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.min(Math.max(parsePage(req.getParameter("page")), 1), totalPages);

        List<Product> products = catalogDAO.search(q, brandId, categoryId, sort, page, PAGE_SIZE);
        List<Long> ids = new ArrayList<>();
        for (Product p : products) {
            ids.add(p.getId());
        }

        req.setAttribute("products", products);
        req.setAttribute("ratings", catalogDAO.getRatings(ids));
        req.setAttribute("categories", categoryDAO.findAll(true));   // chỉ danh mục đang hiện
        req.setAttribute("brands", brandDAO.findAll());

        // Giữ lại bộ lọc để hiển thị lại trên form
        req.setAttribute("q", q);
        req.setAttribute("brandId", brandId);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("sort", sort);

        req.setAttribute("total", total);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("queryString", buildQueryString(q, brandId, categoryId, sort));

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    /** Dựng chuỗi tham số từ các giá trị đã phân tích (đã mã hóa URL) để làm liên kết chuyển trang. */
    private String buildQueryString(String q, Long brandId, Long categoryId, String sort) {
        StringBuilder sb = new StringBuilder();
        if (!q.isEmpty()) {
            sb.append("q=").append(URLEncoder.encode(q, StandardCharsets.UTF_8)).append('&');
        }
        if (brandId != null) {
            sb.append("brandId=").append(brandId).append('&');
        }
        if (categoryId != null) {
            sb.append("categoryId=").append(categoryId).append('&');
        }
        sb.append("sort=").append(sort);
        return sb.toString();
    }

    private String normalizeSort(String s) {
        if ("price_asc".equals(s) || "price_desc".equals(s)) {
            return s;
        }
        return "newest";
    }

    private int parsePage(String s) {
        if (s == null || s.isBlank()) {
            return 1;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 1;
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
}
