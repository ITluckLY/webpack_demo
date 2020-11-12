<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>节点参数</title>
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
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li><a href="${ctx}/nodeparam/ftNodeParam/">节点参数列表</a></li>
		<li class="active"><a href="${ctx}/nodeparam/ftNodeParam/form?nodeId=${ftServiceNode.name}">节点参数<shiro:hasPermission name="nodeparam:ftNodeParam:edit">${not empty ftNodeParam.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="nodeparam:ftNodeParam:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftNodeParam" action="${ctx}/nodeparam/ftNodeParam/save" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		${ftNodeParam}
	</form:form>
</body>
</html>