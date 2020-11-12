<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件上传发送</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">

//        function isClick(sendNodeName){
//           var tempValue =  sendNodeName.value;
//          alert(tempValue);
//            alert(0);
//        };

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

<ul class="nav nav-tabs  nav-tabs-hidden">
	<li><a a href="${ctx}/file/ftFileUpload/sendLink">文件上传列表</a></li>
</ul>
<br/>
    <form:form id="inputForm" modelAttribute="ftFileUpload" action="${ctx}/file/ftFileUpload/send" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>

        <div class="control-group" >
            <label class="control-label">数据节点：</label>
            <div class="controls">
                <form:select path="sendNodeName" class="input-medium" style="width:265px">
                    <form:option value="" label="——请选择——"/>
                    <c:forEach items="${sendNodeList}" var="ftFileSend" >
                        <form:option value="${ftFileSend.dataNodeName}" htmlEscape="false"/>
                    </c:forEach>
                </form:select>
            </div>
        </div>
        <%--<div class="control-group">
            <label class="control-label">监控端地址：</label>
            <div class="controls">
                <form:input path="monitorNodeIp" htmlEscape="false" maxlength="50" class="input-xlarge" style="WIDTH: 250px"
                            readonly="true" value=""/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">监控端端口：</label>
            <div class="controls">
                <form:input path="monitorForDataNodePort" htmlEscape="false" maxlength="50" class="input-xlarge" style="WIDTH: 250px"
                            readonly="true" value=""/>
            </div>
        </div>--%>

		<div class="control-group">
			<label class="control-label">更新类型：</label>
			<div class="controls">
				<form:select path="updateType" class="input-medium" style="width:265px">
					<%--<form:option value="" label="——请选择——"/>--%>
					<form:option value="301" label="全量更新" htmlEscape="false"/>
					<form:option value="302" label="增量更新" htmlEscape="false"/>
				</form:select>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="file:ftFileUpload:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="发 送"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>