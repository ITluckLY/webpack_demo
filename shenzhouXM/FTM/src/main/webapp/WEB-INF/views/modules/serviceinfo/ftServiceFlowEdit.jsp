<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>修改交易码服务</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function check() {
            if(confirm("确认提交?")){
                $("#btnSubmit").attr("disabled","true");
                return true;
            }
            return false;
        }

        function getService(s) {
            $.ajax({
                url: "${ctx}/serviceinfo/ftServiceImports/getService?tranCode="+$(s).val(),
                type: "post",
                success: function (data) {
                    var putUsers = data.putAuth.users;
                    var getUsers = data.getAuth.users;
                    var getArray = new Array();
                    for(var i=0;i <getUsers.length;i++){
                            var user = getUsers[i].user;
                            getArray.push(user);
                        }
                    for(var i=0;i <putUsers.length;i++){
                        var user = putUsers[i].user;
                        $("#producer").val(user).select2();
                    }
                    var des = data.describe.split(/[\[\]]/)[0];
                    var fileName = data.describe.split(/[\[\]]/)[1];
                    $("#customer").val(getArray).trigger("change")
                    $("#flow").val(data.flow);
                    $("#rename").val(data.rename);
                    $("#cross").val(data.cross);
                    $("#filePeriod").val(data.filePeriod);
                    $("#priority").val(data.priority);
                    $("#size").val(data.size);
                    $("#des").val(des);
                    $("#fileName").val(fileName);
                },
                error: function (data) {
                    alert("未获取到此交易码服务");
                }
            });
        }

        $(document).ready(function () {

        });
    </script>
</head>
<body>


<form:form id="inputForm" modelAttribute="ftServiceFlowVo" action="${ctx}/serviceinfo/ftServiceImports/add" method="post"
           class="form-horizontal" enctype="multipart/form-data" onsubmit="return check();" >
    <br/>
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">交易码：</label>
        <div class="controls">
            <form:select id="transCode" path="transCode" class="input-xlarge" style="WIDTH:280PX" onchange="getService(this);">
                <form:option value="请选择"/>
                <form:options items="${tranCodeList}" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">生产方系统：</label>
        <div class="controls">
               <form:select id="producer" path="producer" class="input-xlarge" style="WIDTH:280PX" >
                   <form:options items="${providerList}" htmlEscape="false"/>
               </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">消费方系统：</label>
        <div class="controls">
                <form:select id="customer" path="customer" class="input-xlarge" style="WIDTH:280PX" multiple="true" required="true">
                    <form:options items="${providerList}"  htmlEscape="false"/>
                </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">相关流程：</label>
        <div class="controls">
            <form:select id="flow" path="ftServiceInfo.flow" class="input-xlarge" style="WIDTH:280PX">
                <form:options items="${ftFlowList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">是否重命名：</label>
        <div class="controls">
            <form:select id="rename" path="ftServiceInfo.rename" class="input-xlarge" style="WIDTH:280PX">
                <form:option value="0">否&nbsp;&nbsp;</form:option>
                <form:option value="1">是&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">跨地域传输：</label>
        <div class="controls">
            <form:select id="cross"  path="ftServiceInfo.cross" class="input-xlarge" style="WIDTH:280PX">
                <form:option value="0">否&nbsp;&nbsp;</form:option>
                <form:option value="1">是&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">文件保留期限(分钟)：</label>
        <div class="controls">
            <form:input  path="ftServiceInfo.filePeriod" htmlEscape="false" maxlength="8" class="input-xlarge " style="WIDTH:265PX"  required="true"
                        id="filePeriod"  name="filePeriod"/>
            <span class="help-inline"><font color="red"> * </font>(0:表示永久)</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">优先级：</label>
        <div class="controls">
            <form:input  path="ftServiceInfo.priority" htmlEscape="false" maxlength="1" class="input-xlarge " required="true"
                        style="WIDTH:265PX" id="priority" name="priority"/>
            <span class="help-inline"><font color="red"> * </font>优先级为（1-5级）</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">最大并发数：</label>
        <div class="controls">
            <form:input path="ftServiceInfo.size" htmlEscape="false" maxlength="10" class="input-xlarge " required="true"
                        style="WIDTH:265PX" id="size" name="size"/>
            <span class="help-inline"><font color="red"> * </font></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">交易描述：</label>
        <div class="controls">
            <input type="text" style="WIDTH:265PX" id="des" name="des"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">文件名：</label>
        <div class="controls">
            <input type="text" style="WIDTH:265PX" id="fileName" name="fileName"/>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="serviceinfo:ftServiceImports:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="修 改"/>&nbsp;
            <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        </shiro:hasPermission>
    </div>

</form:form>
</body>
</html>
