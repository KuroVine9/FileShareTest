<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>파일 다운로드</title>
    <script>
        async function mkdirReq(dirName, path) {
            const reqBody = {
                "dirName": dirName,
                "path": path
            }
            console.table(reqBody)
            const response = await fetch("/files/user/mkdir", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(reqBody)
            });

            switch (response.status) {
                case 200:
                    alert("OK");
                    break;
                case 400:
                    alert("not valid name");
                    break;
                case 403:
                    alert("No Permission");
                    break;
                case 409:
                    alert("Already Exist");
                    break;
                default:
                    alert("Unknown Error: Code=" + response.status)
                    break;
            }
            location.href = "/files/user?path=" + dirName;
        }

        async function uploadFile() {
            const payload = new FormData();
            const fileInput = document.getElementById("uploadInput").files;
            console.log(fileInput.length)
            payload.append("payload", fileInput[0]);

            console.table(payload)
            const response = await fetch("/files/user/upload?path=${nowPath}", {
                method: "POST",
                // headers: {
                //     "Content-Type": "multipart/form-data"
                // },
                body: payload
            });
            console.table(response)
            switch (response.status) {
                case 200:
                    alert("OK");
                    break;
                case 400:
                    alert(response.body);
                    break;
                case 403:
                    alert("No Permission");
                    break;
                case 409:
                    alert("Already Exist");
                    break;
                default:
                    alert("Unknown Error: Code=" + response.status)
                    break;
            }
            location.href = "/files/user?path=${nowPath}";

        }

        function init() {
            const mkdirForm = document.getElementById("mkdir");
            mkdirForm.addEventListener("submit", function (e) {
                e.preventDefault();
                mkdirReq(document.getElementById("newDirName").value, "${nowPath}")
            })

            const uploadForm = document.getElementById("uploadForm");
            uploadForm.addEventListener("submit", function (e) {
                e.preventDefault();
                uploadFile();
            })
        }
    </script>
</head>
<body onload="init()">
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
    <form method="post" id="uploadForm">
        <div>
            파일 업로드 : <input type="file" name="payload" id="uploadInput">
        </div>
        <input type="submit">
    </form>
    <form method="post" id="mkdir">
        <div>
            새 폴더 이름 : <input type="text" name="dirName" id="newDirName">
        </div>
        <div hidden="hidden">
            <input type="text" name="path" value="${nowPath}" hidden="hidden">
        </div>
        <input type="submit">
    </form>
</div>

</body>
</html>
