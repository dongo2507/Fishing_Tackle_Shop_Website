<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Đặt hàng"/>
</jsp:include>

<div class="page-head">
    <h1>Đặt hàng</h1>
    <a class="btn" href="${ctx}/cart">← Quay lại giỏ hàng</a>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<div class="cart-layout">
    <form method="post" action="${ctx}/checkout" class="form checkout-form" id="checkoutForm">
        <h2>Thông tin nhận hàng</h2>

        <label for="receiverName">Họ tên người nhận <span class="req">*</span></label>
        <input type="text" id="receiverName" name="receiverName" maxlength="100" required
               value="<c:out value='${info.receiverName}'/>">

        <label for="receiverPhone">Số điện thoại <span class="req">*</span></label>
        <input type="text" id="receiverPhone" name="receiverPhone" maxlength="15" required
               inputmode="tel" placeholder="VD: 0901234567"
               value="<c:out value='${info.receiverPhone}'/>">

        <label for="shippingAddress">Địa chỉ giao hàng <span class="req">*</span></label>
        <textarea id="shippingAddress" name="shippingAddress" rows="2" maxlength="255" required
                  placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành"><c:out value="${info.shippingAddress}"/></textarea>

        <label for="note">Ghi chú</label>
        <textarea id="note" name="note" rows="2" maxlength="500"
                  placeholder="VD: Giao giờ hành chính"><c:out value="${info.note}"/></textarea>

        <h2 class="mt">Phương thức thanh toán</h2>
        <c:forEach var="m" items="${paymentMethods}">
            <label class="pay-option">
                <input type="radio" name="paymentMethod" value="${m}" required
                    ${info.paymentMethod == m || (empty info.paymentMethod && m == 'COD') ? 'checked' : ''}>
                <span>
                    <strong>${m.label}</strong>
                    <span class="muted">
                        <c:choose>
                            <c:when test="${m == 'COD'}">Trả tiền mặt cho nhân viên giao hàng.</c:when>
                            <c:otherwise>Chuyển khoản trước, thông tin tài khoản hiện sau khi đặt hàng. Đơn được giao sau khi cửa hàng nhận tiền.</c:otherwise>
                        </c:choose>
                    </span>
                </span>
            </label>
        </c:forEach>
    </form>

    <aside class="summary-box">
        <h2>Đơn hàng (${cart.totalQuantity} sản phẩm)</h2>
        <c:forEach var="item" items="${cart.items}">
            <div class="sum-item">
                <span><c:out value="${item.product.name}"/> × ${item.quantity}</span>
                <span><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</span>
            </div>
        </c:forEach>
        <div class="sum-row"><span>Tạm tính</span>
            <span><fmt:formatNumber value="${cart.total}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</span></div>
        <div class="sum-row"><span>Giảm giá</span><span>0 đ</span></div>
        <div class="sum-row"><span>Phí vận chuyển</span>
            <span><c:choose>
                <c:when test="${shippingFee.signum() == 0}">Miễn phí</c:when>
                <c:otherwise><fmt:formatNumber value="${shippingFee}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</c:otherwise>
            </c:choose></span></div>
        <c:if test="${shippingFee.signum() > 0}">
            <p class="muted">Miễn phí vận chuyển cho đơn từ
                <fmt:formatNumber value="${freeShippingThreshold}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ.</p>
        </c:if>
        <div class="sum-row total"><span>Tổng cộng</span>
            <span><fmt:formatNumber value="${grandTotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</span></div>

        <%-- Nút nằm ngoài form nên dùng thuộc tính form="checkoutForm" --%>
        <button type="submit" form="checkoutForm" class="btn btn-primary btn-block" id="placeOrderBtn">
            Đặt hàng
        </button>
    </aside>
</div>

<script>
    // Chống bấm "Đặt hàng" nhiều lần (server cũng đã khóa giỏ hàng để chặn)
    document.getElementById('checkoutForm').addEventListener('submit', function () {
        var btn = document.getElementById('placeOrderBtn');
        btn.disabled = true;
        btn.textContent = 'Đang xử lý...';
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
