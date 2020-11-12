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
    <title>内存动态表</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/data.js"></script>
</head>

<body>
<form:form id="inputForm" modelAttribute="ftNodeMonitor"  class="form-horizontal">
    <form:hidden id="id" path="id"/>
    <form:hidden  id="node" path="node"/>
    <form:hidden  id="memoryline" path="memoryline"/>
    <form:hidden  id="memorywarn" path="memorywarn"/>
    <sys:message content="${message}"/>
    <div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回"  onclick="history.go(-1)"/>
    </div>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
        var node=$('#node').val();
        var memoryline=$('#memoryline').val();
        var memorywarn=$('#memorywarn').val();
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                defaultSeriesType: 'spline',
                marginRight: 10,
                events: {
                    load: function () {
                        var id = $('#id').val();
                        var series = this.series[0];
                        setInterval(function () {
                            var x = (new Date()).getTime();
                            $.ajax({
                                url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?id='+id,
                                type: "get",
                                dataType:'json',
                                success: function(data){
                                    series.addPoint([x,parseInt(data.memory)], true, true);
                                }
                            });
                        }, 10000);
                    }
                }
            },
            title: {
                text: node+'内存使用率动态曲线图'
            },
            xAxis: {
                title: {
                    text: '时间'
                },
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: '使用率（%）'
                },
                tickPositions:[0,10,20,30,40,50,60,70,80,90,100],
                plotLines: [
                    {
                        value: parseInt(memoryline),
                        width: 2,
                        color: 'red',
                        label:{
                            text:'严重警告',
                            align:'left',
                            x:10
                        }
                    },
                    {
                        value: parseInt(memorywarn),
                        width: 2,
                        color: 'orange',
                        label:{
                            text:'警告',
                            align:'left',
                            x:10
                        }
                    }
                ]
            },
            tooltip: {
                formatter: function() {
                    return '<b>' + this.series.name + '</b><br/>' +
                            Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                            Highcharts.numberFormat(this.y, 4);
                }
            },
            legend: {
                enabled: true
            },
            exporting: {
                enabled: false
            },
            credits: {
                enabled:false
            },
            series: [
                {
                    name: '内存使用率',
                    data: (function() {
                        var data = [],
                                time = (new Date()).getTime(),
                                i;
                        for (i = -19; i <= 0; i++) {
                            data.push({
                                x: time + i * 1000,
                                y: Math.random() * 100
                            });
                        }
                        return data;
                    })()
                }
            ]
        });
    });
</script>

</body>
</html>
