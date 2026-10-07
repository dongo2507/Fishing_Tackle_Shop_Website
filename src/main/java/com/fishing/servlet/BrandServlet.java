package com.fishing.servlet;

import com.fishing.dao.BrandDAO;
import com.fishing.model.Brand;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller quản lý thương hiệu (Admin).
 * GET : /admin/brands                  -> danh sách
 *       /admin/brands?action=new       -> form thêm
 *       /admin/brands?action=edit&id=1 -> form sửa
 * POST: action=save | delete           (xong thì redirect về danh sách)
 */
@WebServlet("/admin/brands")
public class BrandServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/admin/brand/list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/brand/form.jsp";

    private final BrandDAO brandDAO = new BrandDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equals(action)) {
            req.setAttribute("brand", null);
            req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
        } else if ("edit".equals(action)) {
            Long id = parseId(req.getParameter("id"));
            Brand b = id == null ? null : brandDAO.findById(id);
            if (b == null) {
                flash(req, "error", "Không tìm thấy thương hiệu.");
                redirectToList(req, resp);
                return;
            }
            req.setAttribute("brand", b);
            req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
        } else {
            req.setAttribute("brands", brandDAO.findAll());
            req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
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
        } else {
            redirectToList(req, resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = parseId(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String logoUrl = trim(req.getParameter("logoUrl"));
        String description = trim(req.getParameter("description"));

        Brand b;
        if (id != null) {
            b = brandDAO.findById(id);
            if (b == null) {
                flash(req, "error", "Không tìm thấy thương hiệu.");
                redirectToList(req, resp);
                return;
            }
        } else {
            b = new Brand();
        }
        b.setName(name);
        b.setLogoUrl(logoUrl.isEmpty() ? null : logoUrl);
        b.setDescription(description.isEmpty() ? null : description);

        // Kiểm tra dữ liệu ở tầng Java (không dựa vào database)
        String error = validate(b, id);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("brand", b);
            req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
            return;
        }

        brandDAO.save(b);
        flash(req, "success", id == null ? "Đã thêm thương hiệu." : "Đã cập nhật thương hiệu.");
        redirectToList(req, resp);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        if (id == null || brandDAO.findById(id) == null) {
            flash(req, "error", "Không tìm thấy thương hiệu.");
        } else {
            long count = brandDAO.countProducts(id);
            if (count > 0) {
                flash(req, "error", "Thương hiệu đang có " + count
                        + " sản phẩm nên không thể xóa.");
            } else {
                brandDAO.delete(id);
                flash(req, "success", "Đã xóa thương hiệu.");
            }
        }
        redirectToList(req, resp);
    }

    // ---------- Hàm hỗ trợ ----------

    private String validate(Brand b, Long id) {
        if (b.getName().isEmpty()) {
            return "Tên thương hiệu không được để trống.";
        }
        if (b.getName().length() > 100) {
            return "Tên thương hiệu tối đa 100 ký tự.";
        }
        if (b.getLogoUrl() != null) {
            String u = b.getLogoUrl().toLowerCase();
            if (!(u.startsWith("http://") || u.startsWith("https://"))) {
                return "Đường dẫn logo phải bắt đầu bằng http:// hoặc https://.";
            }
            if (b.getLogoUrl().length() > 255) {
                return "Đường dẫn logo tối đa 255 ký tự.";
            }
        }
        if (b.getDescription() != null && b.getDescription().length() > 500) {
            return "Mô tả tối đa 500 ký tự.";
        }
        if (brandDAO.existsByName(b.getName(), id)) {
            return "Tên thương hiệu đã tồn tại.";
        }
        return null;
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

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/brands");
    }
}
