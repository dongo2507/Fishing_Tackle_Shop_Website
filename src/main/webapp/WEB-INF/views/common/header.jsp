<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.title}"/> - Fishing Shop</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
    <script src="${ctx}/js/main.js" defer></script>
</head>
<body>
<header class="topbar">
    <span class="brand">Fishing Tackle Shop - Trang Quản trị</span>
    <nav>
        <a href="${ctx}/admin/products">Sản phẩm</a>
        <a href="${ctx}/admin/categories">Danh mục</a>
        <a href="${ctx}/admin/brands">Thương hiệu</a>
    </nav>
</header>
<main class="container">
