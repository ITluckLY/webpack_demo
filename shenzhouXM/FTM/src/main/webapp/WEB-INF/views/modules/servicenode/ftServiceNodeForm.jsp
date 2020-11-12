<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>节点管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        function checkType() {
            if (document.getElementById("type").value == "datanode") {
                document.getElementById("systemName").style.display = "block";
                document.getElementById("cmdPort01").style.display = "block";
                document.getElementById("ftpServPort01").style.display = "block";
                document.getElementById("ftpManagePort01").style.display = "block";
                document.getElementById("receivePort01").style.display = "block";
                document.getElementById("nodeModelLabel").style.display = "block";
            } else if (document.getElementById("type").value == "lognode") {
                document.getElementById("systemName").style.display = "none";
                document.getElementById("cmdPort01").style.display = "block";
                document.getElementById("ftpServPort01").style.display = "none";
                document.getElementById("ftpManagePort01").style.display = "none";
                document.getElementById("receivePort01").style.display = "none";
                document.getElementById("nodeModelLabel").style.display = "none";
            } else if (document.getElementById("type").value == "namenode") {
                document.getElementById("systemName").style.display = "none";
                document.getElementById("cmdPort01").style.display = "block";
                document.getElementById("ftpServPort01").style.display = "block";
                document.getElementById("ftpManagePort01").style.display = "block";
                document.getElementById("receivePort01").style.display = "block";
                document.getElementById("nodeModelLabel").style.display = "none";
            }
        }

        function checkValue(systemNameValue) {
            var sysNameValue = systemNameValue.value;
            $.ajax({
                url: "${ctx}/servicenode/ftServiceNode/checkForSys",
                type: "POST",
                data: {sysName: sysNameValue},
                dataType: "text",
                success: function (data) {
                    if (data == "true") {
                        document.getElementById("nodeModelLabel").style.display = "block";
                    }
                    if (data == "false") {
                        document.getElementById("nodeModelLabel").style.display = "none";
                    }
                    if (data == "isSingle") {
                        document.getElementById("nodeModelLabel").style.display = "none";
                        alert("节点组：" + sysNameValue + " 是单节点模式，不允许添加两个以上节点！");
                        document.getElementById("nameNodeType");
                    }
                }
            });
        }

        $(function () {
            var updateFlag = document.getElementById("addOrUpdateValue").value;
            if (updateFlag == 1) {
                $("#type").attr('readonly', true);
                $("#name").attr('readonly', true);
                $("#stateType").attr('readonly', true);
                $("#nameRedColor").attr('hidden', true);
                $("#systemNameValue").attr('readonly', true);
            }
        });

        $(document).ready(function () {
            $("#stateType").attr('readonly', true);

            var sysNameValue = null;
            if (document.getElementById("systemNameValue") != null) {
                sysNameValue = document.getElementById("systemNameValue").value;
            }
            $.ajax({
                url: "${ctx}/servicenode/ftServiceNode/checkForSys",
                type: "POST",
                data: {sysName: sysNameValue},
                dataType: "text",
                success: function (data) {
                    if (data == "true") {
                        document.getElementById("nodeModelLabel").style.display = "block";
                    }
                    if (data == "false" && document.getElementById("nodeModelLabel") != null) {
                        document.getElementById("nodeModelLabel").style.display = "none";
                    }
                    if (data == "isSingle" && document.getElementById("nodeModelLabel") != null) {
                        document.getElementById("nodeModelLabel").style.display = "none";
                    }
                }
            });


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
                    name: {
                        nodeNameCheck: true,
                        required: true
                    },
                    ipAddress: {
                        ipCheck: true,
                        required: true
                    },
                    cmdPort: {
                        portCheck: true,
                        required: true
                    },
                    ftpServPort: {
                        portCheck: true,
                        required: true
                    },
                    ftpManagePort: {
                        portCheck: true,
                        required: true
                    },
                    receivePort: {
                        portCheck: true,
                        required: true
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs nav-tabs-hidden">
    <li><a href="${ctx}/servicenode/ftServiceNode/">节点列表</a></li>
    <li class="active"><a href="${ctx}/servicenode/ftServiceNode/form?name=${ftServiceNode.name}">节点<shiro:hasPermission
            name="servicenode:ftServiceNode:edit">${not empty ftServiceNode.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="servicenode:ftServiceNode:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="ftServiceNode" action="${ctx}/servicenode/ftServiceNode/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="addOrUpdate" id="addOrUpdateValue"/>
    <sys:message content="${message}"/>
    <input type="hidden" name="opt" value="${opt}"/>

    <div class="control-group">
        <label class="control-label">类型：</label>
        <div class="controls">
            <form:select path="type" htmlEscape="false" maxlength="50" class="input-xlarge " onclick="checkType()"
                         id="type" style="WIDTH: 265px">
                <c:if test="${ftServiceNode.type == null}">
                    <form:option value="datanode">datanode(数据节点)&nbsp;&nbsp;</form:option>
                    <form:option value="namenode">namenode(命名节点)&nbsp;&nbsp;</form:option>
                    <form:option value="lognode">lognode(日志节点)&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftServiceNode.type == 'datanode'}">
                    <form:option value="datanode">datanode(数据节点)&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftServiceNode.type == 'namenode'}">
                    <form:option value="namenode">namenode(命名节点)&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftServiceNode.type == 'lognode'}">
                    <form:option value="lognode">lognode(日志节点)&nbsp;&nbsp;</form:option>
                </c:if>
            </form:select>

        </div>
    </div>
    <div class="control-group" >
        <label class="control-label">名称：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" minlength="4" maxlength="20" class="input-xlarge " style="WIDTH: 250px"
                        required="true" id="name" name="name"/><%--<a
                style="color: red;" id="nameRedColor">注：添加之后不能进行修改。</a>--%>
            <span class="help-inline"><font color="red"> *【添加之后不能修改】 </font>英文字母开头，后三位为数字（4-20位）例如：A110</span>
        </div>

    </div>
    </div>
    <c:if test="${ftServiceNode.type == 'datanode' || ftServiceNode.type == null}">
        <div class="control-group" id="systemName">
            <label class="control-label">节点组：</label>
            <div class="controls">
                <form:select path="systemName" class="input-xlarge" style="WIDTH: 265px" id="systemNameValue"
                             onclick="checkValue(systemNameValue)">
                    <form:options items="${systemNameList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
                </form:select>
            </div>
        </div>
    </c:if>

    <c:if test="${ftServiceNode.type == 'datanode' || ftServiceNode.type == null}">
        <div class="control-group" id="nodeModelLabel">
            <label class="control-label">节点模式：</label>
            <div class="controls">
                <form:select path="nodeModel" htmlEscape="false" maxlength="50" class="input-xlarge "
                             style="WIDTH: 265px">
                    <form:option value="ms-m">主备模式-主&nbsp;&nbsp;</form:option>
                    <form:option value="ms-s">主备模式-备&nbsp;&nbsp;</form:option>
                </form:select>
            </div>
        </div>
    </c:if>

    <div class="control-group">
        <label class="control-label">IP地址：</label>
        <div class="controls">
            <form:input path="ipAddress" htmlEscape="false" maxlength="15" class="input-xlarge " style="WIDTH: 250px"
                        required="true" id="ipAddress" name="ipAddress"/>
            <span class="help-inline"><font color="red"> * </font>例如：192.168.1.110</span>
        </div>
    </div>
    <%--<c:if test="${ftServiceNode.type != 'lognode'}">--%>
        <div class="control-group" id="cmdPort01">
            <label class="control-label">命令端口：</label>
            <div class="controls">
                <form:input path="cmdPort" htmlEscape="false" maxlength="5" class="input-xlarge " style="WIDTH: 250px"
                            required="true" id="cmdPort" name="cmdPort"/>
                <span class="help-inline"><font color="red"> * </font></span>
            </div>
        </div>
    <%--</c:if>--%>
    <c:if test="${ftServiceNode.type != 'lognode'}">
        <div class="control-group" id="ftpServPort01">
            <label class="control-label">服务端口：</label>
            <div class="controls">
                <form:input path="ftpServPort" htmlEscape="false" maxlength="5" class="input-xlarge "
                            style="WIDTH: 250px"
                            required="true" id="ftpServPort" name="ftpServPort"/>
                <span class="help-inline"><font color="red"> * </font></span>
            </div>
        </div>
    </c:if>
    <c:if test="${ftServiceNode.type != 'lognode'}">
        <div class="control-group" id="ftpManagePort01">
            <label class="control-label">管理端口：</label>
            <div class="controls">
                <form:input path="ftpManagePort" htmlEscape="false" maxlength="5" class="input-xlarge "
                            style="WIDTH: 250px" required="true" id="ftpManagePort" name="ftpManagePort"/>
                <span class="help-inline"><font color="red"> * </font></span>
            </div>
        </div>
    </c:if>
    <c:if test="${ftServiceNode.type != 'lognode'}">
        <div class="control-group" id="receivePort01">
            <label class="control-label">接收端口：</label>
            <div class="controls">
                <form:input path="receivePort" htmlEscape="false" maxlength="5" class="input-xlarge "
                            style="WIDTH: 250px"
                            required="true" id="receivePort" name="receivePort"/>
                <span class="help-inline"><font color="red"> * </font></span>
            </div>
        </div>
    </c:if>
    <div class="control-group">
        <label class="control-label">运行状态：</label>
        <div class="controls">
            <form:select path="state" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 265px"
                         id="stateType" name="stateType">

                <%--<c:if test="${ftServiceNode.state=='0'}">
                    <form:option value="0">未启用&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftServiceNode.state=='1'}">
                    <form:option value="1">运行中&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftServiceNode.state!='0'&&ftServiceNode.state!='1'}">
                    <form:option value="0" selected="true">未启用&nbsp;&nbsp;</form:option>
                    <form:option value="1">运行中&nbsp;&nbsp;</form:option>
                </c:if>--%>
                <form:option value="0">未启用&nbsp;&nbsp;</form:option>
                <form:option value="1">运行中&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">隔离状态：</label>
        <div class="controls">
            <form:select path="isolState" htmlEscape="false" maxlength="50" class="input-xlarge " style="WIDTH: 265px">

                <%--<c:if test="${ftServiceNode.isolState=='0'}">--%>
                <form:option value="0">正常&nbsp;&nbsp;</form:option>
                <form:option value="1">隔离&nbsp;&nbsp;</form:option>
                <%--</c:if>--%>
                <%--<c:if test="${ftServiceNode.isolState=='1'}">
                    <form:option value="1">隔离&nbsp;&nbsp;</form:option>
                    <form:option value="0">正常&nbsp;&nbsp;</form:option>
                </c:if>
                <c:if test="${ftServiceNode.isolState!='0'&&ftServiceNode.isolState!='1'}">
                    <form:option value="0" selected="true">正常&nbsp;&nbsp;</form:option>
                    <form:option value="1">隔离&nbsp;&nbsp;</form:option>
                </c:if>--%>
            </form:select>
        </div>
    </div>


    <div class="form-actions">
        <shiro:hasPermission name="servicenode:ftServiceNode:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
            &nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>