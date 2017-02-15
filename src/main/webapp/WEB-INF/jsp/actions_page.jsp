<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%--
  Created by IntelliJ IDEA.
  User: vkalashnykov
  Date: 14.02.17
  Time: 03:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Akcje</title>
</head>
<body style="text-align: center">
<form >
    <h1>Akcje użytkownika: ${sessionScope.currentUser.userName}</h1>
    <input type="submit" value="zmiana hasła" formmethod="get" formaction="changePassword">
    <input type="submit" value="wyloguj" formmethod="post" formaction="logout">
    <table align="center" style="border: solid 1px">
        <tr>
            <th style="border: solid 1px">Typ akcji</th>
            <th style="border: solid 1px">Czas</th>
            <th style="border: solid 1px">Adres IP</th>
        </tr>
        <c:forEach items="${actions}" var="action">
            <tr >
                <td style="border: solid 1px">${action.name}</td>
                <td style="border: solid 1px">${action.actionTime}</td>
                <td style="border: solid 1px">${action.ipAdress}</td>
            </tr>
        </c:forEach>
    </table>
    <br>
    <input type="submit" value="czyść liste" formaction="actionsRemove" formmethod="post">
    <br>
    <br>
    <c:if test="${changePasswordSuccess || removeActionsSuccess}"><p style="color: greenyellow">Ukończono pomyślnie.</p></c:if>
</form>


</body>
</html>
