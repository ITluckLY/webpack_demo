<%--
  Created by IntelliJ IDEA.
  User: vincentfxz
  Date: 16/1/13
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>文件传输监控</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
    <!-- Bootstrap 3.3.4 -->
    <link href="${ctxStatic}/bootstrap/alte/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctxStatic}/AdminLTE/css/ionicons.min.css" rel="stylesheet" type="text/css"/>
    <!-- Theme style -->
    <link href="${ctxStatic}/AdminLTE/css/AdminLTE.min.css" rel="stylesheet" type="text/css"/>
    <!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
    <link href="${ctxStatic}/AdminLTE/css/skins/_all-skins.min.css" rel="stylesheet" type="text/css"/>
    <!--[if IE]>
        <script src="${ctxStatic}/html5/html5.js"></script>
        <script type="text/javascript" src="${ctxStatic}/excanvas/excanvas.min.js"></script>
    <![endif]-->
</head>
<body>
<div style="margin-left: 0px" class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            文件传输监控
            <small>|神州信息</small>
        </h1>
        <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> 文件传输控制台</a></li>
            <li><a href="#">平台监控</a></li>
            <li class="active">文件传输监控</li>
        </ol>
    </section>

    <!-- Main content -->
    <section class="content">
        <div class="row">
            <div class="col-md-4">
                <!-- AREA CHART -->
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">实时交易量</h3>

                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="chart">
                            <canvas id="areaChart" height="180"></canvas>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>
                <!-- /.box -->

                <!-- DONUT CHART -->
                <%--<div class="box box-danger">--%>
                <%--<div class="box-header with-border">--%>
                <%--<h3 class="box-title">Donut Chart</h3>--%>

                <%--<div class="box-tools pull-right">--%>
                <%--<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>--%>
                <%--<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--<div class="box-body">--%>
                <%--<canvas id="pieChart" height="250"></canvas>--%>
                <%--</div>--%>
                <%--<!-- /.box-body -->--%>
                <%--</div>--%>
                <!-- /.box -->

            </div>
            <!-- /.col (LEFT) -->
            <div class="col-md-4">
                <!-- BAR CHART -->
                <div class="box box-success">
                    <div class="box-header with-border">
                        <h3 class="box-title">全局拓扑图</h3>

                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="row">
                            <div align="center" class="col-md-4">
                                <label>FS01</label>
                            </div>
                            <div align="center" class="col-md-4">
                                <label>FS02</label>
                            </div>
                            <div align="center" class="col-md-4">
                                <label>FS032</label>
                            </div>
                        </div>
                        <div class="row">
                            <div align="center" class="col-md-4">
                                <image src="${ctxStatic}/monitor/n1.png" style="max-width: 50px"></image>
                            </div>
                            <div align="center" class="col-md-4">
                                <image src="${ctxStatic}/monitor/n1.png" style="max-width: 50px"></image>
                            </div>
                            <div align="center" class="col-md-4">
                                <image src="${ctxStatic}/monitor/n1.png" style="max-width: 50px"></image>
                            </div>
                        </div>
                        <div class="row">
                            <div align="center" class="col-md-4">
                                <image src="${ctxStatic}/monitor/line.png" style="max-width: 50px"></image>
                            </div>
                            <div align="center" class="col-md-4">
                                <image src="${ctxStatic}/monitor/line.png" style="max-width: 50px"></image>
                            </div>
                            <div align="center" class="col-md-4">
                                <image src="${ctxStatic}/monitor/line.png" style="max-width: 50px"></image>
                            </div>
                        </div>
                        <div align="center" class="chart">
                            <image src="${ctxStatic}/monitor/net.png" width="100%"></image>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>


            </div>
            <!-- /.col (RIGHT) -->

            <div class="col-md-4">
                <%--<div class="box box-info">--%>
                <%--<div class="box-header with-border">--%>
                <%--<h3 class="box-title">Line Chart</h3>--%>

                <%--<div class="box-tools pull-right">--%>
                <%--<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>--%>
                <%--<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--<div class="box-body">--%>
                <%--<div class="chart">--%>
                <%--<canvas id="lineChart" height="180"></canvas>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--<!-- /.box-body -->--%>
                <%--</div>--%>
                <div class="box box-info">
                    <div class="box-header with-border">
                        <h3 class="box-title">系统成功失败笔数</h3>

                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="chart">
                            <canvas id="barChart" height="180"></canvas>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>
                <!-- /.box -->
            </div>
        </div>
        <!-- /.row -->
        <div class="row">
            <div class="col-md-6">
                <div class="box box-info">
                    <div class="box-header with-border">
                        <h3 class="box-title">文件传输平台运行情况</h3>

                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="table-responsive">
                            <table class="table no-margin">
                                <thead>
                                <tr>
                                    <th>节点</th>
                                    <th>当天总数</th>
                                    <th>上传总数</th>
                                    <th>下载总数</th>
                                    <th>成功总数</th>
                                    <th>失败总数</th>
                                    <th>节点状态</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td>FS01</td>
                                    <td>3</td>
                                    <td>2</td>
                                    <td>1</td>
                                    <td>3</td>
                                    <td>0</td>
                                    <td><span class="label label-success">运行中</span></td>
                                </tr>
                                <tr>
                                    <td>FS02</td>
                                    <td>5</td>
                                    <td>4</td>
                                    <td>1</td>
                                    <td>5</td>
                                    <td>0</td>
                                    <td><span class="label label-success">运行中</span></td>
                                </tr>
                                <tr>
                                    <td>FS03</td>
                                    <td>4</td>
                                    <td>4</td>
                                    <td>0</td>
                                    <td>2</td>
                                    <td><a id="failInfo">2</a></td>
                                    <td><span class="label label-danger">故障中</span></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <!-- /.table-responsive -->
                    </div>
                    <!-- /.box-body -->
                </div>
                <!-- DONUT CHART -->
                <div class="box box-danger">
                    <div class="box-header with-border">
                        <h3 class="box-title">节点空间使用</h3>

                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                        <label>FS01</label>
                        <canvas id="pieChart-FS01" height="100"></canvas>
                    </div>
                    <div class="box-body">
                        <label>FS01</label>
                        <canvas id="pieChart-FS02" height="100"></canvas>
                    </div>
                    <div class="box-body">
                        <label>FS01</label>
                        <canvas id="pieChart-FS03" height="100"></canvas>
                    </div>
                    <!-- /.box-body -->
                </div>
                <!-- /.box -->
            </div>
            <div class="col-md-6">
                <div class="box box-info">
                    <div class="box-header with-border">
                        <h3 class="box-title">异常提示信息</h3>

                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                        <div class="table-responsive">
                            <table class="table no-margin">
                                <thead>
                                <tr>
                                    <th>时间</th>
                                    <th>异常信息</th>
                                    <th>异常类型</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td>2016-01-14-09:42:24</td>
                                    <td>[FS01]系统文件数超过［500］</td>
                                    <td><span class="label label-success">普通</span></td>
                                </tr>
                                <tr>
                                    <td>2016-01-14-09:36:11</td>
                                    <td>[FS03]系统持续无响应超过［2］次</td>
                                    <td><span class="label label-danger">严重</span></td>
                                </tr>
                                <tr>
                                    <td>2016-01-14-09:42:24</td>
                                    <td>[FS01]系统文件数超过［500］</td>
                                    <td><span class="label label-success">普通</span></td>
                                </tr>
                                <tr>
                                    <td>2016-01-14-09:30:03</td>
                                    <td>[FS03]系统持续无响应超过［2］次</td>
                                    <td><span class="label label-danger">严重</span></td>
                                </tr>
                                <tr>
                                    <td>2016-01-14-09:42:24</td>
                                    <td>[FS01]系统文件数超过［500］</td>
                                    <td><span class="label label-success">普通</span></td>
                                </tr>
                                <tr>
                                    <td>2016-01-14-09:24:35</td>
                                    <td>[FS03]系统持续无响应超过［2］次</td>
                                    <td><span class="label label-danger">严重</span></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <!-- /.table-responsive -->
                    </div>
                    <!-- /.box-body -->
                </div>
            </div>

        </div>

    </section>
    <!-- /.content -->
</div>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    错误信息明细
                </h4>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                    <table class="table no-margin">
                        <thead>
                        <tr>
                            <th>系统</th>
                            <th>文件</th>
                            <th>错误码</th>
                            <th>错误明细</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>CORE</td>
                            <td>core_160101_0017</td>
                            <td>EFT-ERROR-00001</td>
                            <td>文件不存在</td>
                        </tr>
                        <tr>
                            <td>CORE</td>
                            <td>core_160101_0017</td>
                            <td>EFT-ERROR-00001</td>
                            <td>文件不存在</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <!-- /.table-responsive -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default"
                        data-dismiss="modal">关闭
                </button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal -->
</div>
<script src="${ctxStatic}/respond/respond.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap/alte/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/chartjs/Chart.min.js" type="text/javascript"></script>
<script src='${ctxStatic}/fastclick/fastclick.min.js'></script>
<script src="${ctxStatic}/AdminLTE/js/app.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/AdminLTE/js/demo.js" type="text/javascript"></script>
<script>
    $(function () {
        /* ChartJS
         * -------
         * Here we will create a few charts using ChartJS
         */

        //--------------
        //- AREA CHART -
        //--------------

        // Get context with jQuery - using jQuery's .get() method.
        var getCanvas = function getCanvas(id){
            var canvas = document.getElementById(id);
            if (typeof window.G_vmlCanvasManager!="undefined") {
                canvas=window.G_vmlCanvasManager.initElement(canvas);
                var ctx=canvas.getContext("2d");
                return ctx;
            }else {
                var ctx=canvas.getContext("2d");
                return ctx;
            }
        };
//        var areaChartCanvas = $("#areaChart").get(0).getContext("2d");
        var areaChartCanvas = getCanvas("areaChart");
//        var areaChartCanvas = document.getElementById("areaChart").getContext("2d");
        // This will get the first returned node in the jQuery collection.
        var areaChart = new Chart(areaChartCanvas);

        var areaChartData = {
            labels: ["09:06", "09:12", "09:18", "09:24", "09:30", "09:36", "09:42"],
            datasets: [
                {
                    label: "FS01",
                    fillColor: "rgba(210, 214, 222, 1)",
                    strokeColor: "rgba(210, 214, 222, 1)",
                    pointColor: "rgba(210, 214, 222, 1)",
                    pointStrokeColor: "#c1c7d1",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(220,220,220,1)",
                    data: [0, 2, 0, 0, 1, 0, 0]
                },
                {
                    label: "FS02",
                    fillColor: "rgba(60,141,188,0.9)",
                    strokeColor: "rgba(60,141,188,0.8)",
                    pointColor: "#3b8bba",
                    pointStrokeColor: "rgba(60,141,188,1)",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(60,141,188,1)",
                    data: [0, 0, 0, 3, 0, 0, 2]
                },
                {
                    label: "FS03",
                    fillColor: "rgba(119,182,83,0.9)",
                    strokeColor: "rgba(60,141,188,0.8)",
                    pointColor: "#3b8bba",
                    pointStrokeColor: "rgba(60,141,188,1)",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(60,141,188,1)",
                    data: [2, 0, 0, 0, 2, 0, 0]
                }
            ]
        };

        var areaChartOptions = {
            //Boolean - If we should show the scale at all
            showScale: true,
            //Boolean - Whether grid lines are shown across the chart
            scaleShowGridLines: false,
            //String - Colour of the grid lines
            scaleGridLineColor: "rgba(0,0,0,.05)",
            //Number - Width of the grid lines
            scaleGridLineWidth: 1,
            //Boolean - Whether to show horizontal lines (except X axis)
            scaleShowHorizontalLines: true,
            //Boolean - Whether to show vertical lines (except Y axis)
            scaleShowVerticalLines: true,
            //Boolean - Whether the line is curved between points
            bezierCurve: true,
            //Number - Tension of the bezier curve between points
            bezierCurveTension: 0.3,
            //Boolean - Whether to show a dot for each point
            pointDot: false,
            //Number - Radius of each point dot in pixels
            pointDotRadius: 4,
            //Number - Pixel width of point dot stroke
            pointDotStrokeWidth: 1,
            //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
            pointHitDetectionRadius: 20,
            //Boolean - Whether to show a stroke for datasets
            datasetStroke: true,
            //Number - Pixel width of dataset stroke
            datasetStrokeWidth: 2,
            //Boolean - Whether to fill the dataset with a color
            datasetFill: true,
            //String - A legend template
            //Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
            maintainAspectRatio: false,
            //Boolean - whether to make the chart responsive to window resizing
            responsive: true
        };

        //Create the line chart
        areaChart.Line(areaChartData, areaChartOptions);

        //-------------
        //- LINE CHART -
        //--------------
//        var lineChartCanvas = $("#lineChart").get(0).getContext("2d");
//        var lineChart = new Chart(lineChartCanvas);
//        var lineChartOptions = areaChartOptions;
//        lineChartOptions.datasetFill = false;
//        lineChart.Line(areaChartData, lineChartOptions);

        //-------------
        //- PIE CHART -
        //-------------
        // Get context with jQuery - using jQuery's .get() method.
        var pieChartCanvas1 = $("#pieChart-FS01").get(0).getContext("2d");
        var pieChart1 = new Chart(pieChartCanvas1);
        var PieDataFS01 = [
            {
                value: 10,
                color: "#f56954",
                highlight: "#f56954",
                label: "已用空间"
            },
            {
                value: 90,
                color: "#00a65a",
                highlight: "#00a65a",
                label: "可用空间"
            }

        ];
        var pieOptions = {
            //Boolean - Whether we should show a stroke on each segment
            segmentShowStroke: true,
            //String - The colour of each segment stroke
            segmentStrokeColor: "#fff",
            //Number - The width of each segment stroke
            segmentStrokeWidth: 2,
            //Number - The percentage of the chart that we cut out of the middle
            percentageInnerCutout: 50, // This is 0 for Pie charts
            //Number - Amount of animation steps
            animationSteps: 100,
            //String - Animation easing effect
            animationEasing: "easeOutBounce",
            //Boolean - Whether we animate the rotation of the Doughnut
            animateRotate: true,
            //Boolean - Whether we animate scaling the Doughnut from the centre
            animateScale: false,
            //Boolean - whether to make the chart responsive to window resizing
            responsive: true,
            // Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
            maintainAspectRatio: false
            //String - A legend template
        };
        //Create pie or douhnut chart
        // You can switch between pie and douhnut using the method below.
        pieChart1.Doughnut(PieDataFS01, pieOptions);
        var pieChartCanvas2 = $("#pieChart-FS02").get(0).getContext("2d");
        var pieChart2 = new Chart(pieChartCanvas2);
        var PieDataFS02 = [
            {
                value: 21,
                color: "#f56954",
                highlight: "#f56954",
                label: "已用空间"
            },
            {
                value: 79,
                color: "#00a65a",
                highlight: "#00a65a",
                label: "可用空间"
            }
        ];
        pieChart2.Doughnut(PieDataFS02, pieOptions);
        var pieChartCanvas3 = $("#pieChart-FS03").get(0).getContext("2d");
        var pieChart3 = new Chart(pieChartCanvas3);
        var PieDataFS03 = [
            {
                value: 21,
                color: "#f56954",
                highlight: "#f56954",
                label: "已用空间"
            },
            {
                value: 79,
                color: "#00a65a",
                highlight: "#00a65a",
                label: "可用空间"
            }

        ];
        pieChart3.Doughnut(PieDataFS03, pieOptions);

        //-------------
        //- BAR CHART -
        //-------------
        var barChartCanvas = $("#barChart").get(0).getContext("2d");
        var barChart = new Chart(barChartCanvas);
        var barChartData = {
            labels: ["CORE", "ATM", "IBPS", "CRM", "IBANK"],
            datasets: [
                {
                    label: "SUCC",
                    fillColor: "rgba(100, 169, 47, 1)",
                    strokeColor: "rgba(100, 169, 47, 1)",
                    pointColor: "rgba(100, 169, 47, 1)",
                    pointStrokeColor: "#c1c7d1",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(100, 169, 47, 1)",
                    data: [6, 2, 0, 1, 1]
                },
                {
                    label: "FAIL",
                    fillColor: "rgba(206,45,24,0.9)",
                    strokeColor: "rgba(206,45,24,0.9)",
                    pointColor: "#3b8bbb",
                    pointStrokeColor: "rgba(206,45,24,0.9)",
                    pointHighlightFill: "#fff",
                    pointHighlightStroke: "rgba(206,45,24,0.9)",
                    data: [1, 0, 1, 0, 0]
                }
            ]
        };

//        barChartData.datasets[1].fillColor = "#00a65a";
//        barChartData.datasets[1].strokeColor = "#00a65a";
//        barChartData.datasets[1].pointColor = "#00a65a";
        var barChartOptions = {
            //Boolean - Whether the scale should start at zero, or an order of magnitude down from the lowest value
            scaleBeginAtZero: true,
            //Boolean - Whether grid lines are shown across the chart
            scaleShowGridLines: true,
            //String - Colour of the grid lines
            scaleGridLineColor: "rgba(0,0,0,.05)",
            //Number - Width of the grid lines
            scaleGridLineWidth: 1,
            //Boolean - Whether to show horizontal lines (except X axis)
            scaleShowHorizontalLines: true,
            //Boolean - Whether to show vertical lines (except Y axis)
            scaleShowVerticalLines: true,
            //Boolean - If there is a stroke on each bar
            barShowStroke: true,
            //Number - Pixel width of the bar stroke
            barStrokeWidth: 2,
            //Number - Spacing between each of the X value sets
            barValueSpacing: 5,
            //Number - Spacing between data sets within X values
            barDatasetSpacing: 1,
            //String - A legend template
            //Boolean - whether to make the chart responsive
            responsive: true,
            maintainAspectRatio: false
        };

        barChartOptions.datasetFill = false;
        barChart.Bar(barChartData, barChartOptions);
        $("#failInfo").click(function () {
            $('#myModal').modal('show');
        });
    });
</script>
</body>
</html>
