<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
  <title>文件广播目标</title>
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
  <li><a href="${ctx}/filebroadcast/ftFileBroadcast/">文件广播列表</a></li>
  <shiro:hasPermission name="filebroadcast:ftFileBroadcast:edit"><li><a href="${ctx}/filebroadcast/ftFileBroadcast/form">基本参数</a></li></shiro:hasPermission>
  <li class="active"><a href="${ctx}/filebroadcast/ftFileBroadcast/">目标列表</a></li>
</ul>
<form:form id="searchForm" modelAttribute="ftFileBroadcast" action="${ctx}/filebroadcast/ftFileBroadcast/" method="post" class="breadcrumb form-search">
  <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
  <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
  <ul class="ul-form">
    <li><label>目标系统：</label>
      <form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
    </li>
    <%--<li><label>中文描述：</label>--%>
      <%--<form:input path="des" htmlEscape="false" maxlength="256" class="input-medium"/>--%>
    <%--</li>--%>
    <%--<li><label>备注：</label>--%>
      <%--<form:input path="remarks" htmlEscape="false" maxlength="256" class="input-medium"/>--%>
    <%--</li>--%>
    <%--<li><label>文件：</label>--%>
      <%--<form:input path="fileId" htmlEscape="false" maxlength="256" class="input-medium"/>--%>
    <%--</li>--%>
    <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
    <li class="clearfix"></li>
  </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
  <thead>
  <tr>
    <th>目标系统</th>
    <th>推送模式</th>
    <th>预定义流程</th>
    <shiro:hasPermission name="filebroadcast:ftFileBroadcast:edit"><th>操作</th></shiro:hasPermission>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${page.list}" var="ftFileBroadcast">
    <tr>
      <td><a href="${ctx}/filebroadcast/ftFileBroadcast/form?id=${ftFileBroadcast.id}">
        ${ftFileBroadcast.name}
      </a></td>
      <td>
        <select class="required" style="width:100px;">
          <option value="">FTP</option>
          <option value="">SFTP</option>
          <option value="">TCP</option>
        </select>
      </td>
      <td>
        ${ftFileBroadcast.retryNum}
      </td>
      <shiro:hasPermission name="filebroadcast:ftFileBroadcast:edit"><td>
        <a href="${ctx}/filebroadcast/ftFileBroadcast/form?id=${ftFileBroadcast.id}">参数设置</a>
        <a href="${ctx}/filebroadcast/ftFileBroadcast/delete?id=${ftFileBroadcast.id}" onclick="return confirmx('确认要删除该文件广播吗？', this.href)">删除</a>
      </td></shiro:hasPermission>
    </tr>
  </c:forEach>
  </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>