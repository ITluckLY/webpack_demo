<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文件命名</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });
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
    <li class="active"><a href="${ctx}/file/ftFileRename/">节点列表</a></li>
    <shiro:hasPermission name="servicenode:ftServiceNode:edit">
        <li><a href="${ctx}/file/ftFileRename/form">节点添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="file:ftFileRename:view">
        <a href="${ctx}/file/ftFileRename/form">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftFileRename" action="${ctx}/file/ftFileRename/list" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>路径类型：</label>
            <form:select path="type" htmlEscape="false" maxlength="500" class="input-medium" style="width:150px">
                <form:option value="" label="——请选择——" />
                <form:option value="file" label="文件"/>
                <form:option value="dir" label="目录"/>
            </form:select>
        </li>
        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th>路径类型</th>
        <th>路径名称</th>
        <th>节点组名称</th>
        <shiro:hasPermission name="file:ftFileRename:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftFileRename">
        <tr>
            <td>
                    ${ftFileRename.type}
            </td>
            <td>
                    ${ftFileRename.path}
            </td>
            <td>
                    ${ftFileRename.sysname}
            </td>

            <shiro:hasPermission name="file:ftFileRename:edit">
                <td>
                    <a href="${ctx}/file/ftFileRename/form?id=${ftFileRename.id}">修改</a>
                    <a href="${ctx}/file/ftFileRename/delete?id=${ftFileRename.id}&type=${ftFileRename.type}"
                       onclick="return confirmx('确认要删除该路径吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>