<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>파일 다운로드</title>
</head>
<body>
<h3>${userName}'s Home</h3>
<h5>파일 탐색/다운로드</h5>
<hr>
<table>
    <tr>
        <td>Type</td>
        <td>Name</td>
        <td>Size</td>
    </tr>
    <c:forEach var="file" items="${fileInfoList}">
        <tr>
            <c:choose>
                <c:when test="${file.dir}">
                    <td>Dir</td>
                    <td><a href="
                        <c:url value="/files/user">
                             <c:param name="path" value="${file.fullPath}"/>
                        </c:url>
                    ">${file.name}</a></td>
                    <td>${file.sizeStr}</td>
                </c:when>
                <c:otherwise>
                    <td>File</td>
                    <td>
                        <a href="
                            <c:url value="/files/user/download">
                                <c:param name="path" value="${file.fullPath}"/>
                            </c:url>
                        ">${file.name}</a>
                    </td>
                    <td>${file.sizeStr}</td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
</table>
<c:if test="${fileInfoList.size() == 0}">
    <p>빈 디렉토리</p>
</c:if>

<div id="pageControl">
    <button onclick="location.href='/files/user'">
        /
    </button>
    <%--    <%--%>
    <%--        String directory = request.getParameter("dir");--%>
    <%--        if (directory == null) return;--%>

    <%--        int slashIdx = directory.lastIndexOf("/");--%>
    <%--        if (slashIdx == -1) return;--%>
    <%--        String parent = directory.substring(0, slashIdx);--%>
    <%--        //TODO--%>
    <%--        out.println("<button onclick=\"location.href='/share/files?dir=" + parent + "'\"> 상위 디렉토리 </button>");--%>
    <%--    %>--%>
</div>

<div id="fileTransferControl">
    <form method="post" action="<c:url value="/files/user/upload?path=${nowPath}"/>" enctype="multipart/form-data">
        <div>
            파일 업로드 : <input type="file" name="payload" id="uploadInput">
        </div>
        <input type="submit">
    </form>
</div>

</body>
</html>
