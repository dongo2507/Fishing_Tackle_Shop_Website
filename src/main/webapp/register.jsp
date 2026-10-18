
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - Fishing Tackle Shop</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/login.css">
</head>

<body class="login-page">
    <div class="login-container">
        <h1>Đăng ký</h1>
        <p class="description">Tạo tài khoản Fishing Tackle Shop</p>

        <% if (request.getAttribute("error") != null) { %>
            <p class="error-message">
                <%= request.getAttribute("error") %>
            </p>
        <% } %>

        <form action="${pageContext.request.contextPath}/register"
              method="post">

            <label for="fullName">Họ và tên</label>
            <input type="text"
                   id="fullName"
                   name="fullName"
                   maxlength="100"
                   autocomplete="name"
                   required>

            <label for="email">Email</label>
            <input type="email"
                   id="email"
                   name="email"
                   maxlength="150"
                   autocomplete="email"
                   required>

            <label for="phone">Số điện thoại</label>
            <input type="tel"
                   id="phone"
                   name="phone"
                   maxlength="20"
                   autocomplete="tel">

            <label for="password">Mật khẩu</label>
            <input type="password"
                   id="password"
                   name="password"
                   minlength="8"
                   autocomplete="new-password"
                   required>

            <label for="confirmPassword">Xác nhận mật khẩu</label>
            <input type="password"
                   id="confirmPassword"
                   name="confirmPassword"
                   minlength="8"
                   autocomplete="new-password"
                   required>

            <button type="submit">Tạo tài khoản</button>
        </form>

        <div class="register-link">
            Đã có tài khoản?
            <a href="${pageContext.request.contextPath}/login.jsp">
                Đăng nhập
            </a>
        </div>
    </div>
</body>
</html>
