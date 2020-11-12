<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>rule</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
    </style>
    <script src="${ctxStatic}/compare/compareTxt.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            getParam();
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function getParam(){
            $.ajax({
                url: "${ctx}/client/clientSyn/getParam?fileName=rule.xml",
                type: "post",
                success: function (data) {
                    for(var i=0;i<data.length;i++){
                        $('#cliUser'+i).html(data[i].user);
                        $('#cliTranCode'+i).html(data[i].tranCode);
                        $('#cliType'+i).html(data[i].type);
                        $('#cliMode'+i).html(data[i].mode);
                        $('#cliDes'+i).html(data[i].destination);
                    }
                    for(var i=0;;i++){
                        var dom1 = document.getElementById("dataUser"+i);
                        var dom2 = document.getElementById("cliUser"+i);
                        var dom3 = document.getElementById("dataTranCode"+i);
                        var dom4 = document.getElementById("cliTranCode"+i);
                        var dom5 = document.getElementById("dataType"+i);
                        var dom6 = document.getElementById("cliType"+i);
                        var dom7 = document.getElementById("dataMode"+i);
                        var dom8 = document.getElementById("cliMode"+i);
                        var dom9 = document.getElementById("dataDes"+i);
                        var dom10 = document.getElementById("cliDes"+i);
                        if(!dom1 && !dom2){
                            break;
                        }
                        CompareTxt(dom1,dom2);
                        CompareTxt(dom3,dom4);
                        CompareTxt(dom5,dom6);
                        CompareTxt(dom7,dom8);
                        CompareTxt(dom9,dom10);
                    }
                },
                error: function (data) {
                    alert("客户端参数获取失败");
                }
            });
        };
        $(function() {
            $("#sure").hide();
            $("#cancle").hide();
            $("#edit").click(function(){
                $("#sync").hide();
                $("#edit").hide();
                $("#sure").show();
                $("#cancle").show();
                $("td.value").each(function (i, item) {
                    $(item).addClass('input').html('<input type="text" value="'+$(item).text().trim()+'" />');
                })
            });
            $("#sure").click(function(){
                $("#sync").show();
                $("#edit").show();
                $("#sure").hide();
                $("#cancle").hide();
                var clientRules = new Array();
                for(var i=0;i<($('tr').length-1)/5;i++){
                    var clientRule = new Object();
                    clientRule.id = i+1;
                    clientRule.user = $("#dataUser"+i).children().val();
                    clientRule.tranCode = $("#dataTranCode"+i).children().val();
                    clientRule.type = $("#dataType"+i).children().val();
                    clientRule.mode = $("#dataMode"+i).children().val();
                    clientRule.destination = $("#dataDes"+i).children().val();
                    clientRules[i] = clientRule;
                }
                $.ajax({
                    type:'post',
                    url:'${ctx}/client/clientSyn/editCfg?fileName=rule.xml',
                    data:{'map':JSON.stringify(clientRules)},
                    dataType:'text',
                    success:function(data){
                        window.location.reload();
                    },
                    error:function(data){
                        alert("客户端参数修改失败");
                        window.location.reload();
                    }
                });
            });

            $("#cancle").click(function(){
                window.location.reload();
            });
            $("#sync").click(function(){
                if(confirm("确定同步客户端参数么")){
                    $.ajax({
                        url: "${ctx}/client/clientSyn/sync?fileName=rule.xml",
                        type: "get",
                        success: function () {
                            alert("客户端参数同步完成");
                            window.location.reload();
                        },
                        error: function () {
                            alert("客户端参数同步失败");
                        }
                    });
                };
            });
        });

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/client/clientSyn/ftsApiCfg">FtsApiConfig</a></li>
    <li><a href="${ctx}/client/clientSyn/ftsApiCfgCache">FtsApiCfgCache</a></li>

    <li><a href="${ctx}/client/clientSyn/ftsClientConfig">FtsClientConfig</a></li>
    <li><a href="${ctx}/client/clientSyn/log4j2">log4j2</a></li>
    <li><a href="${ctx}/client/clientSyn/quartz">quartz</a></li>
    <li class="active"><a href="${ctx}/client/clientSyn/rule">rule</a></li>
    <li><a href="${ctx}/client/clientSyn">返回上一页</a></li>
</ul>
<div style="position:absolute;right:20px;">
    <input id="sync" class="btn btn-primary" type="button" value="同步">
    <input id="edit" class="btn btn-primary" type="button" value="修改">
    <input id="sure" class="btn btn-primary" type="button" value="确定" >
    <input id="cancle" class="btn btn-primary" type="button" value="取消" >
</div>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th width="3%">序号</th>
        <th width="7%">参数名</th>
        <th width="45%">数据库参数值</th>
        <th width="45%">客户端参数值</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${returnMsg}" var="ftNodeParam" varStatus="status">
        <tr>
            <td rowspan="5" style="WORD-WRAP:break-word">${ftNodeParam.id}</td>
            <td  style="WORD-WRAP:break-word">用户名</td>
            <td  class="value" id="dataUser${status.index}" style="WORD-WRAP:break-word">${ftNodeParam.user}</td>
            <td id="cliUser${status.index}" style="WORD-WRAP:break-word"></td>
        </tr>
        <tr>
            <td  style="WORD-WRAP:break-word">交易码</td>
            <td  class="value" id="dataTranCode${status.index}" style="WORD-WRAP:break-word">${ftNodeParam.tranCode}</td>
            <td id="cliTranCode${status.index}" style="WORD-WRAP:break-word"></td>
        </tr>
        <tr>
            <td  style="WORD-WRAP:break-word">类型</td>
            <td  class="value" id="dataType${status.index}" style="WORD-WRAP:break-word">${ftNodeParam.type}</td>
            <td id="cliType${status.index}" style="WORD-WRAP:break-word"></td>
        </tr>
        <tr>
            <td  style="WORD-WRAP:break-word">模式</td>
            <td  class="value" id="dataMode${status.index}" style="WORD-WRAP:break-word">${ftNodeParam.mode}</td>
            <td id="cliMode${status.index}" style="WORD-WRAP:break-word"></td>
        </tr>
        <tr>
            <td  style="WORD-WRAP:break-word">目标路径</td>
            <td  class="value" id="dataDes${status.index}" style="WORD-WRAP:break-word">${ftNodeParam.destination}</td>
            <td id="cliDes${status.index}" style="WORD-WRAP:break-word"></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>