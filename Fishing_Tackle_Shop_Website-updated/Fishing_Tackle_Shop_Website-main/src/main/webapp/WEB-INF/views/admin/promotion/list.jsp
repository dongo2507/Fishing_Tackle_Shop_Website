<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Khuyến mãi"/>
</jsp:include>

<div class="page-head">
    <h1>Mã giảm giá</h1>
    <a class="btn btn-primary" href="${ctx}/admin/promotions?action=new">+ Thêm mã</a>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}"><c:out value="${sessionScope.flashMessage}"/></div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<table class="table">
    <thead>
    <tr><th>Mã</th><th>Chương trình</th><th>Giảm</th><th>Tối đa</th><th>Hiệu lực</th><th>Trạng thái</th><th>Thao tác</th></tr>
    </thead>
    <tbody>
    <c:forEach var="p" items="${promotions}">
        <tr>
            <td><strong><c:out value="${p.code}"/></strong></td>
            <td><c:out value="${p.name}"/></td>
            <td><fmt:formatNumber value="${p.discountPercent}" maxFractionDigits="2"/>%</td>
            <td>
                <c:choose>
                    <c:when test="${empty p.maxDiscount}">Không giới hạn</c:when>
                    <c:otherwise><fmt:formatNumber value="${p.maxDiscount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</c:otherwise>
                </c:choose>
            </td>
            <td>${p.startDate} → ${p.endDate}</td>
            <td>
                <c:choose>
                    <c:when test="${p.active}"><span class="badge badge-on">Đang áp dụng</span></c:when>
                    <c:when test="${!p.enabled}"><span class="badge badge-off">Đã tắt</span></c:when>
                    <c:otherwise><span class="badge badge-off">Ngoài thời gian</span></c:otherwise>
                </c:choose>
            </td>
            <td class="actions">
                <a class="btn" href="${ctx}/admin/promotions?action=edit&id=${p.id}">Sửa</a>
                <form method="post" action="${ctx}/admin/promotions" class="inline">
                    <input type="hidden" name="action" value="toggle">
                    <input type="hidden" name="id" value="${p.id}">
                    <button type="submit" class="btn">${p.enabled ? 'Tắt' : 'Bật'}</button>
                </form>
                <form method="post" action="${ctx}/admin/promotions" class="inline"
                      data-confirm="Bạn có chắc muốn xóa mã &quot;<c:out value='${p.code}'/>&quot;?">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${p.id}">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty promotions}">
        <tr><td colspan="7" class="empty">Chưa có mã giảm giá nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
