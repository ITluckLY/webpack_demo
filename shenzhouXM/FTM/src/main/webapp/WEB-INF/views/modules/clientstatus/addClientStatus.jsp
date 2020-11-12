<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>数据文件</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });

    </script>
    <style>
        form{
            left:120px;
        }
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/client/clientStatus/list">客户端状态信息</a></li>
    <li class="active"><a href="#">添加</a></li>
</ul>
<form:form id="searchForm" modelAttribute="ftClientStatus" action="${ctx}/client/clientStatus/addClientStatus"
           method="post" class="breadcrumb form-horizontal">
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">客户端ID：</label>
        <div class="controls">
            <%--<input type="text" id="id" name="id" required="true"/>--%>
            <form:input path="id" htmlEscape="false" maxlength="256" class="input-medium"/>
        </div>
    </div>
    <%--<div class="control-group">--%>
    <%--<label class="control-label">客户端ID：</label>--%>
    <%--<div class="controls">--%>
    <%--<form:select path="id" class="input-xlarge" style="WIDTH:220PX">--%>
    <%--<form:options items="${idList}" htmlEscape="false"/>--%>
    <%--</form:select>--%>
    <%--</div>--%>
    <%--</div>--%>

    <div class="control-group">
        <label class="control-label">客户端名称：</label>
        <div class="controls">
            <%--<input type="text" id="name" name="name"/>--%>
            <form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">类型：</label>
        <div class="controls">
            <%--<input type="text" id="type" name="type"/>--%>
            <form:select path="type" htmlEscape="false" class="input-medium">
                <form:option value="enable" />
                <form:option value="disable" />
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">模式：</label>
        <div class="controls">
            <%--<input type="text" id="mode" name="mode"/>--%>
                <form:select path="mode" htmlEscape="false" class="input-medium">
                    <form:option value="auto" />
                    <form:option value="manual" />
                </form:select>
        </div>
    </div>
    <div class="control-group" style="display: none">
        <label class="control-label">状态：</label>
        <div class="controls">
            <%--<input type="text" id="status" name="status"/>--%>
                <form:select path="status" htmlEscape="false" class="input-medium">
                    <form:option value="stopped" />
                    <form:option value="running" />
                </form:select>
        </div>
    </div>
    <div class="form-actions">
        <%--<shiro:hasPermission name="serviceinfo:ftServiceImports:edit">--%>
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
            <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        <%--</shiro:hasPermission>--%>
    </div>
</form:form>
</body>
</html>