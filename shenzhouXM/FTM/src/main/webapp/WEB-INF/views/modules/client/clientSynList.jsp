<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>客户端概况</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<form:form id="searchForm" modelAttribute="clientSyn" action="${ctx}/client/clientSyn/list" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>目标名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<div>
    <shiro:hasPermission name="client:clientSyn:view">
        <a href="javascript:void(0)" onclick="otherCfg()">配置文件</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="client:clientSyn:view">
        <a href="javascript:void(0)" onclick="lookFile()" style="margin-left: 60px">数据文件</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="client:clientSyn:view">
        <a href="javascript:void(0)" onclick="history()" style="margin-left: 60px">变更历史</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="client:clientSyn:view">
        <a href="javascript:void(0)" onclick="start()" style="margin-left: 60px">启动</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="client:clientSyn:view">
        <a href="javascript:void(0)" onclick="stop()" style="margin-left: 60px">停止</a>
    </shiro:hasPermission>
</div>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>选择</th>
        <th>客户端</th>
        <th>IP地址</th>
        <th>域名</th>
        <th>端口</th>
        <th>监控端口</th>
        <th>节点组</th>
        <th>重启次数</th>
        <th>运行状态</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="clientSyn" varStatus="status">
        <tr>
            <td nowrap>
                <label>
                    <input type="radio" name="selRaido" data-name="${clientSyn.name}" data-ip="${clientSyn.ip}"
                           data-port="${clientSyn.port}" data-cmdport="${clientSyn.cmdPort}" data-status="${clientSyn.status}"/>${status.index+1}
                </label>
            </td>
            <td>
                ${clientSyn.name}
            </td>
            <td>
                    ${clientSyn.ip}
            </td>
            <td>
                    ${clientSyn.userDomainName}
            </td>
            <td>
                    ${clientSyn.port}
            </td>
            <td>
                    ${clientSyn.cmdPort}
            </td>
            <td>
                    ${clientSyn.sysname}
            </td>
            <td>
                    ${clientSyn.rebootTimes}
            </td>
            <td>
                    ${clientSyn.status}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<script type="text/javascript">

    function page(n, s) {
        $("#pageNo").val(n);
        $("#pageSize").val(s);
        $("#searchForm").submit();
        return false;
    }

    function otherCfg() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = ctx + "/client/clientSyn/ftsApiCfg?ip=" + sel.data("ip") + "&port=" + sel.data("port")+"&name=" + sel.data("name")+"&cmdPort=" + sel.data("cmdport");
            openWin(url)
        }
    }
    function selectedRadio() {
        return $('#contentTable').find(':radio[name=selRaido]:checked');
    }
    function openWin(url) {
        window.open(url, "_self");
    }

    function lookFile() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = ctx + "/client/clientFile/index?name=" + sel.data("name") + "&ip=" + sel.data("ip") + "&port=" + sel.data("port")+"&cmdPort=" + sel.data("cmdport");
            openWin(url)
        }
    }

    function history() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = ctx + "/client/clientSyn/history?name=" + sel.data("name") + "&ip=" + sel.data("ip") + "&port=" + sel.data("port");
            openWin(url)
        }
    }

    function start() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = '${ctx}/client/clientSyn/clientStart?name=' + sel.data("name") + "&ip=" + sel.data("ip")+ "&status=" + sel.data("status")+"&cmdPort=" + sel.data("cmdport");
            openWin(url);
        }
    }

    function stop() {
        var sel = selectedRadio();
        if (sel.size() === 1) {
            var url = ctx + "/client/clientSyn/clientStop?name=" + sel.data("name") + "&ip=" + sel.data("ip")+ "&status=" + sel.data("status")+"&cmdPort=" + sel.data("cmdport");
            openWin(url)
        }
    }
</script>
</body>
</html>