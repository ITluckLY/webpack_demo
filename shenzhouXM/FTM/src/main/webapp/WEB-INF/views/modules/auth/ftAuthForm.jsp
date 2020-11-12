<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>目录权限管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        $(document).ready(function () {

            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                },
                rules:{
                    name:{
                        dire2Check:true
                    }
                }
            });
        });

    </script>
</head>
<body>
<%--<ul class="nav nav-tabs">
    <li><a href="${ctx}/auth/ftAuth/">目录权限管理</a></li>
    <li class="active"><a href="${ctx}/auth/ftAuth/addPage?id=${ftUser.id}&name=${ftAuth.name}">目录权限<shiro:hasPermission name="auth:ftAuth:edit">添加</shiro:hasPermission><shiro:lacksPermission name="auth:ftAuth:edit">修改</shiro:lacksPermission></a></li>
</ul><br/>--%>
<br/>
<form:form id="inputForm" modelAttribute="ftAuth" action="${ctx}/auth/ftAuth/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">目录名称：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge " id="directoryName" name="directoryName" onblur="isHaveVal(directoryName)" style="WIDTH: 250px" required="true"/>
            <span class="help-inline"><font color="red"> * </font>例如：/esb/test001</span>
        </div>
    </div>

    <%--<div class="control-group">
        <label class="control-label">系统名称：</label>
        <div class="controls">
            <form:input path="path" htmlEscape="false" maxlength="256" class="input-xlarge" readonly="true" style="WIDTH: 250px"/>
        </div>

    </div>--%>

    <div class="control-group">
        <label class="control-label">用户名：</label>
        <div class="controls">

            <select name="username" id="select1" htmlEscape="false" maxlength="50" class="input-xlarge" style="WIDTH: 265px">
               <c:forEach items="${ftUserList}" var="ftu">
                    <option>${ftu.name}</option>
               </c:forEach>
        </select>
        </div>

        <br/>
        <label class="control-label">权限：</label>
        <div class="controls">

            <select name="permessionTemp" htmlEscape="false" maxlength="20" class="input-xlarge" style="WIDTH: 265px">
                <option value="A">所有</option>
                <option value="R">只读</option>
                <option value="W">写入</option>
            </select>

        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="auth:ftAuth:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                            value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>