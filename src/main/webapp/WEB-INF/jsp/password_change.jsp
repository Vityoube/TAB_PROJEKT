<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    Stare hasło: <input type="password" name="oldPassword"/>&nbsp;
    <c:if test="${!empty oldPasswordError}">
        <p style="color: red; padding-left: 1em;" >${oldPasswordError}</p>
    </c:if>
    <br>
    Nowe hasło: <input type="password" name="newPassword"/>&nbsp;
    <c:if test="${!empty newPasswordError}">
        <p style="color: red;padding-left: 1em;">${newPasswordError}</p>
    </c:if>
    <br>
    Powtórz nowe hasło: <input type="password" name="newPasswordConfirm"/>&nbsp;
    <c:if test="${!empty passwordConfirmError}">
        <p style="color: red;padding-left: 1em;">${passwordConfirmError}</p>
    </c:if>
    <br>
    <input type="submit" value="zatwierdź" formaction="changePassword"/>
    <input type="submit" value="powróć" formaction="cancel"/>
</form>
</body>
</html>
