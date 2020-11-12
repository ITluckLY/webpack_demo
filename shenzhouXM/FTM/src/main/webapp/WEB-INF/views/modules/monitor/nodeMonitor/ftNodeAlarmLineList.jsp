<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>监控默认告警线</title>
  <meta name="decorator" content="default"/>
  <script src="${ctxStatic}/selfDefine/formValidate.js" type="text/javascript"></script>
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
                  filenumberwarn: {
                      numCheck: true
                  },
                  filenumberline: {
                      numCheck: true
                  },
                  storagewarn: {
                      percCheck: true
                  },
                  storageline: {
                      percCheck: true
                  },
                  cpuwarn: {
                      percCheck: true
                  },
                  cpuline: {
                      percCheck: true
                  },
                  diskwarn: {
                      percCheck: true
                  },
                  diskline: {
                      percCheck: true
                  },
                  memorywarn: {
                      percCheck: true
                  },
                  memoryline: {
                      percCheck: true
                  },
                  networkwarn: {
                      percCheck: true
                  },
                  networkline: {
                      percCheck: true
                  }
              }
          });
      });
  </script>
</head>
<body>
<form:form id="inputForm" modelAttribute="ftNodeAlarmLine" action="${ctx}/monitor/FtNodeMonitor/saveAlarmLineList"
           method="post"
           class="form-horizontal">
  <form:hidden path="id"/>
  <sys:message content="${message}"/>
  <br/>
  <div class="control-group">
    <label class="control-label" style="WIDTH: 200px">默认文件数一般告警线： </label>
    <div class="controls">
      <form:input path="filenumberwarn" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："400"</span>
    </div>
    <br/>
    <label class="control-label" style="WIDTH: 200px">默认文件数严重告警线： </label>
    <div class="controls">
      <form:input path="filenumberline" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："600"</span>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" style="WIDTH: 200px">默认存储空间一般告警线： </label>
    <div class="controls">
      <form:input path="storagewarn" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："40%"</span>
    </div>
    <br/>
    <label class="control-label" style="WIDTH: 200px">默认存储空间严重告警线： </label>
    <div class="controls">
      <form:input path="storageline" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："60%"</span>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" style="WIDTH: 200px">默认CPU使用率一般告警线： </label>
    <div class="controls">
      <form:input path="cpuwarn" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："40%"</span>
    </div>
    <br/>
    <label class="control-label" style="WIDTH: 200px">默认CPU使用率严重告警线： </label>
    <div class="controls">
      <form:input path="cpuline" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："60%"</span>
    </div>
  </div>
  <div class="control-group">

    <label class="control-label" style="WIDTH: 200px">默认内存使用率一般告警线： </label>
    <div class="controls">
      <form:input path="memorywarn" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："40%"</span>
    </div>
    <br/>
    <label class="control-label" style="WIDTH: 200px">默认内存使用率严重告警线： </label>
    <div class="controls">
      <form:input path="memoryline" htmlEscape="false" maxlength="20" class="input-xlarge required" required="true"
                  style="WIDTH: 250px"/>
      <span class="help-inline"><font color="red"> * </font>例如："60%"</span>
    </div>
  </div>
  <div class="form-actions">
    <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                      type="submit"
                                                                      value="保 存"/>&nbsp;</shiro:hasPermission>
      <%--<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>--%>
  </div>
</form:form>
</body>
</html>