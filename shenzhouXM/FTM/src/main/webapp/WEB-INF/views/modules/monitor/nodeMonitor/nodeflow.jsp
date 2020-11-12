<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>节点流水</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script type="text/javascript">
        var xmlrequest = null;
        var pie = null;
        //var data = [{name:'Firefox',y: 105.0},{name: 'Chrome',y: 12.8,sliced: true,selected: true}];
        $(document).ready(function () {
            //alert('ready');
            var result =  $.ajax({
                url: "${ctx}/monitor/FtNodeMonitor/nodemonitorcontent",
                type: "GET",
                dataType: "json",
                async:true,
                success:function(data){
                    alert('success');

                },
                complete:function (XMLHttpRequest){
                    xmlrequest=XMLHttpRequest;
                    if(200 == xmlrequest.status){
                        var txt = xmlrequest.responseText;
                        //alert(txt);
                        var json = eval('('+txt+')');
                        //alert(json.data);
                        data = json;
                        // createPie();
                        pie = createPie(data);
                    }
                    else{

                    }

                }

            });

        });

        function createPie(data){
            Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function(color) {
                return {
                    radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
                    stops: [
                        [0, color],
                        [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
                    ]
                };
            });
            // Build the chart
            var t = $('#container').highcharts({
                chart: {
                    //主图表区背景色，即x轴与y轴围成的区域背景色
                    plotBackgroundColor: null,
                    //主图表区边框的宽度
                    plotBorderWidth: null,
                    //是否设置阴影
                    plotShadow: false
                },
                title: {
                    text: '资产对比图',
                    //x 设置标题距离
                    x: 0
                },
                //plotOptions 用于设置图表中的数据点相关属性。
                //tooltip:用于设置当鼠标滑向数据点时，显示的提示框信息
                tooltip: {
                    //pointFormat,字符串，工具提示改点线的html,变量用花括号括起来，
                    //可用的变量是point.x,point.y,series.name和series.color和相同的
                    //表单上的其他属性，此外，point.y可以通过延长tooltip.yPrefix和
                    //tooltip.ySuffix变量，
                    enabled:true,
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            // enabled，是否在数据点上显示数据
                            enabled: true,
                            color: '#000000',
                            connectorColor: '#000000',
                            formatter: function() {
                                return '<b style="font-size:5px">'+ this.point.name +'</b>: '+ this.percentage +' %';
                            }
                        }
                    }
                },

                series:[{
                    type: 'pie',
                    name: 'Browser share',
                    data:data
                }],
                // 去掉源代码'highcharts.com' 字样
                credits: {
                    text: '',
                    href: 'http://www.example.com'
                },
            });
        }

    </script>
</head>
<body>
<div id="container"
     style="min-width: 310px; height: 400px; margin: 0 auto"></div>
</body>
</html>

