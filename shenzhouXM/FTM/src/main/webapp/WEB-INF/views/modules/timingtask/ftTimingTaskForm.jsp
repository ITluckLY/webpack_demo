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
				},
                rules:{
                    seq:{
						timeTaskingCheck:true,
                        required:true
                    },
                    timeExp:{
						paramNoLimitCheck:true,
                        required:true
                    },
                    count:{
                        numCheck:true,
                        required:true
                    },
                    params:{
						paramNoLimitCheck:true,
                        required:true
                    },
					description:{
						demoCheck:true
					}

                },
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li><a href="${ctx}/timingtask/ftTimingTask/">定时任务列表</a></li>
		<li class="active"><a href="${ctx}/timingtask/ftTimingTask/form?id=${ftTimingTask.id}&seq=${ftTimingTask.seq}">定时任务<shiro:hasPermission name="timingtask:ftTimingTask:edit">${not empty ftTimingTask.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="timingtask:ftTimingTask:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="ftTimingTask" action="${ctx}/timingtask/ftTimingTask/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
        <form:hidden path="state"/>
        <form:hidden path="seq"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">任&nbsp;&nbsp;务&nbsp;&nbsp;名：</label>
			<div class="controls">
				<form:input path="taskName" htmlEscape="false" maxlength="30" class="input-xlarge" required="true" style="WIDTH:250PX" id="seq" name="seq"/>
				<span class="help-inline"><font color="red"> * </font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">节&nbsp;&nbsp;&nbsp;点：</label>
			<div class="controls">
                <form:select id="nodeNameTemp" path="nodeNameTemp" class="input-medium" style="width:265px">
                        <%--<form:option value="" label="——请选择——"/>--%>
                    <form:options items="${nodeNameList}" htmlEscape="false"/>
                </form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">表达式：</label>
			<div class="controls">
				<form:input path="timeExp" htmlEscape="false" maxlength="50" class="input-xlarge" required="true" style="WIDTH:250PX" id="timeExp" name="timeExp"/>
				<span class="help-inline"><font color="red"> * </font>格式：MM dd hh mm ss 例如：01 * 12 * 00</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">任&nbsp;&nbsp;&nbsp;务：</label>
			<div class="controls">
				<%--<form:input path="flowId" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH:250PX"/>--%>
					<form:select path="flowId" htmlEscape="false" maxlength="100" class="input-xlarge " style="WIDTH: 265px">
                        <%--<form:option value="">——请选择——&nbsp;&nbsp;</form:option>&ndash;%&gt;--%>
                        <form:options items="${dictList}" itemLabel="value" itemValue="value" htmlEscape="false"/>
					</form:select>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">最大执行次数：</label>
            <div class="controls">
                <form:input path="count" htmlEscape="false" maxlength="5" class="input-xlarge " style="WIDTH:250PX" id="count" name="count"/>
				<span class="help-inline"><font color="red"> * </font></span>
			</div>
        </div>
		<div class="control-group">
			<label class="control-label">参&nbsp;&nbsp;&nbsp;数：</label>
			<div class="controls">
				<form:textarea path="params" htmlEscape="false" rows="3" maxlength="2000" class="input-xlarge " style="WIDTH:450PX" id="params" name="params" required="true"/>
				<span class="help-inline"><font color="red"> * </font>例如：a=1,b=2,c=d</span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备&nbsp;&nbsp;&nbsp;注：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge " style="WIDTH:450PX"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="timingtask:ftTimingTask:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>