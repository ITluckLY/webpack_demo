<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>基础配置-节点管理-当前系统</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            var mes = '${message}';
            if (mes != null && mes != "") {
                alert(mes);
                parent.location.reload();
            }
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>

<ul class="nav nav-tabs">
</ul>
<form:form id="searchForm" modelAttribute="ftServiceNode" action="${ctx}/servicenode/ftServiceNode/set" method="post"
           class="breadcrumb form-search">

    <ul class="ul-form" id="editSys">
        <c:if test="${not empty ftServiceNode.systemName?'true':'false'}">
            <span style="color: grey">当前节点组：${ftServiceNode.systemName}</span>
        </c:if>
        <c:if test="${empty ftServiceNode.systemName?'true':'false'}">
            <span style="color: grey">当前未选择节点组</span>
        </c:if>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="color: grey">${tagMsg}</span>
    </ul>
    <br/>
    <ul class="ul-form" id="systemId">
        <li><label>节点组：</label>
            <form:select id="sname" path="systemName" class="input-medium" style="WIDTH: 200px">
                <form:option value="">——请选择——</form:option>
                <form:options items="${ftSysInfoList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </li>
            <li class="btns">
                <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-save">&nbsp;&nbsp;设置</i>
                </button>
            </li>
    </ul>
</form:form>
</button>
</body>
</html>