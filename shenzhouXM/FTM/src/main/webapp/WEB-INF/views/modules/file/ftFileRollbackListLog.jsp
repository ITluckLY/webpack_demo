<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>版本回滚列表管理</title>
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
	<ul class="nav nav-tabs">
		<li class="active"><a>版本回滚历史记录</a></li>
		<%--<shiro:hasPermission name="file:ftFile:edit"><li><a href="${ctx}/file/ftFile/form?path=${path}">文件添加</a></li></shiro:hasPermission>--%>
	</ul>

	<form:form id="searchForm" modelAttribute="ftFileRollbackLog" action="${ctx}/file/ftFileRollback/listLog" method="post">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
		<thead>
			<tr>
				<th>版本回滚时间</th>
				<th>数据节点</th>
				<th>备份文件名称</th>
				<th>监控端IP</th>
				<th>响应代码</th>
				<th>响应数据</th>
				<th>响应消息</th>
				<%--<shiro:hasPermission name="file:ftFileUpload:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftFileRollbackLog">
			<tr>
				<td>
					<fmt:formatDate value="${ftFileRollbackLog.createDate}" type="both"/>
				</td>
				<td>
                        ${ftFileRollbackLog.dataNodeName}
                </td>
				<td>
						${ftFileRollbackLog.bakFileName}
				</td>
				<td>
                    	${ftFileRollbackLog.monitorIp}
                </td>
				<td>
                    	${ftFileRollbackLog.retCode}
                </td>
				<td>
                    	${ftFileRollbackLog.retData}
                </td>
				<td>
                    	${ftFileRollbackLog.retMsg}
                </td>
				<%--<shiro:hasPermission name="file:ftFileUpload:edit"><td>
					<a href="${ctx}/file/ftFileUpload/listOneLog?id=${ftFileRollbackLog.id}">详情</a>
				</td></shiro:hasPermission>--%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>