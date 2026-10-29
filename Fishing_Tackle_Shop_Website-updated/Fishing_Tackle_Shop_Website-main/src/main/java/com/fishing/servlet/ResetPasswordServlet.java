package com.fishing.servlet;

import com.fishing.dao.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || !Boolean.TRUE.equals(session.getAttribute("resetVerified"))
                || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/forgot-password.jsp");
            return;
        }

        Object expiryObject = session.getAttribute("resetVerifiedExpiry");

        if (!(expiryObject instanceof Long)
                || System.currentTimeMillis() > (Long) expiryObject) {
            clearResetSession(session);
            request.setAttribute("error",
                    "Phiên đặt lại mật khẩu đã hết hạn. Vui lòng thực hiện lại.");
            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password == null || confirmPassword == null
                || password.length() < 8) {
            request.setAttribute("error",
                    "Mật khẩu phải có ít nhất 8 ký tự.");
            request.getRequestDispatcher("/reset-password.jsp")
                    .forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error",
                    "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/reset-password.jsp")
                    .forward(request, response);
            return;
        }

        try {
            String email = (String) session.getAttribute("resetEmail");
            String hashedPassword = BCrypt.hashpw(
                    password, BCrypt.gensalt());

            CustomerDAO customerDAO = new CustomerDAO();

            if (customerDAO.updatePassword(email, hashedPassword)) {
                clearResetSession(session);
                session.removeAttribute("customer");

                request.setAttribute("message",
                        "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);
            } else {
                request.setAttribute("error",
                        "Không tìm thấy tài khoản. Vui lòng thực hiện lại.");
                request.getRequestDispatcher("/forgot-password.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            log("Không thể cập nhật mật khẩu.", e);
            request.setAttribute("error",
                    "Có lỗi khi cập nhật mật khẩu. Vui lòng thử lại.");
            request.getRequestDispatcher("/reset-password.jsp")
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
