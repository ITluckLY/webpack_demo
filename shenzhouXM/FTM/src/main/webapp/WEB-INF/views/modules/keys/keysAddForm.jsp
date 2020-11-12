<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>秘钥管理管理</title>
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
<form:form id="inputForm" modelAttribute="ftKey" action="${ctx}/keys/ftKey/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">用户名称：</label>
        <div class="controls">
            <form:input path="user" htmlEscape="false" maxlength="50" class="input-xlarge required" required="true"
                        style="WIDTH: 250px"/>
            <span class="help-inline"><font color="red"> * </font>名称长度必须介于 0 和 30 之间</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">秘钥类型：</label>
        <div class="controls">
            <form:select id="protocol" path="type" class="input-medium" style="WIDTH: 265px">
                <form:options items="${dictList}" itemLabel="value" itemValue="value" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
    	<label class="control-label">	秘钥内容：</label>
    	<div class="controls">
    		<form:textarea path="content" htmlEscape="false" rows="4" maxlength="2000" class="input-xlarge "  required="true" style="WIDTH: 350px"/>
    		<span class="help-inline"><font color="red"> * </font>秘钥长度必须介于 0 和 2000 之间</span>
        </div>
    </div>
    <div class="form-actions">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>