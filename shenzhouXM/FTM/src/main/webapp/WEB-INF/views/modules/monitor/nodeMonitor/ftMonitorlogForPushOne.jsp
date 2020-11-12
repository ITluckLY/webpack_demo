<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文件推送记录详情</title>
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
		<li class="active"><a>文件推送记录详情</a></li>
	<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
			<thead>
			<tr>
				<th>文件推送列表名</th>
				<th>列表值</th>
			</tr>
			</thead>
			<tbody>
			<tr><td>编号</td><td>${bizFilePushLogTemp.id}</td></tr>
			<tr><td>交易码</td><td>${bizFilePushLogTemp.tranCode}</td></tr>
			<tr><td>节点组名</td><td>${bizFilePushLogTemp.sysname}</td></tr>
			<tr><td>客户端文件名</td><td>${bizFilePushLogTemp.clientFileName}</td></tr>
			<tr><td>创建时间</td><td><fmt:formatDate value="${bizFilePushLogTemp.createdTime}" type="both"/></td></tr>
			<tr><td>修改时间</td><td><fmt:formatDate value="${bizFilePushLogTemp.modifiedTime}" type="both"/></td></tr>
			<tr><td>IP地址</td><td>${bizFilePushLogTemp.ip}</td></tr>
			<tr><td>服务端文件名</td><td>${bizFilePushLogTemp.serverFileName}</td></tr>
			<tr><td>成功标识</td><td>${bizFilePushLogTemp.succ=="0"?false:true}</td></tr>
			<tr><td>发送方</td><td>${bizFilePushLogTemp.fromUid}</td></tr>
			<tr><td>接收方</td><td>${bizFilePushLogTemp.toUid}</td></tr>
			<tr><td>错误码</td><td>${bizFilePushLogTemp.errCode}</td></tr>
			<tr><td>错误信息</td><td>${bizFilePushLogTemp.errMsg}</td></tr>
			<tr><td>路由名称</td><td>${bizFilePushLogTemp.routeName}</td></tr>
			<tr><td>端口号</td><td>${bizFilePushLogTemp.port}</td></tr>
			<tr><td>是否同步</td><td>${bizFilePushLogTemp.sync}</td></tr>
			<tr><td>nano</td><td>${bizFilePushLogTemp.nano}</td></tr>
			<tr><td>prenano</td><td>${bizFilePushLogTemp.prenano}</td></tr>
			<tr><td>消息标识</td><td>${bizFilePushLogTemp.msgId}</td></tr>
			<tr><td>消息重推次数</td><td>${bizFilePushLogTemp.repushCount}</td></tr>
			<tr><td>流水号</td><td>${bizFilePushLogTemp.flowNo}</td></tr>
			<tr><td>节点名称</td><td>${bizFilePushLogTemp.nodeName}</td></tr>
			</tbody>
		</table>

		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
</body>
</html>