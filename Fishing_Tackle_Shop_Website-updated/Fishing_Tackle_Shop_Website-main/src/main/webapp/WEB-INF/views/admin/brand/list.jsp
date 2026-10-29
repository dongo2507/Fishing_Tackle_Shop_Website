<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý thương hiệu"/>
</jsp:include>

<div class="page-head">
    <h1>Thương hiệu</h1>
    <a class="btn btn-primary" href="${ctx}/admin/brands?action=new">+ Thêm thương hiệu</a>
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
        <th>ID</th>
        <th>Logo</th>
        <th>Tên thương hiệu</th>
        <th>Mô tả</th>
        <th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="b" items="${brands}">
        <tr>
            <td>${b.id}</td>
            <td>
                <c:if test="${not empty b.logoUrl}">
                    <img class="logo-thumb" src="<c:out value='${b.logoUrl}'/>" alt="logo">
                </c:if>
            </td>
            <td><c:out value="${b.name}"/></td>
            <td><c:out value="${b.description}"/></td>
            <td class="actions">
                <a class="btn" href="${ctx}/admin/brands?action=edit&id=${b.id}">Sửa</a>

                <form method="post" action="${ctx}/admin/brands" class="inline"
                      data-confirm="Bạn có chắc muốn xóa thương hiệu &quot;<c:out value='${b.name}'/>&quot;?">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${b.id}">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty brands}">
        <tr><td colspan="5" class="empty">Chưa có thương hiệu nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
