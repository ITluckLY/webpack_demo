<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统管理员管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
<%--<ul class="nav nav-tabs nav-tabs-hidden">
	<li class="active"><a href="${ctx}/sysManager/ftSysManager/">系统管理员管理</a></li>
	<shiro:hasPermission name="sysManager:ftSysManager:edit"><li><a href="${ctx}/sysManager/ftSysManager/addPage">系统管理员添加</a></li></shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
	<shiro:hasPermission name="sysManager:ftSysManager:view">
		<a href="${ctx}/sysManager/ftSysManager/addPage">新增</a>
	</shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftSysManager" action="${ctx}/sysManager/ftSysManager/" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<ul class="ul-form">
		<li><label>管理员名称：</label>
			<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
		</li>
		<li><label>电话：</label>
			<form:input path="phone" htmlEscape="false" maxlength="50" class="input-medium"/>
		</li>
		<%--<li><label>部门：</label>
			<form:input path="department" htmlEscape="false" maxlength="256" class="input-medium"/>
		</li>--%>
		<li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
	<thead>
	<tr>
		<th>管理员名称</th>
		<th>电话</th>
		<th>邮件</th>
		<%--<th>公司</th>--%>
		<%--<th>部门</th>--%>
		<th>系统</th>
		<shiro:hasPermission name="sysManager:ftSysManager:edit"><th>操作</th></shiro:hasPermission>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="ftSysManager">
		<tr>
			<td><a href="${ctx}/sysManager/ftSysManager/form?id=${ftSysManager.id}&name=${ftSysManager.name}">
					${ftSysManager.name}
			</a></td>
			<td>
					${ftSysManager.phone}
			</td>
			<td>
					${ftSysManager.email}
			</td>
			<%--<td>
					${ftSysManager.company}
			</td>
			<td>
					${ftSysManager.department}
			</td>--%>
			<td>
					${ftSysManager.systems}
			</td>
			<shiro:hasPermission name="sysManager:ftSysManager:edit"><td>
				<a href="${ctx}/sysManager/ftSysManager/form?id=${ftSysManager.id}&name=${ftSysManager.name}">修改</a>
				<a href="${ctx}/sysManager/ftSysManager/delete?id=${ftSysManager.id}&name=${ftSysManager.name}" onclick="return confirmx('确认要删除该目录吗？', this.href)">删除</a>
			</td></shiro:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>