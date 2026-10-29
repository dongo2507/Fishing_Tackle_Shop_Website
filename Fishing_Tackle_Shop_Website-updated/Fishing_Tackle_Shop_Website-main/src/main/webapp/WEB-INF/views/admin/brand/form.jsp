<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${not empty brand and not empty brand.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Thương hiệu"/>
</jsp:include>

<div class="page-head">
    <h1>${isEdit ? 'Sửa thương hiệu' : 'Thêm thương hiệu'}</h1>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<form method="post" action="${ctx}/admin/brands" class="form">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${isEdit ? brand.id : ''}">

    <label for="name">Tên thương hiệu <span class="req">*</span></label>
    <input type="text" id="name" name="name" maxlength="100" required
           value="<c:out value='${brand.name}'/>">

    <label for="logoUrl">Đường dẫn logo (tùy chọn)</label>
    <input type="text" id="logoUrl" name="logoUrl" maxlength="255"
           placeholder="https://..."
           value="<c:out value='${brand.logoUrl}'/>">

    <label for="description">Mô tả</label>
    <textarea id="description" name="description" rows="4" maxlength="500"><c:out value="${brand.description}"/></textarea>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a class="btn" href="${ctx}/admin/brands">Hủy</a>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
