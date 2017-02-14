<%--
  Created by IntelliJ IDEA.
  User: Radar
  Date: 2017-02-14
  Time: 12:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body  style="text-align: center">
    <h1>Logowanie</h1>
    <p style="color: red"> ${error} </p>
    <form method="post">
        Login: <input type="text" name="username"/>
        <br>
        <br>
        Hasło: <input type="password" name="password"/>
        <br>
        <br>
        <input type="submit" value="zaloguj" formaction="login"/>
        <input type="submit" value="zarejstruj" formaction="login"/>
    </form>

</body>
</html>
