package com.fishing.servlet;

import com.fishing.dao.NotificationDAO;
import com.fishing.model.Customer;
import com.fishing.model.Notification;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationDAO dao = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Customer customer = session == null ? null
                : (Customer) session.getAttribute("customer");

        if (customer == null) {
            resp.setStatus(401);
            resp.getWriter().write("[]");
            return;
        }

        String id = customer.getCustomerId();
        String action = req.getParameter("action");

        if ("count".equals(action)) {
            resp.getWriter().write(
                    "{\"count\":" + dao.countUnreadByRecipient(id) + "}");
            return;
        }

        List<Notification> list = "all".equals(action)
                ? dao.findByRecipient(id)
                : dao.findLatestByRecipient(id, 5);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Notification n = list.get(i);
            if (i > 0) json.append(",");

            json.append("{\"id\":\"").append(n.getNotificationId())
                .append("\",\"title\":\"").append(n.getTitle())
                .append("\",\"content\":\"").append(n.getContent())
                .append("\",\"isRead\":").append(n.isRead()).append("}");
        }

        json.append("]");
        resp.getWriter().write(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Customer customer = session == null ? null
                : (Customer) session.getAttribute("customer");

        if (customer == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"success\":false}");
            return;
        }

        boolean success = dao.markAsRead(
                req.getParameter("id"), customer.getCustomerId());

        long count = dao.countUnreadByRecipient(customer.getCustomerId());

        resp.getWriter().write(
                "{\"success\":" + success + ",\"count\":" + count + "}");
    }
}
