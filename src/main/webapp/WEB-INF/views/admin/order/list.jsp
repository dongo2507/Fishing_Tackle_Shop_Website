<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý đơn hàng"/>
</jsp:include>

<div class="page-head">
    <h1>Đơn hàng</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<%-- Tab lọc theo trạng thái, kèm số lượng đơn --%>
<c:set var="allCount" value="0"/>
<c:forEach var="s" items="${statuses}"><c:set var="allCount" value="${allCount + counts[s]}"/></c:forEach>
<nav class="tabs">
    <a class="${empty param.status ? 'active' : ''}" href="${ctx}/admin/orders">Tất cả (${allCount})</a>
    <c:forEach var="s" items="${statuses}">
        <a class="${param.status == s.name() ? 'active' : ''}" href="${ctx}/admin/orders?status=${s}">${s.label} (${counts[s]})</a>
    </c:forEach>
</nav>

<form method="get" action="${ctx}/admin/orders" class="filter-bar">
    <c:if test="${not empty param.status}"><input type="hidden" name="status" value="<c:out value='${param.status}'/>"></c:if>
    <input type="text" name="q" placeholder="Tìm theo mã đơn (DH000012), số điện thoại hoặc tên người nhận..."
           value="<c:out value='${param.q}'/>">
    <button type="submit" class="btn btn-primary">Tìm</button>
    <a class="btn" href="${ctx}/admin/orders">Xóa lọc</a>
</form>

<table class="table">
    <thead>
    <tr>
        <th>Mã đơn</th>
        <th>Ngày đặt</th>
        <th>Người nhận</th>
        <th class="num">Số SP</th>
        <th class="num">Tổng tiền</th>
        <th>Trạng thái</th>
        <th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="o" items="${orders}">
        <tr>
            <td><a href="${ctx}/admin/orders?id=${o.id}"><strong>${o.code}</strong></a></td>
            <td>${o.orderDateText}</td>
            <td><c:out value="${o.receiverName}"/><br><span class="muted"><c:out value="${o.receiverPhone}"/></span></td>
            <td class="num">${o.totalQuantity}</td>
            <td class="num">
                <fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
            </td>
            <td><span class="status status-${o.status}">${o.status.label}</span></td>
            <td class="actions">
                <a class="btn" href="${ctx}/admin/orders?id=${o.id}">Chi tiết</a>
                <c:if test="${not empty o.status.next()}">
                    <form method="post" action="${ctx}/admin/orders" class="inline">
                        <input type="hidden" name="action" value="next">
                        <input type="hidden" name="id" value="${o.id}">
                        <input type="hidden" name="filterStatus" value="<c:out value='${param.status}'/>">
                        <button type="submit" class="btn btn-primary">→ ${o.status.next().label}</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty orders}">
        <tr><td colspan="7" class="empty">Không có đơn hàng nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
