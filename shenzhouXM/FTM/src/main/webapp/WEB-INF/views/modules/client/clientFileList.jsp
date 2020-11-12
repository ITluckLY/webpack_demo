<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>数据文件</title>
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
		<li><a href="${ctx}/client/clientSyn" target="_parent">返回上一页</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="clientFile" action="${ctx}/client/clientFile/query" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>文件名称：</label>
				<form:input path="fileName" htmlEscape="false" maxlength="256" class="input-medium"/>
			</li>
			<li><label>客户端：</label>
				<form:input path="systemName" htmlEscape="false" maxlength="256" class="input-medium" readonly="true"/>
			</li>
			<li><label>文件路径：</label>
				<form:input path="parentPath" htmlEscape="false" value="${path}" maxlength="256" class="input-medium"/>
			</li>
			<li><label>上传时间：</label>
				<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${clientFile.createDate}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
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
				<th>客户端</th>
				<th>文件大小(单位:字节)</th>
				<th>备注</th>
				<th>文件路径</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="clientFile">
			<tr>
				<td>
					${clientFile.fileName}
				</td>
				<td>
					${clientFile.systemName}
				</td>
				<td>
					${clientFile.fileSize}
				</td>
				<td>
					${clientFile.remarks}
				</td>
				<td>
					${clientFile.parentPath}
				</td>
				<td>
					<shiro:hasPermission name="client:clientFile:repush">
						<a href="${ctx}/client/clientFile/repush?fileName=${clientFile.fileName}&parentPath=${clientFile.parentPath}">重新上传</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>