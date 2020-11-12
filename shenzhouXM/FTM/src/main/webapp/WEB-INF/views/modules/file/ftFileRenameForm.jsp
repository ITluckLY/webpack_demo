<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文件命名</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        function isPath(pathStr) {
            var path = pathStr.value;
            if(path != ""){
                exp = /\./;
                var reg = path.match(exp);
                if(document.getElementById("type").value == "file"){
                    if (reg == null) {
                        alert("文件路径有误,请重新输入！");
                        return;
                    }
                }else if(document.getElementById("type").value == "dir"){
                    if (reg == ".") {
                        alert("目录路径不能包含\".\",请重新输入！");
                        return;
                    }
                }
            }
        };

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
                    path:{
                        required:true
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/file/ftFileRename/">文件命名</a></li>
    <li class="active"><a href="${ctx}/file/ftFileRename/form?name=${ftFileRename.id}">节点<shiro:hasPermission
            name="file:ftFileRename:edit">${not empty ftFileRename.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="file:ftFileRename:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftFileRename" action="${ctx}/file/ftFileRename/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
    </div>
    <div class="control-group">
        <label class="control-label">路径类型：</label>
        <div class="controls">
            <form:select path="type" htmlEscape="false" maxlength="50" class="input-xlarge " onclick="checkType()"
                         id="type" style="width:265px">
                <c:if test="${ftFileRename.type == null}">
                    <form:option value="file">文件&nbsp;&nbsp;</form:option>
                    <form:option value="dir">目录&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftFileRename.type == 'file'}">
                    <form:option value="file">文件&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftFileRename.type == 'dir'}">
                    <form:option value="dir">目录&nbsp;&nbsp;</form:option>
                </c:if>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">路径名称：</label>
        <div class="controls">
            <form:input path="path" id="pathText" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 250px"
                        required="true" onblur="isPath(path)"/>
            <span class="help-inline"><font color="red"> * </font>例如：文件类型"/esb/test001.txt"；目录类型"/esb/test001"</span>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="file:ftFileRename:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
            &nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>