<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${not empty category and not empty category.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Danh mục"/>
</jsp:include>

<div class="page-head">
    <h1>${isEdit ? 'Sửa danh mục' : 'Thêm danh mục'}</h1>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<form method="post" action="${ctx}/admin/categories" class="form">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${isEdit ? category.id : ''}">

    <label for="name">Tên danh mục <span class="req">*</span></label>
    <input type="text" id="name" name="name" maxlength="100" required
           value="<c:out value='${category.name}'/>">

    <label for="description">Mô tả</label>
    <textarea id="description" name="description" rows="4" maxlength="500"><c:out value="${category.description}"/></textarea>

    <label class="check">
        <input type="checkbox" name="visible"
               <c:if test="${empty category or category.visible}">checked</c:if>>
        Hiển thị trên giao diện người dùng
    </label>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a class="btn" href="${ctx}/admin/categories">Hủy</a>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
