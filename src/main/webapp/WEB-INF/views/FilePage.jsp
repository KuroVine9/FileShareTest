<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test</title>
</head>
<body>
    <h3>대충 H3 제목</h3>
    <p>클릭하면 아마도 다운로드 될겁니다</p>
    <hr>
    <c:forEach var="fileName" items="${fileList}">
        <p><a href="/share/files/${fileName}">${fileName}</a></p>
    </c:forEach>
</body>
</html>
