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

    <title>流量监控历史记录表</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highstock/Highstock-4.2.5/js/highstock.js"></script>
    <%--<script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>--%>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>

</head>

<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/monitor/FtNodeMonitor/">返回上一页</a></li>
</ul>
<form:form id="inputForm" modelAttribute="ftNodeMonitor"  class="form-horizontal">
    <%--<form:hidden id="id" path="id"/>--%>
    <form:hidden  id="node" path="node"/>
    <form:hidden  id="networkline" path="networkline"/>
    <form:hidden  id="networkwarn" path="networkwarn"/>
    <%--<sys:message content="${message}"/>--%>
    <div id="flowratecontainer" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
    <%--<div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" align="center" onclick="history.go(-1)"/>
    </div>--%>
</form:form>
<script type="text/javascript">
    Highcharts.setOptions({ global: { useUTC: false } });
</script>
<script type="text/javascript">
    $(document).ready(function() {
        var node=$('#node').val();
        var networkline=$('#networkline').val();
        var networkwarn=$('#networkwarn').val();
        var flowratedata=[];

        $.ajax({
            url: '${ctx}/monitor/FtNodeMonitor/nodeInfoLogOneHour?node=' + node,
            type: "get",
            dataType: 'json',
            success: function (data) {
                $.each(data, function (i, item) {
                    flowratedata[i] = [item.time, parseFloat(item.flowrate)]
                });
                if (flowratedata.length == 0) {
                    alert("该节点在指定日期内，无对应网络流量记录");
                }
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
            }
        })
        
    })

</script>

</body>
</html>
