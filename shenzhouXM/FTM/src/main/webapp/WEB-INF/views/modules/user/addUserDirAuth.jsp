<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户权限管理</title>
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
                    userDir:{
                        dire2Check:true
                    }
                }
			});
		});
	</script>
</head>
<body>

<form:form id="inputForm" modelAttribute="userAuth" action="${ctx}/user/ftUser/saveUserDirAuth" method="post" class="form-horizontal">
	<sys:message content="${message}"/>

	<div class="control-group">
		<label class="control-label">用户名称：</label>
		<div class="controls">
			<form:select path="userName" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 265px">
				<c:forEach items="${ftUserList}" var="ftu">
					<option>${ftu.name}</option>
				</c:forEach>
			</form:select>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">目录路径：</label>
		<div class="controls">
			<form:input path="path" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="userDirTemp" name="userDirTemp"/>
			<span class="help-inline"><font color="red"> * </font>例如：/esb/test001</span>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">目录权限：</label>
		<div class="controls">
			<form:select path="auth" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 265px">
				<%--<form:options items="${fns:getDictList('permessionType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
				<form:option value="A">全部</form:option>
				<form:option value="R">只读</form:option>
				<form:option value="W">写入</form:option>
			</form:select>
		</div>
	</div>

	<div class="form-actions">
		<shiro:hasPermission name="user0:ftUserDirAuth:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>

</form:form>
</body>
</html>