<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件管理</title>
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
		<li class="active"><a >文件列表</a></li>
		<li><a href="${ctx}/servicenode/ftServiceNode/list" target="_parent">返回上一页</a></li>
		<%--<shiro:hasPermission name="file:ftFile:edit"><li><a href="${ctx}/file/ftFile/form?path=${path}">文件添加</a></li></shiro:hasPermission>--%>
	</ul>
	<%--<%@include file="/WEB-INF/views/include/new.jsp"%>--%>
	<form:form id="searchForm" modelAttribute="ftFile" action="${ctx}/file/ftFile/query" method="post" class="breadcrumb form-search">
		<%--<input id="pageNo" name="nodeId" type="hidden" value="${nodeId}"/>--%>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<%--<input id="path" name="path" type="text" value="${path}"/>--%>
		<ul class="ul-form">
			<li><label>文件名称：</label>
				<form:input path="fileName" htmlEscape="false" maxlength="256" class="input-medium"/>
			</li>
			<li><label>上传节点组：</label>
				<form:input path="systemName" htmlEscape="false" maxlength="256" class="input-medium" readonly="true"/>
			</li>
			<li><label>上传时间：</label>
				<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${ftFile.createDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				<%--<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"--%>
					   <%--value="<fmt:formatDate value="${ftFile.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
					   <%--onclick="WdatePicker({dateFmt: 'H:mm:ss', minDate: '8:00:00', maxDate: '11:30:00' });"/>--%>
			</li>
			<%--<li><label>文件大小：</label>
				<form:input path="fileSize" htmlEscape="false" maxlength="256" class="input-medium"/>
			</li>
			<li><label>备注：</label>
				<form:input path="remarks" htmlEscape="false" maxlength="256" class="input-medium"/>
			</li>--%>
			<li><label>文件路径：</label>
				<form:input path="parentPath" htmlEscape="false" value="${path}" maxlength="256" class="input-medium"/>
			</li>
			<li class="btns" style="right:500px "><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>文件名称</th>
				<th>上传节点组</th>
				<th>上传时间</th>
				<th>文件大小(单位:字节)</th>
				<th>备注</th>
				<th>文件路径</th>
				<shiro:hasPermission name="file:ftFile:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftFile">
			<tr>
				<td>
					<shiro:hasPermission name="file:ftFile:edit"><a href="${ctx}/file/ftFile/download?fileId=${ftFile.id}&nodeId=${nodeId}&fileName=${ftFile.fileName}"></shiro:hasPermission>
						${ftFile.fileName}
					<shiro:hasPermission name="file:ftFile:edit"></a></shiro:hasPermission>
				</td>
				<td>
					${ftFile.systemName}
				</td>
				<td>
					${ftFile.lastModifiedDate}
				</td>
				<td>
					${ftFile.fileSize}
				</td>
				<td>
					${ftFile.remarks}
				</td>
				<td>
					${ftFile.parentPath}
				</td>
				<shiro:hasPermission name="file:ftFile:edit"><td>
					<a href="${ctx}/file/ftFile/download?fileId=${ftFile.id}&nodeId=${nodeId}&fileName=${ftFile.fileName}">下载</a>
    				<%--<a href="${ctx}/file/ftFile/form?id=${ftFile.id}">修改</a>--%>
					<%--<a href="${ctx}/file/ftFile/delete?id=${ftFile.id}" onclick="return confirmx('确认要删除该文件管理吗？', this.href)">删除</a>--%>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>