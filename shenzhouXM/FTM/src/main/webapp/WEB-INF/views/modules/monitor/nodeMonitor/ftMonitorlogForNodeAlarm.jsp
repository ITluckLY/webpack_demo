<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>节点告警记录</title>
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
<form:form id="searchForm" modelAttribute="ftNodeAlarmLogMonitor" action="${ctx}/monitor/FtNodeMonitor/monitorLogForNodeAlarm" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>节点名：</label>
    <form:select id="nodeNameTemp" path="nodeNameTemp" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${nodeNameList}" htmlEscape="false"/>
    </form:select>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    &nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <%--<th>编号</th>--%>
        <th>监控时间</th>
        <th>节点名</th>
        <th>监控信息</th>
        <th>创建时间</th>
        <th>消息类型</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="alarmLog">
        <tr>
                <%--<td>${alarmLog.id}</td>--%>
            <td><fmt:formatDate value="${alarmLog.time}" type="both"/></td>
            <td>${alarmLog.nodeNameTemp}</td>
            <td>${alarmLog.message}</td>
            <td><fmt:formatDate value="${alarmLog.createTime}" type="both"/></td>
            <td>${alarmLog.msgType}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>