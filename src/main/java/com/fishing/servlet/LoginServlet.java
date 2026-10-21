package com.fishing.servlet;

import com.fishing.dao.CustomerDAO;
import com.fishing.model.Admin;
import com.fishing.model.Customer;
import com.fishing.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    
    // TÀI KHOẢN ADMIN
    /*System.out.println(
    org.mindrot.jbcrypt.BCrypt.hashpw(
        "Admin@12345",
        org.mindrot.jbcrypt.BCrypt.gensalt(10)
        )
    );*/
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
            HttpSession session = request.getSession();
            request.changeSessionId();

            session.removeAttribute("customer");
            session.setAttribute("admin", admin);

            response.sendRedirect(
                    request.getContextPath() + "/admin/orders");
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
    Customer customer = customerDAO.findByEmail(username);

    if (customer != null
            && customer.getPassword() != null
            && BCrypt.checkpw(password, customer.getPassword())) {

        HttpSession session = request.getSession();
        request.changeSessionId();

        session.removeAttribute("admin");
        session.setAttribute("customer", customer);

        response.sendRedirect(request.getContextPath() + "/");
        return;
    }

    request.setAttribute("error",
            "Email/username hoặc mật khẩu không đúng.");
    request.getRequestDispatcher("/login.jsp")
           .forward(request, response);
}

}
