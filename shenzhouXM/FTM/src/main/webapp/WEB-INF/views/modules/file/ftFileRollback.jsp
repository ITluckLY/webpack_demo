<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>备份文件回滚</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        function changeDN(dataNodeName) {
            var tempValue = dataNodeName.value;
            var selectObj = document.getElementById("baklist");
            selectObj.options.length=0;
            selectObj.options[0] = new Option("——请选择——", null);
//            selectObj.options[0].selected=true;
            if(tempValue == 0){
                window.location.reload();
            }
            $.ajax({
                url: "${ctx}/file/ftFileRollback/bakFile",
                type: "get",
                data: {dataNodeName: tempValue},
                dataType: "json",
                success: function (data) {
                    $.each(data, function (i, item) {
//                        alert(item.bakFileName);
                        selectObj.options[i+1] = new Option(item.updateDate, item.bakFileName);
                    })
                },
                error: function () {
                    showTip("节点名不能为空");
                }
            });
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
                }
            });
        });
    </script>
</head>
<body>

<%--<ul class="nav nav-tabs  nav-tabs-hidden">
	<li><a a href="${ctx}/file/ftFileRollback/rollback">文件回滚列表</a></li>
</ul>--%>
<br/>
<form:form id="inputForm" modelAttribute="ftFileSend" action="${ctx}/file/ftFileRollback/send" method="post"
           class="form-horizontal">
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">数据节点：</label>
        <div class="controls">
            <form:select path="dataNodeName" class="input-medium" style="width:265px" onchange="changeDN(dataNodeName)">
                <form:option value="0" label="——请选择——"/>
                <c:forEach items="${sendNodeList}" var="ftFileSend">
                    <form:option value="${ftFileSend.dataNodeName}" htmlEscape="false"/>
                </c:forEach>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备份日期：</label>
        <div class="controls">
            <form:select path="bakFileName" class="input-medium" style="width:265px" id="baklist">
                <form:option value="" label="——请选择——"/>
            </form:select>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="file:ftFileUpload:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                                  value="回 滚"/>&nbsp;</shiro:hasPermission>
        <%--<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>--%>
    </div>
</form:form>
</body>
</html>