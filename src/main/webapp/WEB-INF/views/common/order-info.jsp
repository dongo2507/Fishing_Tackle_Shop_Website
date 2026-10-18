<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Thanh tiến trình + thông tin người nhận của 1 đơn. Dùng chung cho khách và Admin. --%>
<c:choose>
    <c:when test="${order.status == 'CANCELLED'}">
        <div class="alert alert-error">
            Đơn hàng đã bị hủy lúc ${order.cancelledAtText}.
            <c:if test="${not empty order.cancelReason}">Lý do: <c:out value="${order.cancelReason}"/></c:if>
        </div>
    </c:when>
    <c:otherwise>
        <%-- 4 bước: Chờ xác nhận -> Đã xác nhận -> Đang giao -> Giao thành công --%>
        <ol class="progress">
            <c:forEach var="s" items="${['PENDING','CONFIRMED','SHIPPING','SUCCESS']}" varStatus="loop">
                <li class="${loop.index <= order.status.ordinal() ? 'done' : ''} ${loop.index == order.status.ordinal() ? 'current' : ''}">
                    <span class="dot">${loop.index + 1}</span>
                    <span class="step-label">
                        <c:choose>
                            <c:when test="${s == 'PENDING'}">Chờ xác nhận</c:when>
                            <c:when test="${s == 'CONFIRMED'}">Đã xác nhận</c:when>
                            <c:when test="${s == 'SHIPPING'}">Đang giao</c:when>
                            <c:otherwise>Giao thành công</c:otherwise>
                        </c:choose>
                    </span>
                </li>
            </c:forEach>
        </ol>
    </c:otherwise>
</c:choose>

<div class="info-grid">
    <div><span class="muted">Mã đơn</span><strong>${order.code}</strong></div>
    <div><span class="muted">Ngày đặt</span>${order.orderDateText}</div>
    <div><span class="muted">Trạng thái</span><span class="status status-${order.status}">${order.status.label}</span></div>
    <div><span class="muted">Người nhận</span><c:out value="${order.receiverName}"/></div>
    <div><span class="muted">Số điện thoại</span><c:out value="${order.receiverPhone}"/></div>
    <div class="wide"><span class="muted">Địa chỉ giao hàng</span><c:out value="${order.shippingAddress}"/></div>
    <c:if test="${not empty order.note}">
        <div class="wide"><span class="muted">Ghi chú</span><c:out value="${order.note}"/></div>
    </c:if>
</div>
