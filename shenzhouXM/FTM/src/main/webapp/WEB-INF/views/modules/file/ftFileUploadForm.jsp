<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>文件上传管理</title>
  <meta name="decorator" content="default"/>
  <script type="text/javascript">


      $(document).ready(function () {
          //$("#name").focus();
          $("#inputForm").validate({
              submitHandler: function (form) {
                  loading('正在提交，请稍等...');
                  form.submit();
              },
              errorContainer: "#messageBox",
              errorPlacement: function (error, element) {
                  $("#messageBox").text("输入有误，请先更正。");
                  if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                      error.appendTo(element.parent().parent());
                  } else {
                      error.insertAfter(element);
                  }
              },
              rules: {
                  remarks: {
                      demoCheck: true
                  }
              }
          });
      });
  </script>
</head>
<body>

<ul class="nav nav-tabs">
  <li><a a href="${ctx}/file/ftFileUpload/list">文件上传列表</a></li>
  <li class="active">
	  <a href="${ctx}/file/ftFileUpload/form">上传
		  <shiro:hasPermission name="file:ftFileUpload:edit"></shiro:hasPermission>
		  <shiro:lacksPermission name="file:ftFileUpload:edit">查看</shiro:lacksPermission></a>
  </li>
</ul>
<%--<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/file/ftFileUpload/form">文件上传管理<shiro:hasPermission name="file:ftFileUpload:edit">${not empty ftFileUpload.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="file:ftFileUpload:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>--%>
<form:form id="inputForm" modelAttribute="ftFileUpload" action="${ctx}/file/ftFileUpload/save" method="post"
           class="form-horizontal" enctype="multipart/form-data">
  <form:hidden path="systemName"/>
  <form:hidden path="nodeName"/>
  <form:hidden path="uploadUser"/>
  <sys:message content="${message}"/>
  <div class="control-group">
    <label class="control-label">文件名称：</label>
    <div class="controls">
      <input type="file" id="uploadFileName" name="uploadFileName" required="true"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">备注：</label>
    <div class="controls">
      <form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="80" class="input-xxlarge "/>
    </div>
  </div>
  <div class="form-actions">
    <shiro:hasPermission name="file:ftFileUpload:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                              value="保 存"/>&nbsp;</shiro:hasPermission>
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
  </div>
</form:form>
</body>
</html>