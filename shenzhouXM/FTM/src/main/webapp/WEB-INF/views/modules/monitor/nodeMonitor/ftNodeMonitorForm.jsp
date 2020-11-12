<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>节点监控阀值</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/selfDefine/formValidate.js" type="text/javascript"></script>
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
                },
                rules: {
                    filenumberwarn: {
                        numCheck: true
                    },
                    filenumberline: {
                        numCheck: true
                    },
                    storagewarn: {
                        percCheck: true
                    },
                    storageline: {
                        percCheck: true
                    },
                    cpuwarn: {
                        percCheck: true
                    },
                    cpuline: {
                        percCheck: true
                    },
                    diskwarn: {
                        percCheck: true
                    },
                    diskline: {
                        percCheck: true
                    },
                    memorywarn: {
                        percCheck: true
                    },
                    memoryline: {
                        percCheck: true
                    },
                    networkwarn: {
                        percCheck: true
                    },
                    networkline: {
                        percCheck: true
                    }
                }
            });
        });
    </script>
</head>
<body>
<form:form id="inputForm" modelAttribute="ftNodeMonitor" action="${ctx}/monitor/FtNodeMonitor/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="node"/>
    <sys:message content="${message}"/>
    <br/>
    <div class="control-group">
        <label class="control-label">文件数一般告警线：</label>
        <div class="controls">
            <form:input path="filenumberwarn" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："400"</span>
        </div>
        <br/>
        <label class="control-label">文件数严重告警线：</label>
        <div class="controls">
            <form:input path="filenumberline" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："600"</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">存储空间一般告警线：</label>
        <div class="controls">
            <form:input path="storagewarn" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："40%"</span>
        </div>
        <br/>
        <label class="control-label">存储空间严重告警线：</label>
        <div class="controls">
            <form:input path="storageline" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："60%"</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">CPU使用率一般告警线：</label>
        <div class="controls">
            <form:input path="cpuwarn" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："40%"</span>
        </div>
        <br/>
        <label class="control-label">CPU使用率严重告警线：</label>
        <div class="controls">
            <form:input path="cpuline" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："60%"</span>
        </div>
    </div>
    <%--<div class="control-group">
        <label class="control-label">磁盘警告：</label>
        <div class="controls">
            <form:input path="diskwarn" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："40%"</span>
        </div>
        <br/>
        <label class="control-label">磁盘严重警告：</label>
        <div class="controls">
            <form:input path="diskline" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："60%"</span>
        </div>
    </div>--%>
    <div class="control-group">

        <label class="control-label">内存使用率一般告警线：</label>
        <div class="controls">
            <form:input path="memorywarn" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："40%"</span>
        </div>
        <br/>
        <label class="control-label">内存使用率严重告警线：</label>
        <div class="controls">
            <form:input path="memoryline" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">例如："60%"</span>
        </div>
    </div>
    <%--<div class="control-group">
        <label class="control-label">网络警告：</label>
        <div class="controls">
            <form:input path="networkwarn" htmlEscape="false" maxlength="20" class="input-xlarge "/>
        </div>
        <label class="control-label">网络严重警告：</label>
        <div class="controls">
            <form:input path="networkline" htmlEscape="false" maxlength="20" class="input-xlarge "/>
        </div>
    </div>--%>

    <div class="form-actions">
        <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                          type="submit"
                                                                          value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>