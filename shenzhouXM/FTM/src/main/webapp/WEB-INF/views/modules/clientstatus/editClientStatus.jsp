<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>数据文件</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/client/clientStatus/list">客户端状态信息</a></li>
    <li class="active"><a href="#">修改</a></li>
</ul>
<form:form id="searchForm" modelAttribute="ftClientStatus" action="${ctx}/client/clientStatus/updateClientStatus"
           method="post" class="breadcrumb form-horizontal">
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">客户端ID：</label>
        <div class="controls">
            <input type="text" id="id" name="id" value="${ftClientStatus.id}" required="true" readonly="true"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">客户端名称：</label>
        <div class="controls">
            <input type="text" id="name" name="name" value="${ftClientStatus.name}" />
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">类型：</label>
        <div class="controls">
            <%--<input type="text" id="type" name="type" value="${ftClientStatus.type}" />--%>
            <form:select path="type" class="input-medium" id="type">
                <%--<form:options items="${ftClientStatus.type}" />--%>
                <form:option value="enable" />
                <form:option value="disable" />
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">模式：</label>
        <div class="controls">
            <%--<input type="text" id="mode" name="mode" value="${ftClientStatus.mode}" />--%>
            <form:select path="mode" class="input-medium" id="mode">
                <%--<form:options items="${ftClientStatus.mode}" />--%>
                <form:option value="auto" />
                <form:option value="manual" />
            </form:select>
        </div>
    </div>
    <div class="control-group" style="display: none;">
        <label class="control-label">状态：</label>
        <div class="controls">
            <%--<input type="text" id="status" name="status" value="${ftClientStatus.status}" />--%>
            <form:select path="status" class="input-medium" id="status">
                <%--<form:options items="${ftClientStatus.status}" />--%>
                <form:option value="running" />
                <form:option value="stopped" />
            </form:select>
        </div>
    </div>
    <div class="form-actions">
            <%--<shiro:hasPermission name="serviceinfo:ftServiceImports:edit">--%>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
            <%--</shiro:hasPermission>--%>
    </div>
</form:form>
<script type="text/javascript">
    $(document).ready(function(){
        console.log("ready");
        $("#type").children("option").each(function(){
           if($(this).val()=="${ftClientStatus.type}"){
               $(this).attr("selected","selected");
           }
        });
        $("#mode").children("option").each(function(){
            if($(this).val()=="${ftClientStatus.mode}"){
                $(this).attr("selected","selected");
            }
        });
        $("#status").children("option").each(function(){
            if($(this).val()=="${ftClientStatus.status}"){
                $(this).attr("selected","selected");
            }
        });

    });

</script>
</body>
</html>