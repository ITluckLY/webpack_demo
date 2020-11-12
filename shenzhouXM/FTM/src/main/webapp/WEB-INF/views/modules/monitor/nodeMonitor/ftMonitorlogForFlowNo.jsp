<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>

<html>
<head>
    <title>流水号查询</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        td {
            width: 20%;
            WORD-BREAK: break-all;
            WORD-WRAP:break-word;
            white-space:nowrap;
            overflow:hidden;
            text-overflow: ellipsis;
        }
    </style>
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
        function showAll(a) {
            a.style.overflow = "visible";
            a.style.whiteSpace = "inherit";
        }
    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="bizFileFlowNoLog" action="${ctx}/monitor/FtNodeMonitor/monitorlogForFlow" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>流水号：</label>
    <form:input path="flowNo" htmlEscape="false" style="width:150px" class="input-medium"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
    </form:select>
    <label>用户名称：</label>
    <form:select id="uname" path="uname" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftUserInfoList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
    </form:select>
    <br><br>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:99%;table-layout:fixed;word-wrap:break-word;">
    <thead>
    <tr>
        <th width="3%">编号</th>
        <th>来源</th>
        <th width="15%">流水号</th>
        <th width="5%">交易码</th>
        <th width="4%">用户名称</th>
        <th>客户端IP</th>
        <th width="4%">节点名</th>
        <th width="15%">文件名</th>
        <th width="10%">开始时间</th>
        <th width="10%">结束时间</th>
        <th width="6%">错误信息</th>
        <th width="5%">操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="bizFileFlowNoLogTemp">
        <tr>
            <td>${bizFileFlowNoLogTemp.id}</td>
            <td>${bizFileFlowNoLogTemp.status}</td>
            <td>${bizFileFlowNoLogTemp.flowNo}</td>
            <td>${bizFileFlowNoLogTemp.tranCode}</td>
            <td>${bizFileFlowNoLogTemp.uname}</td>
            <td>${bizFileFlowNoLogTemp.clientIp}</td>
            <td>${bizFileFlowNoLogTemp.nodeName}</td>
            <td onclick="showAll(this);">${bizFileFlowNoLogTemp.fileName}</td>
            <td><fmt:formatDate value="${bizFileFlowNoLogTemp.startTime}" type="both"/></td>
            <td><fmt:formatDate value="${bizFileFlowNoLogTemp.endTime}" type="both"/></td>
            <td>${bizFileFlowNoLogTemp.errCode}</td>
            <td></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>