<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>节点传输监控</title>
    <script type="text/javascript">
        function nodetrasfor(nodeName,sysname){

            $("#transforTable").css("display","block");
            var nodename = document.getElementsByName('nodename');
            var syaname = document.getElementsByName('syaname');
            var upsucc = document.getElementsByName('upsucc');
            var upfail = document.getElementsByName('upfail');
            var downsucc = document.getElementsByName('downsucc');
            var downfail = document.getElementsByName('downfail');
//            setInterval(function() {
                $.ajax({
                    url: '${ctx}/monitor/ftTransforMonitor/findTransDetail?nodeName='+nodeName+'&sysname='+sysname,
                    type: "get",
                    dataType:'json',
                    success: function(data){
                        console.debug(data);
                        nodename[0].innerHTML=nodeName;
                        if(sysname == null || sysname == 'undefined'){
                            syaname[0].innerHTML="无系统(非数据节点)";
                        }else{
                            syaname[0].innerHTML=sysname;
                        }
                        upsucc[0].innerHTML=data.upsucc;
                        upfail[0].innerHTML=data.upfail;
                        downsucc[0].innerHTML=data.downsucc;
                        downfail[0].innerHTML=data.downfail;

                    }
                })
//            },30000)
        }
    </script>
</head>
<body>
<table id="transforTable" >
    <thead>
    <tr>
        <th>节点名称</th>
        <th>节点组名称</th>
        <th>上传成功</th>
        <th>上传失败</th>
        <th>下载成功</th>
        <th>下载失败</th>
    </tr>
    </thead>
    <tbody>
        <tr>
            <td name='nodename' width="150PX"></td>
            <td name='syaname' width="150PX"></td>
            <td name='upsucc' width="150PX"></td>
            <td name="upfail" width="150PX"></td>
            <td name='downsucc' width="150PX"></td>
            <td name="downfail" width="150PX"></td>
        </tr>
    </tbody>
    <hr/>
</table>
<style type="text/css">
    #transforTable {
        display:none;
        font-family: verdana,arial,sans-serif;
        font-size:11px;
        color:#333333;
        border-width: 1px;
        border-color: #666666;
        border-collapse: collapse;
        width:90%;
    }
    #transforTable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #dedede;
    }
    #transforTable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #ffffff;
        text-align: center;
    }
</style>

</body>
</html>