<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>版本发布列表管理</title>
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
		<li class="active"><a>版本发布历史记录</a></li>
		<%--<shiro:hasPermission name="file:ftFile:edit"><li><a href="${ctx}/file/ftFile/form?path=${path}">文件添加</a></li></shiro:hasPermission>--%>
	</ul>

    <form:form id="searchForm" modelAttribute="ftFileUploadLog" action="${ctx}/file/ftFileUpload/listLog" method="post">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    </form:form>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
		<thead>
			<tr>
				<th>版本发布时间</th>
				<th>客户端文件名称</th>
				<th>接收节点</th>
				<th>响应代码</th>
				<th>响应消息</th>
				<shiro:hasPermission name="file:ftFileUpload:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftFileUploadLog">
			<tr>
				<td>
					<fmt:formatDate value="${ftFileUploadLog.updateDate}" type="both"/>
				</td>
				<td>${ftFileUploadLog.fileName}</td>
				<td>${ftFileUploadLog.sendNodeName}</td>
				<td>${ftFileUploadLog.retCode}</td>
				<td>${ftFileUploadLog.retMsg}</td>
				<shiro:hasPermission name="file:ftFileUpload:edit"><td>
					<a href="${ctx}/file/ftFileUpload/listOneLog?id=${ftFileUploadLog.id}">详情</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>