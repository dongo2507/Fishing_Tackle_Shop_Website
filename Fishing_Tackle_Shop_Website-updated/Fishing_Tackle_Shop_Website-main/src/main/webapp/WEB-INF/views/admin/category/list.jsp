<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý danh mục"/>
</jsp:include>

<div class="page-head">
    <h1>Danh mục thiết bị câu cá</h1>
    <a class="btn btn-primary" href="${ctx}/admin/categories?action=new">+ Thêm danh mục</a>
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
        <th>Tên danh mục</th>
        <th>Mô tả</th>
        <th>Hiển thị</th>
        <th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${categories}">
        <tr>
            <td>${c.id}</td>
            <td><c:out value="${c.name}"/></td>
            <td><c:out value="${c.description}"/></td>
            <td>
                <c:choose>
                    <c:when test="${c.visible}"><span class="badge badge-on">Đang hiện</span></c:when>
                    <c:otherwise><span class="badge badge-off">Đang ẩn</span></c:otherwise>
                </c:choose>
            </td>
            <td class="actions">
                <a class="btn" href="${ctx}/admin/categories?action=edit&id=${c.id}">Sửa</a>

                <form method="post" action="${ctx}/admin/categories" class="inline">
                    <input type="hidden" name="action" value="toggle">
                    <input type="hidden" name="id" value="${c.id}">
                    <button type="submit" class="btn">${c.visible ? 'Ẩn' : 'Hiện'}</button>
                </form>

                <form method="post" action="${ctx}/admin/categories" class="inline"
                      data-confirm="Bạn có chắc muốn xóa danh mục &quot;<c:out value='${c.name}'/>&quot;?">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${c.id}">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty categories}">
        <tr><td colspan="5" class="empty">Chưa có danh mục nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
