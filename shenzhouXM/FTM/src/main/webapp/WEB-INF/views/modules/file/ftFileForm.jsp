<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件管理管理</title>
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
		<li><a href="${ctx}/file/ftFile/getContent?path=${path}">文件管理列表</a></li>
		<li class="active"><a href="${ctx}/file/ftFile/form?id=${ftFile.id}">文件管理<shiro:hasPermission name="file:ftFile:edit">${not empty ftFile.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="file:ftFile:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="ftFile" action="${ctx}/file/ftFile/save" method="post" enctype="multipart/form-data" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">文件名称：</label>
			<div class="controls">
				<input type="file" id="file" name="file" class="required"/>
				<%--<span class="help-inline">支持文件格式：zip、*</span>--%>
				<%--<form:input path="fileName" htmlEscape="false" maxlength="256" class="input-xlarge "/>--%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">文件路径：</label>
			<div class="controls">
				<form:input path="parentPath" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上传节点组：</label>
			<div class="controls">
				<form:input path="systemName" htmlEscape="false" maxlength="50" class="input-xlarge " disabled="true"/>
			</div>
		</div>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">文件大小：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="fileSize" htmlEscape="false" maxlength="256" class="input-xlarge " disabled="true"/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">上传时间：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="createDate" htmlEscape="false" maxlength="256" class="input-xlarge " disabled="true"/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">源IP：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="" htmlEscape="false" maxlength="256" class="input-xlarge " disabled="true"/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">源文件：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="" htmlEscape="false" maxlength="256" class="input-xlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">MD5串：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="" htmlEscape="false" maxlength="256" class="input-xlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">字符集：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="" htmlEscape="false" maxlength="256" class="input-xlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">备注：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="256" class="input-xxlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">文件内容：</label>--%>
			<%--<div class="controls">--%>
				<%--<input id="showContent" style="margin-bottom: 1em" class="btn" type="button" value="查看内容" onclick="history.go(-1)"/>--%>
				<%--<br />--%>
				<%--<form:textarea path="" htmlEscape="false" rows="4" maxlength="256" class="input-xxlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="form-actions">
			<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<%--<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="下 载"/>&nbsp;</shiro:hasPermission>--%>
			<%--<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="重命名"/>&nbsp;</shiro:hasPermission>--%>
			<%--<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="转 码"/>&nbsp;</shiro:hasPermission>--%>
			<%--<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="加 密"/>&nbsp;</shiro:hasPermission>--%>
			<%--<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="解 密"/>&nbsp;</shiro:hasPermission>--%>
			<%--<shiro:hasPermission name="file:ftFile:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="扩展处理"/>&nbsp;</shiro:hasPermission>--%>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>