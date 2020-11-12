<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>文件传输成功</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highstock/Highstock-4.2.5/js/highstock.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>

</head>

<body>
<form:form id="inputForm" modelAttribute="ftTransforMonitor"  class="form-horizontal">
    <form:hidden id="nodeName" path="nodeName"/>
    <form:hidden  id="sysname" path="sysname"/>
    <sys:message content="${message}"/>
    <div id="uploadcontainer" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    <div id="downloadcontainer" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" align="center" onclick="history.go(-1)"/>
    </div>
</form:form>
<script type="text/javascript">
    Highcharts.setOptions({
        global: { useUTC: false },
        lang:{
            months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
            shortMonths: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一', '十二'],
            weekdays: ['星期天', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
            exportButtonTitle:'导出PDF',
            printButtonTitle:'打印报表'
        },
    });
</script>
<script type="text/javascript">
    var nodeName=$('#nodeName').val();
    var sysname=$('#sysname').val();
    var uploaddata=[];
    var downloaddata=[];
    var upsum=0;
    var downsum=0;
    $.ajax({
        url: '${ctx}/monitor/FtTransforMonitor/findDownloadNumberPerHour?nodeName='+nodeName+'&sysname='+sysname,
        type: "get",
        dataType:'json',
        success: function(data){
            $.each(data, function(i,item) {
                downloaddata.push({
                    x: item.hour,
                    y: item.count,
                });
                downsum+=item.count;
            })
            $('#downloadcontainer').highcharts('StockChart', {
                chart: {
                    alignTicks: false
                },


                title : {
                    text : sysname+'节点组'+nodeName+'节点'+'文件下载成功(总数：'+downsum+')',
                },
                credits: {
                    enabled:false
                },
//                yAxis: {
//                    opposite:false,
//                },
                rangeSelector: {
                    buttons: [{
                        type: 'day',
                        count: 1,
                        text: '1天'
                    }, {
                        type: 'week',
                        count: 1,
                        text: '1周'
                    }, {
                        type: 'month',
                        count: 1,
                        text: '1月'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '半年'
                    },{
                        type: 'year',
                        count: 1,
                        text: '1年'
                    }, {
                        type: 'all',
                        text: '所有'
                    }],
                    buttonTheme: {
                        width: 36,
                        height: 16,
                        padding: 1,
                        r: 0,
                        stroke: '#68A',
                        zIndex: 7

                    },
                    selected : 0,
                    inputEnabled: true,
                    inputDateFormat: '%Y-%m-%d',
                    inputEditDateFormat: '%Y-%m-%d',
                },
                tooltip:{
                    xDateFormat: '%Y-%m-%d %H:00:00'
                },
                xAxis:{
                    dateTimeLabelFormats: {
                        second: '%Y-%m-%d<br/>%H:%M:%S',
                        minute: '%Y-%m-%d<br/>%H:%M',
                        hour: '%Y-%m-%d<br/>%H:%M',
                        day: '%Y<br/>%m-%d',
                        week: '%Y<br/>%m-%d',
                        month: '%Y-%m',
                        year: '%Y'
                    }
                },
                series : [{
                    type: 'column',
                    name : '文件下载数',
                    data :downloaddata,
                    dataGrouping: {
                        units: [[
                            'day',[1]
                        ],[
                                'week',[1]
                        ],[
                                'month',[1]
                        ]]
                    }
                }]
            });
        }
    });
    $.ajax({
        url: '${ctx}/monitor/FtTransforMonitor/findUploadNumberPerHour?nodeName='+nodeName+'&sysname='+sysname,
        type: "get",
        dataType:'json',
        success: function(data){
            $.each(data, function(i,item) {
                uploaddata.push({
                    x: item.hour,
                    y: item.count,
                });
                upsum+=item.count;
            })
            $('#uploadcontainer').highcharts('StockChart', {
                chart: {
                    alignTicks: false
                },


                title : {
                    text : sysname+'节点组'+nodeName+'节点'+'文件上传成功(总数：'+upsum+')',
                },
                credits: {
                    enabled:false
                },
//                yAxis: {
//                    opposite:false,
//                },
                rangeSelector: {
                    buttons: [{
                        type: 'day',
                        count: 1,
                        text: '1天'
                    }, {
                        type: 'week',
                        count: 1,
                        text: '1周'
                    }, {
                        type: 'month',
                        count: 1,
                        text: '1月'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '半年'
                    },{
                        type: 'year',
                        count: 1,
                        text: '1年'
                    }, {
                        type: 'all',
                        text: '所有'
                    }],
                    inputEnabled: true,
                    selected: 0,
                },
                tooltip:{
                    xDateFormat: '%Y-%m-%d %H:00:00'
                },
                xAxis:{
                    dateTimeLabelFormats: {
                        second: '%Y-%m-%d<br/>%H:%M:%S',
                        minute: '%Y-%m-%d<br/>%H:%M',
                        hour: '%Y-%m-%d<br/>%H:%M',
                        day: '%Y<br/>%m-%d',
                        week: '%Y<br/>%m-%d',
                        month: '%Y-%m',
                        year: '%Y'
                    }
                },
                series : [{
                    type: 'column',
                    name : '文件上传数',
                    data :uploaddata,
                    dataGrouping: {
                        units: [[
                            'day',[1]
                        ],[
                            'week',[1]
                        ],[
                            'month',[1]
                        ]]
                    }
                }]
            });
        }
    })

</script>

</body>
</html>
