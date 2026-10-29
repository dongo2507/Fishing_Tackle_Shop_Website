<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${not empty form and not empty form.id}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Sổ địa chỉ"/>
</jsp:include>

<div class="page-head">
    <h1>Sổ địa chỉ giao hàng</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}"><c:out value="${sessionScope.flashMessage}"/></div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<table class="table">
    <thead>
    <tr><th>Người nhận</th><th>Điện thoại</th><th>Địa chỉ</th><th></th><th>Thao tác</th></tr>
    </thead>
    <tbody>
    <c:forEach var="a" items="${addresses}">
        <tr>
            <td><c:out value="${a.receiverName}"/></td>
            <td><c:out value="${a.receiverPhone}"/></td>
            <td><c:out value="${a.fullAddress}"/></td>
            <td><c:if test="${a.default}"><span class="badge badge-on">Mặc định</span></c:if></td>
            <td class="actions">
                <a class="btn" href="${ctx}/account/addresses?edit=${a.id}">Sửa</a>
                <c:if test="${!a.default}">
                    <form method="post" action="${ctx}/account/addresses" class="inline">
                        <input type="hidden" name="action" value="default">
                        <input type="hidden" name="id" value="${a.id}">
                        <button type="submit" class="btn">Đặt mặc định</button>
                    </form>
                </c:if>
                <form method="post" action="${ctx}/account/addresses" class="inline"
                      data-confirm="Bạn có chắc muốn xóa địa chỉ này?">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${a.id}">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty addresses}">
        <tr><td colspan="5" class="empty">Bạn chưa lưu địa chỉ nào.</td></tr>
    </c:if>
    </tbody>
</table>

<form method="post" action="${ctx}/account/addresses" class="form">
    <h2>${isEdit ? 'Sửa địa chỉ' : 'Thêm địa chỉ mới'}</h2>
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${isEdit ? form.id : ''}">

    <label for="receiverName">Họ tên người nhận <span class="req">*</span></label>
    <input type="text" id="receiverName" name="receiverName" maxlength="100" required
           value="<c:out value='${form.receiverName}'/>">

    <label for="receiverPhone">Số điện thoại <span class="req">*</span></label>
    <input type="text" id="receiverPhone" name="receiverPhone" maxlength="15" required inputmode="tel"
           placeholder="VD: 0901234567" value="<c:out value='${form.receiverPhone}'/>">

    <label for="street">Số nhà, đường <span class="req">*</span></label>
    <input type="text" id="street" name="street" maxlength="150" required value="<c:out value='${form.street}'/>">

    <label for="ward">Phường/Xã</label>
    <input type="text" id="ward" name="ward" maxlength="80" value="<c:out value='${form.ward}'/>">

    <label for="district">Quận/Huyện</label>
    <input type="text" id="district" name="district" maxlength="80" value="<c:out value='${form.district}'/>">

    <label for="city">Tỉnh/Thành phố <span class="req">*</span></label>
    <input type="text" id="city" name="city" maxlength="80" required value="<c:out value='${form.city}'/>">

    <label class="check">
        <input type="checkbox" name="isDefault" <c:if test="${not empty form and form.default}">checked</c:if>>
        Đặt làm địa chỉ mặc định
    </label>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <c:if test="${isEdit}"><a class="btn" href="${ctx}/account/addresses">Hủy sửa</a></c:if>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
