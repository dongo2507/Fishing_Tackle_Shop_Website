package com.fishing.servlet;

import com.fishing.dao.BrandDAO;
import com.fishing.dao.CategoryDAO;
import com.fishing.dao.ProductDAO;
import com.fishing.dao.ProductImageDAO;
import com.fishing.enums.ProductStatus;
import com.fishing.model.Brand;
import com.fishing.model.Category;
import com.fishing.model.Product;
import com.fishing.model.ProductImage;
import com.fishing.util.UploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller quản lý sản phẩm (Admin).
 * GET : /admin/products                       -> danh sách (q, brandId, status để tìm/lọc)
 *       /admin/products?action=new            -> form thêm
 *       /admin/products?action=edit&id=1      -> form sửa (kèm quản lý ảnh)
 * POST: action=save | delete | status         (xong thì redirect)
 * Ảnh đại diện và album do ProductImageServlet xử lý.
 */
@WebServlet("/admin/products")
public class ProductServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/admin/product/list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/product/form.jsp";
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999999");
    private static final int MAX_STOCK = 1_000_000;

    private final ProductDAO productDAO = new ProductDAO();
    private final ProductImageDAO imageDAO = new ProductImageDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equals(action)) {
            showForm(req, resp, new Product());
        } else if ("edit".equals(action)) {
            Long id = parseId(req.getParameter("id"));
            Product p = id == null ? null : productDAO.findById(id);
            if (p == null) {
                flash(req, "error", "Không tìm thấy sản phẩm.");
                redirectToList(req, resp);
                return;
            }
            showForm(req, resp, p);
        } else {
            list(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("save".equals(action)) {
            save(req, resp);
        } else if ("delete".equals(action)) {
            delete(req, resp);
        } else if ("status".equals(action)) {
            changeStatus(req, resp);
        } else {
            redirectToList(req, resp);
        }
    }

    // ---------- Các chức năng ----------

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = trim(req.getParameter("q"));
        Long brandId = parseId(req.getParameter("brandId"));
        ProductStatus status = parseStatus(req.getParameter("status"));

        req.setAttribute("products", productDAO.search(keyword, brandId, status));
        req.setAttribute("brands", brandDAO.findAll());
        req.setAttribute("statuses", ProductStatus.values());
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product product)
            throws ServletException, IOException {
        req.setAttribute("product", product);
        req.setAttribute("images", product.getId() == null
                ? new ArrayList<ProductImage>()
                : imageDAO.findByProduct(product.getId()));
        req.setAttribute("categories", categoryDAO.findAll(false));
        req.setAttribute("brands", brandDAO.findAll());
        req.setAttribute("statuses", ProductStatus.values());
        req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = parseId(req.getParameter("id"));

        Product p;
        if (id != null) {
            p = productDAO.findById(id);
            if (p == null) {
                flash(req, "error", "Không tìm thấy sản phẩm.");
                redirectToList(req, resp);
                return;
            }
        } else {
            p = new Product();
        }

        // Đọc dữ liệu từ form (ảnh đại diện KHÔNG nằm trong form này nên không bị ghi đè)
        String name = trim(req.getParameter("name"));
        String description = emptyToNull(trim(req.getParameter("description")));
        BigDecimal price = parsePrice(req.getParameter("price"));
        Integer stock = parseStock(req.getParameter("stockQuantity"));
        ProductStatus status = parseStatus(req.getParameter("status"));
        Long categoryId = parseId(req.getParameter("categoryId"));
        Long brandId = parseId(req.getParameter("brandId"));
        Category category = categoryId == null ? null : categoryDAO.findById(categoryId);
        Brand brand = brandId == null ? null : brandDAO.findById(brandId);

        // Gán vào đối tượng (nếu lỗi thì form hiển thị lại đúng dữ liệu đã nhập)
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setBrand(brand);
        if (price != null) {
            p.setPrice(price.setScale(2, RoundingMode.HALF_UP));
        }
        if (stock != null) {
            p.setStockQuantity(stock);
        }
        if (status != null) {
            p.setStatus(status);
        }

        // Kiểm tra dữ liệu ở tầng Java (không dựa vào database)
        String error = validate(name, description, price, stock, status, category, brand);
        if (error != null) {
            req.setAttribute("error", error);
            showForm(req, resp, p);
            return;
        }

        Product saved = productDAO.save(p);
        if (id == null) {
            // Sản phẩm mới: chuyển thẳng sang trang sửa để thêm ảnh
            flash(req, "success", "Đã thêm sản phẩm. Bạn có thể thêm ảnh đại diện và album bên dưới.");
            resp.sendRedirect(req.getContextPath() + "/admin/products?action=edit&id=" + saved.getId());
        } else {
            flash(req, "success", "Đã cập nhật sản phẩm.");
            redirectToList(req, resp);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        Product existing = id == null ? null : productDAO.findById(id);
        if (existing == null) {
            flash(req, "error", "Không tìm thấy sản phẩm.");
        } else {
            // Gom danh sách file ảnh trước khi xóa dữ liệu
            List<String> files = new ArrayList<>();
            if (existing.getThumbnailUrl() != null) {
                files.add(existing.getThumbnailUrl());
            }
            for (ProductImage img : imageDAO.findByProduct(id)) {
                files.add(img.getImageUrl());
            }
            try {
                productDAO.delete(id);
                for (String f : files) {
                    UploadUtil.deleteQuietly(f);    // xóa file thật sau khi xóa DB thành công
                }
                flash(req, "success", "Đã xóa sản phẩm.");
            } catch (RuntimeException e) {
                getServletContext().log("Không xóa được sản phẩm id=" + id, e);
                flash(req, "error", "Không thể xóa sản phẩm này (có thể đã nằm trong giỏ hàng hoặc đơn hàng). "
                        + "Hãy đổi trạng thái sang \"Ngừng kinh doanh\" hoặc \"Ẩn\".");
            }
        }
        redirectToList(req, resp);
    }

    private void changeStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        ProductStatus status = parseStatus(req.getParameter("status"));
        if (id == null || status == null) {
            flash(req, "error", "Dữ liệu không hợp lệ.");
        } else {
            productDAO.updateStatus(id, status);
            flash(req, "success", "Đã cập nhật trạng thái: " + status.getLabel() + ".");
        }
        redirectToList(req, resp);
    }

    // ---------- Kiểm tra dữ liệu ----------

    private String validate(String name, String description, BigDecimal price, Integer stock,
                            ProductStatus status, Category category, Brand brand) {
        if (name.isEmpty()) {
            return "Tên sản phẩm không được để trống.";
        }
        if (name.length() > 255) {
            return "Tên sản phẩm tối đa 255 ký tự.";
        }
        if (price == null) {
            return "Giá không hợp lệ (phải là số lớn hơn hoặc bằng 0).";
        }
        if (price.compareTo(MAX_PRICE) > 0) {
            return "Giá quá lớn.";
        }
        if (stock == null) {
            return "Số lượng tồn phải là số nguyên từ 0 đến " + MAX_STOCK + ".";
        }
        if (status == null) {
            return "Trạng thái không hợp lệ.";
        }
        if (category == null) {
            return "Vui lòng chọn danh mục.";
        }
        if (brand == null) {
            return "Vui lòng chọn thương hiệu.";
        }
        if (description != null && description.length() > 5000) {
            return "Mô tả tối đa 5000 ký tự.";
        }
        return null;
    }

    // ---------- Hàm hỗ trợ ----------

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

    private BigDecimal parsePrice(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            BigDecimal v = new BigDecimal(s.trim());
            return v.signum() < 0 ? null : v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseStock(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            int v = Integer.parseInt(s.trim());
            return (v < 0 || v > MAX_STOCK) ? null : v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private ProductStatus parseStatus(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return ProductStatus.valueOf(s.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String emptyToNull(String s) {
        return s.isEmpty() ? null : s;
    }

    private void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMessage", message);
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
