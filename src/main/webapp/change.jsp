<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Change Password</title>
</head>
<body>
<h2>Смена пароля для пользователя: <%= request.getAttribute("login") %></h2>
<form action="/login" method="POST">
    Login: <input type="text" readonly name="login" value="<%= request.getAttribute("login") %>"/>
    <br/>
    Password: <input type="text" name="password"/>
    <br/>
    <input type="hidden" name="action" value="change">
    <input type="submit" value="Submit"/>
</form>
</body>
</html>
