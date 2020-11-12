<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件广播管理</title>
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
		<li class="active"><a href="${ctx}/filebroadcast/ftFileBroadcast/">文件广播列表</a></li>
		<shiro:hasPermission name="filebroadcast:ftFileBroadcast:edit"><li><a href="${ctx}/filebroadcast/ftFileBroadcast/form">基本参数</a></li></shiro:hasPermission>
		<li><a href="${ctx}/filebroadcast/ftFileBroadcast/target">目标列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="ftFileBroadcast" action="${ctx}/filebroadcast/ftFileBroadcast/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium" style="width:130px"/>
			</li>
			<li><label>中文描述：</label>
				<form:input path="des" htmlEscape="false" maxlength="50" class="input-medium" style="width:130px"/>
			</li>
			<li><label>备注：</label>
				<form:input path="remarks" htmlEscape="false" maxlength="50" class="input-medium" style="width:130px"/>
			</li>
			<li><label>文件：</label>
				<form:input path="fileId" htmlEscape="false" maxlength="50" class="input-medium"  style="width:130px"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
		<thead>
			<tr>
				<th>名称</th>
				<th>中文描述</th>
				<th>重发次数</th>
				<th>重发间隔</th>
				<th>备注</th>
				<th>文件</th>
				<shiro:hasPermission name="filebroadcast:ftFileBroadcast:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftFileBroadcast">
			<tr>
				<td><a href="${ctx}/filebroadcast/ftFileBroadcast/form?id=${ftFileBroadcast.id}">
					${ftFileBroadcast.name}
				</a></td>
				<td>
					${ftFileBroadcast.des}
				</td>
				<td>
					${ftFileBroadcast.retryNum}
				</td>
				<td>
					${ftFileBroadcast.retryTime}
				</td>
				<td>
					${ftFileBroadcast.remarks}
				</td>
				<td>
					${ftFileBroadcast.fileId}
				</td>
				<shiro:hasPermission name="filebroadcast:ftFileBroadcast:edit"><td>
    				<a href="${ctx}/filebroadcast/ftFileBroadcast/form?id=${ftFileBroadcast.id}">修改</a>
					<a href="${ctx}/filebroadcast/ftFileBroadcast/delete?id=${ftFileBroadcast.id}" onclick="return confirmx('确认要删除该文件广播吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>