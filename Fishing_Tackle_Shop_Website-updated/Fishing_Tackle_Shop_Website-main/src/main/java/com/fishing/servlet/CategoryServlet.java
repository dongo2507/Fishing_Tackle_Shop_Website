package com.fishing.servlet;

import com.fishing.dao.CategoryDAO;
import com.fishing.model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller quản lý danh mục (Admin).
 * GET : /admin/categories              -> danh sách
 *       /admin/categories?action=new   -> form thêm
 *       /admin/categories?action=edit&id=1 -> form sửa
 * POST: action=save | delete | toggle  (xong thì redirect về danh sách)
 */
@WebServlet("/admin/categories")
public class CategoryServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/admin/category/list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/category/form.jsp";

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equals(action)) {
            req.setAttribute("category", null);
            req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
        } else if ("edit".equals(action)) {
            Category c = findFromParam(req);
            if (c == null) {
                flash(req, "error", "Không tìm thấy danh mục.");
                redirectToList(req, resp);
                return;
            }
            req.setAttribute("category", c);
            req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
        } else {
            req.setAttribute("categories", categoryDAO.findAll(false));
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
        } else if ("toggle".equals(action)) {
            toggle(req, resp);
        } else {
            redirectToList(req, resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = parseId(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String description = trim(req.getParameter("description"));
        boolean visible = "on".equals(req.getParameter("visible"));

        Category c;
        if (id != null) {
            c = categoryDAO.findById(id);
            if (c == null) {
                flash(req, "error", "Không tìm thấy danh mục.");
                redirectToList(req, resp);
                return;
            }
        } else {
            c = new Category();
        }
        c.setName(name);
        c.setDescription(description.isEmpty() ? null : description);
        c.setVisible(visible);

        // Kiểm tra dữ liệu ở tầng Java (không dựa vào database)
        String error = validate(c, id);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("category", c);
            req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
            return;
        }

        categoryDAO.save(c);
        flash(req, "success", id == null ? "Đã thêm danh mục." : "Đã cập nhật danh mục.");
        redirectToList(req, resp);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        if (id == null || categoryDAO.findById(id) == null) {
            flash(req, "error", "Không tìm thấy danh mục.");
        } else {
            long count = categoryDAO.countProducts(id);
            if (count > 0) {
                flash(req, "error", "Danh mục đang có " + count
                        + " sản phẩm nên không thể xóa. Hãy ẩn danh mục thay vì xóa.");
            } else {
                categoryDAO.delete(id);
                flash(req, "success", "Đã xóa danh mục.");
            }
        }
        redirectToList(req, resp);
    }

    private void toggle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseId(req.getParameter("id"));
        if (id == null) {
            flash(req, "error", "Không tìm thấy danh mục.");
        } else {
            categoryDAO.toggleVisible(id);
            flash(req, "success", "Đã đổi trạng thái hiển thị.");
        }
        redirectToList(req, resp);
    }


    private String validate(Category c, Long id) {
        if (c.getName().isEmpty()) {
            return "Tên danh mục không được để trống.";
        }
        if (c.getName().length() > 100) {
            return "Tên danh mục tối đa 100 ký tự.";
        }
        if (c.getDescription() != null && c.getDescription().length() > 500) {
            return "Mô tả tối đa 500 ký tự.";
        }
        if (categoryDAO.existsByName(c.getName(), id)) {
            return "Tên danh mục đã tồn tại.";
        }
        return null;
    }

    private Category findFromParam(HttpServletRequest req) {
        Long id = parseId(req.getParameter("id"));
        return id == null ? null : categoryDAO.findById(id);
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
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }
}