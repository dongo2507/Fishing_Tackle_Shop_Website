<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- Bảng sản phẩm + tổng tiền của 1 đơn hàng. Dùng chung cho trang khách và trang Admin.
     Cần request attribute "order". --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<table class="table">
    <thead>
    <tr>
        <th>Sản phẩm</th>
        <th class="num">Đơn giá</th>
        <th class="num">Số lượng</th>
        <th class="num">Thành tiền</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="li" items="${order.items}">
        <tr>
            <td>
                <div class="cart-product">
                    <c:choose>
                        <c:when test="${not empty li.thumbnailUrl}">
                            <img class="thumb" src="${ctx}<c:out value='${li.thumbnailUrl}'/>" alt="">
                        </c:when>
                        <c:otherwise><span class="thumb thumb-empty"></span></c:otherwise>
                    </c:choose>
                    <a href="${ctx}/product?id=${li.product.id}"><c:out value="${li.productName}"/></a>
                </div>
            </td>
            <td class="num"><fmt:formatNumber value="${li.unitPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td>
            <td class="num">${li.quantity}</td>
            <td class="num"><fmt:formatNumber value="${li.subtotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td>
        </tr>
    </c:forEach>
    </tbody>
    <tfoot>
    <tr><td colspan="3" class="num">Tạm tính</td>
        <td class="num"><fmt:formatNumber value="${order.subtotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td></tr>
    <tr><td colspan="3" class="num">Giảm giá</td>
        <td class="num">− <fmt:formatNumber value="${order.discountAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td></tr>
    <tr><td colspan="3" class="num">Phí vận chuyển</td>
        <td class="num"><fmt:formatNumber value="${order.shippingFee}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td></tr>
    <tr class="total-row"><td colspan="3" class="num">Tổng cộng</td>
        <td class="num"><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td></tr>
    </tfoot>
</table>
