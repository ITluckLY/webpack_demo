<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>传输监控日志</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="ftTransforMonitor" action="${ctx}/monitor/FtTransforMonitor/" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>节点组名称：</label>
            <form:input path="sysname" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>节点名称：</label>
            <form:input path="nodeName" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>文件名称：</label>
            <form:input path="fileName" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>节点组名称</th>
        <th>节点名称</th>
        <th>传输时间</th>
        <th>文件名称</th>
        <th>传输状态</th>
        <th>文件大小</th>
        <th>客户端IP</th>
        <th>服务器IP</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftTransforMonitor">
        <tr>
            <td>
                    ${ftTransforMonitor.sysname}
            </td>
            <td>
                    ${ftTransforMonitor.nodeName}
            </td>
            <td>
                    ${ftTransforMonitor.createdTime}
            </td>
            <td>
                    ${ftTransforMonitor.fileName}
            </td>
            <td>
                    ${ftTransforMonitor.suss}
            </td>
            <td>
                    ${ftTransforMonitor.contLen}
            </td>
            <td>
                    ${ftTransforMonitor.clientIp}
            </td>
            <td>
                    ${ftTransforMonitor.serverIp}
            </td>

        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>