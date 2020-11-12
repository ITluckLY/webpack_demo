<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>定时任务管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

			$("#btnSubmit").on("click", function(){
				$("#keepTime").val($("#kptTime").val() + $("#time").val());
			});

        });
    </script>
    <link href="${ctxStatic}/jquery-spinner/bootstrap-spinner.css" rel="stylesheet">
    <script src="${ctxStatic}/jquery-spinner/jquery.spinner.min.js"></script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/fileclean/ftFileClean/form?id=${ftFileClean.id}">文件清理<shiro:hasPermission
            name="fileclean:ftFileClean:edit">${not empty ftFileClean.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="fileclean:ftFileClean:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftFileClean" action="${ctx}/timingtask/ftTimingTask/savefileclear" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">目标目录：</label>

        <div class="controls">
            <form:input path="targetDir" htmlEscape="false" maxlength="50" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">用户：</label>

        <div class="controls">
            <form:input path="userName" htmlEscape="false" maxlength="20" class="input-xlarge "/>
        </div>
    </div>
		<div class="control-group">
			<label class="control-label">保留时间：</label>
			<form:hidden id="keepTime" path="keepTime" />
			<div class="controls">

				<div class="input-append spinner" data-trigger="spinner">
					<input id="kptTime" type="text" value="1" data-rule="quantity" />

					<div class="add-on">
						<a href="javascript:;" class="spin-up" data-spin="up"><i
							class="icon-sort-up"></i></a> <a href="javascript:;"
							class="spin-down" data-spin="down"><i class="icon-sort-down"></i></a>
					</div>
				</div>

				<select id="time">
					<option value="d" selected="selected">日</option>
					<option value="h">时</option>
				</select>


			</div>
		</div>
		<!--  
    <div class="control-group">
        <label class="control-label">状态：</label>

        <div class="controls">
            <form:select path="state">
                <option value="0">等待</option>
                <option value="1">运行</option>
            </form:select>
        </div>
    </div>
    -->
    <div class="control-group">
        <label class="control-label">文件归档：</label>

        <div class="controls">
            <form:select path="isBackup">
                <option value="true" selected="selected" >是</option>
                <option value="false">否</option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注：</label>

        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="100" class="input-xxlarge "/>
        </div>
    </div>
    <!--  
    <div class="control-group">
        <label class="control-label">归档类型：</label>
        <div class="controls">
            <select>
                <option>是</option>
            </select>
        </div>
    </div>
    -->
    <div class="control-group">
        <label class="control-label">归档路径：</label>

        <div class="controls">
            <form:input path="backupPath" htmlEscape="false" maxlength="100" class="input-xlarge " value="/esb" disabled="true"/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="fileclean:ftFileClean:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                      type="submit"
                                                                      value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>