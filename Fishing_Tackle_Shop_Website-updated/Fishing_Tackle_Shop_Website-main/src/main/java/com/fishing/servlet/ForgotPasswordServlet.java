
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

    private static final long OTP_VALIDITY = 5 * 60 * 1000L;
    private final SecureRandom random = new SecureRandom();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            request.setAttribute("error",
                    "Vui lòng nhập địa chỉ email.");
            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        email = email.trim().toLowerCase(java.util.Locale.ROOT);

        try {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.findByEmail(email);

            // Không tiết lộ email có đăng ký hay không.
            if (customer == null) {
                request.setAttribute("message",
                        "Nếu email đã đăng ký, hướng dẫn xác minh "
                                + "sẽ được gửi đến hộp thư của bạn.");
                request.getRequestDispatcher("/forgot-password.jsp")
                        .forward(request, response);
                return;
            }

            String otp = String.format(
                    "%06d", random.nextInt(1_000_000));

            // Chỉ lưu OTP vào session sau khi email gửi thành công.
            EmailUtil.sendOtp(email, otp);

            HttpSession session = request.getSession();

            // Xóa quy trình đặt lại mật khẩu cũ nếu có.
            clearResetSession(session);

            session.setAttribute("resetEmail", email);
            session.setAttribute("resetOtp", otp);
            session.setAttribute("resetOtpExpiry",
                    System.currentTimeMillis() + OTP_VALIDITY);
            session.setAttribute("resetOtpAttempts", 0);

            // POST -> Redirect -> GET, tránh gửi lại form khi refresh.
            response.sendRedirect(
                    request.getContextPath() + "/verify-otp.jsp");

        } catch (Exception e) {
            log("Gửi OTP đặt lại mật khẩu thất bại.", e);

            request.setAttribute("error",
                    "Không thể gửi OTP lúc này. "
                            + "Vui lòng kiểm tra cấu hình email và thử lại.");

            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
        }
    }

    private void clearResetSession(HttpSession session) {
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetOtp");
        session.removeAttribute("resetOtpExpiry");
        session.removeAttribute("resetOtpAttempts");
        session.removeAttribute("resetVerified");
        session.removeAttribute("resetVerifiedExpiry");
    }
}
