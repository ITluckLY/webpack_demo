<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>交易场景记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        $(function () {
            $("#startTime").click(function () {
                WdatePicker({
                    skin: "twoer",
                    maxDate: ($("#endTime").val() ? $("#endTime").val() : new Date()),
                    format: "yyyy-MM-dd"
                });
            });

            var date = new Date();
            date.setDate(date.getDate() + 1);
            $("#endTime").click(function () {
                WdatePicker({
                    skin: "twoer",
                    minDate: $("#startTime").val(),
                    maxDate: date,
                    format: "yyyy-MM-dd"
                });
            });
        });


    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="tradeScenario" action="${ctx}/monitor/TradeScenario/trade" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

    <label>生产者：</label>
    <form:input path="fromUid" htmlEscape="false" style="width:80px" class="input-medium"/>

    <label>消费者：</label>
    <form:input path="toUid" htmlEscape="false" style="width:80px" class="input-medium"/>

    <label>开始时间：</label>

    <input class="Wdate" type="text" name="startTime" id="startTime" value="${startTime}">
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endTime" id="endTime" value="${endTime}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" />


</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed"
       style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>

        <th width="10%">生产者</th>
        <th width="10%">消费者</th>
        <th width="10%">交易场景总数</th>
        <th width="20%">交易码列表</th>
        <%--<th width="10%">成功交易场景数</th>--%>
        <%--<th width="10%">失败交易场景数</th>--%>
        <th width="20%">成功交易笔数信息</th>
        <th width="20%">失败交易笔数信息</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="tradeScenario">
        <tr>
            <td style="WORD-WRAP:break-word">
                    ${tradeScenario.fromUid}
            <td>
                    ${tradeScenario.toUid}
            </td>
            <td>
                    ${tradeScenario.tradeScenarioTotal}
            </td>
            <td>
                    ${tradeScenario.tranCode}
            </td>
            <td style="width:30%;">
                    ${tradeScenario.tradeScenarioPassMess}
            </td>
            <td style="width:30%;">
                    ${tradeScenario.tradeScenarioFailedMess}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>