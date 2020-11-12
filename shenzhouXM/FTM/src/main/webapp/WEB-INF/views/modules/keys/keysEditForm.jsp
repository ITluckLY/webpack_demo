<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>秘钥理管理</title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript">
		$(document).ready(function() {
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
					protocol:{
						nameCheck:true,
						required:true
					},
					ip:{
//						ipCheck:true,
						required:true
					},
					port:{
						portCheck:true,
						required:true
					},
					username:{
						nameCheck:true,
						required:true
					},
					password:{
						passwordCheck:true,
						required:true
					},
					repwd:{
						equalTo:"#password"
					}
				}
			});
		});
		function PwdHidden() {
			if (document.getElementById("pwdOne").style.display == "none") {
				document.getElementById("pwdOne").style.display = "block";
				document.getElementById("repwdpwd").style.display = "block";
				document.getElementById("password").value = "";
				$(document.getElementById("pwdInput")).val("隐藏密码");
			} else {
				document.getElementById("pwdOne").style.display = "none";
				document.getElementById("repwdpwd").style.display = "none";
				$(document.getElementById("pwdInput")).val("修改密码");

			}
		}
	</script>
</head>
<body>
<br/>
	<form:form id="inputForm" modelAttribute="ftKey" action="${ctx}/keys/ftKey/saveEdit" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">用户名称：</label>
			<div class="controls">
				<form:input path="user" htmlEscape="false" maxlength="50" class="input-xlarge required"  required="true" readonly="true" style="WIDTH: 250px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">	秘钥类型：</label>
			<div class="controls">
                <form:input path="type" htmlEscape="false" maxlength="50" class="input-xlarge "  required="true" id="type" name="type" style="WIDTH: 250px" readonly="true"/>			</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">	秘钥内容：</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="4" maxlength="2000" class="input-xlarge "  required="true" style="WIDTH: 350px"/>
				<span class="help-inline"><font color="red"> * </font></span>
            </div>
		</div>

		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>