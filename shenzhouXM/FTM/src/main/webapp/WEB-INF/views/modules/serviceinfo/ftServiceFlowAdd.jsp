<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>新增交易码服务</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var tranCodeList = new Array();
        function check() {
            if(confirm("确认提交?")){
                var tranCode = $("#transCode").val();
                if(tranCode.length!=10 || tranCode.substr(3,2)!="00"){
                    alert("交易码格式不符合规范");
                    return false;
                }
                //console.log(tranCode);
                for(var x=0;x<tranCodeList.length;x++){
                    //console.log(x+":"+tranCodeList[x]);
                    if(tranCode==tranCodeList[x]){
                        alert("交易码已存在,不能重复添加");
                        $("input[type='text']").val("");
                        return false;
                    }
                }
                $("#btnSubmit").attr("disabled","true");
                return true;
            }
            return false;

        }
        $(document).ready(function () {
            //$("#name").focus();
            var i=0;
            <c:forEach items="${tranCodeList}" var="tranCode">
                tranCodeList[i++] = "${tranCode}";
            </c:forEach>
            console.log(tranCodeList);
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
            <form:input type="text" path="transCode" style="WIDTH:265PX" id="transCode" name="transCode" required="true"/>
            <span class="help-inline"><font color="red"> * </font>交易码为由3位系统名+00+5位数字、小写字母组合</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">生产方系统：</label>
        <div class="controls">
               <form:select path="producer" class="input-xlarge" style="WIDTH:280PX" >
                   <form:options items="${providerList}" htmlEscape="false"/>
               </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">消费方系统：</label>
        <div class="controls">
                <form:select path="customer" class="input-xlarge" style="WIDTH:280PX" multiple="true" required="true">
                    <form:options items="${providerList}"  htmlEscape="false"/>
                </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">相关流程：</label>
        <div class="controls">
            <form:select path="ftServiceInfo.flow" class="input-xlarge" style="WIDTH:280PX">
                <form:options items="${ftFlowList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">是否重命名：</label>
        <div class="controls">
            <form:select path="ftServiceInfo.rename" class="input-xlarge" style="WIDTH:280PX">
                <form:option value="0">否&nbsp;&nbsp;</form:option>
                <form:option value="1">是&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">跨地域传输：</label>
        <div class="controls">
            <form:select path="ftServiceInfo.cross" class="input-xlarge" style="WIDTH:280PX">
                <form:option value="0">否&nbsp;&nbsp;</form:option>
                <form:option value="1">是&nbsp;&nbsp;</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">文件保留期限(分钟)：</label>
        <div class="controls">
            <form:input path="ftServiceInfo.filePeriod" htmlEscape="false" maxlength="8" class="input-xlarge " style="WIDTH:265PX"  required="true"
                        id="filePeriod"  name="filePeriod"/>
            <span class="help-inline"><font color="red"> * </font>(0:表示永久)</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">优先级：</label>
        <div class="controls">
            <form:input path="ftServiceInfo.priority" htmlEscape="false" maxlength="1" class="input-xlarge " required="true"
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
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
            <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        </shiro:hasPermission>
    </div>

</form:form>
</body>
</html>
