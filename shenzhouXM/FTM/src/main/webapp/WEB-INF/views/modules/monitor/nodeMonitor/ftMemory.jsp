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

    <title>内存监控历史记录表</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
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
    <form:hidden id="id" path="id"/>
    <form:hidden id="node" path="node"/>
    <form:hidden id="memoryline" path="memoryline"/>
    <form:hidden id="memorywarn" path="memorywarn"/>
    <%--<sys:message content="${message}"/>--%>
    <div id="memorycontainer" style="min-width: 400px; height: 400px; margin: 0 auto"></div>
   <%-- <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" align="center" onclick="history.go(-1)"/>
    </div>--%>
</form:form>
<script type="text/javascript">
    Highcharts.setOptions({ global: { useUTC: false } });
</script>
<script type="text/javascript">
    $(document).ready(function() {
        var node=$('#node').val();
        var memoryline=$('#memoryline').val();
        var memorywarn=$('#memorywarn').val();
        var memorydata=[];

        $.ajax({
            url: '${ctx}/monitor/FtNodeMonitor/nodeInfoLogOneHour?node=' + node,
            type: "get",
            dataType: 'json',
            success: function (data) {
                $.each(data, function (i, item) {
                    memorydata[i] = [item.time, parseInt(item.memory)];
                });
                if (memorydata.length == 0) {
                    alert("该节点在指定日期内，无对应内存使用率记录");
                }

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
            }
        })
        

    })


</script>

</body>
</html>
