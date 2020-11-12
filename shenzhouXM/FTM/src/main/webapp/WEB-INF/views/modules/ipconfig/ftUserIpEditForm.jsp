<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>IP控制管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
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
				},
				rules:{
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
		<li class="active"><a href="${ctx}/ipconfig/ftUserIp/form?id=${ftUserIp.id}">IP控制<shiro:hasPermission name="ipconfig:ftUserIp:edit">${not empty ftUserIp.ipAddress?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="ipconfig:ftUserIp:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftUserIp" action="${ctx}/ipconfig/ftUserIp/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">IP&nbsp;&nbsp;地址：</label>
			<div class="controls">
				<form:input path="ipAddress" htmlEscape="false" maxlength="50" class="input-xlarge" readonly="true" style="WIDTH: 250px"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">用户名称：</label>
			<div class="controls">
					<%-- <form:select path="user.id" class="input-xlarge">
                        <form:options items="${userList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                    </form:select> --%>
				<form:input path="ftUserId" htmlEscape="false" maxlength="50" class="input-xlarge" readonly="true" style="WIDTH: 250px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="state" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 265px">
					<form:option value="1">允许</form:option>
					<form:option value="0">禁止</form:option>
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
			<shiro:hasPermission name="ipconfig:ftUserIp:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>