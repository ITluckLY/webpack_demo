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

    <title>磁盘监控表</title>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/data.js"></script>
    <script src="${ctxStatic}/jquery-spinner/jquery.spinner.min.js"></script>
    <script src="${ctxStatic}/selfDefine/strContains.js" type="text/javascript"></script>

</head>

<body>
<style type="text/css">

</style>
<script type="text/javascript">
    Highcharts.setOptions({ global: { useUTC: false } });
</script>
<script type="text/javascript">
    $(document).ready(function () {
            $.ajax({
                type: "get",
                dataType: "json",
                url: '${ctx}/monitor/FtNodeMonitor/allnodeInfo',
                success: function (data) {
                    $.each(data, function(i,item) {
                        $("#container").append("<hr width='1400px'/>");
                        $("#container").append("<div id=\'nodedisk" + i + "\' ><table><tr id=\'eachdisk" + i + "\'></tr></table></div>");
                        var id =[];
                        var node=[];
                        var eachdisk=[];
                        var detail=[];
                        var disk = [];
                        node[i]=item.node;
                        $.ajax({
                            type: "get",
                            dataType: "json",
                            url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?nodename='+node[i],
                            success: function (data1) {

                                if (data1.disk != null) {

                                    // linux 下磁盘信息
                                    if (contains(data1.disk, '磁盘总大小')) {
                                        var index = data1.disk.indexOf("使用率：");
                                        var diskRate = data1.disk.substring(index + 4, data1.disk.indexOf("%"));
                                        var index2 = data1.disk.indexOf("已使用");
                                        var diskTotal = data1.disk.substring(0, index2);
                                        $('#eachdisk' + i + '').append("<td><div id=\'disk" + i + "\' style='height:200px; width: 350px;'></div></td>");
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
                                                text: node[i] + " - " + diskTotal,
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
                                        if (contains(data1.disk, ',')) {
                                            disk = data1.disk.split(",");
                                        } else {
                                            disk = data1.disk;
                                        }
                                        $.each(disk, function (j, item2) {
                                            var detail = item2.split(":");
                                            $('#eachdisk' + i + '').append("<td><div id=\'disk" + i + j + "\' style='height:200px; width: 350px;'></div></td>");
                                            $('#disk' + i + j + '').highcharts({
                                                chart: {
                                                    type: 'pie',
                                                    options3d: {
                                                        enabled: true,
                                                        alpha: 10,
                                                        beta: 0
                                                    }
                                                },
                                                credits: {
                                                    enabled: false
                                                },
                                                exporting: {
                                                    enabled: false
                                                },
                                                title: {
                                                    text: node[i] + " - " + detail[0],
                                                    verticalAlign: 'bottom'
                                                },
                                                tooltip: {
                                                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                                                },
                                                plotOptions: {
                                                    pie: {
                                                        allowPointSelect: true,
                                                        cursor: 'pointer',
                                                        depth: 10,
                                                        dataLabels: {
                                                            distance: 10,
                                                            enabled: true,
                                                            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                                                        }
                                                    }
                                                },
                                                series: [{
                                                    type: 'pie',
                                                    name: '磁盘空间',
                                                    size: '50%',
                                                    data: [
                                                        ['剩余', 100 - parseInt(detail[1])],
                                                        ['已使用', parseInt(detail[1])]
                                                    ]
                                                }]
                                            })
                                        });
                                    }
                                }
                            }

                        });
                    })
                }
            })


    });
</script>
</body>
<div id="container"></div>
<div id="canvas"></div>

</body>
</html>