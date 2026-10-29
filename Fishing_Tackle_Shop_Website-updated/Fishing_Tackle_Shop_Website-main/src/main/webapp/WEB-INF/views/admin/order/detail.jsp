<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Đơn hàng ${order.code}"/>
</jsp:include>

<div class="page-head">
    <h1>Đơn hàng ${order.code}</h1>
    <a class="btn" href="${ctx}/admin/orders">← Danh sách đơn hàng</a>
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
    <p class="muted">Mã khách hàng: #${order.customerId}</p>
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
                <c:if test="${not empty payment.transactionRef}">
                    <p>Mã giao dịch: <strong><c:out value="${payment.transactionRef}"/></strong></p>
                </c:if>

                <c:if test="${payment.method == 'BANK_TRANSFER' && payment.status == 'PENDING' && order.status != 'CANCELLED'}">
                    <form method="post" action="${ctx}/admin/payments" class="cancel-form"
                          data-confirm="Xác nhận đã nhận đủ tiền cho đơn ${order.code}?">
                        <input type="hidden" name="action" value="confirm">
                        <input type="hidden" name="orderId" value="${order.id}">
                        <input type="text" name="transactionRef" maxlength="50" required
                               placeholder="Mã giao dịch ngân hàng">
                        <button type="submit" class="btn btn-primary">Xác nhận đã nhận tiền</button>
                    </form>
                    <p class="muted">Khách chuyển khoản với nội dung <strong>${order.code}</strong>.</p>
                </c:if>
                <c:if test="${payment.method == 'COD' && payment.status == 'PENDING'}">
                    <p class="muted">Tự chuyển sang "Đã thanh toán" khi đơn giao thành công.</p>
                </c:if>
                <c:if test="${payment.status == 'REFUNDED'}">
                    <p class="warn-text">Đơn đã hủy sau khi khách thanh toán – cần hoàn tiền cho khách.</p>
                </c:if>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="panel panel-wide">
        <h2>Vận chuyển</h2>
        <c:choose>
            <c:when test="${empty shipping}"><p class="muted">Chưa có thông tin vận chuyển.</p></c:when>
            <c:otherwise>
                <p><span class="ship-status ship-${shipping.status}">${shipping.status.label}</span>
                    <span class="muted"> · Phí: <fmt:formatNumber value="${shipping.shippingFee}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</span></p>
                <c:if test="${not empty shipping.shippedDate}"><p class="muted">Giao cho ĐVVC: ${shipping.shippedDateText}</p></c:if>
                <c:if test="${not empty shipping.deliveredDate}"><p class="muted">Giao thành công: ${shipping.deliveredDateText}</p></c:if>

                <c:choose>
                    <c:when test="${shipping.editable}">
                        <form method="post" action="${ctx}/admin/shippings" class="form ship-form">
                            <input type="hidden" name="action" value="save">
                            <input type="hidden" name="orderId" value="${order.id}">

                            <label for="carrierName">Đơn vị vận chuyển</label>
                            <select id="carrierName" name="carrierName">
                                <option value="">-- Chọn --</option>
                                <c:forEach var="cr" items="${carriers}">
                                    <option value="<c:out value='${cr}'/>" ${shipping.carrierName == cr ? 'selected' : ''}><c:out value="${cr}"/></option>
                                </c:forEach>
                            </select>

                            <label for="trackingNumber">Mã vận đơn</label>
                            <input type="text" id="trackingNumber" name="trackingNumber" maxlength="30"
                                   placeholder="VD: GHN12345678" value="<c:out value='${shipping.trackingNumber}'/>">

                            <label for="expectedDate">Ngày giao dự kiến</label>
                            <input type="date" id="expectedDate" name="expectedDate" required
                                   value="${shipping.expectedDeliveryDate}">

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Lưu vận chuyển</button>
                            </div>
                        </form>
                        <c:if test="${!shipping.readyToShip}">
                            <p class="muted">Cần nhập đơn vị vận chuyển và mã vận đơn trước khi chuyển đơn sang "Đang giao".</p>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <p>Đơn vị vận chuyển: <strong><c:out value="${shipping.carrierName}" default="—"/></strong></p>
                        <p>Mã vận đơn: <strong><c:out value="${shipping.trackingNumber}" default="—"/></strong></p>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<section class="panel panel-wide">
    <h2>Sản phẩm</h2>
    <jsp:include page="/WEB-INF/views/common/order-items.jsp"/>
</section>

<c:if test="${!order.status.isFinal()}">
    <section class="panel panel-wide">
        <h2>Xử lý đơn</h2>
        <div class="admin-actions">
            <c:if test="${not empty order.status.next()}">
                <form method="post" action="${ctx}/admin/orders" class="inline">
                    <input type="hidden" name="action" value="next">
                    <input type="hidden" name="id" value="${order.id}">
                    <input type="hidden" name="back" value="detail">
                    <button type="submit" class="btn btn-primary">Chuyển sang "${order.status.next().label}"</button>
                </form>
            </c:if>

            <c:if test="${order.status.cancellable}">
                <form method="post" action="${ctx}/admin/orders" class="cancel-form"
                      data-confirm="Hủy đơn ${order.code}? Số lượng sản phẩm sẽ được hoàn về kho.">
                    <input type="hidden" name="action" value="cancel">
                    <input type="hidden" name="id" value="${order.id}">
                    <input type="hidden" name="back" value="detail">
                    <input type="text" name="reason" maxlength="200" placeholder="Lý do hủy (không bắt buộc)">
                    <button type="submit" class="btn btn-danger">Hủy đơn &amp; hoàn kho</button>
                </form>
            </c:if>
        </div>
    </section>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
