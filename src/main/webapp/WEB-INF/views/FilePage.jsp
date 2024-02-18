<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>파일 다운로드</title>
</head>
<body>
<h3>/Share${directory}</h3>
<h5>Logged in as: ${userName}</h5>
<p>파일 탐색/다운로드</p>
<hr>
<table>
    <tr>
        <td>Type</td>
        <td>Name</td>
        <td>Size</td>
    </tr>
    <c:forEach var="file" items="${fileList}">
        <tr>
            <c:choose>
                <c:when test="${file.dir}">
                    <td>Dir</td>
                    <td><a href="/share/files?dir=${directory}/${file.name}">${file.name}</a></td>
                    <td>${file.size} B</td>
                </c:when>
                <c:otherwise>
                    <td>File</td>
                    <td>
                        <a href="/share/download?fileName=${pageContext.request.getParameter("dir")}/${file.name}">${file.name}</a>
                    </td>
                    <td>${file.size} B</td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
</table>
<c:if test="${fileList.size() == 0}">
    <p>빈 디렉토리</p>
</c:if>

<div id="pageControl">
    <button onclick="location.href='/share/files'">
        루트 디렉토리
    </button>
    <%
        String directory = request.getParameter("dir");
        if (directory == null) return;

        int slashIdx = directory.lastIndexOf("/");
        if (slashIdx == -1) return;
        String parent = directory.substring(0, slashIdx);

        out.println("<button onclick=\"location.href='/share/files?dir=" + parent + "'\"> 상위 디렉토리 </button>");
    %>
</div>


</body>
</html>
