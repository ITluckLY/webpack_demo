<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>饼图</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/echarts/echarts.js"></script>
    <script src="${ctxStatic}/fSelect/jquery-1.11.3.min.js"></script>
    <script src="${ctxStatic}/fSelect/fSelect.js"></script>
    <link href="${ctxStatic}/fSelect/fSelect.css" rel="stylesheet" type="text/css">
    <script type="text/javascript">
        function getLegend(nameList) {
            var options = document.getElementById("legend").options;
            options.length = 0;
            for(var i = 0; i < nameList.length;i++){
                var option = new Option(nameList[i],nameList[i]);
                options.add(option);
            }
        }
        function drawEcharts2(json,showList,selectList){
            // 路径配置
            require.config({
                paths: {
                    echarts: '${ctxStatic}/echarts'
                }
            });
            // 使用
            require(
                [
                    'echarts',
                    'echarts/chart/pie'
                ],
                function (ec) {
                    // 基于准备好的dom，初始化echarts图表
                    var myChart = ec.init(document.getElementById('main'));

                    var option = {
                        title : {
                            text: '文件传输比例',
                            x:'center',
                            y:'bottom'
                        },
                        tooltip : {
                            trigger: 'item',
                            formatter: "{a} <br/>{b} : {c} ({d}%)"
                        },
                        toolbox: {
                            show : true,
                            feature : {
                                mark : {show: false},
                                dataView : {show: false, readOnly: false},
                                magicType : {
                                    show: false,
                                },
                                restore : {show: true},
                                saveAsImage : {show: false}
                            }
                        },

                        legend: {
                            orient: 'vertical',
                            x: 'left',
                            y:'center',
                            data: showList,
                            selected:selectList
                        },
                        calculable : true,
                        series : [
                            {
                                name:'文件传输比例',
                                type:'pie',
                                center: ["50%","60%"],
                                radius : "55%",
                                data:json,
                                itemStyle: {
                                    normal:{
                                        label:{
                                            show:true,
                                            formatter: '{b} : {c} \n ({d}%)'
                                        },
                                        labelLine:{
                                            show:true
                                        }
                                    },
                                    emphasis: {
                                        shadowBlur: 10,
                                        shadowOffsetX: 0,
                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                    }
                                }
                            }
                        ]
                    };
                    // 为echarts对象加载数据
                    myChart.setOption(option);
                }
            );
        }
        var nameList = [];
        var showList = [];
        var selectList = new Object();
        $(document).ready(function(){
            <c:set var="i" value="0"/>
            <c:forEach items="${result}" var="name">
                var n = "${name["name"]}";
                nameList.push("${name["name"]}");
                eval("selectList."+n+"= false" );
                <c:if test="${i<5}">
                eval("selectList."+n+"= true" );
                showList.push(Object.keys(selectList)[${i}]);
                </c:if>
                <c:set var="i" value="${i+1}"/>
            </c:forEach>
            getLegend(nameList);
            drawEcharts2(${result},showList,selectList);
            showList = [];
            $('.demo').fSelect();
        });

        $(function(){
            $("#beginDate").click(function(){
                WdatePicker({
                    skin:"twoer",
                    maxDate:($("#endDate").val()?$("#endDate").val():new Date()),
                    format:"yyyy-MM-dd"
                });
            });
            var date = new Date();
            date.setDate(date.getDate() + 1);
            $("#endDate").click(function(){
                WdatePicker({
                    skin:"twoer",
                    minDate:$("#beginDate").val(),
                    maxDate:date,
                    format:"yyyy-MM-dd"
                });
            });

            $('#showLegend').click(function(){
                $('.legend').find('.fs-option.selected').each(function(i, el) {
                    showList.push($(el).find('.fs-option-label').text());
                });
                var keys = Object.keys(selectList);
                for (var i = 0,len = keys.length; i < len; i++) {
                    var key = keys[i];
                    selectList[key] = false;
                    for(var j = 0; j < showList.length; j++){
                        if(key == showList[j]){
                            selectList[key] = true;
                        }
                    }
                }
                drawEcharts2(${result},showList,selectList);
                showList = [];
            });
        });

    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="graphicParam" action="${ctx}/client/clientGraphic/pie" method="post" class="breadcrumb form-search">
    <form:select id="type" path="type" class="input-medium" style="width:100px">
        <form:option value="sys" label="系统"/>
        <form:option value="user" label="用户"/>
        <form:option value="client" label="客户端"/>
        <form:option value="tranCode" label="交易码"/>
    </form:select>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<div style="margin-left: 0px" class="content-wrapper">
    <!-- Main content -->
    <div class="legend">
        <select class="demo" id="legend" multiple="multiple">
        </select>
        <input id="showLegend" class="btn btn-primary" type="button" value="确定"/>
    </div>
    <section class="content">
        <div class="row" style="background:#ffffff;">
            <div class="col-md-4" style="padding-left:2px;padding-right:2px;background:#ffffff;width: 98%">
                <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
                <div class="box box-primary" style="margin-bottom:2px;">
                    <div id="main" style="height:700px;width:100%;float: left;margin-left: 20px;" ></div>
                </div>
            </div>
        </div>
    </section>
</div>
</body>
</html>
