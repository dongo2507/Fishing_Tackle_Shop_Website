
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lại mật khẩu</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/forgot-password.css">
</head>
<body>
<div class="container">
    <h2>Đặt lại mật khẩu</h2>
    <p>Nhập mật khẩu mới cho tài khoản của bạn.</p>

    <% if (request.getAttribute("error") != null) { %>
    <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="${pageContext.request.contextPath}/reset-password"
          method="post">
        <label for="password">Mật khẩu mới</label>
        <input type="password" id="password" name="password"
               minlength="8" autocomplete ="new-password" required>

        <label for="confirmPassword">Xác nhận mật khẩu</label>
        <input type="password" id="confirmPassword"
               name="confirmPassword" minlength="8" autocomplete = "new-password" required>

        <button type="submit">Đổi mật khẩu</button>
    </form>
</div>
</body>
</html>