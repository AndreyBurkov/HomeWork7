<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Welocome</title>
</head>
<body>
    <h1>Welcome, <%= request.getAttribute("login") %>!!!</h1>
    <a href="login.html"><=BACK</a>
</body>
</html>
