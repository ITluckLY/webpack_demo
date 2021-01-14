<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>服务管理</title>
    <meta name="decorator" content="default"/>
</head>
<body>

<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/serviceinfo/ftServiceInfo/">服务管理列表</a></li>
    <li class="active">
        <a href="${ctx}/serviceinfo/ftServiceInfo/form">用户管理
            <shiro:hasPermission  name="serviceInfo:ftServiceInfo:edit">添加</shiro:hasPermission>
            <shiro:lacksPermission name="serviceInfo:ftServiceInfo:edit">查看</shiro:lacksPermission></a>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftServiceInfo" action="${ctx}/serviceinfo/ftServiceInfo/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">交易码：</label>
        <div class="controls">
            <form:input path="trancode" htmlEscape="false" maxlength="20" class="input-xlarge " required="true" style="WIDTH:250PX" id="trancode" name="trancode"/>
            <span class="help-inline"><font color="red"> * </font>交易码为由3位系统名+00+5位数字、小写字母组合</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">节点组：</label>
        <div class="controls">
            <form:input path="systemName" htmlEscape="false" maxlength="50" class="input-xlarge " required="true" style="WIDTH:250PX" disabled="true"/>
                <%--<form:select path="systemName" class="input-xlarge" style="WIDTH:265PX" multiple="true">--%>
                <%--<form:options items="${sysInfolist}" itemLabel="name" itemValue="name" htmlEscape="false"/>--%>
                <%--</form:select>--%>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">是否重命名：</label>
        <div class="controls">
            <form:select path="rename" class="input-xlarge" style="WIDTH:265PX">
                <form:option value="0">否&nbsp;&nbsp;</form:option>
                <form:option value="1">是&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">跨地域传输：</label>
        <div class="controls">
            <form:select path="cross" class="input-xlarge" style="WIDTH:280PX">
                <form:option value="0">否&nbsp;&nbsp;</form:option>
                <form:option value="1">是&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">文件保留期限(分钟)：</label>
        <div class="controls">
            <form:input path="filePeriod" htmlEscape="false" maxlength="8" class="input-xlarge " style="WIDTH:250PX"
                        required="true" id="filePeriod" name="filePeriod"/>
            <span class="help-inline"><font color="red"> * </font>(0:表示永久)</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">优先级：</label>
        <div class="controls">
            <form:input path="priority" htmlEscape="false" maxlength="1" class="input-xlarge " required="true"
                        style="WIDTH:250PX" id="priority" name="priority"/>
            <span class="help-inline"><font color="red"> * </font> 优先级为（1-5级）</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">最大并发数：</label>
        <div class="controls">
            <form:input path="size" htmlEscape="false" maxlength="10" class="input-xlarge " required="true"
                        style="WIDTH:250PX" id="size" name="size"/>
            <span class="help-inline"><font color="red"> * </font></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">相关流程：</label>
        <div class="controls">
            <form:select path="flow" class="input-xlarge" style="WIDTH:265PX">
                <form:options items="${ftFlowList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">校验流程：</label>
        <div class="controls">
            <form:select path="psFlow" class="input-xlarge" style="WIDTH:265PX">
                <form:options items="${ftPSFlowList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">交易码描述：</label>
        <div class="controls">
            <form:textarea path="describe" htmlEscape="false" rows="4" maxlength="100" class="input-xlarge" style="WIDTH:250PX"/>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="serviceinfo:ftServiceInfo:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

<script type="text/javascript">
    $(function () {
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
                trancode: {
                    tranCheck: false,
                    required: true
                },
                filePeriod: {
                    numCheck: true,
                    required: true
                },
                priority: {
                    priorityCheck: true,
                    required: true
                },
                size: {
                    numCheck: true,
                    required: true
                },
                describe: {
                    demoCheck: true
                }
            }
        });
    });
</script>
</body>
</html>