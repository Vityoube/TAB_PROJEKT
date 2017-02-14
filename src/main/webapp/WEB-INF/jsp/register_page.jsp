<%--
  Created by IntelliJ IDEA.
  User: vkalashnykov
  Date: 14.02.17
  Time: 13:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rejestracja</title>
</head>
<body style="text-align: center">
    <h1>Rejestracja</h1>
    <form method="post">
        Login: <input type="text" name="username"/>
        <br>
        Imię: <input type="text" name="firstName"/>
        <br>
        Nazwisko: <input type="text" name="lasstName"/>
        <br>
        Email: <input type="text" name="email"/>
        <br>
        Telefon: <input type="text" name="phone"/>
        <br>
        <input type="submit" value="rejestruj" formaction="login"/>
    </form>
</body>
</html>
