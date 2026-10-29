<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Trang chủ"/>
</jsp:include>

<link rel="stylesheet" href="${ctx}/css/home.css">

<section class="hero">
    <h1>Thiết bị câu cá chính hãng</h1>
    <p>Cần câu, máy câu, dây câu, mồi câu từ các thương hiệu hàng đầu.</p>
    <form class="hero-search" method="get" action="${ctx}/products">
        <input type="text" name="q" placeholder="Bạn đang tìm gì? Ví dụ: cần câu carbon">
        <button type="submit">Tìm kiếm</button>
    </form>
</section>

<c:if test="${not empty categories}">
    <section class="section">
        <div class="section-head"><h2>Danh mục sản phẩm</h2></div>
        <div class="cat-grid">
            <c:forEach var="c" items="${categories}">
                <a class="cat-tile" href="${ctx}/products?categoryId=${c.id}"><c:out value="${c.name}"/></a>
            </c:forEach>
        </div>
    </section>
</c:if>

<c:if test="${not empty topRated}">
    <section class="section">
        <div class="section-head"><h2>Được đánh giá cao</h2></div>
        <div class="grid">
            <c:forEach var="p" items="${topRated}">
                <t:productCard product="${p}" rating="${ratings[p.id]}"/>
            </c:forEach>
        </div>
    </section>
</c:if>

<section class="section">
    <div class="section-head">
        <h2>Sản phẩm mới</h2>
        <a href="${ctx}/products">Xem tất cả &rarr;</a>
    </div>
    <c:choose>
        <c:when test="${not empty newest}">
            <div class="grid">
                <c:forEach var="p" items="${newest}">
                    <t:productCard product="${p}" rating="${ratings[p.id]}"/>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise><p class="muted">Cửa hàng đang cập nhật sản phẩm.</p></c:otherwise>
    </c:choose>
</section>

<c:if test="${not empty brands}">
    <section class="section">
        <div class="section-head"><h2>Thương hiệu</h2></div>
        <div class="pill-row">
            <c:forEach var="b" items="${brands}">
                <a class="pill" href="${ctx}/products?brandId=${b.id}"><c:out value="${b.name}"/></a>
            </c:forEach>
        </div>
    </section>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
