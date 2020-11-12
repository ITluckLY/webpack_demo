<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>客户端版本记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        $(function(){
            $("#beginDate").click(function(){
                WdatePicker({
                    skin:"twoer",
                    maxDate:($("#endDate").val()?$("#endDate").val():new Date()),
                    format:"yyyy-MM-dd"
                });
            });
            var date = new Date();
            date.setDate(date.getDate() + 1);
            $("#endDate").click(function(){
                WdatePicker({
                    skin:"twoer",
                    minDate:$("#beginDate").val(),
                    maxDate:date,
                    format:"yyyy-MM-dd"
                });
            });
        });

    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="clientVersionLog" action="${ctx}/monitor/ClientVersion/clientVersion" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>客户端IP：</label>
    <form:input path="clientIp" htmlEscape="false" style="width:100px" class="input-medium"/>
    <label>用户名：</label>
    <form:input path="uid" htmlEscape="false" style="width:60px" class="input-medium"/>
    <label>客户端版本号：</label>
    <form:input path="clientVersion" htmlEscape="false" style="width:150px" class="input-medium"/>
    <br>
    <br>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>

        <th>客户端IP</th>
        <th>端口</th>
        <th>用户名</th>
        <th>最新版本号</th>
        <th>该版本最近记录时间</th>
        <th>操作</th>
       <%-- <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><th>操作</th></shiro:hasPermission>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="client">
        <tr>
            <td>${client.clientIp}</td>
            <td>${client.port}</td>
            <td>${client.uid}</td>
            <td>${client.clientVersion}</td>
            <td><fmt:formatDate value="${client.createdTime}" type="both"/></td>
            <td><a href="${ctx}/monitor/ClientVersion/clientVersionLog?uid=${client.uid}&clientIp=${client.clientIp}">详情</a></td>
          <%--  <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><td>
                <a href="${ctx}/monitor/ClientVersion/clientVersionLog?uid=${client.uid}&clientIp=${client.clientIp}">详情</a>
            </td></shiro:hasPermission>--%>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>