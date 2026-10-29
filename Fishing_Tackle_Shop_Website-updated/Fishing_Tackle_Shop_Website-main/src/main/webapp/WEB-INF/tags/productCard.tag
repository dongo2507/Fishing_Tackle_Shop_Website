<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="product" type="com.fishing.model.Product" required="true" %>
<%@ attribute name="rating" type="com.fishing.dao.RatingSummary" required="false" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<a class="card" href="${ctx}/product?id=${product.id}">
    <div class="card-img">
        <c:choose>
            <c:when test="${not empty product.thumbnailUrl}">
                <img src="${ctx}<c:out value='${product.thumbnailUrl}'/>" alt="">
            </c:when>
            <c:otherwise><span class="muted">Chưa có ảnh</span></c:otherwise>
        </c:choose>
        <c:if test="${product.status == 'OUT_OF_STOCK'}">
            <span class="card-flag">Hết hàng</span>
        </c:if>
    </div>
    <div class="card-body">
        <div class="card-brand"><c:out value="${product.brand.name}"/></div>
        <div class="card-title"><c:out value="${product.name}"/></div>

        <div class="stars card-rating">
            <c:choose>
                <c:when test="${not empty rating}">
                    <c:set var="rounded" value="${rating.average + 0.5}"/>
                    <c:forEach begin="1" end="5" var="i">
                        <span class="star ${i <= rounded ? 'on' : ''}">★</span>
                    </c:forEach>
                    <span class="muted">(${rating.count})</span>
                </c:when>
                <c:otherwise><span class="muted">Chưa có đánh giá</span></c:otherwise>
            </c:choose>
        </div>

        <div class="card-price">
            <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
        </div>
    </div>
</a>
