<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- MENU BÊN TRÁI CỦA ADMIN. Mỗi thành viên chỉ sửa nhóm của mình.
     Cách thêm một mục:  <a class="${sp == '/admin/xxx' ? 'active' : ''}" href="${ctx}/admin/xxx">Tên mục</a>
     (biến sp là đường dẫn servlet hiện tại, dùng để tô sáng mục đang xem) --%>

<%-- ===== Catalog (Đô) ===== --%>
<div class="menu-group">Catalog</div>
<a class="${sp == '/admin/products' ? 'active' : ''}" href="${ctx}/admin/products">Sản phẩm</a>
<a class="${sp == '/admin/categories' ? 'active' : ''}" href="${ctx}/admin/categories">Danh mục</a>
<a class="${sp == '/admin/brands' ? 'active' : ''}" href="${ctx}/admin/brands">Thương hiệu</a>
<a class="${sp == '/admin/feedbacks' ? 'active' : ''}" href="${ctx}/admin/feedbacks">Đánh giá</a>

<%-- ===== Bán hàng (An, Lộc) ===== --%>
<div class="menu-group">Bán hàng</div>
<a class="${sp == '/admin/orders' ? 'active' : ''}" href="${ctx}/admin/orders">Đơn hàng</a>
<a class="${sp == '/admin/payments' ? 'active' : ''}" href="${ctx}/admin/payments">Thanh toán</a>
<a class="${sp == '/admin/shippings' ? 'active' : ''}" href="${ctx}/admin/shippings">Vận chuyển</a>
