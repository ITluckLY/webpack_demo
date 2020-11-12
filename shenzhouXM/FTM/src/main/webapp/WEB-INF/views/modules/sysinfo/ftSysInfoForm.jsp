<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>业务系统管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        $(document).ready(function () {
            if (document.getElementById("type").value == "single") {
                document.getElementById("switchModelLabel").style.display = "none";
            } else if (document.getElementById("type").value == "more") {
                document.getElementById("switchModelLabel").style.display = "none";
            } else if (document.getElementById("type").value == "ms") {
                document.getElementById("switchModelLabel").style.display = "block";
            }

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
                rules:{
                    name: {remote: "${ctx}/sysInfo/ftSysInfo/checkSysName?systemName=" + encodeURIComponent('${ftSysInfo.name}')},
                    des:{
                        demoCheck:true
                    }
                },
                messages: {
                    name: {remote: "节点组已存在"},
                }
            });
        });


       /* function checkSysName(sysName){
            var sysNameValue = sysName.value;
            exp = /^[a-z][a-z0-9]{2,19}$/i;
            var reg = sysNameValue.match(exp);
            if (reg == null) {
                alert("系统名不合法（请用英文字母开头加上英文或数字,且长度在3-20位之间），请重新输入！");
                document.getElementById("sysName").value="";
                return;
            };
            if(!(sysNameValue == null || sysNameValue=="")){
                $.ajax({
                    url: "${ctx}/sysInfo/ftSysInfo/checkSysName",
                    type: "POST",
                    data: {sysName: sysNameValue},
                    dataType: "text",
                    success: function (data) {
                        if(data=="true"){
                            alert("系统名：" + sysNameValue + " 已经存在，请重新输入一个系统名。");
                            document.getElementById("sysName").value="";
                        }
                    }
                });
            }
        }*/

        function checkType() {
            if (document.getElementById("type").value == "single") {
                document.getElementById("switchModelLabel").style.display = "none";
            } else if (document.getElementById("type").value == "more") {
                document.getElementById("switchModelLabel").style.display = "none";
            } else if (document.getElementById("type").value == "ms") {
                document.getElementById("switchModelLabel").style.display = "block";
            }
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/sysInfo/ftSysInfo/">系统列表</a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftSysInfo" action="${ctx}/sysInfo/ftSysInfo/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">节点组名称：</label>
        <div class="controls">
            <%--<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge " style="WIDTH:235PX" onblur="checkSysName(sysName)" id="sysName"
                        name="sysName" required="true"/>--%>
                <input id="systemName" name="systemName" type="hidden" value="${ftSysInfo.name}">
                <form:input path="name" htmlEscape="false" minlength="3" maxlength="20" class="input-xlarge " style="WIDTH:235PX"
                            required="true"/>
                <span class="help-inline"><font color="red"> * </font>必填，英文字母开头+英文、数字或下划线的组合（3-20位）</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">节点模式：</label>
        <div class="controls">
            <form:select path="sysNodeModel" htmlEscape="false" maxlength="50" class="input-xlarge "
                         id="type" style="WIDTH:250PX" onclick="checkType(type)">
                <form:option value="single">单节点模式&nbsp;&nbsp;</form:option>
                <form:option value="more">多节点并行模式&nbsp;&nbsp;</form:option>
                <form:option value="ms">主备模式&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group" id="switchModelLabel">
        <label class="control-label">主备切换模式：</label>
        <div class="controls">
            <form:select path="switchModel" htmlEscape="false" maxlength="50" class="input-xlarge"
                          style="WIDTH:250PX">
                <form:option value="auto">自动切换模式&nbsp;&nbsp;</form:option>
                <form:option value="handle">手动切换模式&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">文件存储模式：</label>
        <div class="controls">
            <form:select path="storeModel" htmlEscape="false" maxlength="50" class="input-xlarge"
                          style="WIDTH:250PX">
                <form:option value="single">单点模式&nbsp;&nbsp;</form:option>
                <form:option value="sync">同步模式&nbsp;&nbsp;</form:option>
                <form:option value="async">异步模式&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">节点组描述：</label>
            <div class="controls">
                <form:textarea path="des" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge "
                               style="WIDTH: 350px" />
            </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="sysInfo:ftSysInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                                  value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>