<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>服务管理</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/putGetTotal?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">服务管理列表</a></li>
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/addPutAuth?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">新增上传权限</a></li>
    <li class="active"><a href="${ctx}/serviceinfo/ftServiceInfo/addGetAuth?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">新增下载权限</a></li>
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/list">返回上一页</a></li>
</ul>
<sys:message content="${message}"/>
<form:form id="inputForm" modelAttribute="getAuthEntity" action="${ctx}/serviceinfo/ftServiceInfo/saveGetAuth" method="post" class="form-horizontal">
    <%--<form:hidden path="id"/>--%>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">交易码：</label>
        <div class="controls">
            <form:input path="trancode" htmlEscape="false" maxlength="50" class="input-xlarge " required="true" style="WIDTH:250PX" readonly="true"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">用户名：</label>
        <div class="controls">
            <%--<form:input path="userName" htmlEscape="false" maxlength="50" class="input-xlarge " required="true" style="WIDTH:250PX"/>--%>
            <form:select path="userName" class="input-xlarge" style="WIDTH:265PX">
                <form:options items="${ftUserList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

</body>
</html>