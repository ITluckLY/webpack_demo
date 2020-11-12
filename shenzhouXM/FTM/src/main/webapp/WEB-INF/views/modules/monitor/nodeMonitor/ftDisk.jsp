<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/taglibMonitor.jsp"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>磁盘饼图</title>
    <%--<meta http-equiv="pragma" content="no-cache">--%>
    <%--<meta http-equiv="cache-control" content="no-cache">--%>
    <%--<meta http-equiv="expires" content="0">--%>
    <%--<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">--%>
    <%--<meta http-equiv="description" content="This is my page">--%>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>
    <script src="${ctxStatic}/selfDefine/strContains.js" type="text/javascript"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            var node = $('#node').val();
            $.ajax({
                type: "get",
                dataType: "json",
                url: '${ctx}/monitor/FtNodeMonitor/nodeInfo?nodename=' + node,
                success: function (data) {
                    if (data.disk != null) {
                        // linux 下磁盘信息
                        if (contains(data.disk, '磁盘总大小')) {
                            $("#container").html("<table><tr id=disk></tr></table>");

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
                            $("#container").html("<table><tr id=disk></tr></table>");
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
                                                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
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
                        $("#container").html("无记录");
                    }
                }
            });

        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/monitor/FtNodeMonitor/">返回上一页</a></li>
</ul>
<form:form id="inputForm" modelAttribute="ftNodeMonitor"  class="form-horizontal">
    <form:hidden id="id" path="id"/>
    <form:hidden  id="node" path="node"/>
    <%--<sys:message content="${message}"/>--%>
    <div id="container" style="min-width: 1000px; height: 200px; margin: 0 auto"></div>
    <%--<div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" align="center" onclick="history.go(-1)"/>
    </div>--%>

</form:form>

</body>
</html>