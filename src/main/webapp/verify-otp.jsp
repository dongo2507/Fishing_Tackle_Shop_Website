<%@page contentType = "text/html" pageEncoding = "UTF-8"%>
<!DOCTYPE html>
<html lang ="vi">
    <head>
        <metadata charset ="UTF-8">
        <title>Xác minh OTP</title>
        <link rel ="stylesheet" href ="${pageContext.request.contextPath}/css/forgot-password.css">
    </head>
    <body>
        <div class ="container">
            <h2>Xác minh OTP </h2>
            <p>Nhập mã OTP đã được gửi đến email của bạn. Mã có hiệu lực trong 5 phút.</p>
            <% if (request.getAttribute("error") != null) {%>
            <p class ="error"><%= request.getAttribute("error") %></p>
            <% } %>
            <form action="${pageContext.request.contextPath}/verify-otp"
                method="post">
                <label for="otp">Mã OTP gồm 6 chữ số</label>
                <input type="text" id="otp" name="otp"
                       inputmode="numeric" pattern="[0-9]{6}"
                       maxlength="6" placeholder="Nhập mã OTP" required>
                <button type="submit">Xác minh</button>
            </form>
        </div>
    </body>
</html>