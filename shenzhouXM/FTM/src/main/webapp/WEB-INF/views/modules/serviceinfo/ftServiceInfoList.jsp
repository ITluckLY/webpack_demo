<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>服务管理</title>
  <meta name="decorator" content="default"/>
  <script type="text/javascript">
      function page(n, s) {
          $("#pageNo").val(n);
          $("#pageSize").val(s);
          $("#searchForm").submit();
          return false;
      }
  </script>
</head>
<body>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/serviceinfo/ftServiceInfo/">服务管理列表</a></li>
    <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit">
        <li><a href="${ctx}/serviceinfo/ftServiceInfo/addPage">服务管理添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
  <shiro:hasPermission name="serviceinfo:ftServiceInfo:view">
    <a href="${ctx}/serviceinfo/ftServiceInfo/addPage">新增</a>
  </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftServiceInfo" action="${ctx}/serviceinfo/ftServiceInfo/" method="post"
           class="breadcrumb form-search marfl-new">
  <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
  <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
  <ul class="ul-form">
    <li><label>交易码：</label>
      <form:input path="trancode" htmlEscape="false" maxlength="50" class="input-medium"/>
    </li>
    <li><label>相关流程：</label>
      <form:input path="flow" htmlEscape="false" maxlength="50" class="input-medium"/>
    </li>
    <li><label>校验流程：</label>
      <form:input path="flow" htmlEscape="false" maxlength="50" class="input-medium"/>
    </li>
    <li><label>交易码描述：</label>
      <form:input path="describe" htmlEscape="false" maxlength="100" class="input-medium"/>
    </li>
    <li class="btns">
      <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
      </button>
    </li>
    <li class="btns">
      </button>
      <a href="${ctx}/serviceinfo/ftServiceInfo/export?filename=TranCode_all.csv">
        &nbsp;&nbsp;批量导出
      </a>
    </li>
    <li class="clearfix">
    </li>
  </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed"
       style="width: 99%;TABLE-LAYOUT:fixed">
  <thead>
  <tr>
    <th width="8%">交易码</th>
    <th width="7%">节点组</th>
    <th width="12%">相关流程</th>
    <th width="12%">校验流程</th>
    <th width="5%">是否重命名</th>
    <th width="8%">文件保留期限(min)</th>
    <th width="7%">优先级</th>
    <th width="10%">最大并发数</th>
    <th width="8%">交易码关联</th>
    <th width="15%">交易码描述</th>
    <shiro:hasPermission name="serviceInfo:ftServiceInfo:edit">
      <th width="8%">操作</th>
    </shiro:hasPermission>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${page.list}" var="ftServiceInfo">
    <tr>
      <td style="WORD-WRAP:break-word">
        <a href="${ctx}/serviceinfo/ftServiceInfo/form?id=${ftServiceInfo.id}&trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">
            ${ftServiceInfo.trancode}
        </a></td>
      <td>
          ${ftServiceInfo.systemName}
      </td>
      <td style="WORD-WRAP:break-word">
          ${ftServiceInfo.flow}
      </td>
      <td style="WORD-WRAP:break-word">
          ${ftServiceInfo.psflow}
      </td>
      <td>
          ${ftServiceInfo.rename=="1"?"是":""}
          ${ftServiceInfo.rename=="0"?"否":""}
      </td>
      <td>
          ${ftServiceInfo.filePeriod}
      </td>
      <td>
          ${ftServiceInfo.priority}
      </td>
      <td>
          ${ftServiceInfo.size}
      </td>
      <td>
        <a href="${ctx}/serviceinfo/ftServiceInfo/putGetTotal?trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">权限关联</a>
      </td>
      <td style="WORD-WRAP:break-word">
          ${ftServiceInfo.describe}
      </td>
      <shiro:hasPermission name="serviceInfo:ftServiceInfo:edit">
        <td>
          <a href="${ctx}/serviceinfo/ftServiceInfo/form?id=${ftServiceInfo.id}&trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}">修改</a>
          <a href="${ctx}/serviceinfo/ftServiceInfo/delete?id=${ftServiceInfo.id}&trancode=${ftServiceInfo.trancode}&systemName=${ftServiceInfo.systemName}"
             onclick="return confirmx('确认要删除该服务吗？', this.href)">删除</a>
        </td>
      </shiro:hasPermission>
    </tr>
  </c:forEach>
  </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>