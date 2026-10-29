<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Giỏ hàng"/>
</jsp:include>

<div class="page-head">
    <h1>Giỏ hàng</h1>
    <c:if test="${not empty cart.items}">
        <form method="post" action="${ctx}/cart" class="inline"
              data-confirm="Xóa toàn bộ sản phẩm trong giỏ hàng?">
            <input type="hidden" name="action" value="clear">
            <button type="submit" class="btn btn-danger">Xóa tất cả</button>
        </form>
    </c:if>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<c:choose>
    <c:when test="${empty cart.items}">
        <div class="empty-box">
            <p>Giỏ hàng của bạn đang trống.</p>
            <a class="btn btn-primary" href="${ctx}/products">Tiếp tục mua sắm</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cart-layout">
            <table class="table cart-table">
                <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th class="num">Đơn giá</th>
                    <th>Số lượng</th>
                    <th class="num">Thành tiền</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cart.items}">
                    <c:set var="p" value="${item.product}"/>
                    <tr class="${item.available ? '' : 'row-warn'}">
                        <td>
                            <div class="cart-product">
                                <c:choose>
                                    <c:when test="${not empty p.thumbnailUrl}">
                                        <img class="thumb" src="<c:out value='${p.thumbnailUrl}'/>" alt="">
                                    </c:when>
                                    <c:otherwise><span class="thumb thumb-empty"></span></c:otherwise>
                                </c:choose>
                                <div>
                                    <a href="${ctx}/product?id=${p.id}"><c:out value="${p.name}"/></a>
                                    <c:if test="${!item.available}">
                                        <div class="warn-text">
                                            <c:choose>
                                                <c:when test="${p.status != 'ACTIVE'}">${p.status.label} – vui lòng xóa khỏi giỏ</c:when>
                                                <c:otherwise>Chỉ còn ${p.stockQuantity} sản phẩm – vui lòng giảm số lượng</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </td>
                        <td class="num">
                            <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
                        </td>
                        <td>
                            <%-- 3 form nhỏ cạnh nhau: [-] [ô số lượng] [+] --%>
                            <div class="qty-box">
                                <form method="post" action="${ctx}/cart">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <input type="hidden" name="quantity" value="${item.quantity - 1}">
                                    <button type="submit" class="btn btn-qty" title="Giảm">−</button>
                                </form>
                                <form method="post" action="${ctx}/cart">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <input type="number" name="quantity" value="${item.quantity}"
                                           min="0" max="${maxQty}" class="qty-input" required
                                           onchange="this.form.submit()" aria-label="Số lượng">
                                </form>
                                <form method="post" action="${ctx}/cart">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <input type="hidden" name="quantity" value="${item.quantity + 1}">
                                    <button type="submit" class="btn btn-qty" title="Tăng"
                                        ${item.quantity >= p.stockQuantity || item.quantity >= maxQty ? 'disabled' : ''}>+</button>
                                </form>
                            </div>
                        </td>
                        <td class="num strong">
                            <fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
                        </td>
                        <td class="actions">
                            <form method="post" action="${ctx}/cart" class="inline">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productId" value="${p.id}">
                                <button type="submit" class="btn btn-danger">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <aside class="summary-box">
                <h2>Tóm tắt</h2>
                <div class="sum-row"><span>Số sản phẩm: </span><span> ${cart.totalQuantity}</span></div>
                <div class="sum-row total">
                    <span>Tạm tính</span>
                    <span><fmt:formatNumber value="${cart.total}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</span>
                </div>
                <p class="muted">Giảm giá và phí vận chuyển được tính ở bước đặt hàng.</p>
                <c:choose>
                    <c:when test="${cart.hasProblem()}">
                        <p class="warn-text">Có sản phẩm cần điều chỉnh trước khi đặt hàng.</p>
                        <button class="btn btn-primary btn-block" disabled>Tiến hành đặt hàng</button>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-primary btn-block" href="${ctx}/checkout">Tiến hành đặt hàng</a>
                    </c:otherwise>
                </c:choose>
            </aside>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
