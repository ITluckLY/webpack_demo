<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>服务管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(function(){
//			history.go(0);
        });

        $(document).ready(function() {

        });
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>


</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/serviceinfo/ftServiceInfo/putGetTotal?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">服务管理列表</a></li>
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/addPutAuth?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">新增上传权限</a></li>
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/addGetAuth?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">新增下载权限</a></li>
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/list">返回上一页</a></li>
</ul>
<sys:message content="${message}"/>
<br/>
<a>上传权限</a>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <th>交易码</th>
        <th>上传目录(权限)</th>
        <th>用户名</th>
        <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit">
        <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${paeList}" var="paeTemp">
        <tr>
            <td>
                    ${paeTemp.trancode}
            </td>
            <td>
                    ${paeTemp.directoy}
            </td>
            <td>
                    ${paeTemp.userName}
            </td>
            <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit">
            <td>
                <a href="${ctx}/serviceinfo/ftServiceInfo/delPutAuth?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}&userName=${paeTemp.userName}&directoy=${paeTemp.directoy}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
            </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>

<br/>
<a>下载权限</a>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <th>交易码</th>
        <th>用户名</th>
        <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit">
        <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${gaeList}" var="gaeTemp">
        <tr>
            <td>
                    ${gaeTemp.trancode}
            </td>
            <td>
                    ${gaeTemp.userName}
            </td>
            <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit">
            <td>
                <a href="${ctx}/serviceinfo/ftServiceInfo/delGetAuth?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}&userName=${gaeTemp.userName}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
            </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>