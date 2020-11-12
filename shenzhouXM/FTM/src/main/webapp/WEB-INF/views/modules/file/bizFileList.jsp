<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>文件记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        /*function checkType() {
            if (document.getElementById("clientUserName").value != "") {
                document.getElementById("nodeNameBiz").disabled = true;
                document.getElementById("clientFileName").disabled = true;
            }else if (document.getElementById("nodeNameBiz").value != "") {
                document.getElementById("clientUserName").disabled = true;
                document.getElementById("clientFileName").disabled = true;
            }else if (document.getElementById("clientFileName").value != "") {
                document.getElementById("clientUserName").disabled = true;
                document.getElementById("nodeNameBiz").disabled = true;
            }
        }*/
    </script>

</head>
<body>
    <form:form id="searchForm" modelAttribute="bizFile" action="${ctx}/file/bizFile/" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>用户名称：</label>
        <form:input path="clientUserName" htmlEscape="false" maxlength="256" class="input-medium"/>
        <label>节点名称：</label>
        <form:input path="nodeNameBiz" htmlEscape="false" maxlength="256" class="input-medium"/>
        <label>客户端文件名 ：</label><form:input path="clientFileName" htmlEscape="false" maxlength="50" class="input-medium"/>
        &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
    </form:form>

    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
        <thead>
        <tr>
            <th>文件路径</th>
            <th>客户端文件名称</th>
            <th>文件大小</th>
            <th>节点组名称</th>
            <th>用户名称</th>
            <th>节点名称</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="bizFile">
            <tr>
                <td>${bizFile.requestFilePath}</td>
                <td>${bizFile.clientFileName}</td>
                <td>${bizFile.fileSize}</td>
                <td>${bizFile.systemName}</td>
                <td>${bizFile.clientUserName}</td>
                <td>${bizFile.nodeNameBiz}</td>
                <shiro:hasPermission name="file:bizFile:view">
                    <td><a href="${ctx}/file/bizFile/form?id=${bizFile.id}">详情</a></td>
                </shiro:hasPermission>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="pagination">${page}</div>
</body>
</html>