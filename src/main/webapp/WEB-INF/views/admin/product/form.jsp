<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${not empty product.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Sản phẩm"/>
</jsp:include>

<div class="page-head">
    <h1>${isEdit ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}</h1>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<form method="post" action="${ctx}/admin/products" class="form">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${isEdit ? product.id : ''}">

    <label for="name">Tên sản phẩm <span class="req">*</span></label>
    <input type="text" id="name" name="name" maxlength="255" required
           value="<c:out value='${product.name}'/>">

    <label for="categoryId">Danh mục <span class="req">*</span></label>
    <select id="categoryId" name="categoryId" required>
        <option value="">-- Chọn danh mục --</option>
        <c:forEach var="c" items="${categories}">
            <option value="${c.id}" ${product.category.id == c.id ? 'selected' : ''}>
                <c:out value="${c.name}"/><c:if test="${not c.visible}"> (đang ẩn)</c:if>
            </option>
        </c:forEach>
    </select>

    <label for="brandId">Thương hiệu <span class="req">*</span></label>
    <select id="brandId" name="brandId" required>
        <option value="">-- Chọn thương hiệu --</option>
        <c:forEach var="b" items="${brands}">
            <option value="${b.id}" ${product.brand.id == b.id ? 'selected' : ''}>
                <c:out value="${b.name}"/>
            </option>
        </c:forEach>
    </select>

    <label for="price">Giá (đ) <span class="req">*</span></label>
    <input type="number" id="price" name="price" min="0" step="1000" required
           value="<c:out value='${product.price}'/>">

    <label for="stockQuantity">Số lượng tồn <span class="req">*</span></label>
    <input type="number" id="stockQuantity" name="stockQuantity" min="0" step="1" required
           value="${product.stockQuantity}">

    <label for="status">Trạng thái <span class="req">*</span></label>
    <select id="status" name="status" required>
        <c:forEach var="s" items="${statuses}">
            <option value="${s}" ${product.status == s ? 'selected' : ''}>${s.label}</option>
        </c:forEach>
    </select>

    <label for="thumbnailUrl">Đường dẫn ảnh đại diện (tùy chọn)</label>
    <input type="text" id="thumbnailUrl" name="thumbnailUrl" maxlength="255"
           placeholder="https://..."
           value="<c:out value='${product.thumbnailUrl}'/>">

    <label for="description">Mô tả</label>
    <textarea id="description" name="description" rows="6" maxlength="5000"><c:out value="${product.description}"/></textarea>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a class="btn" href="${ctx}/admin/products">Hủy</a>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
