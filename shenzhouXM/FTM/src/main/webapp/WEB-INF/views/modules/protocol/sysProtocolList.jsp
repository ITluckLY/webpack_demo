<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>系统管理管理</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="protocol:sysProtocol:view">
        <a href="${ctx}/protocol/sysProtocol/addPage">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="sysProtocol" action="${ctx}/protocol/sysProtocol/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>目标名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="btns"><input class="btn btn-primary" onclick="telnetAll()" type="button" value="批量连通测试" readonly/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>目标名称</th>
        <th>协议名称</th>
        <th>IP地址</th>
        <th>端口</th>
        <th>用户名</th>
        <th>上传路径</th>
        <th>下载路径</th>
        <th>连通状态</th>
        <%--<shiro:hasPermission name="protocol:sysProtocol:edit">--%>
        <th>操作</th>
        <%--</shiro:hasPermission>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="sysProtocol" varStatus="status">
        <tr>
            <td>
                <a href="${ctx}/protocol/sysProtocol/form?name=${sysProtocol.name}">${sysProtocol.name}</a>
            </td>
            <td>
                    ${sysProtocol.protocol}
            </td>
            <td>
                    ${sysProtocol.ip}
            </td>
            <td>
                    ${sysProtocol.port}
            </td>
            <td>
                    ${sysProtocol.username}
            </td>
            <td>
                    ${sysProtocol.uploadPath}
            </td>
            <td>
                    ${sysProtocol.downloadPath}
            </td>
            <td id="state_${status.index}" width="200px;">
                未知连通状态
            </td>
            <td>
                <shiro:hasPermission name="protocol:sysProtocol:edit">
                    <a href="${ctx}/protocol/sysProtocol/form?name=${sysProtocol.name}">修改</a>
                    <a href="${ctx}/protocol/sysProtocol/delete?name=${sysProtocol.name}"
                       onclick="return confirmx('确认要删除该系统管理吗？', this.href)">删除</a>
                    &nbsp;&nbsp;
                </shiro:hasPermission>
                <a class="telnet" href="javascript:void(0)" data-ip="${sysProtocol.ip}" data-port="${sysProtocol.port}" data-index="${status.index}"
                   onclick="telnetThis(this)">连通测试</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<script type="text/javascript">
    $(function () {
        telnetAll();
    });

    function page(n, s) {
        $("#pageNo").val(n);
        $("#pageSize").val(s);
        $("#searchForm").submit();
        return false;
    }

    function telnetOne(ip, port, index) {
        var $state = $('#state_' + index);
        $state.text("尝试连通中...");
        $.ajax({
            url: "${ctx}/protocol/sysProtocol/telnet?ip=" + ip + "&port=" + port,
            type: "get",
            success: function (data) {
                $state.text(data);
            },
            error: function () {
                $state.text("连通异常");
            }
        });
    }

    function telnetThis(a) {
        var $a = $(a);
        var ip = $a.data('ip');
        var port = $a.data('port');
        var index = $a.data('index');
        telnetOne(ip, port, index);
    }

    function telnetAll() {
        $('.telnet').each(function () {
            var $a = $(this);
            var ip = $a.data('ip');
            var port = $a.data('port');
            var index = $a.data('index');
            telnetOne(ip, port, index);
        });
    }
</script>
</body>
</html>