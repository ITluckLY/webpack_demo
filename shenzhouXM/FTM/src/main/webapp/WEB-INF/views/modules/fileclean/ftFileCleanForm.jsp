<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文件清理管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        function checkType() {
//            alert(document.getElementById("backupPath").value);
            if (document.getElementById("isBackup").value == "false") {
                document.getElementById("backDivId").value = "";
                document.getElementById("backDivId").style.display = "none";
            } else if (document.getElementById("isBackup").value == "true") {
                document.getElementById("backDivId").style.display = "block";
            }
        }

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
                    keepTime: {
                        numCheck: true,
                        required: true
                    },
                    targetDir: {
                        dire2Check: true
                    },
                    backupPath: {
                        dire2Check: true
                    },
                    remarks: {
                        demoCheck: true
                    }
                }
            });
        });

    </script>
    <link href="${ctxStatic}/jquery-spinner/bootstrap-spinner.css" rel="stylesheet">

</head>
<body>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/fileclean/ftFileClean/">文件清理列表</a></li>
    <li class="active"><a href="${ctx}/fileclean/ftFileClean/form?id=${ftFileClean.id}">文件清理
        <shiro:hasPermission name="fileclean:ftFileClean:edit">${not empty ftFileClean.id?'修改':'添加'}</shiro:hasPermission>
        <shiro:lacksPermission name="fileclean:ftFileClean:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>--%>
<form:form id="inputForm" modelAttribute="ftFileClean" action="${ctx}/fileclean/ftFileClean/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">

        <label class="control-label">目标目录：</label>

        <div class="controls">
            <form:input path="targetDir" htmlEscape="false" maxlength="50" class="input-xlarge" style="WIDTH: 250px"
                        required="true"/>
            <span class="help-inline"><font color="red"> * </font>例如："/esb/test001"</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">保留时间：</label>
        <div class="controls">
                <%--<form:input path="keepTime" htmlEscape="false" onblur="isNum(keepTime)" id="keepTime" style="WIDTH: 85px" required="true"/>--%>
            <form:input path="keepTime" htmlEscape="false" id="keepTime" style="WIDTH: 85px" required="true"
                        maxlength="8"/>
            <label>分钟</label>
            <span class="help-inline"><font color="red"> * </font></span>
        </div>
    </div>
    </div>

    <div class="control-group">
        <label class="control-label">文件归档：</label>
        <div class="controls">
            <form:select path="isBackup" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 100px" onchange="checkType()">
                <form:option value="true">是</form:option>
                <form:option value="false">否</form:option>
            </form:select>
        </div>
    </div>

    <div class="control-group" id="backDivId">
        <label class="control-label">归档路径：</label>
        <div class="controls">
            <form:input path="backupPath" htmlEscape="false" maxlength="50" class="input-xlarge" style="WIDTH: 250px"/>
            <span class="help-inline">例如：/esb/test001</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</label>

        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="100" class="input-xxlarge"
                           style="WIDTH: 350px"/>
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