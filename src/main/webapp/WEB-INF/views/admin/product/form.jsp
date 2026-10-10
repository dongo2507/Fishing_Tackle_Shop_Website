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

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

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

    <label for="description">Mô tả</label>
    <textarea id="description" name="description" rows="6" maxlength="5000"><c:out value="${product.description}"/></textarea>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a class="btn" href="${ctx}/admin/products">Hủy</a>
    </div>
</form>

<c:choose>
    <c:when test="${isEdit}">

        <%-- ===== Ảnh đại diện ===== --%>
        <section class="panel">
            <h2>Ảnh đại diện</h2>
            <c:choose>
                <c:when test="${not empty product.thumbnailUrl}">
                    <img class="thumb-large" src="${ctx}<c:out value='${product.thumbnailUrl}'/>" alt="Ảnh đại diện">
                </c:when>
                <c:otherwise>
                    <p class="muted">Chưa có ảnh đại diện.</p>
                </c:otherwise>
            </c:choose>

            <form method="post" enctype="multipart/form-data" class="upload"
                  action="${ctx}/admin/product-images?action=uploadThumbnail&amp;productId=${product.id}">
                <input type="file" name="thumbnail" required
                       accept="image/jpeg,image/png,image/gif,image/webp"
                       data-preview="thumbPreview">
                <button type="submit" class="btn btn-primary">Tải lên</button>
            </form>
            <div id="thumbPreview" class="preview"></div>

            <c:if test="${not empty product.thumbnailUrl}">
                <form method="post" class="upload" data-confirm="Xóa ảnh đại diện?"
                      action="${ctx}/admin/product-images?action=removeThumbnail&amp;productId=${product.id}">
                    <button type="submit" class="btn btn-danger">Xóa ảnh đại diện</button>
                </form>
            </c:if>
            <p class="muted">JPG, PNG, GIF hoặc WebP, tối đa 5MB.</p>
        </section>

        <%-- ===== Album ảnh chi tiết ===== --%>
        <section class="panel">
            <h2>Album ảnh chi tiết (<c:out value="${images.size()}"/>/20)</h2>

            <c:if test="${empty images}">
                <p class="muted">Chưa có ảnh nào trong album.</p>
            </c:if>
            <div class="album">
                <c:forEach var="img" items="${images}">
                    <div class="album-item">
                        <img src="${ctx}<c:out value='${img.imageUrl}'/>" alt="">
                        <form method="post" data-confirm="Xóa ảnh này khỏi album?"
                              action="${ctx}/admin/product-images?action=deleteImage&amp;productId=${product.id}&amp;imageId=${img.id}">
                            <button type="submit" class="btn btn-danger">Xóa</button>
                        </form>
                    </div>
                </c:forEach>
            </div>

            <form method="post" enctype="multipart/form-data" class="upload"
                  action="${ctx}/admin/product-images?action=uploadAlbum&amp;productId=${product.id}">
                <input type="file" name="images" multiple required
                       accept="image/jpeg,image/png,image/gif,image/webp"
                       data-max-files="5" data-preview="albumPreview">
                <button type="submit" class="btn btn-primary">Tải lên</button>
            </form>
            <div id="albumPreview" class="preview"></div>
            <p class="muted">Chọn tối đa 5 ảnh mỗi lần, mỗi ảnh tối đa 5MB.</p>
        </section>

    </c:when>
    <c:otherwise>
        <p class="muted">Hãy lưu sản phẩm trước, sau đó bạn có thể thêm ảnh đại diện và album ảnh.</p>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
