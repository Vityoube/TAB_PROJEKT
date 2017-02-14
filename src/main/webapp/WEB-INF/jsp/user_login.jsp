<%--
  Created by IntelliJ IDEA.
  User: vkalashnykov
  Date: 12.02.17
  Time: 10:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Logowanie</title>
</head>
<body>
    <form method="post">
        Użytkownik : <input type="text" name="username" style=""/>
        <br/>
        <br/>
        Hasło: <input type="password" name="password" style="margin-left:5em"/>
        <br/>
        <input type="submit" value="login" style="margin-left:10em" name="zajestruj"/>
        <p style="color: red" style="margin-left:10em">${error}</p>
    </form>
</body>
</html>
