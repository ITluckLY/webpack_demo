<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>FtsClientConfig</title>
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
                url: "${ctx}/client/clientSyn/getParam?fileName=FtsClientConfig.properties",
                type: "post",
                success: function (data) {
                    var i = 0;
                    for(var param in data){
                        $('#cliValue'+i).html(data[param]);
                        i++;
                    }
                    for(var i=0;;i++){
                        var dom1 = document.getElementById("dataValue"+i);
                        var dom2 = document.getElementById("cliValue"+i);
                        if(!dom1 && !dom2){
                            break;
                        }
                        CompareTxt(dom1,dom2);
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
                var map = {};
                $("#sync").show();
                $("#edit").show();
                $("#sure").hide();
                $("#cancle").hide();
                $("td.value").each(function (i, item) {
                    var key = $(item).prev().text().trim();
                    var value = $(item).find('input').val().trim();
                    map[key] = value;
                })
                $.ajax({
                    type:'post',
                    url:'${ctx}/client/clientSyn/editCfg?fileName=FtsClientConfig.properties',
                    data:{'map':JSON.stringify(map)},
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
                        url: "${ctx}/client/clientSyn/sync?fileName=FtsClientConfig.properties",
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

    <li class="active"><a href="${ctx}/client/clientSyn/ftsClientConfig">FtsClientConfig</a></li>
    <li><a href="${ctx}/client/clientSyn/log4j2">log4j2</a></li>
    <li><a href="${ctx}/client/clientSyn/quartz">quartz</a></li>
    <li><a href="${ctx}/client/clientSyn/rule">rule</a></li>
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
        <th width="40%">参数名称</th>
        <th width="30%">数据库参数值</th>
        <th width="30%">客户端参数值</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${returnMsg}" var="ftNodeParam" varStatus="status">
        <tr>
            <td style="WORD-WRAP:break-word">${ftNodeParam.key}</td>
            <td class="value" id="dataValue${status.index}" style="WORD-WRAP:break-word">${ftNodeParam.value}</td>
            <td id="cliValue${status.index}" style="WORD-WRAP:break-word"></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>