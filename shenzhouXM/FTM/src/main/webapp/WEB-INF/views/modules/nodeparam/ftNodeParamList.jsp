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
	<ul class="nav nav-tabs nav-tabs-hidden">
		<li class="active"><a href="${ctx}/nodeparam/ftNodeParam/">节点参数列表</a></li>
		<shiro:hasPermission name="nodeparam:ftNodeParam:edit"><li><a href="${ctx}/nodeparam/ftNodeParam/form">节点参数添加</a></li></shiro:hasPermission>
	</ul>


	<form:form id="searchForm" modelAttribute="ftNodeParam" action="${ctx}/nodeparam/ftNodeParam/" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="nav nav-tabs">
		<li><a href="${ctx}/nodeparam/ftNodeParam/form">新增</a></li>
		<li><a href="${ctx}/servicenode/ftServiceNode/list">返回上一页</a></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
		<thead>
			<tr>
				<th width="25%">参数名称</th>
				<th width="25%">参数值</th>
				<th width="30%">描述</th>
				<shiro:hasPermission name="nodeparam:ftNodeParam:edit"><th width="20%">操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<%--<c:forEach items="${page.list}" var="ftNodeParam">--%>
		<c:forEach items="${ftNodeParamList}" var="ftNodeParam">
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
				<shiro:hasPermission name="nodeparam:ftNodeParam:edit"><td style="WORD-WRAP:break-word">
				<%--<a href="${ctx}/nodeparam/ftNodeParam/editForm?id=${ftNodeParam.id}&nodeId=${ftNodeParam.node.id}&name=${ftNodeParam.name}">修改</a>--%>
					<c:if test="${ftNodeParam.name eq 'FTP_MAX_FILE_SIZE'|| ftNodeParam.name eq 'NETWORK_CTRL_THRESHOLD'
					||ftNodeParam.name eq 'IP_CHECK'||ftNodeParam.name eq 'MAX_NETWORK_SPEED'
					||ftNodeParam.name eq 'NETWORK_CTRL_SLEEP_TIME'||ftNodeParam.name eq 'CFG_BAK_KEEP_TIME'
					||ftNodeParam.name eq 'FILE_MSG_PUSH_DELAY_TIME' || ftNodeParam.name eq 'DISTRIBUTE_NODE_NUM'}">
						<a href="${ctx}/nodeparam/ftNodeParam/editForm?name=${ftNodeParam.name}&value=${ftNodeParam.value}&des=${ftNodeParam.des}">修改</a>
					</c:if>

				<%--<a href="${ctx}/nodeparam/ftNodeParam/delete?name=${ftNodeParam.name}" onclick="return confirmx('确认要删除该节点参数吗？', this.href)">删除</a>--%>
			</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
    <div class="pagination">${page}</div>
</body>
</html>