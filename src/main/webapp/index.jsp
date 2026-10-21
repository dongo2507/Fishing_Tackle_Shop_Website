<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fishing Tackle Shop</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/notification.css">
</head>
<body>

    <!-- Thanh cửa hàng -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/" class="logo">
            Fishing Tackle Shop
        </a>
        <% 
            com.fishing.model.Customer customer = (com.fishing.model.Customer) session.getAttribute("customer");
        %>
        <!-- ACCOUNT -->
        <div class ="account-wrapper">
            <% if (customer != null) { %>
            <span class ="account-name">
                Xin chào, <%= customer.getFullName() %>
            </span>
            <a href="${pageContext.request.contextPath}/addresses"
               class ="login-button">
                Địa chỉ giao hàng
            </a>
               <% } else { %>
               <a href="${pageContext.request.contextPath}/login.jsp"
                  class ="login-button">
                   Đăng nhập
               </a>
            <% } %>      
        </div>
        <!-- Chuông thông báo -->
        <div class="notification-wrapper">
            <button type="button"
                    class="notification-bell"
                    id="notificationBell"
                    aria-label="Thông báo"
                    aria-expanded="false"
                    aria-controls="notificationDropdown">
                <span aria-hidden="true"> 🔔 </span>
                <span class="notification-badge"
                      id="notificationBadge"
                      hidden>0</span>
            </button>

            <!-- Danh sách thông báo mới nhất -->
            <div class  ="notification-dropdown"
                 id ="notificationDropdown"
                hidden>
                <div class ="notification-dropdown-header">
                     <h3>Thông báo</h3>
                </div>
                <div
                    class = "notification-list"
                    id="latestNotifications">
                </div>
                <a class ="notification-view-all"
                   href="${pageContext.request.contextPath}/notification.jsp">
                    Xem tất cả thông báo →
                </a>
            </div>
        </div>
    </header>

    <main>
        <h1>Chào mừng đến với Fishing Tackle Shop</h1>
        <p>Khám phá các sản phẩm câu cá của chúng tôi.</p>
    </main>

    <script src="${pageContext.request.contextPath}/js/notification.js"
            defer></script>
</body>
</html>