<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统管理员管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">

        function isToSame(repwd) {
            var pwd = document.getElementById("pwd").value;
            console.debug(pwd);
            console.debug(repwd.value);
            if (repwd.value != pwd) {
                alert("两次输入的密码不一致,请重新输入!");
                document.getElementById("pwd").value = "";
                repwd.value = "";
                return;
            }
        }

        function isName(name) {
            var username = name.value;
            exp = /^[a-z][a-z0-9]+$/i;
            var reg = username.match(exp);
            if (reg == null) {
                alert("用户名不合法 ,请重新输入！");
                document.getElementById("username").value="";
                return;
            }

        }

		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
	<li><a href="${ctx}/sysManager/ftSysManager/">系统管理员管理</a></li>
	<li class="active"><a href="${ctx}/sysManager/ftSysManager/addPage?id=${ftSysManager.id}&name=${ftSysManager.name}">系统管理<shiro:hasPermission name="sysManager:ftSysManager:edit">添加</shiro:hasPermission><shiro:lacksPermission name="sysManager:ftSysManager:edit">修改</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="ftSysManager" action="${ctx}/sysManager/ftSysManager/save" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
	<%--<div class="control-group">
		<label class="control-label">系统管理员名称：</label>
		<div class="controls">
			<form:input path="name" htmlEscape="false" maxlength="256" class="input-xlarge " required="true"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">密码：</label>
		<div class="controls">
			<form:input path="password" type="password" htmlEscape="false" maxlength="256" class="input-xlarge "/>
		</div>
	</div>--%>


    <div class="control-group">
        <label class="control-label">用&nbsp;户&nbsp;名：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 250px"
                        required="true" onblur="isName(username)" id="username" name="username"/>&nbsp;&nbsp;
                <%--<button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-edit">&nbsp;&nbsp;修改密码</i></button>--%>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码：</label>
        <div class="controls">
                <%--<form:input path="pwd" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px"--%>
                <%--id="pwd" name="pwd" type="password" value="" required="true"/>--%>
            <input id="pwd" name="pwd" style="WIDTH: 250px" type="password" required="true"
				   id="password01" name="password01" maxlength="50"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">确认密码：</label>
        <div class="controls">
            <input id="repwd" name="repwd" style="WIDTH: 250px" type="password" required="true"
                   onblur="isToSame(repwd)" maxlength="50"/>
        </div>
    </div>


	<div class="control-group">
		<label class="control-label">电话：</label>
		<div class="controls">
			<form:input path="phone" htmlEscape="false" maxlength="50" class="input-xlarge " required="true" style="WIDTH: 250px"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">邮件：</label>
		<div class="controls">
			<form:input path="email" htmlEscape="false" maxlength="50" class="input-xlarge " required="true" style="WIDTH: 250px"/>
		</div>
	</div>
	<%--<div class="control-group">
		<label class="control-label">公司：</label>
		<div class="controls">
			<form:input path="company" htmlEscape="false" maxlength="256" class="input-xlarge " required="true"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">部门：</label>
		<div class="controls">
			<form:input path="department" htmlEscape="false" maxlength="256" class="input-xlarge"/>
		</div>
	</div>--%>


	<div class="form-actions">
		<shiro:hasPermission name="sysManager:ftSysManager:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</form:form>
</body>
</html>