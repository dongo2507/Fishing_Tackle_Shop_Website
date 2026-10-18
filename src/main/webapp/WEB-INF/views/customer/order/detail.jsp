<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Đơn hàng ${order.code}"/>
</jsp:include>

<div class="page-head">
    <h1>Đơn hàng ${order.code}</h1>
    <a class="btn" href="${ctx}/orders">← Danh sách đơn hàng</a>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<section class="panel panel-wide">
    <jsp:include page="/WEB-INF/views/common/order-info.jsp"/>
</section>

<%-- Thanh toán & Vận chuyển (phần của Lộc) --%>
<div class="two-col">
    <section class="panel panel-wide">
        <h2>Thanh toán</h2>
        <c:choose>
            <c:when test="${empty payment}"><p class="muted">Chưa có thông tin thanh toán.</p></c:when>
            <c:otherwise>
                <p>${payment.method.label}</p>
                <p><span class="pay-status pay-${payment.status}">${payment.status.label}</span>
                    <c:if test="${not empty payment.paymentDate}"><span class="muted"> lúc ${payment.paymentDateText}</span></c:if></p>
                <p>Số tiền: <strong><fmt:formatNumber value="${payment.amount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</strong></p>

                <c:if test="${payment.method == 'BANK_TRANSFER' && payment.status == 'PENDING' && order.status != 'CANCELLED'}">
                    <div class="bank-box">
                        <p><strong>Vui lòng chuyển khoản theo thông tin sau:</strong></p>
                        <p>Ngân hàng: <strong><c:out value="${bankName}"/></strong></p>
                        <p>Số tài khoản: <strong><c:out value="${bankAccountNo}"/></strong></p>
                        <p>Chủ tài khoản: <strong><c:out value="${bankAccountName}"/></strong></p>
                        <p>Số tiền: <strong><fmt:formatNumber value="${payment.amount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</strong></p>
                        <p>Nội dung: <strong>${order.code}</strong></p>
                        <p class="muted">Đơn sẽ được giao sau khi cửa hàng xác nhận đã nhận tiền.</p>
                    </div>
                </c:if>
                <c:if test="${payment.status == 'REFUNDED'}">
                    <p class="muted">Cửa hàng sẽ liên hệ để hoàn tiền cho bạn.</p>
                </c:if>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="panel panel-wide">
        <h2>Vận chuyển</h2>
        <c:choose>
            <c:when test="${empty shipping}"><p class="muted">Chưa có thông tin vận chuyển.</p></c:when>
            <c:otherwise>
                <p><span class="ship-status ship-${shipping.status}">${shipping.status.label}</span></p>
                <p>Đơn vị vận chuyển: <strong><c:out value="${shipping.carrierName}" default="Đang cập nhật"/></strong></p>
                <p>Mã vận đơn: <strong><c:out value="${shipping.trackingNumber}" default="Đang cập nhật"/></strong></p>
                <c:choose>
                    <c:when test="${not empty shipping.deliveredDate}">
                        <p>Đã giao lúc: <strong>${shipping.deliveredDateText}</strong></p>
                    </c:when>
                    <c:when test="${shipping.status != 'CANCELLED' && not empty shipping.expectedDeliveryDate}">
                        <p>Dự kiến giao: <strong>${shipping.expectedDeliveryDateText}</strong></p>
                    </c:when>
                </c:choose>
                <c:if test="${not empty shipping.shippedDate}">
                    <p class="muted">Đã giao cho đơn vị vận chuyển lúc ${shipping.shippedDateText}</p>
                </c:if>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<section class="panel panel-wide">
    <h2>Sản phẩm</h2>
    <jsp:include page="/WEB-INF/views/common/order-items.jsp"/>
</section>

<%-- Khách chỉ tự hủy được khi đơn còn "Chờ xác nhận" --%>
<c:if test="${order.status == 'PENDING'}">
    <section class="panel panel-wide">
        <h2>Hủy đơn hàng</h2>
        <form method="post" action="${ctx}/orders" class="cancel-form"
              data-confirm="Bạn chắc chắn muốn hủy đơn ${order.code}?">
            <input type="hidden" name="action" value="cancel">
            <input type="hidden" name="id" value="${order.id}">
            <input type="text" name="reason" maxlength="200" placeholder="Lý do hủy (không bắt buộc)">
            <button type="submit" class="btn btn-danger">Hủy đơn</button>
        </form>
    </section>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
