<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业务系统管理</title>
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
		<li class="active"><a href="${ctx}/sysInfo/ftSysInfo/">系统管理</a></li>
		<shiro:hasPermission name="sysInfo:ftSysInfo:edit"><li><a href="${ctx}/sysInfo/ftSysInfo/addPage">系统管理添加</a></li></shiro:hasPermission>
	</ul>--%>
	<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
		<shiro:hasPermission name="sysInfo:ftSysInfo:view">
			<a href="${ctx}/sysInfo/ftSysInfo/addPage">新增</a>
		</shiro:hasPermission>
	</div>
	<form:form id="searchForm" modelAttribute="ftSysInfo" action="${ctx}/sysInfo/ftSysInfo/" method="post" class="breadcrumb form-search marfl-new">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>节点组：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>系统管理员：</label>
				<form:input path="admin" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
		<thead>
			<tr>
				<th width="10%">节点组</th>
				<th width="10%">节点模式</th>
				<th width="10%">主备切换模式</th>
                <th width="10%">文件存储模式</th>
				<th width="25%">节点组描述</th>
				<%--<th>备注</th>--%>
				<shiro:hasPermission name="sysInfo:ftSysInfo:edit"><th width="8%">操作</th></shiro:hasPermission>
				<th width="27%">节点组管理员</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftSysInfo">
			<tr>
				<td><a href="${ctx}/sysInfo/ftSysInfo/form?id=${ftSysInfo.id}&name=${ftSysInfo.name}">
					${ftSysInfo.name}
				</a></td>
                <td>
                        ${ftSysInfo.sysNodeModel=="single"?"单节点模式":""}
                        ${ftSysInfo.sysNodeModel=="more"?"多节点并行模式":""}
                        ${ftSysInfo.sysNodeModel=="ms"?"主备模式":""}
                </td>
                <td>
                        ${ftSysInfo.switchModel=="auto"?"自动切换模式":""}
                        ${ftSysInfo.switchModel=="handle"?"手动切换模式":""}
                </td>
                <td>
                        ${ftSysInfo.storeModel=="single"?"单点":""}
                        ${ftSysInfo.storeModel=="sync"?"同步":""}
                        ${ftSysInfo.storeModel=="async"?"异步":""}
                </td>
				<td style="WORD-WRAP:break-word">
				    ${ftSysInfo.des}
				</td>
				<shiro:hasPermission name="sysInfo:ftSysInfo:edit"><td>
    				<a href="${ctx}/sysInfo/ftSysInfo/form?id=${ftSysInfo.id}&name=${ftSysInfo.name}">修改</a>
					<a href="${ctx}/sysInfo/ftSysInfo/delete?id=${ftSysInfo.id}&name=${ftSysInfo.name}" onclick="return confirmx('确认要删除该节点组吗？', this.href)">删除</a>
				</td></shiro:hasPermission>

					<td style="WORD-WRAP:break-word">
							${ftSysInfo.admin}
						<shiro:hasPermission name="sysInfo:ftSysInfo:setAdmin">
							<a href="${ctx}/sysInfo/ftSysInfo/addAdmin?id=${ftSysInfo.id}&name=${ftSysInfo.name}" style="float: right">设置管理员</a>
						</shiro:hasPermission>
					</td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>