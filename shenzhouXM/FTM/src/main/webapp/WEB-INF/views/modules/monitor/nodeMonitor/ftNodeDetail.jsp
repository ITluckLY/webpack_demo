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
    <title>节点详细信息</title>
</head>

<body>
<hr/>
<div id="cpucontainer"></div>
<div id="memorycontainer"></div>
<div id="flowratecontainer"></div>
<div id="networkSpeedtainer" <%--style='height:400px; width: 95%;'--%>></div>

<div id="diskcontainer"></div>

<%--<script type="text/javascript">--%>
<%--Highcharts.setOptions({global: {useUTC: false}});--%>
<%--</script>--%>
<script src="${ctxStatic}/selfDefine/strContains.js" type="text/javascript"></script>
<script type="text/javascript">

    function nodeinfo(node) {
        Highcharts.setOptions({
            global : {
                useUTC : false
            }
        });


        var cpudata = [];
        var memorydata = [];
        var flowratedata = [];
        var disk = [];
        var networkSpeed=[];

        $.ajax({
            type: "get",
            dataType: "json",
            url: '${ctx}/monitor/FtNodeMonitor/allnodeInfo',
            success: function (data) {
                $.each(data, function (i0, item0) {
                    if (item0.node === node) {
                        if (item0.system == null) return true;
                        $.ajax({
                            url: '${ctx}/monitor/FtNodeMonitor/nodeInfoLogOneHour?node=' + node,
                            type: "get",
                            dataType: 'json',
                            success: function (data) {
                                $.each(data, function (i, item) {
                                    cpudata[i] = [item.time, parseFloat(item.cpu)];
                                    memorydata[i] = [item.time, parseInt(item.memory)];
                                    //var flowrate0 = parseFloat(item.flowrate)<0?0:parseFloat(item.flowrate);
                                    //flowratedata[i] = [item.time, flowrate0]
                                    flowratedata[i] = [item.time, parseFloat(item.flowrate)]
                                });


                                /*
                                 * CPU监控图像
                                 */
                                $('#cpucontainer').highcharts('StockChart', {
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
                                        selected: 1,
                                        inputEnabled: true,
                                        inputDateFormat: '%Y-%m-%d',
                                        inputEditDateFormat: '%Y-%m-%d'
                                    },
                                    title: {
                                        text: node + ' - CPU监控'
                                    },
                                    scrollbar: {
                                        enabled: true
                                    },
                                    navigator: {
                                        enabled: true
                                    },
                                    credits: {
                                        enabled: false
                                    },
                                    yAxis: {
                                        title: {
                                            text: '使用率（%）',
                                            rotation: 0,
                                            align: 'high',
                                            x: 10,
                                            y: -20
                                        },
                                        opposite: false,
                                        tickPositions: [0, 20, 40, 60, 80, 100],
                                        plotLines: [
                                            {
                                                value: parseInt(item0.cpuline),
                                                width: 2,
                                                color: 'red',
                                                label: {
                                                    text: '严重警告',
                                                    align: 'left',
                                                    x: 10
                                                }
                                            },
                                            {
                                                value: parseInt(item0.cpuwarn),
                                                width: 2,
                                                color: 'orange',
                                                label: {
                                                    text: '警告',
                                                    align: 'left',
                                                    x: 10
                                                }

                                            }
                                        ]
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
                                        name: 'CPU利用率',
                                        data: cpudata.reverse(),
                                        tooltip: {
                                            valueDecimals: 1,
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
                                        },
                                    }]
                                });
                                $('#cpucontainer').append("<hr/>");


                                /*
                                 * 内存监控图像
                                 */
                                $('#memorycontainer').highcharts('StockChart', {
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
                                        selected: 1,
                                        inputEnabled: true,
                                        inputDateFormat: '%Y-%m-%d',
                                        inputEditDateFormat: '%Y-%m-%d'
                                    },
                                    title: {
                                        text: node + ' - 内存监控'
                                    },
                                    yAxis: {
                                        title: {
                                            text: '使用率（%）',
                                            rotation: 0,
                                            align: 'high',
                                            x: 40,
                                            y: -20
                                        },
                                        tickPositions: [0, 20, 40, 60, 80, 100],
                                        plotLines: [
                                            {
                                                value: parseInt(item0.memoryline),
                                                width: 2,
                                                color: 'red',
                                                label: {
                                                    text: '严重警告',
                                                    align: 'left',
                                                    x: 10
                                                }
                                            },
                                            {
                                                value: parseInt(item0.memorywarn),
                                                width: 2,
                                                color: 'orange',
                                                label: {
                                                    text: '警告',
                                                    align: 'left',
                                                    x: 10
                                                }

                                            }
                                        ],
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
                                        name: '内存利用率',
                                        data: memorydata.reverse(),
                                        tooltip: {
                                            valueDecimals: 1,
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
                                $('#memorycontainer').append("<hr/>");


                                /*
                                 * 流量监控图像
                                 */
                                $('#flowratecontainer').highcharts('StockChart', {
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
                                        selected: 1,
                                        inputEnabled: true,
                                        inputDateFormat: '%Y-%m-%d',
                                        inputEditDateFormat: '%Y-%m-%d'
                                    },
                                    title: {
                                        text: node + ' - 网络流量监控'
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
                                    plotOptions: {
                                        area: {
                                            fillColor: 'green'
                                        }
                                    },
                                    yAxis: {
                                        title: {
                                            text: '流量（kbps）',
                                            rotation: 0,
                                            align: 'high',
                                            x: 10,
                                            y: -20
                                        },
                                        /*tickPositions: [0, 20, 40, 60, 80, 100],*/
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
                                        name: '网络流量',
                                        type: 'area',
                                        data: flowratedata.reverse(),
                                        tooltip: {
                                            valueDecimals: 2
                                        },
                                        fillColor: {
                                            linearGradient: {
                                                x1: 1,
                                                y1: 1,
                                                x2: 1,
                                                y2: 1
                                            },
                                            stops: [
                                                [0, Highcharts.getOptions().colors[0]],
                                                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                            ]
                                        }
                                    }]
                                });
                                $('#flowratecontainer').append("<hr/>");

                                /*
                                 * 流量速率监控图像
                                 */
                                /*$('#networkSpeedtainer').highcharts('StockChart', {
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
                                 selected: 1,
                                 inputEnabled: true,
                                 inputDateFormat: '%Y-%m-%d',
                                 inputEditDateFormat: '%Y-%m-%d'
                                 },
                                 title: {
                                 text: node + ' - 网络流量速率监控',
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
                                 plotOptions: {
                                 area: {
                                 fillColor: 'green'
                                 },
                                 },
                                 yAxis: {
                                 title: {
                                 text: '流量速率（kbps）',
                                 rotation: 0,
                                 align: 'high',
                                 x: 10,
                                 y: -20
                                 },
                                 /!*tickPositions: [0, 20, 40, 60, 80, 100],*!/
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
                                 name: '网络流量速率',
                                 type: 'area',
                                 data: flowratedata.reverse(),
                                 tooltip: {
                                 valueDecimals: 2
                                 },
                                 fillColor: {
                                 linearGradient: {
                                 x1: 1,
                                 y1: 1,
                                 x2: 1,
                                 y2: 1
                                 },
                                 stops: [
                                 [0, Highcharts.getOptions().colors[0]],
                                 [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                 ]
                                 }
                                 }]
                                 });
                                 $('#networkSpeedtainer').append("<hr/>");*/

                                //---------------------------------------


                                //----------------------------------------
                            }
                        });

                    }
                })
            }
        });
            /*
             * 磁盘监控图像
             */
            $.ajax({
                type: "get",
                dataType: "json",
                url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?nodename=' + node,
                success: function (data) {
                    if (data.disk != null) {
                        // linux 下磁盘信息
                        if (contains(data.disk, '磁盘总大小')) {
                            $("#diskcontainer").html("<table><tr id=disk></tr></table>");

                            var index = data.disk.indexOf("使用率：");
                            var diskRate = data.disk.substring(index + 4, data.disk.indexOf("%"));
                            var index2 = data.disk.indexOf("已使用");
                            var diskTotal = data.disk.substring(0, index2);

                            $("#disk").append("<td><div id=\'disk" + 0 + "\' style='height:200px; width: 300px;'></div></td>");
                            $('#disk' + 0 + '').highcharts({
                                chart: {
                                    type: 'pie',
                                    options3d: {
                                        enabled: true,
                                        alpha: 45,
                                        beta: 0
                                    }
                                },
                                innerSize: 2,
                                credits: {
                                    enabled: false
                                },
                                exporting: {
                                    enabled: false
                                },
                                title: {
                                    text: diskTotal,
                                    verticalAlign: 'bottom'
                                },
                                tooltip: {
                                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                                },
                                plotOptions: {
                                    pie: {
                                        allowPointSelect: true,
                                        cursor: 'pointer',
                                        depth: 35,
                                        dataLabels: {
                                            distance: 5,
                                            enabled: true,
                                            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                                        }
                                    }
                                },
                                series: [{
                                    type: 'pie',
                                    size: '50%',
                                    name: '磁盘空间',
                                    data: [
                                        ['剩余', 100 - parseInt(diskRate)],
                                        ['已使用', parseInt(diskRate)]
                                    ]
                                }]
                            });

                            // window 下磁盘信息
                        } else {
                            if (contains(data.disk, ',')) {
                                disk = data.disk.split(",");
                            } else {
                                disk = data.disk;
                            }
                            $("#diskcontainer").html("<table><tr id=disk></tr></table>");
                            $.each(disk, function (i, item) {
                                var detail = item.split(":");
                                $("#disk").append("<td><div id=\'disk" + i + "\' style='height:200px; width: 300px;'></div></td>");
                                $('#disk' + i + '').highcharts({
                                    chart: {
                                        type: 'pie',
                                        options3d: {
                                            enabled: true,
                                            alpha: 45,
                                            beta: 0
                                        }
                                    },
                                    innerSize: 2,
                                    credits: {
                                        enabled: false
                                    },
                                    exporting: {
                                        enabled: false
                                    },
                                    title: {
                                        text: detail[0],
                                        verticalAlign: 'bottom'
                                    },
                                    tooltip: {
                                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                                    },
                                    plotOptions: {
                                        pie: {
                                            allowPointSelect: true,
                                            cursor: 'pointer',
                                            depth: 35,
                                            dataLabels: {
                                                distance: 5,
                                                enabled: true,
                                                format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                                            }
                                        }
                                    },
                                    series: [{
                                        type: 'pie',
                                        size: '50%',
                                        name: '磁盘空间',
                                        data: [
                                            ['剩余', 100 - parseInt(detail[1])],
                                            ['已使用', parseInt(detail[1])]
                                        ]
                                    }]
                                })
                            });
                        }
                    } else {
                        $("#diskcontainer").html("");
                    }
                }
            });
//        }
    }

</script>

<hr/>
</body>
</html>
