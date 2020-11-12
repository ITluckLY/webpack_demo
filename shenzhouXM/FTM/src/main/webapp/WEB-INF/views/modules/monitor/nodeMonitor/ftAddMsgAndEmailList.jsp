<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>告警配置</title>
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
  </script>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
  <li class="active"><a href="${ctx}/monitor/FtNodeMonitor/addMsgAndEmailList">告警配置列表</a></li>
  <shiro:hasPermission name="NodeMonitor:alarmNamelist:edit">
    <li><a href="${ctx}/monitor/FtNodeMonitor/addMsgAndEmailForm">告警配置添加</a></li>
  </shiro:hasPermission>
</ul>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
  <shiro:hasPermission name="NodeMonitor:alarmNamelist:edit">
    <a href="${ctx}/monitor/FtNodeMonitor/addMsgAndEmailForm">新增</a>
  </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftNodeMonitorMsgEmail"
           action="${ctx}/monitor/FtNodeMonitor/addMsgAndEmailList"
           method="post" class="breadcrumb form-search marfl-new">
  <ul class="ul-form">
      <%-- <li><label>分组名：</label>
          <form:input path="name" htmlEscape="false" maxlength="256" class="input-medium"/>
      </li>
  <li class="btns">
      <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
      </button>
  </li>--%>
    <li class="clearfix"></li>
  </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed"
       style="width: 99%;TABLE-LAYOUT:fixed">
  <thead>
  <tr>
    <%--<th>序号</th>--%>
    <th>分组名</th>
    <th>电话号码</th>
    <th>邮件</th>
    <%--<th>发送内容</th>--%>
    <%--<th>发送时间</th>--%>
    <th width="20%">备注</th>
    <shiro:hasPermission name="NodeMonitor:alarmNamelist:edit">
      <th width="8%">操作</th>
    </shiro:hasPermission>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${page.list}" var="msgEmailList">
    <tr>
        <%--<td>${msgEmailList.id}</td>--%>
      <td style="WORD-WRAP:break-word">${msgEmailList.name}</td>
      <td style="WORD-WRAP:break-word">${msgEmailList.telNoList}</td>
      <td style="WORD-WRAP:break-word">${msgEmailList.emailList}</td>
        <%--<td>${msgEmailList.sendMsg}</td>--%>
        <%--<td><fmt:formatDate value="${msgEmailList.createDate}" type="both"/></td>--%>
      <td style="WORD-WRAP:break-word">${msgEmailList.remarks}</td>
      <shiro:hasPermission name="NodeMonitor:alarmNamelist:edit">
        <td>
          <a href="${ctx}/monitor/FtNodeMonitor/addMsgAndEmailForm?id=${msgEmailList.id}">修改</a>
          <a href="${ctx}/monitor/FtNodeMonitor/deleteMsgEmail?id=${msgEmailList.id}"
             onclick="return confirmx('确认要删除该告警名单吗？', this.href)">删除</a>
        </td>
      </shiro:hasPermission>
    </tr>
  </c:forEach>
  </tbody>
</table>
<div class="pagination"></div>
</body>
</html>