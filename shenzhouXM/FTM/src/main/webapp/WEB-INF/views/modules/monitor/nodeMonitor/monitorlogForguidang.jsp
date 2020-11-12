<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>文件归档记录</title>
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
<form:form id="searchForm" modelAttribute="archive" action="${ctx}/monitor/FtNodeMonitor/monitorlogForguidang" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${trancodeList}" htmlEscape="false"/>
     </form:select>
    <label>用户名称：</label>
    <form:select id="user" path="user" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${userNameList}" htmlEscape="false"/>
    </form:select>
    <label>归档成功标识：</label>
   <form:select id="uploadFlag" path="uploadFlag" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:option value="Y" label="true" />
        <form:option value="N" label="false"/>
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
        <th>编号</th>
        <th>交易码</th>
        <th>用户名称</th>
        <th>客户端IP</th>
        <th>归档开始时间</th>
        <th>归档结束时间</th>
        <th>归档成功标识</th>
        <th>客户端文件名</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="archiveTemp">
        <tr>
            <td>${archiveTemp.id}</td>
            <td>${archiveTemp.tranCode}</td>
            <td>${archiveTemp.user}</td>
            <td>${archiveTemp.clientIp}</td>
            <td>${archiveTemp.uploadStartTime}</td>
            <td>${archiveTemp.uploadEndTime}</td>
            <td>${archiveTemp.uploadFlag}</td>
            <td>${archiveTemp.clientFileName}</td>
            <%--<td><fmt:formatDate value="${archiveTemp.startTime}" type="both"/></td>--%>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>