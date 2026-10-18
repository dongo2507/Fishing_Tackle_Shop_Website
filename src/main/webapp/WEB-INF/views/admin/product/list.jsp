<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Sản phẩm"/>
</jsp:include>

<div class="page-head">
    <h1>Thiết bị câu cá</h1>
    <span class="muted">Tìm thấy ${total} sản phẩm</span>
</div>

<form method="get" action="${ctx}/products" class="filter-bar">
    <input type="text" name="q" placeholder="Tìm theo tên sản phẩm..." value="<c:out value='${q}'/>">

    <select name="categoryId" onchange="this.form.submit()">
        <option value="">Tất cả danh mục</option>
        <c:forEach var="c" items="${categories}">
            <option value="${c.id}" ${categoryId == c.id ? 'selected' : ''}><c:out value="${c.name}"/></option>
        </c:forEach>
    </select>

    <select name="brandId" onchange="this.form.submit()">
        <option value="">Tất cả thương hiệu</option>
        <c:forEach var="b" items="${brands}">
            <option value="${b.id}" ${brandId == b.id ? 'selected' : ''}><c:out value="${b.name}"/></option>
        </c:forEach>
    </select>

    <select name="sort" onchange="this.form.submit()">
        <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Mới nhất</option>
        <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Giá tăng dần</option>
        <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Giá giảm dần</option>
    </select>

    <button type="submit" class="btn btn-primary">Tìm</button>
    <a class="btn" href="${ctx}/products">Xóa lọc</a>
</form>

<c:if test="${empty products}">
    <p class="muted">Không tìm thấy sản phẩm nào phù hợp.</p>
</c:if>

<div class="grid">
    <c:forEach var="p" items="${products}">
        <c:set var="rt" value="${ratings[p.id]}"/>
        <a class="card" href="${ctx}/product?id=${p.id}">
            <div class="card-img">
                <c:choose>
                    <c:when test="${not empty p.thumbnailUrl}">
                        <img src="${ctx}<c:out value='${p.thumbnailUrl}'/>" alt="">
                    </c:when>
                    <c:otherwise><span class="muted">Chưa có ảnh</span></c:otherwise>
                </c:choose>
            </div>
            <div class="card-body">
                <div class="card-brand"><c:out value="${p.brand.name}"/></div>
                <div class="card-title"><c:out value="${p.name}"/></div>

                <div class="stars card-rating">
                    <c:if test="${not empty rt}">
                        <c:set var="rounded" value="${rt.average + 0.5}"/>
                        <c:forEach begin="1" end="5" var="i">
                            <span class="star ${i <= rounded ? 'on' : ''}">★</span>
                        </c:forEach>
                        <span class="muted">(${rt.count})</span>
                    </c:if>
                    <c:if test="${empty rt}"><span class="muted">Chưa có đánh giá</span></c:if>
                </div>

                <div class="card-price">
                    <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
                </div>
                <c:if test="${p.status == 'OUT_OF_STOCK'}">
                    <span class="badge badge-off">Hết hàng</span>
                </c:if>
            </div>
        </a>
    </c:forEach>
</div>

<c:if test="${totalPages > 1}">
    <div class="pager">
        <a class="btn ${page <= 1 ? 'disabled' : ''}"
           href="${ctx}/products?<c:out value='${queryString}'/>&page=${page - 1}">&laquo; Trước</a>
        <span>Trang ${page} / ${totalPages}</span>
        <a class="btn ${page >= totalPages ? 'disabled' : ''}"
           href="${ctx}/products?<c:out value='${queryString}'/>&page=${page + 1}">Sau &raquo;</a>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
