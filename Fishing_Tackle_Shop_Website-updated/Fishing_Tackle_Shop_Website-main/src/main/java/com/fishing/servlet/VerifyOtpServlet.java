
package com.fishing.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/verify-otp")
public class VerifyOtpServlet extends HttpServlet {

    private static final int MAX_ATTEMPTS = 5;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("resetOtp") == null
                || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/forgot-password.jsp");
            return;
        }

        Long expiry = (Long) session.getAttribute("resetOtpExpiry");
        Integer attempts = (Integer) session.getAttribute("resetOtpAttempts");

        if (expiry == null || System.currentTimeMillis() > expiry) {
            clearOtp(session);
            request.setAttribute("error",
                    "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
            request.getRequestDispatcher("/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        if (attempts == null) {
            attempts = 0;
        }

        if (attempts >= MAX_ATTEMPTS) {
            clearOtp(session);
            request.setAttribute("error",
                    "Bạn đã nhập sai quá số lần cho phép. Hãy yêu cầu mã mới.");
            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        String submittedOtp = request.getParameter("otp");
        String savedOtp = (String) session.getAttribute("resetOtp");

        if (submittedOtp == null || !savedOtp.equals(submittedOtp.trim())) {
            attempts++;
            session.setAttribute("resetOtpAttempts", attempts);

            if (attempts >= MAX_ATTEMPTS) {
                clearOtp(session);
                request.setAttribute("error",
                        "Bạn đã nhập sai 5 lần. Vui lòng yêu cầu mã mới.");
                request.getRequestDispatcher("/forgot-password.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute("error",
                    "Mã OTP không chính xác. Bạn còn "
                            + (MAX_ATTEMPTS - attempts) + " lần thử.");
            request.getRequestDispatcher("/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        session.removeAttribute("resetOtp");
        session.removeAttribute("resetOtpExpiry");
        session.removeAttribute("resetOtpAttempts");
        session.setAttribute("resetVerified", true);
        session.setAttribute("resetVerifiedExpiry",
                System.currentTimeMillis() + 10 * 60 * 1000L);

        response.sendRedirect(
                request.getContextPath() + "/reset-password.jsp");
    }

    private void clearOtp(HttpSession session) {
        session.removeAttribute("resetOtp");
        session.removeAttribute("resetOtpExpiry");
        session.removeAttribute("resetOtpAttempts");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetVerified");
        session.removeAttribute("resetVerifiedExpiry");
    }
}