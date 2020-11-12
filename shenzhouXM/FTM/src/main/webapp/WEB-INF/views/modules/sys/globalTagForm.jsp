<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>添加标签</title>
    <meta name="decorator" content="default"/>
    <link href="${ctxStatic}/bootstrap/2.3.1/css_default/bootstrap.min.css" rel="stylesheet"/>
    <script href="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript" ></script>
    <script type="text/javascript">
        $(function(){

        }) ;

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sys/tagsetting/list" >标签管理</a></li>
    <li class="active"><a href="${ctx}/sys/tagsetting/addTag">新增标签</a></li>
    <li><a href="${ctx}/sys/tagsetting/setGlobalTag">设置全局标签</a></li>
</ul>
<div>
    <form:form id="searchForm" modelAttribute="optTag" action="${ctx}/sys/tagsetting/add" method="post"
               class="breadcrumb form-search marfl-new">

        <sys:message content="${message}"/>

        <div class="control-group">
            <label class="control-label">标签名称：</label>
            <div class="controls">
                <input type="text" id="name" name="name" required="true"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">所属类型：</label>
            <div class="controls">
                <form:select path="dictId" class="input-xlarge" style="WIDTH:220PX" required="true">
                    <form:options items="${dicts}" itemValue="id" itemLabel="label" htmlEscape="false"/>
                </form:select>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">备注：</label>
            <div class="controls">
                <div class="controls">
                    <input type="text" id="remark" name="remark"/>
                </div>
            </div>
        </div>

        <div class="form-actions">
            <shiro:hasPermission name="serviceinfo:ftServiceImports:edit">
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
            </shiro:hasPermission>
        </div>

    </form:form>
</div>

</body>
</html>