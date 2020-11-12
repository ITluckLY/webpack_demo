<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业务系统管理</title>
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
<%--<ul class="nav nav-tabs nav-tabs-hidden">
	<li class="active"><a href="${ctx}/sysInfo/ftSysInfo/">系统管理</a></li>
	<shiro:hasPermission name="sysInfo:ftSysInfo:edit"><li><a href="${ctx}/sysInfo/ftSysInfo/addPage">系统管理添加</a></li></shiro:hasPermission>
</ul>--%>
<form:form id="inputForm" modelAttribute="ftSysInfo" action="${ctx}/sysInfo/ftSysInfo/saveSystem" method="post" class="form-horizontal">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr>
		<th>系统管理员名称</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="userTemp">
		<tr>
			<td>
				<input name="admin"  value="${userTemp.loginName}"type=checkbox
					   <c:if test="${fn:contains(ftSysInfo.admin,userTemp.loginName)}">checked</c:if>
				/>${userTemp.loginName}
			</td>
            <%--<td>
                <input name="adminId" type="hidden" value="${userTemp.id}"/>
            </td>--%>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
	<div class="form-actions">
		<shiro:hasPermission name="sysInfo:ftSysInfo:setAdmin"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</form:form>
</body>
</html>