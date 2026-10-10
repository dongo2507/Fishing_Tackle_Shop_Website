<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="mainSrc" value="${not empty product.thumbnailUrl ? product.thumbnailUrl : (not empty images ? images[0].imageUrl : '')}"/>
<c:set var="rounded" value="${summary.average + 0.5}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="${product.name}"/>
</jsp:include>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<section class="detail">
    <div>
        <c:choose>
            <c:when test="${not empty mainSrc}">
                <img id="mainImage" class="detail-main" src="${ctx}<c:out value='${mainSrc}'/>" alt="">
            </c:when>
            <c:otherwise>
                <div class="no-image">Chưa có ảnh</div>
            </c:otherwise>
        </c:choose>

        <div class="gallery">
            <c:if test="${not empty product.thumbnailUrl}">
                <img class="gallery-thumb" src="${ctx}<c:out value='${product.thumbnailUrl}'/>"
                     data-full="${ctx}<c:out value='${product.thumbnailUrl}'/>" alt="">
            </c:if>
            <c:forEach var="img" items="${images}">
                <img class="gallery-thumb" src="${ctx}<c:out value='${img.imageUrl}'/>"
                     data-full="${ctx}<c:out value='${img.imageUrl}'/>" alt="">
            </c:forEach>
        </div>
    </div>

    <div>
        <h1 class="detail-title"><c:out value="${product.name}"/></h1>

        <div class="rating-line stars">
            <c:forEach begin="1" end="5" var="i">
                <span class="star ${i <= rounded && summary.count > 0 ? 'on' : ''}">★</span>
            </c:forEach>
            <c:choose>
                <c:when test="${summary.count > 0}">
                    <span class="muted">
                        <fmt:formatNumber value="${summary.average}" minFractionDigits="1" maxFractionDigits="1"/>/5
                        (${summary.count} đánh giá)
                    </span>
                </c:when>
                <c:otherwise><span class="muted">Chưa có đánh giá</span></c:otherwise>
            </c:choose>
        </div>

        <div class="price">
            <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
        </div>

        <p class="meta">Thương hiệu: <strong><c:out value="${product.brand.name}"/></strong></p>
        <p class="meta">Danh mục: <strong><c:out value="${product.category.name}"/></strong></p>
        <p class="meta">Tình trạng: <strong>${product.status.label}</strong>
            <c:if test="${product.status == 'ACTIVE'}"> (còn ${product.stockQuantity} sản phẩm)</c:if>
        </p>

        <%-- Nút thêm vào giỏ hàng sẽ ghép với phần giỏ hàng của An --%>

        <h3>Mô tả</h3>
        <div class="desc"><c:out value="${product.description}" default="Chưa có mô tả."/></div>
    </div>
</section>

<section class="feedback-section" id="feedback">
    <h2>Đánh giá sản phẩm</h2>

    <div class="rating-summary">
        <c:choose>
            <c:when test="${summary.count > 0}">
                <span class="rating-number">
                    <fmt:formatNumber value="${summary.average}" minFractionDigits="1" maxFractionDigits="1"/>
                </span>
                <span class="stars big">
                    <c:forEach begin="1" end="5" var="i">
                        <span class="star ${i <= rounded ? 'on' : ''}">★</span>
                    </c:forEach>
                </span>
                <span class="muted">${summary.count} đánh giá</span>
            </c:when>
            <c:otherwise><span class="muted">Chưa có đánh giá nào. Hãy là người đầu tiên!</span></c:otherwise>
        </c:choose>
    </div>

    <form method="post" action="${ctx}/feedback" class="feedback-form">
        <input type="hidden" name="productId" value="${product.id}">

        <div class="star-input" title="Chọn số sao">
            <input type="radio" id="star5" name="rating" value="5" required><label for="star5" title="5 sao">★</label>
            <input type="radio" id="star4" name="rating" value="4"><label for="star4" title="4 sao">★</label>
            <input type="radio" id="star3" name="rating" value="3"><label for="star3" title="3 sao">★</label>
            <input type="radio" id="star2" name="rating" value="2"><label for="star2" title="2 sao">★</label>
            <input type="radio" id="star1" name="rating" value="1"><label for="star1" title="1 sao">★</label>
        </div>

        <textarea name="comment" rows="3" maxlength="2000" placeholder="Chia sẻ cảm nhận của bạn về sản phẩm..."></textarea>
        <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
    </form>

    <c:forEach var="f" items="${feedbacks}">
        <div class="feedback-item">
            <div class="feedback-head">
                <span class="stars">
                    <c:forEach begin="1" end="5" var="i">
                        <span class="star ${i <= f.rating ? 'on' : ''}">★</span>
                    </c:forEach>
                </span>
                <strong>Khách hàng #${f.customerId}</strong>
                <span>${f.createdAt.toLocalDate()}</span>
            </div>
            <c:if test="${not empty f.comment}">
                <p class="feedback-text"><c:out value="${f.comment}"/></p>
            </c:if>

            <c:forEach var="r" items="${f.subFeedbacks}">
                <div class="reply">
                    <div class="reply-label">Phản hồi từ cửa hàng · ${r.createdAt.toLocalDate()}</div>
                    <div class="feedback-text"><c:out value="${r.content}"/></div>
                </div>
            </c:forEach>
        </div>
    </c:forEach>
</section>

<script>
    // Bấm vào ảnh nhỏ thì đổi ảnh lớn
    (function () {
        var main = document.getElementById('mainImage');
        if (!main) { return; }
        document.querySelectorAll('.gallery-thumb').forEach(function (t) {
            t.addEventListener('click', function () {
                main.src = t.getAttribute('data-full');
            });
        });
    })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
