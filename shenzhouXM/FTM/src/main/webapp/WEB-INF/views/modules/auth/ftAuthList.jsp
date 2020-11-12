<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>目录权限管理</title>
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

<%--修改列表--%>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/auth/ftAuth/">目录权限管理</a></li>
    <shiro:hasPermission name="user:ftUser:dirList:edit">
        <li><a href="${ctx}/auth/ftAuth/addPage">目录权限添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="auth:ftAuth:edit">
        <a href="${ctx}/auth/ftAuth/addPage">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftAuth" action="${ctx}/auth/ftAuth/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>目录名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
        </li>
        <li><label>用户名：</label>
            <form:input path="permession" htmlEscape="false" maxlength="256" class="input-medium"/>
        </li>
        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
            <%--<li class="clearfix"></li>--%>
    </ul>
</form:form>
<sys:message content="${message}"/>

<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th width="40%">目录名称</th>
        <%--<th>所属系统</th>--%>
        <th width="20%">用户名</th>
        <th width="20%">权限</th>
        <shiro:hasPermission name="auth:ftAuth:edit">
            <th width="20%">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="auth" varStatus="loop">
        <tr>
            <td  style="WORD-WRAP:break-word"><%--<a href="${ctx}/auth/ftAuth/form?id=${ftAuth.id}&name=${ftAuth.name}">--%>
                    ${auth.name}
                <%--</a>--%></td>
            <%--<td>
                    ${auth.sysname}
            </td>--%>
            <td  style="WORD-WRAP:break-word">
                    ${auth.user}
            </td>
            <td>
                    ${auth.permType=="A"?"全部":""}
                    ${auth.permType=="R"?"只读":""}
                    ${auth.permType=="W"?"写入":""}
            </td>
            <shiro:hasPermission name="auth:ftAuth:edit">
                <td>
                        <%--<a href="${ctx}/auth/ftAuth/form?id=${ftAuth.id}&name=${ftAuth.name}">修改</a>--%>
                    <a href="${ctx}/auth/ftAuth/delete?name=${auth.user}&path=${auth.name}&permession=${auth.permType}"
                       onclick="return confirmx('确认要删除该目录权限吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>