<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>

<html>
<head>
    <title>客户端资源使用历史</title>
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
<form:form id="searchForm" modelAttribute="clientMonitorLog" action="${ctx}/client/ClientMonitor/resource" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>客户端ip：</label>
    <form:input path="fileName" htmlEscape="false" style="width:60px" class="input-medium"/>
    <label>用户名：</label>
    <form:input path="fileName" htmlEscape="false" style="width:60px" class="input-medium"/>
    <br><br>
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
        <th>采集时间</th>
        <th>监控时间</th>
        <th>客户端ip</th>
        <th>用户名</th>
        <th>文件数</th>
        <th>存储</th>
        <th>cpu</th>
        <th>内存</th>
        <th>流量</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="bizFilePushLogTemp">
        <tr>
            <td>${bizFilePushLogTemp.tranCode}</td>
            <td>${bizFilePushLogTemp.flow}</td>
            <td>${bizFilePushLogTemp.fromUid}</td>
            <td>${bizFilePushLogTemp.toUid}</td>
            <td>${bizFilePushLogTemp.fileName}</td>
            <td>${bizFilePushLogTemp.startTime}</td>
            <td>${bizFilePushLogTemp.endTime}</td>
            <td>${bizFilePushLogTemp.startTime}</td>
            <td>${bizFilePushLogTemp.endTime}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>