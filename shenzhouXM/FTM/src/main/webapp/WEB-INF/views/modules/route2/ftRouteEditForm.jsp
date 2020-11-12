<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易码路由管理</title>
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
                    destination: {
                        required: true
                    }
                }
			});
		});

	</script>
</head>
<body>
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li><a href="${ctx}/route/ftRoute/">路由管理列表</a></li>
		<li class="active"><a href="${ctx}/route/ftRoute/form?id=${ftRoute.id}&tran_code=${ftRoute.tran_code}">路由管理<shiro:hasPermission name="route:ftRoute:edit">修改</shiro:hasPermission><shiro:lacksPermission name="route:ftRoute:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftRoute" action="${ctx}/route/ftRoute/saveEdit" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="type" value="s" />
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">交易码：</label>
			<div class="controls">
				<form:input path="tran_code" htmlEscape="false" maxlength="50" class="input-xlarge " readonly="true" style="width:250px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">(上传)用户名称：</label>
			<div class="controls">
				<form:input path="user" htmlEscape="false" maxlength="50" class="input-xlarge " style="width:250px" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">模式：</label>
			<div class="controls">
				<%--<form:input path="mode" htmlEscape="false" maxlength="256" class="input-xlarge "/>--%>
					<form:select path="mode" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 265px">
						<form:option value="syn">同步&nbsp;&nbsp;</form:option>
						<form:option value="asyn">异步&nbsp;&nbsp;</form:option>
					</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">路由目标：</label>
			<%--<div class="controls">
				<form:input path="destination" htmlEscape="false" maxlength="50" class="input-xlarge " style="width:250px"/>
				<span class="help-inline"><font color="red"> * </font>多个以英式逗号分隔</span>
			</div>--%>
			<div class="controls">
				<form:select path="destination" class="input-xlarge" style="WIDTH:265PX" multiple="true">
					<form:options items="${routeNameList}" htmlEscape="false" selected="selected"/>
					<form:options items="${routeNameListTmp}" htmlEscape="false" />
				</form:select>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="route:ftRoute:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>