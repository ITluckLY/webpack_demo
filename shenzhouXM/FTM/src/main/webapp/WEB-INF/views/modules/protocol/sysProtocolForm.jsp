<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>路由目标管理管理</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">
        window.onload=function(){
            $("#username").attr('value','');
            $("#password").attr('value','');
        };
        $(document).ready(function () {
            //$("#name").focus();
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
                rules: {
                    name: {
                        nameCheck: true,
                        required: true
                    },
                    ip: {
//                        ipCheck: true,
                        required: true
                    },
                    port: {
                        portCheck: true,
                        required: true
                    },
                    username: {
                        nameCheck: true,
                        required: true
                    },
                    password: {
                        passwordCheck: true,
                        required: true
                    },
                    repwd: {
                        equalTo: "#password"
                    }
                }
            });
        });
    </script>


</head>
<body>
<br/>
<form:form id="inputForm" modelAttribute="sysProtocol" action="${ctx}/protocol/sysProtocol/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">目标名称：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required" required="true"
                        style="WIDTH: 250px"/>
            <span class="help-inline"><font color="red"> * </font>英文字母开头+英文、数字或下划线的组合（3-20位）</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">协议名称：</label>
        <div class="controls">
            <form:select id="protocol" path="protocol" class="input-medium" style="WIDTH: 265px">
                <form:options items="${dictList}" itemLabel="value" itemValue="value" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">地址：</label>
        <div class="controls">
            <form:input path="ip" htmlEscape="false" maxlength="50" class="input-xlarge " required="true"
                        style="WIDTH: 250px"/>
            <span class="help-inline"><font color="red"> * </font>如：192.168.1.110</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">端口：</label>
        <div class="controls">
            <form:input path="port" htmlEscape="false" maxlength="5" class="input-xlarge " required="true"
                        style="WIDTH: 250px"/>
            <span class="help-inline"><font color="red"> * </font></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">用户名：</label>
        <div class="controls">
            <form:input path="username" htmlEscape="false" maxlength="20" class="input-xlarge " required="true" style="WIDTH: 250px" id="username"/>
            <span class="help-inline"><font color="red"> * </font>英文字母开头+英文、数字或下划线的组合（3-20位）</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">上传路径：</label>
        <div class="controls">
            <form:input path="uploadPath" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 250px"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">下载路径：</label>
        <div class="controls">
             <form:input path="downloadPath" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 250px"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">密码：</label>
        <div class="controls">
            <form:input path="password" htmlEscape="false" maxlength="20" class="input-xlarge " required="true"
                        style="WIDTH: 250px" type="password" id="password"/>
            <span class="help-inline"><font color="red"> * </font>5-20位</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">确认密码：</label>
        <div class="controls">
            <input id="repwd" name="repwd" style="WIDTH: 250px" type="password" required="true" maxlength="20"/>
            <span class="help-inline"><font color="red"> * </font></span>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="protocol:sysProtocol:edit"> <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>

</html>