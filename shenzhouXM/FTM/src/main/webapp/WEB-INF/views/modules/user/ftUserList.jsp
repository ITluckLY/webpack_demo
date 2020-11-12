<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li class="active"><a href="${ctx}/user/ftUser/">用户管理列表</a></li>
		<shiro:hasPermission name="user0:ftUser:edit"><li><a href="${ctx}/user/ftUser/addPage">用户管理添加</a></li></shiro:hasPermission>
	</ul>
	<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
		<shiro:hasPermission name="user0:ftUser:view">
			<a href="${ctx}/user/ftUser/addPage">新增</a>
		</shiro:hasPermission>
	</div>
	<form:form id="searchForm" modelAttribute="ftUser" action="${ctx}/user/ftUser/" method="post" class="breadcrumb form-search marfl-new">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名：</label>
				<form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
			</li>
			<li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
		<thead>
			<tr>
				<th>用户名</th>
				<%--<th>所属系统</th>--%>
				<%--<th width="30%">用户目录</th>--%>
				<%--<th>权限</th>--%>
				<%--<th>客户端地址</th>--%>
				<th>描述</th>
				<shiro:hasPermission name="user0:ftUser:edit"><th width="20%">操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftUser">
			<tr>
				<td><a href="${ctx}/user/ftUser/form?name=${ftUser.name}">
					${ftUser.name}
				</a></td>
				<%--<td>--%>
				    <%--${ftUserTemp.systemName}--%>
				<%--</td>--%>
				<%--<td>--%>
					<%--${ftUserTemp.userDir}--%>
			<%--</td>--%>
				<%--<td>
                                ${ftUserTemp.permession=="A"?"全部":""}
                                ${ftUserTemp.permession=="R"?"只读":""}
                                ${ftUserTemp.permession=="W"?"写入":""}
				</td>--%>
                <%--<td>
                        ${ftUser.clientAddress}
                </td>--%>
                <td>
                        ${ftUser.des==null||ftUser.des==""?"":ftUser.des}
                </td>
				<shiro:hasPermission name="user0:ftUser:edit"><td>
    				<a href="${ctx}/user/ftUser/form?name=${ftUser.name}&userDir=${ftUser.userDir}">修改</a>
					<a href="${ctx}/user/ftUser/delete?name=${ftUser.name}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>