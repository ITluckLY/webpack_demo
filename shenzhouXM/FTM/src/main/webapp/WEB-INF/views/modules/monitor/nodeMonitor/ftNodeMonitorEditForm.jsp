<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>节点监控阀值设置</title>
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
<form:form id="inputForm" modelAttribute="ftNodeMonitor" action="${ctx}/monitor/FtNodeMonitor/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden  id="node" path="node"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">文件数阀值：</label>
        <div class="controls">
            <form:input path="filenumber_threshold" htmlEscape="false" maxlength="50" class="input-xlarge " readonly="true"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">存储空间阀值：</label>
        <div class="controls">
            <form:input path="storage_threshold" htmlEscape="false" maxlength="50" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">CPU阀值：</label>
        <div class="controls">
            <form:input path="cpu_threshold" htmlEscape="false" maxlength="50" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">磁盘阀值：</label>
        <div class="controls">
            <form:input path="disk_threshold" htmlEscape="false" maxlength="50" class="input-xlarge "/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">内存阀值：</label>
        <div class="controls">
            <form:input path="memory_threshold" htmlEscape="false" maxlength="50" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">网络阀值：</label>
        <div class="controls">
            <form:input path="network_threshold" htmlEscape="false" maxlength="50" class="input-xlarge "/>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>