<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>交易码告警配置</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function del(trancode){
            if(confirm("确定删除?")){
                $.ajax({
                    url:"${ctx}/monitor/FtNodeMonitor/delTranCodeAlarmLine",
                    type:"post",
                    dataType:"text",
                    data:{"tranCode":trancode},
                    success:function (data) {
                        if(data=="true"){
                            alert("删除成功!");
                            var id = "#"+trancode;
                            $(id).remove();
                        }else{
                            alert("删除失败!");
                        }
                    }
                });
            }
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="#">交易码告警阈值</a></li>
    <shiro:hasPermission name="monitor:ftTranCodeAlarmLine:edit"><li><a href="${ctx}/monitor/FtNodeMonitor/addTranCodeAlarmLine?tranCode=zero">添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="ftTranCodeAlarmLine" action="${ctx}/monitor/FtNodeMonitor/tranCodeAlarmLineList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>交易码名称：</label>
            <form:input path="tranCode" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>交易码</th>
        <th>生产方</th>
        <th>消费方</th>
        <th>超时时间(min)</th>
        <shiro:hasPermission name="monitor:ftTranCodeAlarmLine:edit"><th>操作</th></shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftTranCodeAlarmLine">
        <tr id="${ftTranCodeAlarmLine.tranCode}">
            <td>
                    ${ftTranCodeAlarmLine.tranCode}
            </td>

            <td>
                    ${ftTranCodeAlarmLine.putUser}
            </td>
            <td>
                    ${ftTranCodeAlarmLine.getUser}
            </td>
            <td>
                    ${ftTranCodeAlarmLine.timeout}
            </td>
            <shiro:hasPermission name="monitor:ftTranCodeAlarmLine:edit">
                <td>
                    <a href="${ctx}/monitor/FtNodeMonitor/addTranCodeAlarmLine?tranCode=${ftTranCodeAlarmLine.tranCode}">修改</a>
                    <a href="javascript:del('${ftTranCodeAlarmLine.tranCode}')">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>

</html>