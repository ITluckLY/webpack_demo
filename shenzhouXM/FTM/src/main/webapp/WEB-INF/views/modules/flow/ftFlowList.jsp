<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>流程管理</title>
  <meta name="decorator" content="default"/>
  <script type="text/javascript">
      $(document).ready(function () {

      });

      function page(n, s) {
          $("#pageNo").val(n);
          $("#pageSize").val(s);
          $("#searchForm").submit();
          return false;
      }

      function edit(id) {
          var formObj = $("#" + id);
          formObj.attr("action", "${ctx}/flow/ftFlow/form");
          formObj.submit();
      }

      <%--function removeFlow(id){--%>
      <%--var formObj = $("#" + id);--%>
      <%--formObj.attr("action", "${ctx}/flow/ftFlow/delete");--%>
      <%--formObj.submit();--%>
      <%--}--%>
  </script>
</head>
<body>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
  <shiro:hasPermission name="flow:ftFlow:view">
    <a href="${ctx}/flow/ftFlow/form">新增</a>
  </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftFlow" action="${ctx}/flow/ftFlow/" method="post"
           class="breadcrumb form-search marfl-new">
  <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
  <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
  <ul class="ul-form">
    <li><label>流程名称：</label>
      <form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
    </li>
    <li><label>流程描述：</label>
      <form:input path="des" htmlEscape="false" maxlength="256" class="input-medium"/>
    </li>
      <%-- <li><label>组件：</label>
           <form:input path="components" htmlEscape="false" maxlength="256" class="input-medium"/>
       </li>--%>
    <li class="btns">
      <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
      </button>
    </li>
      <%-- <li class="clearfix"></li>--%>
  </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed"
       style="width: 99%;TABLE-LAYOUT:fixed">
  <thead>
  <tr>
    <th width="10%">流程名称</th>
    <th width="35%">组件</th>
    <th width="10%">节点组</th>
    <shiro:hasPermission name="flow:ftFlow:edit">
      <th width="10%">流程处理</th>
    </shiro:hasPermission>
    <th width="25%">流程描述</th>
    <shiro:hasPermission name="flow:ftFlow:edit">
      <th width="10%">操作</th>
    </shiro:hasPermission>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${page.list}" var="ftFlow">
    <tr>
      <td><a onclick="edit('form_${ftFlow.id}')">
          ${ftFlow.name}
      </a></td>
      <td style="WORD-WRAP:break-word">
          ${ftFlow.components}
      </td>
      <td>
          ${ftFlow.systemName}
      </td>
      <shiro:hasPermission name="flow:ftFlow:edit">
        <td>
            <a href="${ctx}/flow/ftFlow/component?id=${ftFlow.id}&name=${ftFlow.name}&components=${ftFlow.components}&systemName=${ftFlow.systemName}&des=${ftFlow.des}">流程处理</a>
        </td>
      </shiro:hasPermission>
      <td>
          ${ftFlow.des}
      </td>
      <shiro:hasPermission name="flow:ftFlow:edit">
        <td>
          <c:choose>
            <c:when test="${ftFlow.systemName == '*'}">
              --
            </c:when>
            <c:otherwise>
              <form id="form_${ftFlow.id}">
                <input name="id" value="${ftFlow.id}" type="hidden">
                <input name="name" value="${ftFlow.name}" type="hidden">
                <input name="des" value="${ftFlow.des}" type="hidden">
                <input name="systemName" value="${ftFlow.systemName}" type="hidden">
                <input name="components" value='${ftFlow.components}' type="hidden">
                <a href="javascript:void(0)" onclick="edit('form_${ftFlow.id}')">修改</a>
                <a href="${ctx}/flow/ftFlow/delete?name=${ftFlow.name}&systemName=${ftFlow.systemName}"
                   onclick="return confirmx('确认要删除该流程吗？', this.href)">删除</a>
              </form>
            </c:otherwise>
          </c:choose>
        </td>
      </shiro:hasPermission>
    </tr>
  </c:forEach>
  </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>