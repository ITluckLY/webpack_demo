<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>系统管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/sysInfo/vsysmap/list/">系统映射</a></li>
    <shiro:hasPermission name="sysInfo:ftSysInfo:edit">
        <li><a href="${ctx}/sysInfo/vsysmap/addPage">系统映射添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit">
        <a href="${ctx}/sysInfo/vsysmap/addPage">系统映射添加</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="" action="" method="post" class="breadcrumb form-search marfl-new">
    <ul class="ul-form">
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th width="25%">虚拟系统名称</th>
        <th width="25%">真实系统名称</th>
        <shiro:hasPermission name="sysInfo:ftSysInfo:edit">
            <th width="8%">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="maplist">
        <tr>
            <td>
                ${maplist.key}
            </td>
            <td>
                ${maplist.val}
            </td>
            <shiro:hasPermission name="sysInfo:ftSysInfo:edit">
                <td>
                    <a href="${ctx}/sysInfo/vsysmap/delete?key=${maplist.key}&val=${maplist.val}"
                       onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>