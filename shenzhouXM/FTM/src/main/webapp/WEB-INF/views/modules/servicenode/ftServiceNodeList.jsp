<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>节点管理</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/servicenode/ftServiceNode/">节点列表</a></li>
    <shiro:hasPermission name="servicenode:ftServiceNode:edit">
        <li><a href="${ctx}/servicenode/ftServiceNode/form">节点添加</a></li>
    </shiro:hasPermission>
</ul>
<%--<%@include file="/WEB-INF/views/include/new.jsp" %>--%>
<form:form id="searchForm" modelAttribute="ftServiceNode" action="${ctx}/servicenode/ftServiceNode/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>节点名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>IP地址：</label>
            <form:input path="ipAddress" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li><label>运行状态：</label>
            <form:select path="state" htmlEscape="false" maxlength="50" class="input-medium">
                <form:option value="">全部</form:option>
                <form:option value="0">未启用</form:option>
                <form:option value="1">运行中</form:option>
            </form:select>
        </li>
        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<div style="margin-left: 20px">
    <shiro:hasPermission name="servicenode:ftServiceNode:view">
        <a href="${ctx}/servicenode/ftServiceNode/form">新增</a>
        <a id="jid_edit" href="javascript:void(0)" onclick="onEditForm()">修改</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="servicenode:ftServiceNode:edit">
        <a id="jid_del" href="javascript:void(0)" onclick="onDel()">删除</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="nodeparam:ftNodeParam:view">
        <a id="jid_nodeParam" href="javascript:void(0)" onclick="nodeParam()">节点参数</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="servicenode:ftServiceNode:view">
    <a id="jid_otherCfg" href="javascript:void(0)" onclick="otherCfg()">配置文件</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="file:ftFile:view">
        <a id="jid_lookFile" href="javascript:void(0)" onclick="lookFile()">数据文件</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="servicenode:ftServiceNode:startStop">
        <a id="jid_startDataNode" href="javascript:void(0)" onclick="startDatanode()">启动</a>
        <a id="jid_stopDataNode" href="javascript:void(0)" onclick="stopDatanode()">停止</a>
    </shiro:hasPermission>
</div>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 100%">
    <thead>
    <tr>
        <th>选择</th>
        <th>节点名称</th>
        <th>节点类型</th>
        <th>节点组</th>
        <th>节点模式(系统)</th>
        <th>节点模式(主备)</th>
        <th>主备切换</th>
        <th>存储模式</th>
        <th>IP地址</th>
        <th>命令端口</th>
        <th>服务端口</th>
        <th>管理端口</th>
        <th>接收端口</th>
        <th>运行状态</th>
        <th>隔离状态</th>
        <th>任务数</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftServiceNode" varStatus="status">
        <tr>
            <td nowrap>
                <label>
                    <input type="radio" name="selRaido" data-name="${ftServiceNode.name}" data-type="${ftServiceNode.type}" data-ip="${ftServiceNode.ipAddress}"
                           data-state="${ftServiceNode.state}" data-cmdport="${ftServiceNode.cmdPort}" data-sysname="${ftServiceNode.systemName}"/>${status.index+1}
                </label>
            </td>
            <td>
                <a href="${ctx}/servicenode/ftServiceNode/form?name=${ftServiceNode.name}&ipAddress=${ftServiceNode.ipAddress}">
                        ${ftServiceNode.name}
                </a>
            </td>
            <td>
                    ${ftServiceNode.type}
            </td>
            <td>
                    ${ftServiceNode.systemName}
            </td>
            <td>
                    ${ftServiceNode.sysNodeModel=="single"?"单节点":""}
                    ${ftServiceNode.sysNodeModel=="more"?"多节点并行":""}
                    ${ftServiceNode.sysNodeModel=="ms"?"主备":""}
            </td>
            <td>
                    ${ftServiceNode.nodeModel=="ms-m"?"主备-主":""}
                    ${ftServiceNode.nodeModel=="ms-s"?"主备-备":""}
            </td>
            <td>
                    ${ftServiceNode.switchModel=="auto"?"自动切换":""}
                    ${ftServiceNode.switchModel=="handle"?"手动切换":""}
            </td>
            <td>
                    ${ftServiceNode.storeModel=="single"?"单点":""}
                    ${ftServiceNode.storeModel=="sync"?"同步":""}
                    ${ftServiceNode.storeModel=="async"?"异步":""}
            </td>
            <td>
                    ${ftServiceNode.ipAddress}
            </td>
            <td>
                    ${ftServiceNode.cmdPort}
            </td>
            <td>
                    ${ftServiceNode.ftpServPort}
            </td>
            <td>
                    ${ftServiceNode.ftpManagePort}
            </td>
            <td>
                    ${ftServiceNode.receivePort}
            </td>

            <td>
                    ${ftServiceNode.state=="0"?"未启用":""}
                    ${ftServiceNode.state=="WAITING"?"未启用":""}
                    ${ftServiceNode.state=="1"?"运行中":""}
                    ${ftServiceNode.state=="RUNNING"?"运行中":""}
            </td>
            <td>
                    ${ftServiceNode.isolState=="0"?"正常":""}
                    ${ftServiceNode.isolState=="WAITING"?"正常":""}
                    ${ftServiceNode.isolState=="1"?"隔离":""}
                    ${ftServiceNode.isolState=="RUNNING"?"隔离":""}
            </td>
            <td>
                    ${ftServiceNode.taskCount}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

<script type="text/javascript">
    var ctx = "${ctx}";
    function page(n, s) {
        $("#pageNo").val(n);
        $("#pageSize").val(s);
        $("#searchForm").submit();
        return false;
    }
    function selectedRadio() {
        return $('#contentTable').find(':radio[name=selRaido]:checked');
    }
    function openWin(url) {
        window.open(url, "_self");
    }
    function onEditForm() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = ctx + "/servicenode/ftServiceNode/form?name=" + sel.data("name") + "&ipAddress=" + sel.data("ip") + "&state=" + sel.data("state");
            openWin(url)
        }
    }
    function onDel() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = ctx + "/servicenode/ftServiceNode/delete?name=" + sel.data("name") + "&ipAddress=" + sel.data("ip") + "&state=" + sel.data("state");
            if (confirm('确认要删除该节点吗？')) openWin(url)
        }
    }

    //not lognode
    function nodeParam() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var type = sel.data('type');
            if (type !== "lognode") {
                var url = ctx + "/nodeparam/ftNodeParam/list?name=" + sel.data("name") + "&ipAddress=" + sel.data("ip") + "&state=" + sel.data("state")
                    + "&type=" + type + "&cmdPort=" + sel.data("cmdport");
                openWin(url)
            }
        }
    }
    //not lognode
    function otherCfg() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var type = sel.data('type');
            if (type !== "lognode") {
                var url = ctx + "/servicenode/ftServiceNode/otherConf?name=" + sel.data("name") + "&ipAddress=" + sel.data("ip") + "&state=" + sel.data("state")
                    + "&type=" + type + "&cmdPort=" + sel.data("cmdport");
                openWin(url)
            }
        }
    }
    //is datanode
    function lookFile() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var type = sel.data('type');
            if (type === "datanode") {
                var url = ctx + "/file/ftFile/index?name=" + sel.data("name") + "&ipAddress=" + sel.data("ip") + "&state=" + sel.data("state")
                    + "&systemName=" + sel.data("sysname") + "&cmdPort=" + sel.data("cmdport");
                openWin(url)
            }
        }
    }

    function startDatanode() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var type = sel.data('type');
            if (type === "datanode") {
                if (confirm('确认要启动该节点吗？')) {
                    /*$.ajax({
                        type: "get",
                        dataType: "json",
                        url: '${ctx}/servicenode/ftServiceNode/startDatanode?name=' + sel.data("name") + '&ipAddress='
                            + sel.data("ip") + '&state=' + sel.data("state") + '&type=' + type + '&cmdPort=' + sel.data("cmdport"),
                        success: function (data) {
                            alert("执行成功");
                            window.location.reload()
                        }
                    })*/
                    var url = '${ctx}/servicenode/ftServiceNode/startDatanode?name=' + sel.data("name") + '&ipAddress='
                    + sel.data("ip") + '&state=' + sel.data("state") + '&type=' + type + '&cmdPort=' + sel.data("cmdport");
                    openWin(url);
                }
            }
        }
    }
    function stopDatanode() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var type = sel.data('type');
            if (type === "datanode") {
                if (confirm('确认要停止该节点吗？')) {
                    /*$.ajax({
                        type: "get",
                        dataType: "json",
                        url: '${ctx}/servicenode/ftServiceNode/stopDatanode?name=' + sel.data("name") + '&ipAddress='
                        + sel.data("ip") + '&state=' + sel.data("state") + '&type=' + type + '&cmdPort=' + sel.data("cmdport"),
                        success: function (data) {
                            alert("执行成功");
                            window.location.reload()
                        }
                    })*/
                    var url = '${ctx}/servicenode/ftServiceNode/stopDatanode?name=' + sel.data("name") + '&ipAddress='
                    + sel.data("ip") + '&state=' + sel.data("state") + '&type=' + type + '&cmdPort=' + sel.data("cmdport");
                    openWin(url);
                }
            }
        }
    }

    $(function () {
        $('#contentTable').find(':radio[name=selRaido]').click(function () {
            var that = $(this);
            if (that.is(":checked")) {
                var type = that.data('type') || "";
                type = type.toLowerCase();
                $('#jid_edit').show();
                $('#jid_del').show();
                if (type === "lognode") $('#jid_nodeParam').hide();
                else $('#jid_nodeParam').show();

                if (type === "lognode") $('#jid_otherCfg').hide();
                else $('#jid_otherCfg').show();

                if (type === "datanode") $('#jid_lookFile').show();
                else $('#jid_lookFile').hide();

                if (type === "datanode") $('#jid_startDataNode').show();
                else $('#jid_startDataNode').hide();

                if (type === "datanode") $('#jid_stopDataNode').show();
                else $('#jid_stopDataNode').hide();
            }
        });
        $('#contentTable').find('tr').each(function () {
            $(this).find("td:gt(1)").click(function () {
                $(this).parent().find(':radio[name=selRaido]').prop("checked", true).click();
            });
        });
    });
</script>
</body>
</html>
