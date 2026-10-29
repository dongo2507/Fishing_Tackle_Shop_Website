package com.fishing.servlet;

import com.fishing.dao.CustomerDAO;
import com.fishing.model.Admin;
import com.fishing.model.Customer;
import com.fishing.util.AuthUtil;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null
                || username.trim().isEmpty() || password.isEmpty()) {
            request.setAttribute("error",
                    "Vui lòng nhập username/email và mật khẩu.");
            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
            return;
        }

        username = username.trim();

        // 1. Kiểm tra tài khoản Admin trước
        EntityManager em = JPAUtil.createEntityManager();

        try {
            Admin admin = em.createQuery(
                            "SELECT a FROM Admin a WHERE a.username = :username",
                            Admin.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (BCrypt.checkpw(password, admin.getPassword())) {
                AuthUtil.loginAdmin(request, admin);

                String next = AuthUtil.safeNext(request, request.getParameter("next"));
                response.sendRedirect(next != null && next.contains("/admin/")
                        ? next
                        : request.getContextPath() + "/admin/products");
                return;
            }

        } catch (NoResultException e) {
            // Không phải username Admin, tiếp tục kiểm tra Customer.
        } catch (IllegalArgumentException e) {
            request.setAttribute("error",
                    "Mật khẩu tài khoản chưa đúng định dạng.");
            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
            return;
        } finally {
            em.close();
        }

        // 2. Kiểm tra tài khoản Customer bằng email
        Customer customer = customerDAO.findByEmail(username.toLowerCase());

        if (customer != null
                && customer.getPassword() != null
                && BCrypt.checkpw(password, customer.getPassword())) {

            AuthUtil.loginCustomer(request, customer);

            String next = AuthUtil.safeNext(request, request.getParameter("next"));
            response.sendRedirect(next != null && !next.contains("/admin/")
                    ? next
                    : request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("error",
                "Email/username hoặc mật khẩu không đúng.");
        request.getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }

}