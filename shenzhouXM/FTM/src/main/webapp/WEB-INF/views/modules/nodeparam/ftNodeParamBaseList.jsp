<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>节点参数</title>
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
	<form:form id="searchForm" modelAttribute="ftNodeParam" action="${ctx}/nodeparam/ftNodeParam/" method="post">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
		<thead>
			<tr>
				<th width="25%">参数名称</th>
				<th width="25%">参数值</th>
				<th width="25%">描述</th>
				<th width="25%">节点名称</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ftNodeParam">
			<tr>
				<td style="WORD-WRAP:break-word">
					${ftNodeParam.name}
				</td>
				<td style="WORD-WRAP:break-word">
					${ftNodeParam.value}
				</td>
				<td style="WORD-WRAP:break-word">
					${ftNodeParam.des}
				</td>
				<td style="WORD-WRAP:break-word">
					${ftNodeParam.node.name}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>