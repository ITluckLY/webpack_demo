<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>编辑</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        $(function(){
            var tran_code = "${tranCode}";
            //console.log("tran_code:"+tran_code);
            //如果是本页是修改页面：
            if(tran_code!=NaN && tran_code!=""){
                $("#tran_code").children("option").each(function(){
                    if($(this).val==tran_code){
                        $(this).attr("selected","selected");
                    }
                });
                $("#btnSubmit").val("保存修改");
                $("ul").find("a").eq(1).text("修改");
                //console.log("123");
                //$("#tran_code").attr("disabled","disabled");
                $("#putUser").val("${ftTranCodeAlarmLine.putUser}");
                $("#getUser").val("${ftTranCodeAlarmLine.getUser}");
                $("#fileName").val("${ftTranCodeAlarmLine.fileName}");
                $("#timeout").val(${ftTranCodeAlarmLine.timeout});
            }else{
                changed();
            }
        });

        function changed(){
            var trancode = $("#tran_code").val();
            //console.log("changed trancode:"+trancode);
            $.ajax({
                url:"${ctx}/monitor/FtNodeMonitor/getTranCodeUser",
                type:"post",
                dataType:"json",
                data:{"trancode":trancode},
                success:function (data) {
                    var putuser = data.putUser;
                    var getuser = data.getUser;
                    //console.log(putuser+","+getuser);
                    $("#getUser").val(getuser);
                    $("#putUser").val(putuser);
                }

            });

        }

    </script>

</head>
<body>


<ul class="nav nav-tabs">
    <li><a href="${ctx}/monitor/FtNodeMonitor/tranCodeAlarmLineList">交易码告警阈值</a></li>
    <shiro:hasPermission name="monitor:ftTranCodeAlarmLine:edit"> <li class="active"><a href="#">新增</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="ftTranCodeAlarmLine" action="${ctx}/monitor/FtNodeMonitor/saveTranCodeAlarmLine" method="post"
           class="breadcrumb form-search form-horizontal" enctype="multipart/form-data">
    <div class="control-group">
        <label class="control-label">交易码：</label>
        <div class="controls">
            <form:select path="tranCode" id="tran_code" class="input-xlarge" onchange="changed()" style="WIDTH:220PX">
                <form:options items="${tranCodeList}" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">生产方：</label>
        <div class="controls">
            <input type="text" id="putUser" name="putUser" readonly="readonly"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">消费方：</label>
        <div class="controls">
            <input type="text" id="getUser" name="getUser" readonly="readonly"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">超时时间(min)：</label>
        <div class="controls">
            <input type="number" id="timeout" name="timeout" required="true"/>
        </div>
    </div>


    <div class="form-actions">
        <shiro:hasPermission name="monitor:ftTranCodeAlarmLine:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存新增"/>&nbsp;
            <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        </shiro:hasPermission>
    </div>
</form:form>

</body>
</html>