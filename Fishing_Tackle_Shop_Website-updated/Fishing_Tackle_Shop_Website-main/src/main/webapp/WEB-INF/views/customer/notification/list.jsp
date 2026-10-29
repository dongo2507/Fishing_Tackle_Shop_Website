<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Thông báo"/>
</jsp:include>

<div class="page-head">
    <h1>Thông báo của bạn</h1>
    <c:if test="${not empty notifications}">
        <form method="post" action="${ctx}/notifications" class="inline">
            <input type="hidden" name="action" value="readAll">
            <button type="submit" class="btn">Đánh dấu tất cả đã đọc</button>
        </form>
    </c:if>
</div>
<p class="muted">Cập nhật mới nhất về đơn hàng, thanh toán và vận chuyển.</p>

<section class="notifications-all-list" aria-label="Danh sách thông báo">
    <c:forEach var="n" items="${notifications}">
        <article class="notification-item ${n.read ? '' : 'is-unread'}">
            <div class="notification-icon"><c:out value="${n.type.icon}"/></div>
            <div class="notification-content">
                <div class="notification-item-heading">
                    <strong><c:out value="${n.title}"/></strong>
                    <c:if test="${!n.read}"><span class="unread-dot"></span></c:if>
                </div>
                <p><c:out value="${n.content}"/></p>
                <span class="notification-time"><c:out value="${n.timeText}"/></span>
                <c:if test="${!n.read}">
                    <form method="post" action="${ctx}/notifications" class="inline">
                        <input type="hidden" name="action" value="read">
                        <input type="hidden" name="id" value="${n.id}">
                        <button type="submit" class="btn btn-sm">Đánh dấu đã đọc</button>
                    </form>
                </c:if>
            </div>
        </article>
    </c:forEach>
    <c:if test="${empty notifications}">
        <p class="empty">Bạn chưa có thông báo nào.</p>
    </c:if>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
