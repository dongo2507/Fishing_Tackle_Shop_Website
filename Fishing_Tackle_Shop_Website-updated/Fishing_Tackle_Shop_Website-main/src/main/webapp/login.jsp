<%@page contentType = "text/html" pageEncoding = "UTF-8"%>
<!DOCTYPE html>
<html lang ="vi">
<head>
    <meta charset ="UTF-8">
    <meta name ="viewport" content ="width = device-width, initial-scale = 1.0">
    <link rel ="stylesheet"
          href="${pageContext.request.contextPath}/css/login.css">
    <Title>Đăng nhập - Fishing Tackle Shop</Title>
</head>
<body class="login-page">
<div class ="login-container">
    <h1>Đăng nhập</h1>
    <p class ="description">Fishing Tackle Shop</p>
    <% if (request.getAttribute("error") != null) { %>
    <p class ="error-message">
        <%= request.getAttribute("error") %>
    </p>
    <% } %>
    <form action = "${pageContext.request.contextPath}/login"
          method ="post">
        <label for ="email">Email hoặc Username</label>
        <input type ="text" id="email" name="username"
               placeholder="Nhập email hoặc username"
               autocomplete ="username"
               required>
        <label for="password">Mật khẩu</label>
        <input type="password" id="password" name="password"
               placeholder="Nhập mật khẩu"
               autocomplete="current-password"
               required>
        <button type ="submit">Đăng nhập</button>
    </form>
    <div class="register-link">
        <a href="${pageContext.request.contextPath}/forgot-password.jsp">
            Quên mật khẩu?
        </a>
    </div>
    <div class ="register-link">
        Chưa có tài khoản
        <a href ="${pageContext.request.contextPath}/register.jsp">
            Đăng ký
        </a>
    </div>
</div>
</body>
</html>