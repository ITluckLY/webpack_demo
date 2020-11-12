<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件分发记录详情</title>
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
		<li class="active"><a>文件分发记录详情</a></li>
	<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
			<thead>
			<tr>
				<th>文件分发列表名</th>
				<th>列表值</th>
			</tr>
			</thead>
			<tbody>
			<tr><td>编号</td><td>${bizFileDistributeLogTemp.id}</td></tr>
			<tr><td>交易码</td><td>${bizFileDistributeLogTemp.tranCode}</td></tr>
			<tr><td>节点组名</td><td>${bizFileDistributeLogTemp.sysnamels}</td></tr>
			<tr><td>节点名</td><td>${bizFileDistributeLogTemp.nodeNamels}</td></tr>
			<tr><td>文件名</td><td>${bizFileDistributeLogTemp.fileName}</td></tr>
			<tr><td>真实文件名</td><td>${bizFileDistributeLogTemp.realFileName}</td></tr>
			<tr><td>原始文件名</td><td>${bizFileDistributeLogTemp.oriFilename}</td></tr>
			<tr><td>创建时间</td><td><fmt:formatDate value="${bizFileDistributeLogTemp.createdTime}" type="both"/></td></tr>
			<tr><td>修改时间</td><td><fmt:formatDate value="${bizFileDistributeLogTemp.modifiedTime}" type="both"/></td></tr>
			<tr><td>操作时间</td><td><fmt:formatDate value="${bizFileDistributeLogTemp.modifiedTime}" type="both"/></td></tr>
			<tr><td>文件版本号</td><td>${bizFileDistributeLogTemp.fileVersion}</td></tr>
			<tr><td>状态</td> <td><c:if test="${bizFileDistributeLogTemp.state==1}" >分发成功</c:if>
				<c:if test="${bizFileDistributeLogTemp.state==0}" >未进行分发</c:if>
				<c:if test="${bizFileDistributeLogTemp.state==-1}" >分发失败</c:if></td></tr>


			</tbody>
		</table>

		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
</body>
</html>