<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>系统映射</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<form:form id="inputForm" action="${ctx}/sysInfo/vsysmap/save" method="post" class="form-horizontal">
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <input name="key" type="text" value="${vsysmap.key}"/>
        <input name="val" type="text" value="${vsysmap.val}"/>
    </table>
    <div class="form-actions">
        <shiro:hasPermission name="sysInfo:ftSysInfo:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>