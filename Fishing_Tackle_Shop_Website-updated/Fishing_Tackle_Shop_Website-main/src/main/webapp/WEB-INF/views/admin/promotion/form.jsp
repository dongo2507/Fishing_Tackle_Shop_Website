<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${not empty promotion and not empty promotion.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Mã giảm giá"/>
</jsp:include>

<div class="page-head">
    <h1>${isEdit ? 'Sửa mã giảm giá' : 'Thêm mã giảm giá'}</h1>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<form method="post" action="${ctx}/admin/promotions" class="form">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="id" value="${isEdit ? promotion.id : ''}">

    <label for="code">Mã giảm giá <span class="req">*</span></label>
    <input type="text" id="code" name="code" maxlength="30" required style="text-transform:uppercase"
           placeholder="VD: CAUCA10" value="<c:out value='${promotion.code}'/>">

    <label for="name">Tên chương trình <span class="req">*</span></label>
    <input type="text" id="name" name="name" maxlength="100" required value="<c:out value='${promotion.name}'/>">

    <label for="discountPercent">% giảm <span class="req">*</span></label>
    <input type="number" id="discountPercent" name="discountPercent" min="0.01" max="100" step="0.01" required
           value="${empty promotion ? '10' : promotion.discountPercent}">

    <label for="maxDiscount">Giảm tối đa (đồng, để trống nếu không giới hạn)</label>
    <input type="number" id="maxDiscount" name="maxDiscount" min="1" step="1"
           value="${empty promotion.maxDiscount ? '' : promotion.maxDiscount.toBigInteger()}">

    <label for="startDate">Ngày bắt đầu <span class="req">*</span></label>
    <input type="date" id="startDate" name="startDate" required value="${promotion.startDate}">

    <label for="endDate">Ngày kết thúc <span class="req">*</span></label>
    <input type="date" id="endDate" name="endDate" required value="${promotion.endDate}">

    <label class="check">
        <input type="checkbox" name="enabled" <c:if test="${empty promotion or promotion.enabled}">checked</c:if>>
        Đang bật
    </label>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a class="btn" href="${ctx}/admin/promotions">Hủy</a>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
