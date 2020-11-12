<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户端版本详情</title>
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
	<li class="active"><a>客户端版本详情</a></li>
	<form:form id="searchForm" modelAttribute="clientVersionlog" action="${ctx}/monitor/ClientVersion/clientVersionLog" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<input id="uid" name="uid" type="hidden" value="${uid}">
	<input id="clientIp" name="clientIp" type="hidden" value="${clientIp}">
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
		<thead>
		<tr>

			<th>客户端版本号</th>
			<th>该版本首次使用时间</th>

		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="client">
			<tr>

				<td>${client.clientVersion}</td>
				<td><fmt:formatDate value="${client.startTime}" type="both"/></td>
			</tr>
		</c:forEach>

		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</body>
</html>