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
<ul class="nav nav-tabs">
    <li><a href="${ctx}/client/clientSyn">返回上一页</a></li>
</ul>
<form:form id="searchForm" modelAttribute="clientSyn" action="${ctx}/client/clientSyn/history" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
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
        <th>客户端名称</th>
        <th>客户端</th>
        <th>端口</th>
        <th>连接服务器ip</th>
        <th>升级时间</th>
        <th>启动时间</th>
        <th>停止时间</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="client">
        <tr>
            <td>${client.name}</td>
            <td>${client.ip}</td>
            <td>${client.port}</td>
            <td>${client.connectServer}</td>
            <td><fmt:formatDate value="${client.modifiedTime}" type="both"/></td>
            <td><fmt:formatDate value="${client.startTime}" type="both"/></td>
            <td><fmt:formatDate value="${client.endTime}" type="both"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>