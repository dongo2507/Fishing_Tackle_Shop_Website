    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
    
    
    
    <c:set var="ctx" value="${pageContext.request.contextPath}"/>
    <jsp:include page="WEB-INF/views/common/header.jsp">
            <jsp:param name="title" value="Quản lí thanh toán"/>
        </jsp:include>
            <div class="page-head"
                 <h1> Thanh toán </h1>
                <div>
    
    <c:if test="${not empty sessionScope.flashMessage}">
        <div class=""alert alert-${sessionScope.flashType}">
            <c:out value="${sessionScope.flashMessage}"/>
            </div> 
            <c:remove var="flashMessage" scope="session"/>
            <c:remove var="flashType" scope="session"/>
    </c:if>
                    
                    
                    <form method="get" action="${ctx}/admin/payments" class=""fillter-bar"> <!--method="get" lọc chỉ đọc dữ liệu nên dùng get --!>
                        <select name="method">
                            <option value="">Tất cả phương thức</option>
                            <c:forEach var="m" items="${method}">
                                <option value="${m}" ${param.method ==m.name() ? 'selected' : ''}>${m.label}</option>
                            </c:forEach>
                        </select>
                        <button type="submit" class="btn-primary">Lọc</button>
                        <a class="btn" href="${ctx}/admin/payments">Xóa lọc </a> 
                    </form>