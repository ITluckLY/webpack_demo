<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务管理</title>
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
	<li><a href="${ctx}/servicenode/ftServiceNode/otherConf">节点参数配置</a></li>
	<li><a href="${ctx}/servicenode/ftServiceNode/listAll">节点列表配置</a></li>
	<%--<li><a href="${ctx}/sysinfo/ftSysInfo/otherConf">系统配置</a></li>--%>
	<li><a href="${ctx}/user/ftUser/otherConf">用户配置</a></li>
	<li><a href="${ctx}/file/ftFile/otherConf">文件配置</a></li>

	<li><a href="${ctx}/file/ftFileRename/otherConf">文件重命名配置</a></li>

	<li><a href="${ctx}/fileclean/ftFileClean/otherConf">文件清理配置</a></li>
	<li><a href="${ctx}/component/ftComponent/otherConf">组件配置</a></li>
	<li class="active"><a href="${ctx}/serviceinfo/ftServiceInfo/otherConf">服务配置</a></li>
	<li><a href="${ctx}/timingtask/ftTimingTask/otherConf">定时任务配置</a></li>
	<li><a href="${ctx}/protocol/sysProtocol/otherConf">系统配置</a></li>
	<li><a href="${ctx}/flow/ftFlow/otherConf">流程配置</a></li>
	<li><a href="${ctx}/route/ftRoute/otherConf">路由配置</a></li>
	<%--<li><a href="${ctx}/flow/ftFlow/ruleOtherConf">规则配置</a></li>--%>
	<li><a href="${ctx}/client/clientStatus/otherConf">客户端状态</a></li>
	<li><a href="${ctx}/servicenode/ftServiceNode/list">返回上一页</a></li>
</ul>
<pre>
    <xmp>
		${returnMsg}
	</xmp>
</pre>
</body>
</html>