<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>netty管理</title>
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


<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
  <shiro:hasPermission name="user0:ftUserDirAuth:edit">
    <a style="padding-right: 20px " href="${ctx}/user/ftUserNetty/addPrarm">新增公共权限</a>
    <a href="${ctx}/user/ftUserNetty/addChannelSpeed">新增用户宽带权限</a>
  </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftUserNetty" action="${ctx}/user/ftUserNetty/nettyList/" method="post" class="breadcrumb form-search marfl-new">
  <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
  <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

  <ul class="ul-form">
    <li><label>用户名：</label>
      <form:input path="userName" htmlEscape="false" maxlength="50" class="input-medium"/>
    </li>
    <li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
    <li class="clearfix"></li>
  </ul>
</form:form>


<sys:message content="${message}"/>
<br/>
<a>新增公共权限</a>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%" >
  <thead>
  <tr>
    <th>当前网络允许的最大传输速度(单位MB)</th>
    <th>最大的睡眠时间</th>
    <th>扫描时间(单位毫秒)</th>
    <shiro:hasPermission name="user0:ftUserDirAuth:edit">
      <th>操作</th>
    </shiro:hasPermission>
  </tr>
  </thead>
  <tbody style="height: 20%">
  <c:forEach items="${prarmList}" var="paeTemp">
    <tr>
      <td>
          ${paeTemp.maxSpeed}
      </td>
      <td>
          ${paeTemp.sleepTime}
      </td>
      <td>
          ${paeTemp.scanTime}
      </td>
      <shiro:hasPermission name="user0:ftUserDirAuth:edit">
        <td>
          <a href="${ctx}/user/ftUserNetty/updateP?maxSpeed=${paeTemp.maxSpeed}&sleepTime=${paeTemp.sleepTime}&scanTime=${paeTemp.scanTime}">修改</a><%-- &nettyId=${paeTemp.nettyId} --%>
          <a href="${ctx}/user/ftUserNetty/delPrarm?maxSpeed=${paeTemp.maxSpeed}&sleepTime=${paeTemp.sleepTime}&scanTime=${paeTemp.scanTime}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
        </td>
      </shiro:hasPermission>
    </tr>
  </c:forEach>
  </tbody>
</table>

<br/>
<a>新增用户宽带权限</a>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;height: 50%">
  <thead>
  <tr>
    <th>用户名</th>
    <th>读取宽带</th>
    <th>写入宽带</th>
    <shiro:hasPermission name="user0:ftUserDirAuth:edit">
      <th>操作</th>
    </shiro:hasPermission>
  </tr>
  </thead>
  <tbody style="height: 45%">
  <c:forEach items="${channelSpeedList}" var="gaeTemp">
    <tr>
      <td>
          ${gaeTemp.userName}
      </td>
      <td>
          ${gaeTemp.readLimit}
      </td>
      <td>
          ${gaeTemp.writeLimit}
      </td>
      <shiro:hasPermission name="user0:ftUserDirAuth:edit">
        <td>
          <a href="${ctx}/user/ftUserNetty/updateC?userName=${gaeTemp.userName}&readLimit=${gaeTemp.readLimit}&writeLimit=${gaeTemp.writeLimit}">修改</a>
          <a href="${ctx}/user/ftUserNetty/delChannel?userName=${gaeTemp.userName}&readLimit=${gaeTemp.readLimit}&writeLimit=${gaeTemp.writeLimit}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
        </td>
      </shiro:hasPermission>
    </tr>
  </c:forEach>
  </tbody>
</table>

</body>
</html>