<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/shop-header.jsp">
    <jsp:param name="title" value="Thông tin cá nhân"/>
</jsp:include>

<div class="page-head">
    <h1>Thông tin cá nhân</h1>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}"><c:out value="${sessionScope.flashMessage}"/></div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<form method="post" action="${ctx}/account/profile" class="form">
    <label for="fullName">Họ và tên <span class="req">*</span></label>
    <input type="text" id="fullName" name="fullName" maxlength="100" required
           value="<c:out value='${customer.fullName}'/>">

    <label for="email">Email</label>
    <input type="email" id="email" value="<c:out value='${customer.email}'/>" disabled>
    <div class="hint">Email dùng để đăng nhập nên không thể thay đổi.</div>

    <label for="phone">Số điện thoại</label>
    <input type="tel" id="phone" name="phone" maxlength="15" inputmode="tel"
           placeholder="VD: 0901234567" value="<c:out value='${customer.phone}'/>">

    <div class="hint">
        Thành viên từ
        ${customer.createdAt.toLocalDate()}
    </div>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
        <a class="btn" href="${ctx}/home">Hủy</a>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
