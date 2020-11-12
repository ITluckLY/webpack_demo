<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/9/12
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>文件同步记录跨地域</title>
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
<form:form id="searchForm" modelAttribute="fileSync" action="${ctx}/monitor/FtNodeMonitor/monitorlogForFileSync" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${trancodeList}" htmlEscape="false"/>
    </form:select>
    <label>节点名称：</label>
    <form:select id="NODENAME" path="NODENAME" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${nodeNameList}" htmlEscape="false"/>
    </form:select>
    <label>同步成功标识：</label>
    <form:select id="STATE" path="STATE" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:option value="true" label="true" />
        <form:option value="false" label="false"/>
    </form:select>
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
        <th>文件名称</th>
        <th>文件路径</th>
        <th>节点名称</th>
        <th>交易码</th>
        <th>流水号</th>
        <th>成功标识</th>
        <th>错误信息</th>
        <th>开始时间</th>
        <th>结束时间</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="fileSync">
        <tr>
            <td>${fileSync.fileName}</td>
            <td>${fileSync.realFileName}</td>
            <td>${fileSync.NODENAME}</td>
            <td>${fileSync.tranCode}</td>
            <td>${fileSync.origFlowNo}</td>
            <td>${fileSync.STATE}</td>
            <td>${fileSync.errMsg}</td>
            <td>${fileSync.syncStartTime}</td>
            <td>${fileSync.syncEndTime}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>
