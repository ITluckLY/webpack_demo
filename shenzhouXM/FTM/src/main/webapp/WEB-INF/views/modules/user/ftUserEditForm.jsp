<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>用户管理管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function () {
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
                    pwd: {
                        paramCheck: true,
                        required: true
                    },
                    repwd: {
                        equalTo: "#pwd"
                    },
                    des: {
                        demoCheck: true
                    },
                    clientAddress: {
                        clientAddressCheck: true
                    }
                },
                messages: {
                    repwd: {
                        equalTo: "两次密码输入不一致"
                    }
                }
            });
        });
        function PwdHidden() {
            if (document.getElementById("pwdOne").style.display === "none") {
                document.getElementById("pwdOne").style.display = "block";
                document.getElementById("repwdpwd").style.display = "block";
                document.getElementById("pwd").value = "";
                $("#pwdInput").val("隐藏密码");
            } else {
                document.getElementById("pwdOne").style.display = "none";
                document.getElementById("repwdpwd").style.display = "none";
                $("#pwdInput").val("修改密码");
            }
        }
    </script>

</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/user/ftUser/">用户管理列表</a></li>
    <li class="active"><a href="${ctx}/user/ftUser/form?id=${ftUser.id}">用户管理<shiro:hasPermission
            name="user0:ftUser:edit">修改</shiro:hasPermission><shiro:lacksPermission
            name="user0:ftUser:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftUser" action="${ctx}/user/ftUser/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="userDir"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">用&nbsp;户&nbsp;名：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge " readonly="true" style="WIDTH: 250px"/>&nbsp;&nbsp;
            <input id="pwdInput" class="btn btn-primary" type="button" onclick="PwdHidden()" value="修改密码"/>
        </div>
    </div>
    <div id="pwdOne" class="control-group" style="display:none">
        <label class="control-label">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码：</label>
        <div class="controls">
            <form:input path="pwd" id="pwd" name="pwd" type="password" required="true" style="WIDTH: 250px" maxlength="20"/>
        </div>
    </div>
    <div class="control-group" style="display:none" id="repwdpwd">
        <label class="control-label">确认密码：</label>
        <div class="controls">
            <input name="repwd" style="WIDTH: 250px" type="password" required maxlength="20" id="repwd"/>
        </div>
    </div>

    <%--<div class="control-group">
        <label class="control-label">客户端地址：</label>
        <div class="controls">
            <form:input path="clientAddress" htmlEscape="false" maxlength="50" class="input-xlarge" style="WIDTH: 250px"/>&nbsp;&nbsp;
        </div>
    </div>--%>

    <div class="control-group">
        <label class="control-label">描&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;述：</label>
        <div class="controls">
            <form:textarea path="des" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge " style="WIDTH: 350px"/>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="user0:ftUser:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>