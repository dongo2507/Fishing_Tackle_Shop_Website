<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý đánh giá"/>
</jsp:include>

<div class="page-head">
    <h1>Đánh giá của khách hàng</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<form method="get" action="${ctx}/admin/feedbacks" class="filter-bar">
    <select name="productId">
        <option value="">Tất cả sản phẩm</option>
        <c:forEach var="p" items="${products}">
            <option value="${p.id}" ${param.productId == p.id ? 'selected' : ''}>
                <c:out value="${p.name}"/>
            </option>
        </c:forEach>
    </select>
    <label class="inline-check">
        <input type="checkbox" name="unanswered" value="true" ${param.unanswered == 'true' ? 'checked' : ''}>
        Chỉ hiện đánh giá chưa trả lời
    </label>
    <button type="submit" class="btn btn-primary">Lọc</button>
    <a class="btn" href="${ctx}/admin/feedbacks">Xóa lọc</a>
</form>

<c:forEach var="f" items="${feedbacks}">
    <div class="fb-card">
        <div class="feedback-head">
            <span class="stars">
                <c:forEach begin="1" end="5" var="i">
                    <span class="star ${i <= f.rating ? 'on' : ''}">★</span>
                </c:forEach>
            </span>
            <strong>Khách hàng #${f.customerId}</strong>
            <span>${f.createdAt.toLocalDate()}</span>
            <span>·</span>
            <a href="${ctx}/admin/products?action=edit&id=${f.product.id}">
                <c:out value="${f.product.name}"/>
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty f.comment}">
                <p class="feedback-text"><c:out value="${f.comment}"/></p>
            </c:when>
            <c:otherwise><p class="muted">(Chỉ đánh giá sao, không có bình luận)</p></c:otherwise>
        </c:choose>

        <c:forEach var="r" items="${f.subFeedbacks}">
            <div class="reply">
                <div class="reply-label">Cửa hàng trả lời · ${r.createdAt.toLocalDate()}</div>
                <div class="feedback-text"><c:out value="${r.content}"/></div>
                <form method="post" action="${ctx}/admin/feedbacks" class="inline"
                      data-confirm="Xóa câu trả lời này?">
                    <input type="hidden" name="action" value="deleteReply">
                    <input type="hidden" name="subId" value="${r.id}">
                    <input type="hidden" name="filterProductId" value="<c:out value='${param.productId}'/>">
                    <input type="hidden" name="filterUnanswered" value="<c:out value='${param.unanswered}'/>">
                    <button type="submit" class="btn btn-danger btn-small">Xóa trả lời</button>
                </form>
            </div>
        </c:forEach>

        <form method="post" action="${ctx}/admin/feedbacks" class="reply-form">
            <input type="hidden" name="action" value="reply">
            <input type="hidden" name="feedbackId" value="${f.id}">
            <input type="hidden" name="filterProductId" value="<c:out value='${param.productId}'/>">
            <input type="hidden" name="filterUnanswered" value="<c:out value='${param.unanswered}'/>">
            <textarea name="content" rows="2" maxlength="2000" required
                      placeholder="Viết câu trả lời cho khách..."></textarea>
            <button type="submit" class="btn btn-primary">Trả lời</button>
        </form>

        <form method="post" action="${ctx}/admin/feedbacks" class="inline"
              data-confirm="Xóa đánh giá này (kèm các câu trả lời)?">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="feedbackId" value="${f.id}">
            <input type="hidden" name="filterProductId" value="<c:out value='${param.productId}'/>">
            <input type="hidden" name="filterUnanswered" value="<c:out value='${param.unanswered}'/>">
            <button type="submit" class="btn btn-danger btn-small">Xóa đánh giá</button>
        </form>
    </div>
</c:forEach>

<c:if test="${empty feedbacks}">
    <p class="muted">Không có đánh giá nào.</p>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
