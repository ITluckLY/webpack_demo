<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>节点心跳记录</title>
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
<form:form id="searchForm" modelAttribute="ftNodeStateLogMonitor"
           action="${ctx}/monitor/FtNodeMonitor/monitorLogForNodeState" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>节点名称：</label>
    <form:select id="nodeNameTemp" path="nodeNameTemp" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${nodeNameList}" htmlEscape="false"/>
    </form:select>
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th>编号</th>
        <th>节点名</th>
        <th>节点类型</th>
        <th>节点组名</th>
        <%--<th>主机名</th>--%>
        <th>主机地址</th>
        <th>端口信息</th>
        <th>时间</th>
        <th>启停状态</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="satateLog">
        <tr>
            <td>${satateLog.id}</td>
            <td>${satateLog.nodeNameTemp}</td>
            <td>${satateLog.nodeType}</td>
            <td>${satateLog.sysName}</td>
            <%--<td>${satateLog.hostName}</td>--%>
            <td>${satateLog.hostAddress}</td>
            <td>${satateLog.portInfo}</td>
            <td><fmt:formatDate value="${satateLog.sendTime}" type="both"/></td>
            <td>
                <c:choose>
                    <c:when test="${satateLog.state == '1'}">
                        启动
                    </c:when>
                    <c:otherwise>
                        停止
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>