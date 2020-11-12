<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>告警配置添加</title>
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
                  },
                  emailList: {
                      email: true,
                      demoCheck: true
                  },
                  telNoList: {
                      phone: true,
                      demoCheck: true
                  },
                  name: {
                      demoCheck: true
                  }
              }
          });
      });
  </script>

</head>
<body>
<form:form id="inputForm" modelAttribute="ftNodeMonitorMsgEmail" action="${ctx}/monitor/FtNodeMonitor/saveMsgEmail"
           method="post" class="form-horizontal">
  <form:hidden path="id"/>
  <sys:message content="${message}"/>
  <div class="control-group">
    <label class="control-label">分&nbsp;&nbsp;组&nbsp;&nbsp;名:</label>
    <div class="controls">
      <form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 300px"
                  required="true" id="name" name="name"/>&nbsp;&nbsp;
      <span class="help-inline"><font color="red"> * </font></span>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">电话号码:</label>
    <div class="controls">
      <form:input path="telNoList" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 300px"
                  id="telNoList" name="telNoList"/>&nbsp;&nbsp;
        <%--<span><a style="color: red"> * 电话号码之间用" ，"号（英文半角逗号）进行分割</a></span>--%>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">邮&nbsp;&nbsp;&nbsp;&nbsp;件：</label>
    <div class="controls">
      <form:input path="emailList" htmlEscape="false" maxlength="100" class="input-xlarge " style="WIDTH: 300px"
                  id="emailList" name="emailList"/>&nbsp;&nbsp;
        <%--<span><a style="color: red">  *  邮件地址之间用" ，"号（英文半角逗号）进行分割</a></span>--%>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">备&nbsp;&nbsp;&nbsp;&nbsp;注：</label>
    <div class="controls">
      <form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge "
                     style="WIDTH: 350px"/>
    </div>
  </div>

  <div class="form-actions">
    <shiro:hasPermission name="NodeMonitor:alarmLine:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                                  value="保 存"/>&nbsp;</shiro:hasPermission>
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
  </div>

</form:form>
</body>
</html>