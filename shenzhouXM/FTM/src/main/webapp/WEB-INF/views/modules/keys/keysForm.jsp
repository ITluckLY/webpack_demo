<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>秘钥管理管理</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
        <a href="${ctx}/keys/ftKey/addPage">新增</a>
</div>
<form:form id="searchForm" modelAttribute="ftKey" action="${ctx}/keys/ftKey/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>用户名称：</label>
            <form:input path="user" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>用户名称</th>
        <th>秘钥类型</th>
        <th>秘钥内容</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftKey" varStatus="status">
        <tr>
            <td>
                <%--<a href="${ctx}keys/ftKey/form?name=${ftKey.user}">${ftKey.user}</a>--%>
                ${ftKey.user}
            </td>
            <td>
                    ${ftKey.type}
            </td>
            <td>
                    ${ftKey.content}
            </td>
            <td>
                                <a href="${ctx}/keys/ftKey/form?user=${ftKey.user}&type=${ftKey.type}&content=${ftKey.content}">修改</a>
                                <a href="${ctx}/keys/ftKey/delete?user=${ftKey.user}&type=${ftKey.type}&content=${ftKey.content}"
                                   onclick="return confirmx('确认要删除该证书吗？', this.href)">删除</a>
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