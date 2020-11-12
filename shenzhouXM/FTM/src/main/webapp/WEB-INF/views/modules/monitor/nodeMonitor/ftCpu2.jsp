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

    <title>CPU动态表</title>
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
    <form:hidden  id="cpuline" path="cpuline"/>
    <form:hidden  id="cpuwarn" path="cpuwarn"/>
    <sys:message content="${message}"/>
    <div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" align="center" onclick="history.go(-1)"/>
    </div>
</form:form>
<%--</form:form>--%>
<script type="text/javascript">
    $(document).ready(function() {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
      var node=$('#node').val();
        var cpuline=$('#cpuline').val();
        var cpuwarn=$('#cpuwarn').val();
        //声明报表对象
//var a=document.getElementById("id");
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                defaultSeriesType: 'spline',
                marginRight: 10,
                events: {
                    load: function () {
                        // set up the updating of the chart each second
                        var id = $('#id').val();
                        var series = this.series[0];
                        setInterval(function () {
                            var x = (new Date()).getTime();
                            $.ajax({
                                url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?id='+id,
                                type: "get",
                                 dataType:'json',
                                success: function(data){
                                    var y=parseInt(data.cpu);
                                    series.addPoint([x,y], true, true);
                                }
                            });
                        }, 2000);
                    }
                }
            },
            title: {
                text: node+'CPU使用率动态曲线图'
            },
            xAxis: {
                title: {
                    text: '时间'
                },
                type: 'datetime',
                tickPixelInterval: 20,
                labels: {
                    step: 4,
                    formatter: function () {
                        return Highcharts.dateFormat('%H:%M', this.value);
                    }
                }
            },
            yAxis: {
                title: {
                    text: '使用率（%）',
                    rotation: 0,
                    align: 'high',
                    x:50,
                    y:-20
                },
                tickPositions:[0,10,20,30,40,50,60,70,80,90,100],
                plotLines: [
                    {
                        value: parseInt(cpuline),
                        width: 2,
                        color: 'red',
                        label:{
                            text:'严重警告',
                            align:'left',
                            x:10
                        }
                    },
                    {
                        value: parseInt(cpuwarn),
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
                    name: 'CPU使用率',
                    data: (function() {
                        var data = [],  time = (new Date()).getTime(),i;
                        for (i = -100; i <= 0; i++) {
                            data.push({
                                x: time + i * 40,
                                y:0
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
