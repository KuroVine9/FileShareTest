<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: kuro9
  Date: 24. 3. 2.
  Time: 오후 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Kuro9's Home</title>
</head>
<body>
<h1>Here's my project:</h1>
<hr>
<table>
    <tr>
        <td>Name</td>
        <td>Path</td>
        <td>State</td>
    </tr>
    <tr>
        <td>온라인 파일 드라이브</td>
        <td>
            <a href="<c:url value="/files/user"/>">/files/user</a>
        </td>
        <td>작업중...</td>
    </tr>
</table>
</body>
</html>
