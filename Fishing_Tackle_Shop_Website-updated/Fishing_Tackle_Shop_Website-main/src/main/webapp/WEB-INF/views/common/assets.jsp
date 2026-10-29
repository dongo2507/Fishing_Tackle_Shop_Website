<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Mọi file CSS/JS dùng chung của cả nhóm khai báo Ở ĐÂY (cả khung Admin và khung khách đều include file này).
     Thành viên thêm CSS/JS riêng thì thêm một dòng vào đây, không sửa từng header. --%>
<link rel="stylesheet" href="${ctx}/css/style.css">
<link rel="stylesheet" href="${ctx}/css/shop.css">
<link rel="stylesheet" href="${ctx}/css/shop-header.css">
<link rel="stylesheet" href="${ctx}/css/order.css">
<link rel="stylesheet" href="${ctx}/css/payment.css">
<link rel="stylesheet" href="${ctx}/css/notification.css">

<script src="${ctx}/js/main.js" defer></script>
<script src="${ctx}/js/ui.js" defer></script>
<script src="${ctx}/js/notification.js" defer></script>
