<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>定时任务</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            resetTip();
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function edit(id) {
            var formObj = $("#" + id);
            formObj.attr("action", "${ctx}/timingtask/ftTimingTask/form");
            formObj.submit();
        }
        function startStop(sta) {
            var idsArr = document.getElementsByName("checkbox");
            var dnsArr = document.getElementsByName("dataNodeName");
            var choose = false;
            var ids = "";
            var dns = "";
            var temp = [];
            var z = 1;
            for (var i = 0; i < idsArr.length; i++) {
                if (idsArr[i].checked) {
//                    alert(idsArr[i].value+"已选");
                    ids = ids + idsArr[i].value + ",";
                    dns = dns + dnsArr[i].value + ",";
                    choose = true;

                    var trVal = idsArr[i].parentNode.parentNode;
                    var tdList = trVal.children;

                    for (var j = 1; j < tdList.length; j++) {
                        if (tdList[j].localName == "td") {
                            temp[z] = tdList[j].innerHTML.trim();
                            z = z + 1;
                        }
                    }

                }
            }
            var state = temp[6];
            if(state == "运行"){
                state="start";
            }else if(state == "停止"){
                state="stop";
            }
            if(sta != state){
                if (sta == 'synNodeConf'){
                    location.href = "${ctx}/timingtask/ftTimingTask/synNodeConf?ids=" + ids;
                } else {
                    if (choose) {
                        if (sta == 'start') {
                            location.href = "${ctx}/timingtask/ftTimingTask/start?ids=" + ids;
                        } else if (sta == 'stop') {
                            location.href = "${ctx}/timingtask/ftTimingTask/stop?ids=" + ids + "&dataNodeNames=" + dns;
                        }
                    } else {
                        showTip("未选中任何记录");
                    }
                }
            }else{
                showTip("请勿重复操作!");
            }

        }
    </script>
    <style>
        .form-search .ul-form li label{width: auto}
    </style>
</head>
<body>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/timingtask/ftTimingTask/">定时任务列表</a></li>
    <shiro:hasPermission name="timingtask:ftTimingTask:edit">
        <li><a href="${ctx}/timingtask/ftTimingTask/addPage">定时任务添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="timingtask:ftTimingTask:view">
        <a href="${ctx}/timingtask/ftTimingTask/form">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftTimingTask" action="${ctx}/timingtask/ftTimingTask/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>节点：</label>
            <form:input path="nodeNameTemp" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>任务：</label>
            <form:input path="flowId" htmlEscape="false" maxlength="20" class="input-medium"/>
        </li>
        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
        <shiro:hasPermission name="timingtask:ftTimingTask:edit">
        <li class="btns">
            <button id="btnSynNodeConf" class="btn btn-primary" type="button" onclick="startStop('synNodeConf')"><i
                    class="icon-play">&nbsp;&nbsp;配置同步</i></button>
        </li>
        <li class="btns">
            <button id="btnStart" class="btn btn-primary" type="button" onclick="startStop('start')"><i
                    class="icon-play">&nbsp;&nbsp;启动</i></button>
        </li>
        <li class="btns">
            <button id="btnStop" class="btn btn-primary" type="button" onclick="startStop('stop')"><i class="icon-stop">
                &nbsp;&nbsp;停止</i></button>
        </li>
        </shiro:hasPermission>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <th WIDTH="4%">选择</th>
        <%--<th>编号</th>--%>
        <th>任务名称</th>
        <th>节点</th>
        <th>时间表达式</th>
        <th>任务</th>
        <th>参数</th>
        <th>状态</th>
        <th>最大执行次数</th>
        <th>备注</th>
        <%--<th>执行情况</th>--%>
        <shiro:hasPermission name="timingtask:ftTimingTask:edit">
            <th width="8%">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftTimingTask">
        <tr>
            <td>
                <input name="checkbox" type="checkbox" value="${ftTimingTask.seq}"/>
                <input name="dataNodeName" type="hidden" value="${ftTimingTask.nodeNameTemp}"/>
            </td>
                <%--<a href="${ctx}/timingtask/ftTimingTask/form?id=${ftTimingTask.id}">--%>
           <%-- <td><a onclick="edit('form_${ftTimingTaskTemp.seq}')">
                    ${ftTimingTaskTemp.seq}
            </a>
            </td>--%>
            <td>
                    ${ftTimingTask.taskName}
            </td>
            <td>${ftTimingTask.nodeNameTemp}
            </td>
            <td>
                    ${ftTimingTask.timeExp}
            </td>
            <td>
                    ${ftTimingTask.flowId}
            </td>
            <td>
                    ${ftTimingTask.params}
            </td>
            <td>
                    ${ftTimingTask.state=="1"?"运行":""}
                    ${ftTimingTask.state=="0"?"停止":""}
            </td>
            <td>
                    ${ftTimingTask.count}
            </td>
            <td>
                    ${ftTimingTask.description}
            </td>
            <%--<td>--%>
                    <%--&lt;%&ndash;<a href="${ctx}/timingtask/ftTimingTask/detail?id=${ftTimingTask.id}">点击查看执行情况</a>&ndash;%&gt;--%>
                <%--<a onclick="edit('form_${ftTimingTask.id}')">点击查看执行情况</a>--%>
            <%--</td>--%>
            <shiro:hasPermission name="timingtask:ftTimingTask:edit">
                <td>
                    <form id="form_${ftTimingTask.seq}" method="post">
                        <input name="id" value="${ftTimingTask.seq}" type="hidden">
                        <input name="seq" value="${ftTimingTask.seq}" type="hidden">
                        <input name="taskName" value="${ftTimingTask.taskName}" type="hidden">
                        <input name="timeExp" value="${ftTimingTask.timeExp}" type="hidden">
                        <input name="state" value="${ftTimingTask.state}" type="hidden">
                        <input name="nodeNameTemp" value="${ftTimingTask.nodeNameTemp}" type="hidden">
                        <input name="flowId" value="${ftTimingTask.flowId}" type="hidden">
                        <input name="params" value="${ftTimingTask.params}" type="hidden">
                        <input name="remarks" value="${ftTimingTask.remarks}" type="hidden">
                        <a href="${ctx}/timingtask/ftTimingTask/editForm?id=${ftTimingTask.seq}&seq=${ftTimingTask.seq}">修改</a>
                        <a href="${ctx}/timingtask/ftTimingTask/delete?id=${ftTimingTask.seq}&seq=${ftTimingTask.seq}"
                           onclick="return confirmx('确认要删除该定时任务吗？', this.href)">删除</a>

                    </form>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>