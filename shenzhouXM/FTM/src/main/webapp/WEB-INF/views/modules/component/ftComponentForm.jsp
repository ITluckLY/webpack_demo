<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>组件管理</title>
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
                rules:{
                    name:{
                        clsnameCheck:true,
                        required:true
                    },
                    implement:{
                        compCheck:true,
                        required:true
                    }
                },
            });
        });
    </script>
</head>
<body>



<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/component/ftComponent/">组件列表</a></li>
    <li class="active"><a href="${ctx}/component/ftComponent/form?id=${ftComponent.id}&name=${ftComponent.name}">组件<shiro:hasPermission
            name="component:ftComponent:edit">${not empty ftComponent.name?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="component:ftComponent:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftComponent" action="${ctx}/component/ftComponent/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">组件名称：</label>

        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge "
                        readonly="${not empty ftComponent.name?'true':'false'}" required1="true"/>
            <span class="help-inline"><font color="red"> * </font>英文字母开头+英文、数字或下划线的组合（3-30位）</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">实现类：</label>

        <div class="controls">
            <form:input path="implement" htmlEscape="false" maxlength="100" class="input-xlarge" required="true"/>
            <span class="help-inline"><font color="red"> * </font>例如：com.dcfs.esb.ftp.impls.service.TestService</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">参&nbsp;&nbsp;&nbsp;数：</label>

        <div class="controls">
            <form:input path="param" htmlEscape="false" maxlength="100" class="input-xlarge"/>
            <span class="help-inline">例如：a=1,b=2,c=d</span>
        </div>
    </div>

    <%--将中文描述当成是备注进行处理--%>
    <div class="control-group">
        <label class="control-label">组件描述：</label>

        <div class="controls">

            <form:textarea path="des" htmlEscape="false" rows="2" maxlength="20" class="input-xlarge "/>
        </div>
    </div>




    <div class="form-actions">
        <shiro:hasPermission name="component:ftComponent:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                      type="submit"
                                                                      value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>