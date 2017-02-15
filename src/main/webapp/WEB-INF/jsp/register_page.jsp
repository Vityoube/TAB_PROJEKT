<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        <br>
        Imię: <input type="text" name="firstName"/>
        <br>
        <br>
        Nazwisko: <input type="text" name="lastName"/>
        <br>
        <br>
        Email: <input type="text" name="email"/>
        <br>
        <br>
        Telefon: <input type="text" name="phone"/>
        <br>
        <br>
        Adres:&nbsp;<input type="text" name="address">
        <br>
        <br>
        <input type="submit" value="powrót" formaction=""/>
        <input type="submit" value="rejestruj" formaction="register" formmethod="post"/>

        <c:choose>
            <c:when test="${!empty registerError}">
                <p style="color: red">${registerError}</p>
            </c:when>
            <c:when test="${!empty generatedPassword}">
                <p style="color: greenyellow">Rejestracja pomyślna</p>
                <br>
                <br>
                <p>Proszę wejść na stronę używając hasła: <b>${generatedPassword}</b></p>
            </c:when>
        </c:choose>
    </form>
</body>
</html>
