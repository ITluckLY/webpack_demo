<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定时任务管理</title>
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
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/timingtask/ftTimingTask/">定时任务列表</a></li>
		<li class="active"><a href="${ctx}/timingtask/ftTimingTask/form?id=${ftTimingTask.id}">定时任务<shiro:hasPermission name="timingtask:ftTimingTask:edit">${not empty ftTimingTask.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="timingtask:ftTimingTask:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftTimingTask" action="${ctx}/timingtask/ftTimingTask/next" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">编号2：</label>
			<div class="controls">
				<form:input path="seq" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">表达式：</label>
			<div class="controls">
				<form:input path="timeExp" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调用流程：</label>
			<div class="controls">
				<form:input path="flowId" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<!--  
		<div class="control-group">
			<label class="control-label">参数：</label>
			<div class="controls">
				<form:input path="params" htmlEscape="false" maxlength="100" class="input-xlarge "/>例：a=1,b=2,c=d
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">任务说明：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		-->
		<div class="form-actions">
			<!--<shiro:hasPermission name="timingtask:ftTimingTask:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission> -->
			<shiro:hasPermission name="timingtask:ftTimingTask:edit"><input id="btnNext" class="btn btn-primary" type="submit" value="下一步"/></shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>