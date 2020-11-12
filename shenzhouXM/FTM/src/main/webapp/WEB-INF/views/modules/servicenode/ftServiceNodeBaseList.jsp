<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>


	<title>基础配置-节点管理-节点列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function(){
//			history.go(0);
		});

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
		<li class="active"><a href="${ctx}/servicenode/ftServiceNode/">节点列表</a></li>
	</ul>

	<form:form id="searchForm" modelAttribute="ftServiceNode" action="${ctx}/servicenode/ftServiceNode/baseList" method="post">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
		<thead>
			<tr>
                <th>序号</th>
                <th>节点名称</th>
                <th>节点类型</th>
                <th>节点组</th>
                <th>节点模式</th>
                <th>节点模式(主备)</th>
                <th>切换模式</th>
                <th>存储模式</th>
                <th>IP地址</th>
                <th>命令端口</th>
                <th>服务端口</th>
                <th>管理端口</th>
                <th>接收端口</th>
				<th>运行状态</th>
				<th>隔离状态</th>
			</tr>
		</thead>
		<tbody>
		<%--<c:forEach items="${ftServiceNodeList}" var="ftServiceNode">--%>
		<c:forEach items="${page.list}" var="ftServiceNode" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>
					${ftServiceNode.name}
				</td>
                <td>
                        ${ftServiceNode.type}
                </td>
                <td>
                        ${ftServiceNode.systemName}
                </td>
                <td>
                        ${ftServiceNode.sysNodeModel=="single"?"单节点模式":""}
                        ${ftServiceNode.sysNodeModel=="more"?"多节点并行模式":""}
                        ${ftServiceNode.sysNodeModel=="ms"?"主备模式":""}
                </td>
                <td>
                        ${ftServiceNode.nodeModel=="ms-m"?"主备模式-主":""}
                        ${ftServiceNode.nodeModel=="ms-s"?"主备模式-备":""}
                </td>
                <td>
                        ${ftServiceNode.switchModel=="auto"?"自动模式":""}
                        ${ftServiceNode.switchModel=="handle"?"手动模式":""}
                </td>
                <td>
                        ${ftServiceNode.storeModel=="single"?"单点":""}
                        ${ftServiceNode.storeModel=="sync"?"同步":""}
                        ${ftServiceNode.storeModel=="async"?"异步":""}
                </td>
				<td>
					${ftServiceNode.ipAddress}
				</td>
				<td>
						${ftServiceNode.cmdPort}
				</td>
				<td>
						${ftServiceNode.ftpServPort}
				</td>
				<td>
						${ftServiceNode.ftpManagePort}
				</td>
				<td>
						${ftServiceNode.receivePort}
				</td>

				<td>
					${ftServiceNode.state=="0"?"未启用":""}
                            ${ftServiceNode.state=="WAITING"?"未启用":""}
					${ftServiceNode.state=="1"?"运行中":""}
                            ${ftServiceNode.state=="RUNNING"?"运行中":""}
				</td>
				<td>
						${ftServiceNode.isolState=="0"?"正常":""}
						${ftServiceNode.isolState=="WAITING"?"正常":""}
						${ftServiceNode.isolState=="1"?"隔离":""}
						${ftServiceNode.isolState=="RUNNING"?"隔离":""}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>