package com.fishing.servlet;

import com.fishing.dao.ProductDAO;
import com.fishing.dao.ProductImageDAO;
import com.fishing.util.UploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý ảnh của sản phẩm (Admin). Mọi tham số điều khiển nằm trên URL (query string),
 * phần thân request chỉ chứa file:
 *   POST /admin/product-images?action=uploadThumbnail&productId=1   (file: thumbnail)
 *   POST /admin/product-images?action=removeThumbnail&productId=1
 *   POST /admin/product-images?action=uploadAlbum&productId=1       (file: images, nhiều file)
 *   POST /admin/product-images?action=deleteImage&productId=1&imageId=7
 * Xong thì quay lại trang sửa sản phẩm.
 *
 * Tách riêng khỏi ProductServlet vì Tomcat giới hạn số phần (part) của một request multipart
 * (mặc định 10), nếu gộp chung với form sản phẩm sẽ dễ vượt giới hạn.
 */
@WebServlet("/admin/product-images")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,        // dưới 1MB giữ trong RAM
        maxFileSize = 5L * 1024 * 1024,         // tối đa 5MB mỗi ảnh
        maxRequestSize = 30L * 1024 * 1024)     // tối đa 30MB mỗi lần gửi
public class ProductImageServlet extends HttpServlet {

    private static final int MAX_ALBUM_PER_UPLOAD = 5;
    private static final int MAX_ALBUM_TOTAL = 20;

    private final ProductDAO productDAO = new ProductDAO();
    private final ProductImageDAO imageDAO = new ProductImageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long productId = parseId(req.getParameter("productId"));
        String action = req.getParameter("action");

        if (productId == null || productDAO.findById(productId) == null) {
            flash(req, "error", "Không tìm thấy sản phẩm.");
            resp.sendRedirect(req.getContextPath() + "/admin/products");
            return;
        }

        try {
            switch (action == null ? "" : action) {
                case "uploadThumbnail": uploadThumbnail(req, productId); break;
                case "removeThumbnail": removeThumbnail(req, productId); break;
                case "uploadAlbum":     uploadAlbum(req, productId);     break;
                case "deleteImage":     deleteImage(req, productId);     break;
                default: flash(req, "error", "Thao tác không hợp lệ.");
            }
        } catch (IllegalArgumentException e) {
            flash(req, "error", e.getMessage());
        } catch (IllegalStateException e) {
            // Tomcat ném lỗi này khi vượt giới hạn dung lượng hoặc số lượng file
            flash(req, "error", "Tệp quá lớn hoặc quá nhiều tệp. Mỗi ảnh tối đa 5MB, mỗi lần tối đa "
                    + MAX_ALBUM_PER_UPLOAD + " ảnh.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products?action=edit&id=" + productId);
    }

    // ---------- Các chức năng ----------

    private void uploadThumbnail(HttpServletRequest req, Long productId)
            throws IOException, ServletException {
        String url = UploadUtil.saveImage(req.getPart("thumbnail"));
        if (url == null) {
            throw new IllegalArgumentException("Vui lòng chọn một ảnh.");
        }
        String old;
        try {
            old = imageDAO.replaceThumbnail(productId, url);
        } catch (RuntimeException e) {
            UploadUtil.deleteQuietly(url);      // lưu DB lỗi thì xóa file vừa ghi
            throw e;
        }
        UploadUtil.deleteQuietly(old);          // xóa ảnh đại diện cũ
        flash(req, "success", "Đã cập nhật ảnh đại diện.");
    }

    private void removeThumbnail(HttpServletRequest req, Long productId) {
        String old = imageDAO.replaceThumbnail(productId, null);
        UploadUtil.deleteQuietly(old);
        flash(req, "success", "Đã xóa ảnh đại diện.");
    }

    private void uploadAlbum(HttpServletRequest req, Long productId)
            throws IOException, ServletException {
        List<Part> files = new ArrayList<>();
        for (Part part : req.getParts()) {
            if ("images".equals(part.getName()) && part.getSize() > 0) {
                files.add(part);
            }
        }
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một ảnh.");
        }
        if (files.size() > MAX_ALBUM_PER_UPLOAD) {
            throw new IllegalArgumentException("Mỗi lần chỉ tải lên tối đa " + MAX_ALBUM_PER_UPLOAD + " ảnh.");
        }
        long existing = imageDAO.countByProduct(productId);
        if (existing + files.size() > MAX_ALBUM_TOTAL) {
            throw new IllegalArgumentException("Album tối đa " + MAX_ALBUM_TOTAL
                    + " ảnh (hiện có " + existing + ").");
        }

        List<String> urls = new ArrayList<>();
        try {
            for (Part part : files) {
                urls.add(UploadUtil.saveImage(part));   // ảnh nào lỗi thì ném IllegalArgumentException
            }
            imageDAO.add(productId, urls);
        } catch (IOException | RuntimeException e) {
            for (String u : urls) {
                UploadUtil.deleteQuietly(u);            // hoàn tác: xóa các file đã ghi
            }
            throw e;
        }
        flash(req, "success", "Đã tải lên " + urls.size() + " ảnh.");
    }

    private void deleteImage(HttpServletRequest req, Long productId) {
        Long imageId = parseId(req.getParameter("imageId"));
        String url = imageId == null ? null : imageDAO.delete(productId, imageId);
        if (url == null) {
            flash(req, "error", "Không tìm thấy ảnh.");
            return;
        }
        UploadUtil.deleteQuietly(url);
        flash(req, "success", "Đã xóa ảnh.");
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

    private void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMessage", message);
    }
}
