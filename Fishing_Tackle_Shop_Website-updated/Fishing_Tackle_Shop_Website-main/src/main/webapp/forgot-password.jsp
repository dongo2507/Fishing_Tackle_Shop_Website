
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - Fishing Tackle Shop</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/forgot-password.css">
</head>
<body>
<div class="container">
    <h2>Quên mật khẩu?</h2>

    <% if (request.getAttribute("message") != null) { %>
    <p class="message"><%= request.getAttribute("message") %></p>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
    <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <p>Nhập địa chỉ email đã đăng ký. Hướng dẫn đặt lại mật khẩu sẽ được gửi qua email.</p>

    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
        <label for="email">Địa chỉ email</label>
        <input type="email" id="email" name="email"
               placeholder="Nhập email của bạn" required>
        <button type="submit">Gửi mã OTP</button>
    </form>

    <div class="back">
        <a href="${pageContext.request.contextPath}/login.jsp">Quay lại đăng nhập</a>
    </div>
</div>
</body>
</html>