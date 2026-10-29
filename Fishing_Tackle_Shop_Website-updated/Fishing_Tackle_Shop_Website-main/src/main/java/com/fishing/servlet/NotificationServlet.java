package com.fishing.servlet;

import com.fishing.dao.NotificationDAO;
import com.fishing.model.Notification;
import com.fishing.util.AuthUtil;
import com.fishing.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Thông báo của khách (phần của Dũng).
 * GET : /notifications              -> trang danh sách
 *       /notifications?format=json  -> JSON cho chuông thông báo trên header (js/notification.js)
 * POST: action=read id | readAll
 */
@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/customer/notification/list.jsp";

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long customerId = AuthUtil.getCustomerId(req);

        if ("json".equals(req.getParameter("format"))) {
            List<Notification> latest = notificationDAO.findByRecipient(customerId, 5);
            StringBuilder json = new StringBuilder();
            json.append("{\"unread\":").append(notificationDAO.countUnread(customerId)).append(",\"items\":[");
            for (int i = 0; i < latest.size(); i++) {
                Notification n = latest.get(i);
                if (i > 0) {
                    json.append(',');
                }
                json.append("{\"id\":").append(n.getId())
                        .append(",\"icon\":").append(str(n.getType() == null ? "" : n.getType().getIcon()))
                        .append(",\"title\":").append(str(n.getTitle()))
                        .append(",\"message\":").append(str(n.getContent()))
                        .append(",\"time\":").append(str(n.getTimeText()))
                        .append(",\"unread\":").append(!n.isRead())
                        .append('}');
            }
            json.append("]}");
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(json.toString());
            return;
        }

        req.setAttribute("notifications", notificationDAO.findByRecipient(customerId, 50));
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long customerId = AuthUtil.getCustomerId(req);
        String action = req.getParameter("action");
        if ("readAll".equals(action)) {
            notificationDAO.markAllRead(customerId);
        } else if ("read".equals(action)) {
            Long id = WebUtil.parseId(req.getParameter("id"));
            if (id != null) {
                notificationDAO.markRead(id, customerId);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/notifications");
    }

    /** Chuỗi JSON có escape ký tự đặc biệt. */
    private String str(String s) {
        if (s == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.append('"').toString();
    }
}
