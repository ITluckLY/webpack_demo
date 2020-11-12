<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定时任务执行情况</title>
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
		<li class="active"><a href="${ctx}/timingtask/ftTimingTask/detail?id=1">任务执行情况</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftTimingTask" action="${ctx}/timingtask/ftTimingTask/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">编号：</label>
			<div class="controls">
				<input class="input-xlarge " value="1"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上次时间：</label>
			<div class="controls">
				<input class="input-xlarge " value="2016/01/01 00:01:01" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">下次时间：</label>
			<div class="controls">
				<input class="input-xlarge " value="2016/01/02 00:01:01" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">执行次数：</label>
			<div class="controls">
				<input class="input-xlarge " value="101" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">失败次数：</label>
			<div class="controls">
				<input class="input-xlarge " value="3" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">表达式：</label>
			<div class="controls">
				<input class="input-xlarge " value="0 1 * * *" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调用流程：</label>
			<div class="controls">
				<input class="input-xlarge " value="110010000203" />
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>