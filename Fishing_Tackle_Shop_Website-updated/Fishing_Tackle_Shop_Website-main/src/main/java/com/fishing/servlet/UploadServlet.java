package com.fishing.servlet;

import com.fishing.util.UploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Trả ảnh đã upload (lưu ngoài webapp) cho trình duyệt: /uploads/ten-file.jpg */
@WebServlet("/uploads/*")
public class UploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();           // ví dụ: /abc.jpg
        if (pathInfo == null || pathInfo.length() < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path file = UploadUtil.resolve(pathInfo.substring(1));
        if (file == null || !Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String type = contentType(file.getFileName().toString().toLowerCase());
        if (type == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType(type);
        resp.setContentLengthLong(Files.size(file));
        // Tên file là UUID, nội dung không đổi nên cache được
        resp.setHeader("Cache-Control", "public, max-age=86400");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        Files.copy(file, resp.getOutputStream());
    }

    private String contentType(String name) {
        if (name.endsWith(".jpg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".webp")) return "image/webp";
        return null;
    }
}
