<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>

<html>
<head>
    <title>异常监控</title>
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
<form:form id="searchForm" modelAttribute="clientMonitorLog" action="${ctx}/client/ClientMonitorForFail/failState" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>流水号：</label>
    <form:select id="flowNo" path="flowNo" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="flow" itemValue="flow" htmlEscape="false"/>
    </form:select>
    <label>文件名：</label>
    <form:input path="fileName" htmlEscape="false" style="width:60px" class="input-medium"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
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
        <th>交易码</th>
        <th>流水号</th>
        <th>上传用户</th>
        <th>下载用户</th>
        <th>文件名</th>
        <th>上传创建时间</th>
        <th>下载创建时间</th>
        <th>错误信息</th>
        <th>状态</th>
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
            <td>${bizFilePushLogTemp.errMsg}</td>
            <td>${bizFilePushLogTemp.state}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>