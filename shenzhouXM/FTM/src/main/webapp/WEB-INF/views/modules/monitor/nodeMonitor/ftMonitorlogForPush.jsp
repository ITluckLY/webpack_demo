<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>

<html>
<head>
    <title>文件推送记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function getValue() {
            var str = $("#toUid option:selected").val();
            var options = document.getElementById("routeName").options;
            options.length = 0;
            <c:forEach items="${sysNameList}" var="item">
                var name = "${item.name}";
                if(name.indexOf(str)>=0){
                    options.add(new Option(name,name));
                }
            </c:forEach>


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
<form:form id="searchForm" modelAttribute="ftNodeMonitorPush" action="${ctx}/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForPush" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
    </form:select>
    <label>节点组名称：</label>
    <form:select id="sysname" path="sysname" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${systemNameList}" htmlEscape="false"/>
    </form:select>
    <label>成功标识：</label>
    <form:select id="succ" path="succ" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:option value="1" label="true" />
        <form:option value="0" label="false"/>
    </form:select>
    <br><br>
    <label>发送方：</label>
    <form:select id="fromUid" path="fromUid" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftUserInfoList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
    </form:select>
    <label>接收方：</label>
    <form:select id="toUid" path="toUid" class="input-medium" style="width:150px" onchange="getValue()">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftUserInfoList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
    </form:select>
    <label>路由名称：</label>
    <form:select id="routeName" path="routeName" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${sysNameList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
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
        <th>发送方</th>
        <th>接收方</th>
        <th>路由名称</th>
        <th>IP地址</th>
        <th>端口号</th>
        <th>节点组名</th>
        <%--<th>创建时间</th>--%>
        <th>修改时间</th>
        <th>是否同步</th>
        <th>成功标识</th>
        <th>错误信息</th>
        <th>消息重推次数</th>
        <th>操作</th>
        <%-- <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><th>操作</th></shiro:hasPermission>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="bizFilePushLogTemp">
        <tr>
            <td>${bizFilePushLogTemp.id}</td>
            <td>${bizFilePushLogTemp.tranCode}</td>
            <td>${bizFilePushLogTemp.fromUid}</td>
            <td>${bizFilePushLogTemp.toUid}</td>
            <td>${bizFilePushLogTemp.routeName}</td>
            <td>${bizFilePushLogTemp.ip}</td>
            <td>${bizFilePushLogTemp.port}</td>
            <td>${bizFilePushLogTemp.sysname}</td><%--<td><fmt:formatDate value="${bizFilePushLogTemp.createdTime}" type="both"/></td>--%>
            <td><fmt:formatDate value="${bizFilePushLogTemp.modifiedTime}" type="both"/></td>
            <td>${bizFilePushLogTemp.sync}</td>
            <td>${bizFilePushLogTemp.succ=="0"?false:true}</td>
            <td>${bizFilePushLogTemp.errMsg}</td>
            <td>${bizFilePushLogTemp.repushCount}</td>
            <td>
                <a href="${ctx}/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForPushOne?id=${bizFilePushLogTemp.id}">详情</a>
              <shiro:hasPermission name="NodeMonitor:monitorHistory:repush">
                <a href="${ctx}/monitor/FtNodeMonitor/PushAndDistribute/monitorlogForRePushOne?id=${bizFilePushLogTemp.id}"
                   onclick="return confirmx('重推确认：交易码=${bizFilePushLogTemp.tranCode}，发送方=${bizFilePushLogTemp.fromUid}，接收方=${bizFilePushLogTemp.toUid}', this.href)">重推</a>
              </shiro:hasPermission>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>