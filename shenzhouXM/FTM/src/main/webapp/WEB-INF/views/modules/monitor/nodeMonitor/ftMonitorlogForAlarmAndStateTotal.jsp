<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>节点告警记录汇总</title>
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
<form:form id="searchForm" modelAttribute="ftNodeAlarmLogMonitor" action="${ctx}/monitor/FtNodeMonitor/monitorlogForAlarmAndStateTotal" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<div><a><h5>&nbsp;节点告警统计</h5></a></div>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;">
    <thead>
    <tr>
        <%--<th>告警次数</th>--%>
        <%--<th>一般告警总数</th>--%>
        <%--<th>严重告警总数</th>--%>
        <th>告警节点</th>
        <th>告警次数</th>

    </tr>
    </thead>
    <tbody>
    <%--<tr>
      <td>${totalListForAlarmLog}</td>
        &lt;%&ndash;<td>${totalListForAlarmLogComm}</td>&ndash;%&gt;
         &lt;%&ndash;<td>${totalListForAlarmLogSeri}</td>&ndash;%&gt;
        <td>${alarmLogNodeName}</td>
    </tr>--%>
    <c:forEach items="${page.list}" var="alarmOftenRecordList">
        <tr>
            <td>${alarmOftenRecordList.nodeName}</td>
            <td>${alarmOftenRecordList.nodeCount}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<hr/>
<%-- <div><a><h5>&nbsp;节点心跳统计</h5></a></div>
 <table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;">
     <thead>
     <tr>
         <th>节点心跳总数</th>
         <th>心跳频繁节点名</th>
         <th>节点类型</th>
         <th>访问主机名</th>
         <th>访问主机地址</th>

     </tr>
     </thead>
     <tbody>
     <tr>
         <td>${totalListForStateLog}</td>
         <td>${stateLogNodeName}</td>
         <td>${stateLogNodeType}</td>
         <td>${stateLogHostName}</td>
         <td>${stateLogHostAddress}</td>

     </tr>
     </tbody>
 </table>
 <hr/>--%>

<div class="pagination">${page}</div>
</body>
</html>