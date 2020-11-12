<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>存储监控</title>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/data.js"></script>


    <script type="text/javascript">

        $(document).ready(function () {
            var nodename = [];
            var storage1 = [];
            var storage2 = [];
            var storage3 = [];
            var storage4 = [];
            var use = [];
            var total = [];
            var warn = [];
            var line = [];
            var filenumber = [];
            var filenumberline = [];
            var filenumberwarn = [];
            var sto = [];
            $.ajax({
                type: "get",
                dataType: "json",
                url: '${ctx}/monitor/FtNodeMonitor/allnodeInfo',
                success: function (data) {
                    $.each(data, function (i, item) {
                        nodename[i] = item.system + "-" + item.node;
                        if (item.storage != null) {
                            sto = item.storage.split("/");
                            use[i] = parseInt(sto[0]);
                            total[i] = parseInt(sto[1])
                        }
                        warn[i] = parseInt(total[i] * parseInt(item.storagewarn||'0') / 100);
                        line[i] = parseInt(total[i] * parseInt(item.storageline||'0') / 100);
                        /*  if (use[i] <= warn[i]) {
                         storage1[i] = total[i] - line[i];
                         storage2[i] = line[i] - warn[i];
                         storage3[i] = warn[i] - use[i];
                         storage4[i] = use[i];
                         } else if (warn[i] < use[i] < line[i]) {
                         storage1[i] = total[i] - line[i];
                         storage2[i] = line[i] - use[i];
                         storage3[i] = warn[i] - use[i];
                         storage4[i] = use[i];
                         } else if (line[i] <= use[i]) {
                         storage1[i] = total[i] - use[i];
                         storage2[i] = line[i] - use[i];
                         storage3[i] = warn[i] - use[i];
                         storage4[i] = use[i];
                         }
                         ;*/
                        storage1[i] = total[i] || 0;
                        storage2[i] = use[i] || 0;
                        storage3[i] = warn[i] || 0;
                        storage4[i] = line[i] || 0;

                        storage1[i] = parseInt(storage1[i] / 1024 / 1024);
                        storage2[i] = parseInt(storage2[i] / 1024 / 1024);

                        filenumber[i] = parseInt(item.filenumber || '0');
                        filenumberline[i] = parseInt(item.filenumberline || '0');
                        filenumberwarn[i] = parseInt(item.filenumberwarn || '0');
                    });

                    var chart = {
                        type: 'column',
                        renderTo: "container", width: 1000, height: 350
                    };
                    var title = {
                        text: '各节点系统存储情况'
                    };
                    var xAxis = {
                        categories: nodename,
                        crosshair: true
                    };
                    var yAxis = [{
                        min: 0,
                        title: {
                            text: '存储空间（M）',
                            rotation: 0,
                            align: 'high',
                            x: 50,
                            y: -20
                        }
                    }
                    ];

                    var legend = {
                        enabled: true
                    };

                    var tooltip = {
                        formatter: function () {
                            return '<b>' + this.x + '</b><br/>' +
                                    this.series.name + ': ' + this.y + 'M' + '<br/>';
//                         +'总空间: ' + this.point.stackTotal;
                        }
                    };
                    var credits = {
                        enabled: false
                    };
                    var exporting = {
                        enabled: false
                    };
                    var plotOptions = {
                        column: {
//                            stacking: 'normal',
                            pointPadding: 0.4,
                            borderWidth: 0
                        }

                    };
                    var series = [{
                        name: '总空间(M)',
                        color: 'lightblue',
                        data: storage1,
                        pointPadding: 0.2
                    }, {
                        name: '已用空间(M)',
                        color: 'lightgreen',
                        data: storage2,
                        pointPadding: 0.2
                    }, {
                        name: '警告',
                        color: 'orange',
                        data: storage3,
                        pointPadding: 0.2
                    }, {
                        name: '严重警告',
                        color: 'red',
                        data: storage4,
                        pointPadding: 0.2
                    }
                    ];
                    var json = {};
                    json.chart = chart;
                    json.title = title;
                    json.xAxis = xAxis;
                    json.yAxis = yAxis;
                    json.legend = legend;
                    json.tooltip = tooltip;
                    json.credits = credits;
                    json.exporting = exporting;
                    json.plotOptions = plotOptions;
                    json.series = series;
                    //console.debug(json);
                    $('#container').highcharts(json);
                    $('#container').append("<hr/>");


                    //-----------------------------------------------------------------------


                    $('#container2').highcharts({
                        chart: {
                            type: 'column',
                            renderTo: "container", width: 1000, height: 400
                        },
                        title: {
                            text: '各节点系统文件数'
                        },
                        xAxis: {
                            categories: nodename
                        },
                        yAxis: [{
                            min: 0,
                            title: {
                                text: '文件数（个）',
                                rotation: 0,
                                align: 'high',
                                x: 50,
                                y: -20
                            }
                        }],
                        legend: {
                            shadow: true
                        },
                        tooltip: {
                            shared: true
                        },
                        credits: {
                            enabled: false
                        },
                        exporting: {
                            enabled: false
                        },
                        plotOptions: {
                            column: {
                                pointPadding: 0.4,
                                borderWidth: 0
                            }
                        },

                        series: [{
                            name: '严重警告',
                            color: 'red',
                            data: filenumberline,
                            pointPadding: 0.2,
                            pointPlacement: 0
                        }, {
                            name: '警告',
                            color: 'orange',
                            data: filenumberwarn,
                            pointPadding: 0.2,
                            pointPlacement: 0
                        }, {
                            name: '文件数',
                            color: 'lightgreen',
                            data: filenumber,
                            pointPadding: 0.2,
                            pointPlacement: 0
                        }]
                    });
                }
            })
        });
    </script>
</head>

<body>
<%--<hr/>--%>
<div id="container"></div>
<%--<hr/>--%>
<div id="container2"></div>
</body>
</html>