<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>FTP用户列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<%@include file="/WEB-INF/views/include/addPage.jsp" %>
<form:form id="searchForm" modelAttribute="sysProtocol" action="${ctx}/protocol/sysProtocol/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
            <li><label style="width:120px">FTP服务器地址：</label>
                <form:input path="ip" htmlEscape="false" maxlength="50" class="input-medium" readonly="true"/>
            </li>
            <li><label>用户名称：</label>
                <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
            </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix">&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:" onclick="history.go(-1);" class="btn">返回上一页</a></li>
        <%--<li><a href="javascript:" onclick="history.go(-1);" class="btn">返回上一页</a></li>--%>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>用户名称</th>
        <!--<th>密码</th>-->
        <th>目录</th>
        <th>是否可用</th>
        <th>是否可写</th>
        <th>空闲时间</th>
        <th>最大下载速率</th>
        <th>最大上传速率</th>
        <th>最大登录用户数</th>
        <th>最大同IP登录用户数</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftpUser">
        <tr>
            <td><a href="${ctx}/protocol/sysProtocol/form?name=${ftpUser.name}">
                    ${ftpUser.name}
            </a></td>
            <td>${ftpUser.homeDir}</td>
            <td>${ftpUser.enabled}</td>
            <td>${ftpUser.writeable}</td>
            <td>${ftpUser.maxIdleTimeSec}</td>
            <td>${ftpUser.maxDownloadRate}</td>
            <td>${ftpUser.maxUploadRate}</td>
            <td>${ftpUser.maxConcurrentLogins}</td>
            <td>${ftpUser.maxConcurrentLoginsPerIP}</td>
            <td>
                <a href="${ctx}/protocol/sysProtocol/form?name=${ftpUser.name}">修改</a>
                <a href="${ctx}/protocol/sysProtocol/delete?name=${ftpUser.name}"
                   onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>