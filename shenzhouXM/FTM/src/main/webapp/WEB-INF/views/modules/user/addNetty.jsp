<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
  <title>添加用户</title>
  <meta name="decorator" content="default"/>
  <script type="text/javascript">

      $(function() {
          //$("#name").focus();
          $("#inputForm").validate({
              submitHandler: function(form){
                  loading('正在提交，请稍等...');
                  form.submit();
              },
              errorContainer: "#messageBox",
              errorPlacement: function(error, element) {
                  $("#messageBox").text("输入有误，请先更正。");
                  if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
                      error.appendTo(element.parent().parent());
                  } else {
                      error.insertAfter(element);
                  }
              },
              rules:{
                  userDir:{
                      dire2Check:true
                  }
              }
          });
      });
  </script>
</head>
<body>

<form:form id="inputForm" modelAttribute="ftUserNetty" action="${ctx}/user/ftUserNetty/saveUserNetty" method="post" class="form-horizontal">
  <sys:message content="${message}"/>

  <div class="control-group">
    <label class="control-label">用户名称：</label>
    <div class="controls">
      <form:input path="userName" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="userName" name="userName"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">传输速度：</label>
    <div class="controls">
      <form:input path="maxsPeed" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="maxsPeed" name="maxsPeed"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">睡眠时间：</label>
    <div class="controls">
      <form:input path="sleepTime" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="sleepTime" name="sleepTime"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">扫描时间：</label>
    <div class="controls">
      <form:input path="scanTime" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="scanTime" name="scanTime"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">读取宽带：</label>
    <div class="controls">
      <form:input path="readLimit" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="readLimit" name="readLimit"/>
      <span class="help-inline"><font color="red"> * </font>最大宽带单位是MB</span>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">写入宽带：</label>
    <div class="controls">
      <form:input path="writeLimit" htmlEscape="false" maxlength="256" class="input-xlarge " style="WIDTH: 250px" required="true" id="writeLimit" name="writeLimit"/>
      <span class="help-inline"><font color="red"> * </font>最大宽带单位是MB</span>

    </div>
  </div>


  <div class="form-actions">
    <shiro:hasPermission name="user0:ftUserDirAuth:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
  </div>

</form:form>
</body>
</html>