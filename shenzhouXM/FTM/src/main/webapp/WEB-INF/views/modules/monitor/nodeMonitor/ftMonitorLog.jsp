<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>节点监控日志</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n, s) {
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
<form:form id="searchForm" modelAttribute="ftNodeMonitorLog" action="${ctx}/monitor/FtNodeMonitor/monitorlog"
           method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>节点组名称：</label>
    <form:select id="system" path="system" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${systemNameList}" htmlEscape="false"/>
    </form:select>
    <label>节点名称：</label>
    <form:select id="node" path="node" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${nodeNameList}" htmlEscape="false"/>
    </form:select>
    <br><br>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <li class="btns">
        <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button>
    </li>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <%--<th>编号</th>--%>
        <th>采集时间</th>
        <th>监控时间</th>
        <th>节点组</th>
        <th>节点名</th>
        <th>IP地址</th>
        <%--<th>状态</th>--%>
        <%--<th>分类</th>--%>
        <%--<th>周期</th>--%>
        <th>文件数</th>
        <th>存储</th>
        <th>CPU</th>
        <%--<th>硬盘</th>--%>
        <th>内存</th>
        <th>流量</th>
        <%--<th>网络</th>--%>
        <%--<th>端口</th>--%>
        <%--<th>备注</th>--%>
        <%--<th>创建者</th>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="monitorLogTemp">
        <tr>
                <%--<td>${monitorLogTemp.id}</td>--%>
            <td>${monitorLogTemp.time}</td>
            <td><fmt:formatDate value="${monitorLogTemp.createDate}" type="both"/></td>
            <td>${monitorLogTemp.system}</td>
            <td>${monitorLogTemp.node}</td>
            <td>${monitorLogTemp.ip}</td>
                <%--<td>${monitorLogTemp.state}</td>--%>
                <%--<td>${monitorLogTemp.catalog}</td>--%>
                <%--<td>${monitorLogTemp.periods}</td>--%>
            <td>${monitorLogTemp.filenumber}</td>
            <td>${monitorLogTemp.storage}</td>
            <td>${monitorLogTemp.cpu}</td>
                <%-- <td>${monitorLogTemp.disk}</td>--%>
            <td>${monitorLogTemp.memory}</td>
            <td>${monitorLogTemp.flowrate}</td>
                <%--<td>${monitorLogTemp.network}</td>--%>
                <%--<td>${monitorLogTemp.port}</td>--%>
                <%--<td>${monitorLogTemp.remarks}</td>--%>
                <%--<td>${monitorLogTemp.createBy}</td>--%>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>