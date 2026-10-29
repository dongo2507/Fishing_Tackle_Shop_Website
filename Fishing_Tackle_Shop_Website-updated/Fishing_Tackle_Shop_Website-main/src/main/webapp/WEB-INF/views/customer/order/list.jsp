<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Đơn hàng của tôi"/>
</jsp:include>

<div class="page-head">
    <h1>Đơn hàng của tôi</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<table class="table">
    <thead>
    <tr>
        <th>Mã đơn</th>
        <th>Ngày đặt</th>
        <th class="num">Số SP</th>
        <th class="num">Tổng tiền</th>
        <th>Trạng thái</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="o" items="${orders}">
        <tr>
            <td><strong>${o.code}</strong></td>
            <td>${o.orderDateText}</td>
            <td class="num">${o.totalQuantity}</td>
            <td class="num">
                <fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
            </td>
            <td><span class="status status-${o.status}">${o.status.label}</span></td>
            <td class="actions"><a class="btn" href="${ctx}/orders?id=${o.id}">Xem chi tiết</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty orders}">
        <tr><td colspan="6" class="empty">Bạn chưa có đơn hàng nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
