<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý vận chuyển"/>
</jsp:include>

<div class="page-head">
    <h1>Vận chuyển</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<form method="get" action="${ctx}/admin/shippings" class="filter-bar">
    <input type="text" name="q" placeholder="Mã vận đơn hoặc mã đơn hàng (DH000012)..."
           value="<c:out value='${param.q}'/>">
    <select name="status">
        <option value="">Tất cả trạng thái</option>
        <c:forEach var="s" items="${statuses}">
            <option value="${s}" ${param.status == s.name() ? 'selected' : ''}>${s.label}</option>
        </c:forEach>
    </select>
    <button type="submit" class="btn btn-primary">Lọc</button>
    <a class="btn" href="${ctx}/admin/shippings">Xóa lọc</a>
</form>

<table class="table">
    <thead>
    <tr>
        <th>Mã đơn</th>
        <th>Người nhận</th>
        <th>Đơn vị vận chuyển</th>
        <th>Mã vận đơn</th>
        <th class="num">Phí ship</th>
        <th>Trạng thái</th>
        <th>Dự kiến / Thực tế</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="s" items="${shippings}">
        <tr>
            <td><a href="${ctx}/admin/orders?id=${s.order.id}"><strong>${s.order.code}</strong></a></td>
            <td><c:out value="${s.order.receiverName}"/><br>
                <span class="muted"><c:out value="${s.shippingAddress}"/></span></td>
            <td><c:out value="${s.carrierName}" default="—"/></td>
            <td><c:out value="${s.trackingNumber}" default="—"/></td>
            <td class="num"><fmt:formatNumber value="${s.shippingFee}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td>
            <td><span class="ship-status ship-${s.status}">${s.status.label}</span></td>
            <td>
                <c:choose>
                    <c:when test="${not empty s.deliveredDate}">Đã giao ${s.deliveredDateText}</c:when>
                    <c:when test="${s.status == 'CANCELLED'}">—</c:when>
                    <c:otherwise>Dự kiến ${s.expectedDeliveryDateText}</c:otherwise>
                </c:choose>
            </td>
            <td class="actions"><a class="btn" href="${ctx}/admin/orders?id=${s.order.id}">Cập nhật</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty shippings}">
        <tr><td colspan="8" class="empty">Không có vận đơn nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
