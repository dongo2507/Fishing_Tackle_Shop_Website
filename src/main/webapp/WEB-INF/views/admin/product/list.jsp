<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Quản lý sản phẩm"/>
</jsp:include>

<div class="page-head">
    <h1>Sản phẩm</h1>
    <a class="btn btn-primary" href="${ctx}/admin/products?action=new">+ Thêm sản phẩm</a>
</div>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<form method="get" action="${ctx}/admin/products" class="filter-bar">
    <input type="text" name="q" placeholder="Tìm theo tên sản phẩm..."
           value="<c:out value='${param.q}'/>">

    <select name="brandId">
        <option value="">Tất cả thương hiệu</option>
        <c:forEach var="b" items="${brands}">
            <option value="${b.id}" ${param.brandId == b.id ? 'selected' : ''}>
                <c:out value="${b.name}"/>
            </option>
        </c:forEach>
    </select>

    <select name="status">
        <option value="">Tất cả trạng thái</option>
        <c:forEach var="s" items="${statuses}">
            <option value="${s}" ${param.status == s ? 'selected' : ''}>${s.label}</option>
        </c:forEach>
    </select>

    <button type="submit" class="btn btn-primary">Lọc</button>
    <a class="btn" href="${ctx}/admin/products">Xóa lọc</a>
</form>

<table class="table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Ảnh</th>
        <th>Tên sản phẩm</th>
        <th>Danh mục</th>
        <th>Thương hiệu</th>
        <th>Giá</th>
        <th>Tồn</th>
        <th>Trạng thái</th>
        <th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="p" items="${products}">
        <tr>
            <td>${p.id}</td>
            <td>
                <c:if test="${not empty p.thumbnailUrl}">
                    <img class="thumb" src="<c:out value='${p.thumbnailUrl}'/>" alt="">
                </c:if>
            </td>
            <td><c:out value="${p.name}"/></td>
            <td><c:out value="${p.category.name}"/></td>
            <td><c:out value="${p.brand.name}"/></td>
            <td class="num">
                <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ
            </td>
            <td class="num">${p.stockQuantity}</td>
            <td>
                <form method="post" action="${ctx}/admin/products" class="inline">
                    <input type="hidden" name="action" value="status">
                    <input type="hidden" name="id" value="${p.id}">
                    <select name="status" onchange="this.form.submit()">
                        <c:forEach var="s" items="${statuses}">
                            <option value="${s}" ${p.status == s ? 'selected' : ''}>${s.label}</option>
                        </c:forEach>
                    </select>
                </form>
            </td>
            <td class="actions">
                <a class="btn" href="${ctx}/admin/products?action=edit&id=${p.id}">Sửa</a>

                <form method="post" action="${ctx}/admin/products" class="inline"
                      data-confirm="Bạn có chắc muốn xóa sản phẩm &quot;<c:out value='${p.name}'/>&quot;?">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${p.id}">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty products}">
        <tr><td colspan="9" class="empty">Không có sản phẩm nào.</td></tr>
    </c:if>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
