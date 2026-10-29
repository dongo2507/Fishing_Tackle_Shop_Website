<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- KHU VỰC TÀI KHOẢN: hiển thị theo vai trò trong session (do LoginServlet đặt):
     role = CUSTOMER | ADMIN, currentUserName = tên hiển thị. Chưa đăng nhập thì hiện Đăng nhập / Đăng ký. --%>
<div class="user-area">

    <%-- Chuông thông báo: chỉ khách đã đăng nhập (js/notification.js đọc các id bên dưới, không đổi id) --%>
    <c:if test="${sessionScope.role == 'CUSTOMER'}">
        <div class="notification-wrapper" data-url="${ctx}/notifications?format=json">
            <button type="button" class="notification-bell" id="notificationBell"
                    aria-label="Thông báo" aria-expanded="false" aria-controls="notificationDropdown">
                <span aria-hidden="true"> 🔔 </span>
                <span class="notification-badge" id="notificationBadge" hidden>0</span>
            </button>

            <div class="notification-dropdown" id="notificationDropdown">
                <div class="notification-dropdown-header">
                    <h3>Thông báo</h3>
                </div>
                <div class="notification-list" id="latestNotifications"></div>
                <a class="notification-view-all" href="${ctx}/notifications">Xem tất cả thông báo →</a>
            </div>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty sessionScope.role}">
            <c:choose>
                <c:when test="${sessionScope.role == 'CUSTOMER'}">
                    <a class="user-name user-name-link" href="${ctx}/account/profile" title="Thông tin cá nhân">
                        Xin chào, <c:out value="${sessionScope.currentUserName}"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="user-name">Xin chào, <c:out value="${sessionScope.currentUserName}"/></span>
                </c:otherwise>
            </c:choose>


            <c:if test="${sessionScope.role == 'ADMIN'}">
                <a class="btn btn-ghost btn-sm" href="${ctx}/admin/products">Quản trị</a>
            </c:if>
            <form method="post" action="${ctx}/logout" class="inline">
                <button type="submit" class="btn btn-ghost btn-sm">Đăng xuất</button>
            </form>
        </c:when>
        <c:otherwise>
            <a class="btn btn-accent btn-sm" href="${ctx}/register">Đăng ký</a>
        </c:otherwise>
    </c:choose>
</div>
