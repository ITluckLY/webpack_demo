<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<html>
<head>
    <title>IP控制管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        $(document).ready(function () {
            $("#inputForm").validate({

                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
//                    isIp(ipAddress);
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
                    ipAddress:{
                        ipCheck:true,
                        required:true
                    },
                    des:{
                        demoCheck:true
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/ipconfig/ftUserIp/">IP控制列表</a></li>
    <li class="active"><a href="${ctx}/ipconfig/ftUserIp/editForm?id=${ftUserIp.id}">IP控制<shiro:hasPermission
            name="ipconfig:ftUserIp:edit">${not empty ftUserIp.ipAddress?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="ipconfig:ftUserIp:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftUserIp" action="${ctx}/ipconfig/ftUserIp/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">IP地址：</label>
        <div class="controls">
            <form:input path="ipAddress" htmlEscape="false" maxlength="50" class="input-xlarge " id="ipAddress"
                        name="ipAddress" style="WIDTH: 250px" required="true"/>
            <span class="help-inline"><font color="red"> * </font>例如：192.168.1.100</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">用户名称：</label>
        <div class="controls">
            <form:select path="ftUserId" class="input-xlarge" style="WIDTH: 265px">
                <form:options items="${ftUserList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">状态：</label>
        <div class="controls">
            <form:select path="state" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 265px">
                <form:option value="allowed">允许</form:option>
                <form:option value="forbidden">禁止</form:option>
            </form:select>
        </div>
    </div>


    <div class="control-group">
        <label class="control-label">描述：</label>
        <div class="controls">
            <form:textarea path="des" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge " style="WIDTH: 350px"/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="ipconfig:ftUserIp:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                                  value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>