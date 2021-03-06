<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <title>流量实时监控</title>
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
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/data.js"></script>

</head>

<body>
<script type="text/javascript">
    Highcharts.setOptions({global: {useUTC: false}});
</script>
<script type="text/javascript">
    $(document).ready(function () {

        $.ajax({
            type: "get",
            dataType: "json",
            <%--url: '${ctx}/monitor/FtNodeMonitor/allnodeInfo',--%>
            url: '${ctx}/monitor/FtNodeMonitor/allLiveDatanode',
            success: function (data) {
                $.each(data, function (i, item) {
                    // 不显示nameNode实时监控
                    if (item.system != null) {
                        $("#container").append("<div id=\'cpu" + i + "\' style='height:400px; width: 90%;'></div>");
                        $('#cpu' + i + '').highcharts('StockChart', {
                            chart: {
                                renderTo: "container",
                                events: {
                                    load: function () {
                                        var series = this.series[0];
                                        var period = ${monitorPeriods};
                                        setInterval(function () {
                                            $.ajax({
                                                <%--url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?nodename=' + item.node,--%>
                                                url: '${ctx}/monitor/FtNodeMonitor/RealTime/flowrate?nodename=' + item.node,
                                                type: "get",
                                                dataType: 'json',
                                                success: function (data) {
//                                                    var currentTime = new Date().getTime();
//                                                    var updateTime = (new Date(data.time)).getTime();
//                                                    var x = currentTime - updateTime > period ? currentTime : updateTime;
//                                                    var y = currentTime - updateTime > 12 * period ? null : parseFloat(data.flowrate);
                                                    var x = dataTime = (new Date(data.time)).getTime();
                                                    var y = parseFloat(data.flowrate) < 0 ? null : parseFloat(data.flowrate);
                                                    series.addPoint([x, y], true, false);
                                                }
                                            });
                                        }, period)
                                    }
                                }
                            },
                            title: {
                                text: item.node + ' - ' + '流量实时监控'
                            },
                            navigator: {
                                enabled: true
                            },
                            credits: {
                                enabled: false
                            },
                            scrollbar: {
                                enabled: true
                            },
                            rangeSelector: {
                                buttons: [{
                                    type: 'hour',
                                    count: 1,
                                    text: '时'
                                }, {
                                    type: 'day',
                                    count: 1,
                                    text: '天'
                                }, {
                                    type: 'all',
                                    count: 1,
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
                                selected: 0,
                                inputEnabled: true,
                                inputDateFormat: '%Y-%m-%d',
                                inputEditDateFormat: '%Y-%m-%d'
                            },
                            yAxis: {
                                title: {
                                    text: '流量（/kbps)',
                                    rotation: 0,
                                    align: 'high',
                                    x: 30,
                                    y: 0
                                },
                                opposite: false
                            },
                            exporting: {
                                enabled: false
                            },
                            lang: {
                                printChart: '打印图表',
                                downloadPNG: '下载JPEG 图片',
                                downloadJPEG: '下载JPEG文档',
                                downloadPDF: '下载PDF 图片',
                                downloadSVG: '下载SVG 矢量图',
                                contextButtonTitle: '下载图片'
                            },
                            series: [{
                                name: '流量（kbps）',
                                data: (function () {
                                    var data = [],
                                            time = (new Date()).getTime(),
                                            i;
                                    for (i = -20; i <= 0; i += 1) {
                                        data.push({
                                            x: time + i * ${monitorPeriods},
                                            y: null
                                        });
                                    }
                                    return data;
                                }()),
                                tooltip: {
                                    valueDecimals: 2,
                                    xDateFormat: "%Y-%m-%d %H:%M"
                                },
                                fillColor: {
                                    linearGradient: {
                                        x1: 0,
                                        y1: 0,
                                        x2: 0,
                                        y2: 1
                                    },
                                    stops: [
                                        [0, Highcharts.getOptions().colors[0]],
                                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                    ]
                                }
                            }]
                        });
                        $("#container").append("<hr/>");
                    }
                })
            }
        })
    })
</script>
<hr/>
<div id="container"></div>
</body>
</html>
