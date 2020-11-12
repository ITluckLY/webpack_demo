<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>

<html>
<head>
    <title>文件分发记录</title>
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
<form:form id="searchForm" modelAttribute="ftNodeMonitorDistribute" action="${ctx}/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForDistribute" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
    </form:select>
    <label>节点组名称：</label>
    <form:select id="sysnamels" path="sysnamels" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${systemNameList}" htmlEscape="false"/>
    </form:select>
    <label>节点名称：</label>
    <form:select id="nodeNamels" path="nodeNamels" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${nodeNameList}" htmlEscape="false"/>
    </form:select>
    <label>状态：</label>
    <form:select id="state" path="state" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:option value="0" label="未进行分发"/>
        <form:option value="1" label="分发成功"/>
        <form:option value="-1" label="分发失败"/>
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
        <th>文件名</th>
        <th>节点名</th>
        <th>节点组名</th>
        <th>创建时间</th>
        <th>修改时间</th>
        <th>状态</th>
        <th>操作</th>
        <%--<shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><th>操作</th></shiro:hasPermission>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="bizFileDistributeLogTemp">
        <tr>
            <td>${bizFileDistributeLogTemp.id}</td>
            <td>${bizFileDistributeLogTemp.tranCode}</td>
            <td>${bizFileDistributeLogTemp.fileName}</td>
            <td>${bizFileDistributeLogTemp.nodeNamels}</td>
            <td>${bizFileDistributeLogTemp.sysnamels}</td>
            <td><fmt:formatDate value="${bizFileDistributeLogTemp.createdTime}" type="both"/></td>
            <td><fmt:formatDate value="${bizFileDistributeLogTemp.modifiedTime}" type="both"/></td>
            <td>
                <c:if test="${bizFileDistributeLogTemp.state==1}" >分发成功</c:if>
                <c:if test="${bizFileDistributeLogTemp.state==0}" >未进行分发</c:if>
                <c:if test="${bizFileDistributeLogTemp.state==-1}" >分发失败</c:if>
            </td>
            <td>
                <a href="${ctx}/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForDistributeOne?id=${bizFileDistributeLogTemp.id}">详情</a>
            </td>
           <%-- <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><td>
                <a href="${ctx}/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForDistributeOne?id=${bizFileDistributeLogTemp.id}">详情</a>
            </td></shiro:hasPermission>--%>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>