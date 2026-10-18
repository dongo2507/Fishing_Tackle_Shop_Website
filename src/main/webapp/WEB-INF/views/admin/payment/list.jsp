<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý thanh toán"/>
</jsp:include>

<div class="page-head">
    <h1>Thanh toán</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<form method="get" action="${ctx}/admin/payments" class="filter-bar">
    <select name="method">
        <option value="">Tất cả phương thức</option>
        <c:forEach var="m" items="${methods}">
            <option value="${m}" ${param.method == m.name() ? 'selected' : ''}>${m.label}</option>
        </c:forEach>
    </select>
    <select name="status">
        <option value="">Tất cả trạng thái</option>
        <c:forEach var="s" items="${statuses}">
            <option value="${s}" ${param.status == s.name() ? 'selected' : ''}>${s.label}</option>
        </c:forEach>
    </select>
    <button type="submit" class="btn btn-primary">Lọc</button>
    <a class="btn" href="${ctx}/admin/payments">Xóa lọc</a>
</form>

<table class="table">
    <thead>
    <tr>
        <th>Mã đơn</th>
        <th>Phương thức</th>
        <th class="num">Số tiền</th>
        <th>Trạng thái</th>
        <th>Ngày thanh toán</th>
        <th>Mã giao dịch</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="p" items="${payments}">
        <tr>
            <td><a href="${ctx}/admin/orders?id=${p.order.id}"><strong>${p.order.code}</strong></a></td>
            <td>${p.method.label}</td>
            <td class="num"><fmt:formatNumber value="${p.amount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td>
            <td><span class="pay-status pay-${p.status}">${p.status.label}</span></td>
            <td>${p.paymentDateText}</td>
            <td><c:out value="${p.transactionRef}"/></td>
            <td class="actions"><a class="btn" href="${ctx}/admin/orders?id=${p.order.id}">Xem đơn</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty payments}">
        <tr><td colspan="7" class="empty">Không có giao dịch nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
