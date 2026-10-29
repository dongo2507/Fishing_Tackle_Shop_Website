<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- MENU CỦA KHÁCH HÀNG (trên cùng). Mỗi thành viên thêm mục của mình vào đây.
     Biến sp là đường dẫn servlet hiện tại, dùng để tô sáng mục đang xem. --%>
<a class="${sp == '/home' || sp == '/index.jsp' ? 'active' : ''}" href="${ctx}/home">Trang chủ</a>
<a class="${sp == '/products' || sp == '/product' ? 'active' : ''}" href="${ctx}/products">Sản phẩm</a>

<%-- Giỏ hàng, đơn hàng (An); số lượng giỏ lấy từ CartCountFilter --%>
<a class="${sp == '/cart' || sp == '/checkout' ? 'active' : ''}" href="${ctx}/cart">Giỏ hàng<c:if test="${sessionScope.cartCount > 0}"><span class="cart-count"> (${sessionScope.cartCount})</span></c:if></a>
<a class="${sp == '/orders' ? 'active' : ''}" href="${ctx}/orders">Đơn hàng của tôi</a>

