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
    <title>内存动态监控表</title>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/data.js"></script>

</head>

<body>
<script type="text/javascript">
    $(document).ready(function() {
        $.ajax({
            type: "get",
            dataType: "json",
            url: '${ctx}/monitor/FtNodeMonitor/allnodeInfo',
            success: function (data) {
                $.each(data, function(i,item) {
                    $("#container").append("<div id=\'memory" + i + "\' ></div>");
                    $('#memory'+i+'').highcharts({
                        chart: {
                            defaultSeriesType: 'spline',
                            marginRight: 10,
                            events: {
                                load: function () {
                                    // set up the updating of the chart each second
                                    var series = this.series[0];
                                    setInterval(function () {
                                        var x = (new Date()).getTime();
                                        $.ajax({
                                            url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?id='+item.id,
                                            type: "get",
                                            dataType:'json',
                                            success: function(data){
                                                series.addPoint([x,parseInt(data.memory)], true, true);
                                            }
                                        })
                                    }, 10000);
                                }
                            }
                        },
                        title: {
                            text: item.node+'内存使用率动态曲线图'
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
                                text: '使用率（%）',
                                rotation: 0,
                                align: 'high',
                                x:50,
                                y:-20
                            },
                            tickPositions:[0,10,20,30,40,50,60,70,80,90,100],
                            plotLines: [
                                {
                                    value: parseInt(item.memoryline),
                                    width: 2,
                                    color: 'red',
                                    label:{
                                        text:'严重警告',
                                        align:'left',
                                        x:10
                                    }
                                },
                                {
                                    value: parseInt(item.memorywarn),
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
                })
            }
        })
    })
</script>
<div id="container"></div>
</body>
</html>