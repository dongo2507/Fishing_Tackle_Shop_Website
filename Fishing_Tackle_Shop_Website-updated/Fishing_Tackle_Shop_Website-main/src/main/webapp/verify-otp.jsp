
<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Xác minh OTP - Fishing Tackle Shop</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/forgot-password.css">
</head>
<body>
<div class="container">
    <h2>Xác minh OTP</h2>

    <p>
        Mã xác minh gồm 6 chữ số đã được gửi đến email
        đăng ký tài khoản của bạn.
    </p>

    <p>Mã có hiệu lực trong 5 phút.</p>

    <% if (request.getAttribute("error") != null) { %>
    <p class="error">
        <%= request.getAttribute("error") %>
    </p>
    <% } %>

    <form action="${pageContext.request.contextPath}/verify-otp"
          method="post">

        <label for="otp">Mã OTP</label>

        <input type="text"
               id="otp"
               name="otp"
               inputmode="numeric"
               pattern="[0-9]{6}"
               minlength="6"
               maxlength="6"
               autocomplete="one-time-code"
               placeholder="Nhập mã OTP gồm 6 chữ số"
               required
               autofocus>

        <button type="submit">Xác minh OTP</button>
    </form>

    <div class="back">
        <a href="${pageContext.request.contextPath}/forgot-password.jsp">
            Yêu cầu mã OTP mới
        </a>
    </div>
</div>
</body>
</html>
