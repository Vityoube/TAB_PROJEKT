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
<body>
    <h1>Akcje</h1>
    <table>
        <c:forEach items="${actions}" var="action">
            <tr>
                <td>${action.name}</td>
                <td>${action.actionTime}</td>
                <td>${action.ipAdress}</td>
            </tr>
        </c:forEach>
    </table>

</body>
</html>
