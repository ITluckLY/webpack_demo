<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>IP控制管理</title>
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
    <li class="active"><a href="${ctx}/ipconfig/ftUserIp/">IP控制列表</a></li>
    <shiro:hasPermission name="ipconfig:ftUserIp:edit">
        <li><a href="${ctx}/ipconfig/ftUserIp/form">IP控制添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="ipconfig:ftUserIp:view">
        <a href="${ctx}/ipconfig/ftUserIp/form">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftUserIp" action="${ctx}/ipconfig/ftUserIp/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>IP地址：</label>
            <form:input path="ipAddress" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>用户名称：</label>
            <form:input path="ftUserId" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>允许状态：</label>
            <form:select path="state" htmlEscape="false" maxlength="50" class="input-medium">
                <form:option value="">——请选择——</form:option>
                <form:option value="1">允许</form:option>
                <form:option value="0">禁止</form:option>
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
        <th width="25%">IP地址</th>
        <th width="15%">用户名称</th>
        <th width="15%">状态</th>
        <th width="35%">描述</th>
        <shiro:hasPermission name="ipconfig:ftUserIp:edit">
            <th width="10%">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftUserIp">
        <tr>
            <td style="WORD-WRAP:break-word">
                <a href="${ctx}/ipconfig/ftUserIp/editForm?id=${ftUserIp.id}&ftUserId=${ftUserIp.ftUserId}&ipAddress=${ftUserIp.ipAddress}">
                        ${ftUserIp.ipAddress}
                </a>
            </td>
            <td style="WORD-WRAP:break-word">
                    ${ftUserIp.ftUserId}
            </td>
            <td style="WORD-WRAP:break-word">
                    ${ftUserIp.state=="allowed"?"允许":""}
                    ${ftUserIp.state=="1"?"允许":""}
                    ${ftUserIp.state=="true"?"允许":""}
                    ${ftUserIp.state=="forbidden"?"禁止":""}
                    ${ftUserIp.state=="0"?"禁止":""}
                    ${ftUserIp.state=="false"?"禁止":""}
            </td>
            <td style="WORD-WRAP:break-word">
                    ${ftUserIp.des}
            </td>
            <shiro:hasPermission name="ipconfig:ftUserIp:edit">
                <td style="WORD-WRAP:break-word">
                    <a href="${ctx}/ipconfig/ftUserIp/editForm?ftUserId=${ftUserIp.ftUserId}&ipAddress=${ftUserIp.ipAddress}&systemName=${ftUserIp.systemName}">修改</a>
                    <a href="${ctx}/ipconfig/ftUserIp/delete?ftUserId=${ftUserIp.ftUserId}&ipAddress=${ftUserIp.ipAddress}&systemName=${ftUserIp.systemName}"
                       onclick="return confirmx('确认要删除该IP控制吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>