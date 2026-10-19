package com.fishing.servlet;

import com.fishing.dao.CustomerDAO;
import com.fishing.model.Customer;
import com.fishing.util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final SecureRandom random = new SecureRandom();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            response.sendRedirect(
                    request.getContextPath() + "/forgot-password.jsp");
            return;
        }

        email = email.trim();

        try {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.findByEmail(email);

            // Thông báo giống nhau để tránh tiết lộ email có đăng ký hay không.
            if (customer != null) {
                String otp = String.format("%06d", random.nextInt(1_000_000));

                HttpSession session = request.getSession();
                session.setAttribute("resetEmail", email);
                session.setAttribute("resetOtp", otp);
                session.setAttribute("resetOtpExpiry",
                        System.currentTimeMillis() + 5 * 60 * 1000L);
                session.setAttribute("resetOtpAttempts", 0);

                EmailUtil.sendOtp(email, otp);
            }

            request.getRequestDispatcher("/verify-otp.jsp")
                .forward(request, response);
        } catch (Exception e) {
            log("Không thể xử lý yêu cầu quên mật khẩu.", e);
            request.setAttribute("error",
                    "Chưa thể gửi mã OTP. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
        }
    }
}
