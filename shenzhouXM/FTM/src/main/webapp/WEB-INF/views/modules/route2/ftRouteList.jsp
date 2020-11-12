<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>交易码路由管理</title>
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
    <li class="active"><a href="${ctx}/route/ftRoute/">路由管理</a></li>
    <shiro:hasPermission name="route:ftRoute:edit">
        <li><a href="${ctx}/route/ftRoute/addPage">路由管理添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="route:ftRoute:view">
        <a href="${ctx}/route/ftRoute/addPage">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftRoute" action="${ctx}/route/ftRoute/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>交易码：</label>
            <form:input path="tran_code" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>用户名称：</label>
            <form:input path="user" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>

        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>交易码</th>
        <th>(上传)用户名称</th>
        <%--<th>类型</th>--%>
        <th>模式</th>
        <th>路由目标</th>
        <shiro:hasPermission name="route:ftRoute:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftRoute">
        <tr>
            <td><a href="${ctx}/route/ftRoute/form?id=${ftRoute.id}&tran_code=${ftRoute.tran_code}&user=${ftRoute.user}">
                    ${ftRoute.tran_code}
            </a>
            </td>
            <td>
                    ${ftRoute.user}
            </td>
                <%--<td>
                        ${ftRoute.type}
                </td>--%>
            <td>
                    ${ftRoute.mode=="syn"?"同步":""}
                    ${ftRoute.mode=="asyn"?"异步":""}

            </td>
            <td>
                    ${ftRoute.destination}
            </td>
            <shiro:hasPermission name="route:ftRoute:edit">
                <td>
                    <a href="${ctx}/route/ftRoute/form?id=${ftRoute.id}&tran_code=${ftRoute.tran_code}&user=${ftRoute.user}">修改</a>
                    <a href="${ctx}/route/ftRoute/delete?id=${ftRoute.id}&tran_code=${ftRoute.tran_code}&user=${ftRoute.user}"
                       onclick="return confirmx('确认要删除该路由管理吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>