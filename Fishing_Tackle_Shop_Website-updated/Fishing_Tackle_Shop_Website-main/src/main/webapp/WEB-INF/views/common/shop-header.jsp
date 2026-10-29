<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:set var="sp" value="${requestScope['jakarta.servlet.forward.servlet_path']}" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.title}"/> - Fishing Shop</title>


    <jsp:include page="/WEB-INF/views/common/assets.jsp"/>
</head>
<body>
<header class="shop-header">
    <div class="shop-header-inner">
        <a class="logo" href="${ctx}/home">🎣 <span>Fishing Shop</span></a>

        <form class="header-search" method="get" action="${ctx}/products">
            <input type="text" name="q" placeholder="Tìm cần câu, máy câu, mồi câu..." value="<c:out value='${q}'/>">
            <button type="submit">Tìm</button>
        </form>

        <button type="button" class="nav-toggle" data-toggle="shop-nav" aria-label="Mở menu" aria-expanded="false">☰</button>

        <nav class="shop-nav" id="shop-nav">
            <jsp:include page="/WEB-INF/views/common/shop-menu.jsp"/>
        </nav>

        <jsp:include page="/WEB-INF/views/common/user-menu.jsp"/>
    </div>
</header>
<main class="container">