<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文件上传管理</title>
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
                },
                rules: {
                    remarks: {
                        demoCheck: true
                    }
                }
            });
        });
    </script>
</head>
<body>


<%--<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/file/ftFileUpload/form">文件上传管理<shiro:hasPermission name="file:ftFileUpload:edit">${not empty ftFileUpload.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="file:ftFileUpload:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>--%>
<form:form id="inputForm" modelAttribute="bankSystem" action="${ctx}/user/ftUserImports/save" method="post"
           class="form-horizontal" enctype="multipart/form-data">
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">模板下载:</label>
        <div class="controls">
            <a href="${ctx}/user/ftUserImports/download?filename=users_model.csv">
                系统名_联系人_用户申请单.csv
            </a>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">系统名称：</label>
        <div class="controls">
            <form:select id="bankname" path="bankname" class="input-medium" style="width:150px">
                <form:option value="" label="——请选择——"/>
                <form:options items="${BankSystemList}" itemLabel="bankname" itemValue="bankname" htmlEscape="false" />
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">文件名称：</label>
        <div class="controls">
            <input type="file" id="uploadFileName" name="uploadFileName" required="true"/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="user0:ftUserImports:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>

    </div>
</form:form>
</body>
</html>