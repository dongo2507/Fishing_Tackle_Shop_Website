 
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.fishing.model.Admin" %>
<%
    Object adminObject = session.getAttribute("admin");

    if (adminObject == null || !(adminObject instanceof Admin)) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Admin admin = (Admin) adminObject;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Demo - Fishing Tackle Shop</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f1f5f9;
            color: #1e293b;
        }
        header {
            padding: 22px 32px;
            background: #0f172a;
            color: white;
        }
        main {
            max-width: 900px;
            margin: 40px auto;
            padding: 28px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 16px #0000000d;
        }
        .tag {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            background: #dcfce7;
            color: #166534;
        }
    </style>
</head>
<body>
    <header>
        <h2>Fishing Tackle Shop — Admin Demo</h2>
    </header>
    <main>
        <span class="tag">Đã xác thực Admin</span>
        <h1>Xin chào, <%= admin.getName() %>!</h1>
        <p>Username: <%= admin.getUsername() %></p>
        <p>Đây là trang thử nghiệm phân quyền, không ảnh hưởng giao diện chính của nhóm.</p>
    </main>
</body>
</html>