<%--
  Created by IntelliJ IDEA.
  User: vkalashnykov
  Date: 14.02.17
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Zmiana Hasła</title>
</head>
<body style="text-align: center">
<h1>Zmiana hasła</h1>
<form method="post">
    Stare hasło: <input type="text" name="password"/>
    <br>
    Nowe hasło: <input type="text" name="password"/>
    <br>
    Powtórz nowe hasło: <input type="text" name="password"/>
    <br>
    <input type="submit" value="zatwierdź" formaction="login"/>
</form>
</body>
</html>
