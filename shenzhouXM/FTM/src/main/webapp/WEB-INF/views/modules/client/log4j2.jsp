<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>log4j2</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        textarea {
            width: 100%;
            overflow: hidden;
            resize: none;
            font-family: inherit;
        }
        legend{
            width: 70px;
            margin-left: 30px;
            border-bottom: 0
        }

        pre{
            width: 95%;
            border: 0;
            background-color: #fff;
        }
        fieldset{
            width: 45%;
            position:absolute;
            border:2px solid ;
        }
        #db{
            left:3%;
        }

        #cli{
            right:3%;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {

        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        $(function() {
            $("#sure").hide();
            $("#cancle").hide();
            $("#edit").click(function(){
                $("#sync").hide();
                $("#edit").hide();
                $("#sure").show();
                $("#cancle").show();
                $("#left").addClass('textarea').html('<textarea>'+$("#left").html()+'</textarea>');
                $.each($("textarea"), function(i, n){
                    $(n).css("height", n.scrollHeight + "px");
                });
            });
            $("#sure").click(function(){
                $("#sync").show();
                $("#edit").show();
                $("#sure").hide();
                $("#cancle").hide();
                var map = $("textarea").val();
                $.ajax({
                    type:'post',
                    url:'${ctx}/client/clientSyn/editCfg?fileName=log4j2.xml',
                    data:{'map':map},
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
                        url: "${ctx}/client/clientSyn/sync?fileName=log4j2.xml",
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
    <li class="active"><a href="${ctx}/client/clientSyn/log4j2">log4j2</a></li>
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
<div>
    <fieldset id="db"><legend>数据库</legend>
        <div ><pre id="left"><c:out value="${returnMsg}" escapeXml="true"/></pre></div>
    </fieldset>
    <fieldset id="cli"><legend>客户端</legend>
        <div ><pre id="right"><c:out value="${cliContent}" escapeXml="true"/></pre></div>
    </fieldset>
</div>
<script src="${ctxStatic}/compare/compareTxt.js" type="text/javascript"></script>
<script type="text/javascript">
    var dom1 = document.getElementById("left");
    var dom2 = document.getElementById("right");
    CompareTxt(dom1,dom2);
</script>
</body>
</html>