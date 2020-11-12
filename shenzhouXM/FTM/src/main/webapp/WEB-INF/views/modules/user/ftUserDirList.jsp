1<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户权限管理</title>
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

	<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
		<shiro:hasPermission name="user0:ftUserDirAuth:edit">
			<a href="${ctx}/user/ftUser/addUserDirAuth">新增</a>
		</shiro:hasPermission>
	</div>
	<form:form id="searchForm" modelAttribute="userAuthModel" action="${ctx}/user/ftUser/dirList/" method="post" class="breadcrumb form-search marfl-new">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名：</label>
				<form:input path="userName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
            <li><label>路径：</label>
                <form:input path="path" htmlEscape="false" maxlength="50" class="input-medium"/>
            </li>
            <li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
		<thead>
			<tr>
				<th width="30%">用户名称</th>
				<%--<th>系统名称</th>--%>
				<th width="40%">路径</th>
				<th width="20%">权限</th>
				<shiro:hasPermission name="user0:ftUserDirAuth:edit"><th width="10%">操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userAuth">
			<tr>
				<td style="WORD-WRAP:break-word">${userAuth.userName}</td>
				<%--<td>${userAuthTemp.sysname}</td>--%>
				<td style="WORD-WRAP:break-word">${userAuth.path}</td>
				<td style="WORD-WRAP:break-word">
					${userAuth.auth=="A"?"全部":""}
					${userAuth.auth=="R"?"只读":""}
					${userAuth.auth=="W"?"写入":""}
				</td>
				<shiro:hasPermission name="user0:ftUserDirAuth:edit"><td style="WORD-WRAP:break-word">
					<a href="${ctx}/user/ftUser/delUserAuth?userName=${userAuth.userName}&path=${userAuth.path}&auth=${userAuth.auth}" onclick="return confirmx('确认要删除该用户权限吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>