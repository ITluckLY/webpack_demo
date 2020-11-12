<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>节点参数</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
//			jQuery.validator.addMethod("nameCheck", function (value, element) {
//				return this.optional(element) || /^[a-z][a-z0-9]*$/i.test(value);
//			}, "请输入合法的名称");


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
					name:{
						checkName:true,
						required:true
					}
				},
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li><a href="${ctx}/nodeparam/ftNodeParam/">节点参数列表</a></li>
		<li class="active"><a href="${ctx}/nodeparam/ftNodeParam/editForm?id=${ftNodeParam.id}">节点参数<shiro:hasPermission name="nodeparam:ftNodeParam:edit">${not empty ftNodeParam.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="nodeparam:ftNodeParam:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftNodeParam" action="${ctx}/nodeparam/ftNodeParam/saveEdit" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">节点：</label>
			<div class="controls">
				<form:input path="nodeId" htmlEscape="false" maxlength="30" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">参数名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-xlarge " readonly="true"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">参数值：</label>
			<div class="controls">
				<form:input path="value" htmlEscape="false" maxlength="30" class="input-xlarge " />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="des" htmlEscape="false" rows="4" maxlength="50" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="nodeparam:ftNodeParam:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>

		</div>
	</form:form>
</body>
</html>