<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>流程管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
				},
				rules:{
					des:{
						demoCheck:true
					}
				}
			});
		});

		function go(sta) {
			location.href = "${ctx}/flow/ftFlow/component?name=${ftFlow.name}&des=${ftFlow.des}&components=${ftFlow.components}";
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li><a href="${ctx}/flow/ftFlow/">流程列表</a></li>
		<li class="active"><a href="${ctx}/flow/ftFlow/form?name=${ftFlow.name}&des=${ftFlow.des}">流程<shiro:hasPermission name="flow:ftFlow:edit">${not empty ftFlow.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="flow:ftFlow:edit">查看</shiro:lacksPermission></a></li>
		<%--<li ><a href="${ctx}/flow/ftFlow/component?id=${ftFlow.id}">流程处理</a></li>--%>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftFlow" action="${ctx}/flow/ftFlow/save" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge" required="true" style="WIDTH:250px" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">流程描述：</label>
			<div class="controls">
				<form:textarea path="des" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge " style="WIDTH: 350px"/>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="flow:ftFlow:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			<shiro:hasPermission name="flow:ftFlow:edit">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<%--<button id="btnStop" class="btn btn-primary" type="button" onclick="go('stop')"><i>
						&nbsp;&nbsp;流程处理&nbsp;&nbsp;</i></button>--%>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>