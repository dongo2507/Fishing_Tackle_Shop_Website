
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Thông báo - Fishing Tackle Shop</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/notification.css">
</head>

<body>

    <main class="notifications-page">

        <a class="notifications-back"
           href="<%= request.getContextPath() %>/index.jsp">
            ← Quay lại trang chủ
        </a>

        <header class="notifications-page-header">
            <h1>Thông báo của bạn</h1>
            <p>
                Cập nhật mới nhất về đơn hàng, thanh toán và vận chuyển.
            </p>
        </header>

        <!-- Danh sách được render bằng JavaScript -->
        <section class="notifications-all-list"
                 id="allNotifications"
                 aria-label="Danh sách thông báo">
        </section>

    </main>

    <script src="<%= request.getContextPath() %>/js/notification.js"
            defer></script>

</body>
</html>
