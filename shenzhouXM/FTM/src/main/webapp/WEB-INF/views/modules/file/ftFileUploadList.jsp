<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件上传列表管理</title>
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
		<li class="active"><a>文件上传列表</a></li>
		<li><a href="${ctx}/file/ftFileUpload/form">上传</a></li>
		<form:form id="searchForm" modelAttribute="ftFileUpload" action="${ctx}/file/ftFileUpload/list" method="post">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
		<thead>
			<tr>
				<th>服务端文件名称</th>
                <th>客户端文件名称</th>
				<%--<th>上传系统</th>--%>
				<th>上传用户</th>
				<th>客户端上传时间</th>
				<th>版本发布时间</th>
				<shiro:hasPermission name="file:ftFileUpload:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftFileUploadTemp">
			<tr>
                <td>
                        ${ftFileUploadTemp.renameFileName}
                </td>
				<td>
						${ftFileUploadTemp.fileName}
				</td>
				<%--<td>
					${ftFileUploadTemp.systemName}
				</td>--%>
                <td>
                    ${ftFileUploadTemp.uploadUser}
                </td>
				<td>
                    <fmt:formatDate value="${ftFileUploadTemp.createDate}" type="both"/>
				</td>
				<td>
					<fmt:formatDate value="${ftFileUploadTemp.updateDate}" type="both"/>
				</td>
				<shiro:hasPermission name="file:ftFileUpload:edit"><td>

							<a href="${ctx}/file/ftFileUpload/listOne?id=${ftFileUploadTemp.id}">详情</a>
							<a href="${ctx}/file/ftFileUpload/sendLink?id=${ftFileUploadTemp.id}&renameFileName=${ftFileUploadTemp.renameFileName}">发送</a>
					<a href="${ctx}/file/ftFileUpload/delOne?id=${ftFileUploadTemp.id}&uploadPath=${ftFileUploadTemp.uploadPath}" onclick="return confirmx('确认要删除该文件上传记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>