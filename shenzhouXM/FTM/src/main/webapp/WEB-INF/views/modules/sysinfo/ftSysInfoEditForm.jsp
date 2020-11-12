<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业务系统管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#type").attr('readonly',true);
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
		<li><a href="${ctx}/sysInfo/ftSysInfo/">系统管理</a></li>
		<li class="active"><a href="${ctx}/sysInfo/ftSysInfo/form?id=${ftSysInfo.id}&name=${ftSysInfo.name}">系统管理<shiro:hasPermission name="sysInfo:ftSysInfo:edit">修改</shiro:hasPermission><shiro:lacksPermission name="sysInfo:ftSysInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftSysInfo" action="${ctx}/sysInfo/ftSysInfo/saveEdit" method="post" class="form-horizontal">
		<form:hidden path="id" id="id"/>
		<form:hidden path="admin"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">节点组名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge " readonly="true"  style="WIDTH:235PX"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">节点模式：</label>
			<div class="controls">
				<form:select path="sysNodeModel" htmlEscape="false" maxlength="50" class="input-xlarge "
							 id="type" style="WIDTH:250PX">
					<form:option value="single">单节点模式&nbsp;&nbsp;</form:option>
					<form:option value="more">多节点并行模式&nbsp;&nbsp;</form:option>
					<form:option value="ms">主备模式&nbsp;&nbsp;</form:option>
				</form:select>
			</div>
		</div>
        <c:if test="${ftSysInfo.sysNodeModel=='ms'|| ftSysInfo.sysNodeModel == null}">

            <div class="control-group">
			<label class="control-label">主备切换模式：</label>
			<div class="controls">
				<form:select path="switchModel" htmlEscape="false" maxlength="50" class="input-xlarge"
							 style="WIDTH:250PX">
					<form:option value="auto">自动切换模式&nbsp;&nbsp;</form:option>
					<form:option value="handle">手动切换模式&nbsp;&nbsp;</form:option>
				</form:select>
			</div>
		</div>
        </c:if>
		<div class="control-group">
			<label class="control-label">文件存储模式：</label>
			<div class="controls">
				<form:select path="storeModel" htmlEscape="false" maxlength="50" class="input-xlarge"
							 style="WIDTH:250PX">
					<form:option value="single">单点模式&nbsp;&nbsp;</form:option>
					<form:option value="sync">同步模式&nbsp;&nbsp;</form:option>
					<form:option value="async">异步模式&nbsp;&nbsp;</form:option>
				</form:select>
			</div>
		</div>


		<div class="control-group">
			<label class="control-label">节点组描述：</label>
			<div class="controls">
				<form:textarea path="des" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge "
							   style="WIDTH: 350px"/>			</div>
		</div>
        <%--<div class="control-group">--%>
            <%--<label class="control-label">系统备注：</label>--%>
            <%--<div class="controls">--%>
                <%--<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="256" class="input-xlarge "--%>
                               <%--style="WIDTH: 350px"/>--%>
            <%--</div>--%>
        <%--</div>--%>

		<div class="form-actions">
			<shiro:hasPermission name="sysInfo:ftSysInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>