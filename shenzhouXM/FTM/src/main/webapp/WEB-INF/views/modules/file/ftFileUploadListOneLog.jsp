<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>版本发布历史记录管理</title>
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
<ul class="nav nav-tabs">
  <li class="active"><a>版本发布历史记录</a></li>
  <sys:message content="${message}"/>
  <table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
    <thead>
    <tr>
      <th>版本发布列表名</th>
      <th>列表值</th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td>服务端文件名称</td>
      <td>${ftFileUploadLogTemp.renameFileName}</td>
    </tr>
    <tr>
      <td>客户端文件名称</td>
      <td>${ftFileUploadLogTemp.fileName}</td>
    </tr>
    <tr>
      <td>上传系统</td>
      <td>${ftFileUploadLogTemp.systemName}</td>
    </tr>
    <tr>
      <td>上传用户</td>
      <td>${ftFileUploadLogTemp.uploadUser}</td>
    </tr>
    <tr>
      <td>客户端上传时间</td>
      <td><fmt:formatDate value="${ftFileUploadLogTemp.createDate}" type="both"/></td>
    </tr>
    <tr>
      <td>响应代码</td>
      <td>${ftFileUploadLogTemp.retCode}</td>
    </tr>
    <tr>
      <td>响应消息</td>
      <td>${ftFileUploadLogTemp.retMsg}</td>
    </tr>
    <tr>
      <td>接收节点</td>
      <td>${ftFileUploadLogTemp.sendNodeName}</td>
    </tr>
    <tr>
      <td>监控端接收文件</td>
      <td>${ftFileUploadLogTemp.realFileName}</td>
    </tr>
    <tr>
      <td>监控端原文件备份</td>
      <td>${ftFileUploadLogTemp.bakFileName}</td>
    </tr>
    <tr>
      <td>版本发布时间</td>
      <td><fmt:formatDate value="${ftFileUploadLogTemp.updateDate}" type="both"/></td>
    </tr>
    <tr>
      <td>备注</td>
      <td>${ftFileUploadLogTemp.remarks}</td>
    </tr>
    </tbody>
  </table>

  <div class="form-actions">
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
  </div>
</body>
</html>