<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý sản phẩm - Fishing Tackle Shop</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/admin-products.css">
</head>
<body>

<header class="navbar">
    <a href="${pageContext.request.contextPath}/" class="logo">
        Fishing Tackle Shop
    </a>

    <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/orders">Quản lý đơn hàng</a>
        <a href="${pageContext.request.contextPath}/admin/feedbacks">Quản lý đánh giá</a>
        <a href="${pageContext.request.contextPath}/admin/products" class="active">
            Quản lý sản phẩm
        </a>
    </nav>
</header>

<main class="container">

    <div class="page-heading">
        <div>
            <h1>Quản lý sản phẩm</h1>
            <p>Quản lý danh sách thiết bị câu cá của cửa hàng.</p>
        </div>

        <a class="button button-primary"
           href="${pageContext.request.contextPath}/admin/products?action=new">
            + Thêm sản phẩm
        </a>
    </div>

    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="message ${sessionScope.flashType == 'success' ? 'success' : 'error'}">
            <c:out value="${sessionScope.flashMessage}"/>
        </div>
        <c:remove var="flashMessage" scope="session"/>
        <c:remove var="flashType" scope="session"/>
    </c:if>

    <c:if test="${not empty error}">
        <div class="message error">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <!-- Tìm kiếm và lọc sản phẩm -->
    <section class="filter-section">
        <form method="get"
              action="${pageContext.request.contextPath}/admin/products"
              class="filter-form">

            <input type="text"
                   name="q"
                   placeholder="Nhập tên sản phẩm..."
                   value="<c:out value='${param.q}'/>">

            <select name="brandId">
                <option value="">Tất cả thương hiệu</option>
                <c:forEach var="brand" items="${brands}">
                    <option value="${brand.id}"
                        ${param.brandId == brand.id.toString() ? 'selected' : ''}>
                        <c:out value="${brand.name}"/>
                    </option>
                </c:forEach>
            </select>

            <select name="status">
                <option value="">Tất cả trạng thái</option>
                <c:forEach var="status" items="${statuses}">
                    <option value="${status.name()}"
                        ${param.status == status.name() ? 'selected' : ''}>
                        <c:out value="${status.label}"/>
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="button button-primary">
                Tìm kiếm
            </button>

            <a href="${pageContext.request.contextPath}/admin/products"
               class="button button-secondary">
                Xóa lọc
            </a>
        </form>
    </section>

    <!-- Danh sách sản phẩm -->
    <section class="table-section">
        <div class="table-heading">
            <h2>Danh sách sản phẩm</h2>
        </div>

        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>Mã SP</th>
                        <th>Hình ảnh</th>
                        <th>Tên sản phẩm</th>
                        <th>Thương hiệu</th>
                        <th>Giá bán</th>
                        <th>Tồn kho</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr>
                            <td><c:out value="${product.id}"/></td>

                            <td>
                                <c:choose>
                                    <c:when test="${not empty product.thumbnailUrl}">
                                        <img class="product-image"
                                             src="${pageContext.request.contextPath}<c:out value='${product.thumbnailUrl}'/>"
                                             alt="Ảnh sản phẩm">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-image">Chưa có ảnh</div>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td class="product-name">
                                <c:out value="${product.name}"/>
                            </td>

                            <td><c:out value="${product.brand.name}"/></td>

                            <td class="price">
                                <fmt:formatNumber value="${product.price}"
                                                  type="number"
                                                  groupingUsed="true"
                                                  maxFractionDigits="0"/> đ
                            </td>

                            <td><c:out value="${product.stockQuantity}"/></td>

                            <td>
                                <span class="status">
                                    <c:out value="${product.status.label}"/>
                                </span>
                            </td>

                            <td>
                                <div class="action-buttons">
                                    <a class="button button-edit"
                                       href="${pageContext.request.contextPath}/admin/products?action=edit&id=${product.id}">
                                        Sửa
                                    </a>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/products"
                                          onsubmit="return confirm('Bạn có chắc muốn xóa sản phẩm này?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${product.id}">
                                        <button type="submit" class="button button-delete">
                                            Xóa
                                        </button>
                                    </form>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/products"
                                          class="status-form">
                                        <input type="hidden" name="action" value="status">
                                        <input type="hidden" name="id" value="${product.id}">

                                        <select name="status">
                                            <c:forEach var="status" items="${statuses}">
                                                <option value="${status.name()}"
                                                    ${product.status == status ? 'selected' : ''}>
                                                    <c:out value="${status.label}"/>
                                                </option>
                                            </c:forEach>
                                        </select>

                                        <button type="submit" class="button button-update">
                                            Cập nhật
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty products}">
                        <tr>
                            <td colspan="8" class="empty-message">
                                Không tìm thấy sản phẩm phù hợp.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </section>

</main>

</body>
</html>