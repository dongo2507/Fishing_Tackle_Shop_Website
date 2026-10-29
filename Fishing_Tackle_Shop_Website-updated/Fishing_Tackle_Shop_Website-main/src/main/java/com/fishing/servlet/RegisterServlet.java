
package com.fishing.servlet;

import com.fishing.dao.CustomerDAO;
import com.fishing.model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (fullName == null || email == null || password == null
                || confirmPassword == null
                || fullName.isBlank() || email.isBlank()
                || password.isBlank()) {
            showError(request, response,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc.");
            return;
        }

        fullName = fullName.trim();
        email = email.trim().toLowerCase();
        phone = phone == null ? null : phone.trim();

        if (!password.equals(confirmPassword)) {
            showError(request, response,
                    "Mật khẩu xác nhận không khớp.");
            return;
        }

        if (password.length() < 8) {
            showError(request, response,
                    "Mật khẩu phải có ít nhất 8 ký tự.");
            return;
        }

        try {
            if (customerDAO.findByEmail(email) != null) {
                showError(request, response,
                        "Email này đã được đăng ký.");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            customerDAO.save(new Customer(
                    fullName,
                    email,
                    hashedPassword,
                    phone == null || phone.isBlank() ? null : phone
            ));

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp?registered=1"
            );

        } catch (Exception e) {
            log("Không thể đăng ký tài khoản Customer.", e);
            showError(request, response,
                    "Đăng ký chưa thành công. Vui lòng thử lại.");
        }
    }

    private void showError(HttpServletRequest request,
                           HttpServletResponse response,
                           String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/register.jsp")
                .forward(request, response);
    }
}