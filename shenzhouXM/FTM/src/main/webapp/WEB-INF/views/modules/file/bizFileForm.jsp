<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>文件记录</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<form:form id="inputForm" modelAttribute="bizFile" action="${ctx}/file/bizFile/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>

    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>参数名</th>
            <th>参数值</th>
        </tr>
        </thead>
        <tbody>
        <tr><td>文件平台内绝对路径</td><td>${bizFile.filePath}</td></tr>
        <tr><td>请求文件路径</td><td>${bizFile.requestFilePath}</td></tr>
        <tr><td>文件名称</td><td>${bizFile.fileName}</td></tr>
        <tr><td>文件大小</td><td>${bizFile.fileSize}</td></tr>
        <tr><td>文件后缀</td><td>${bizFile.fileExt}</td></tr>

        <tr><td>节点名称</td><td>${bizFile.nodeNameBiz}</td></tr>
        <tr><td>节点组名称</td><td>${bizFile.systemName}</td></tr>
        <tr><td>客户端用户名称</td><td>${bizFile.clientUserName}</td></tr>
        <tr><td>客户端的文件名称</td><td>${bizFile.clientFileName}</td></tr>
        <tr><td>客户端的文件路径</td><td>${bizFile.clientFilePath}</td></tr>
        <tr><td>服务器的文件路径</td><td>${bizFile.originalFilePath}</td></tr>
        <tr><td>客户端的IP</td><td>${bizFile.clientIp}</td></tr>
        <tr><td>文件创建时间</td><td><fmt:formatDate value="${bizFile.createdTime}" type="both"/></td></tr>
        <tr><td>文件修改时间</td><td><fmt:formatDate value="${bizFile.modifiedTime}" type="both"/></td></tr>
        <tr><td>上传开始时间</td><td><fmt:formatDate value="${bizFile.uploadStartTime}" type="both"/></td></tr>
        <tr><td>上传结束时间</td><td><fmt:formatDate value="${bizFile.uploadEndTime}" type="both"/></td></tr>
        <tr><td>状态</td><td>${bizFile.state}</td></tr>
        <tr><td>文件MD5</td><td>${bizFile.fileMd5}</td></tr>
        <tr><td>流水号</td><td>${bizFile.flowNo}</td></tr>
        </tbody>
    </table>

    <div class="form-actions">
            <%--<shiro:hasPermission name="sys:dict:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>--%>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>