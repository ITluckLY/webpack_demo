<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>用户管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        $(document).ready(function () {
            window.onload=function(){
                $("#name").attr('value','');
                $("#pwd").attr('value','');
            };

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
                    pwd: {
                        paramCheck: true,
                        required: true
                    },
                    repwd: {
                        equalTo: "#pwd"
                    },
                    des: {
                        demoCheck: true
                    }
                },
                messages: {
                    name: {
                        required: "必填信息",
                    },
                    pwd: {
                        required: "必填信息",
                    },
                    repwd: {
                        equalTo: "两次密码输入不一致"
                    }
                }

            });
        });
    </script>

</head>
<body>
<form:form id="inputForm" modelAttribute="ftUser" action="${ctx}/user/ftUser/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="userDir"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">用户名：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge " required="true"
                        style="WIDTH: 250px" id="name"/>
            <span class="help-inline"><font color="red"> * </font>用户名为英文开头+英文或数字</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">密码：</label>
        <div class="controls">
            <form:input path="pwd" htmlEscape="false" maxlength="20" class="input-xlarge " required="true"
                        style="WIDTH: 250px" type="password" id="pwd"/>
            <span class="help-inline"><font color="red"> * </font>6-20位</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">确认密码：</label>
        <div class="controls">
            <input id="repwd" name="repwd" style="WIDTH: 250px" type="password" required="true" maxlength="20"/>
            <span class="help-inline"><font color="red"> * </font></span>
        </div>
    </div>

    <%--<div class="control-group">
        <label class="control-label">客户端地址：</label>
        <div class="controls">
            <form:input path="clientAddress" htmlEscape="false" maxlength="20" class="input-xlarge" style="WIDTH: 250px" id="name"/>
            <span class="help-inline">客户端地址为IP地址:端口</span>
        </div>
    </div>--%>

    <div class="control-group">
        <label class="control-label">描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
        <div class="controls">
            <form:textarea path="des" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge "
                           style="WIDTH: 350px"/>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="user0:ftUser:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                            value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>