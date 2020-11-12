<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>文件传输监控</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/echarts/echarts.js"></script>
    <script type="text/javascript">

        function changRatio() {
            drawEcharts2(${result});
        }
        function drawEcharts2(json){
            var beginDate = "${beginDate}";
            var endDate = "${endDate}";
            var style = $("input[name='style']:checked").val();
            var max = document.getElementById("ratio").value;
            var nameList = [];
            var sourceList = [];
            var selectList = new Object();
            <c:set var="i" value="0"/>
            <c:forEach items="${selected}" var="source">
                sourceList.push("${source}");
                var n = "${source}";
                <c:if test="${i>5}">
                eval("selectList."+n+"= false" );
                </c:if>
                <c:set var="i" value="${i+1}"/>
            </c:forEach>
            <c:forEach items="${result}" var="name">
            var index2 = $.inArray(("${name["source"]}"),nameList);
            if(index2 < 0){
                nameList.push({"name":"${name["source"]}","symbol":style});
            }
            </c:forEach>
            <c:forEach items="${result}" var="name">
            var index1 = $.inArray(("${name["target"]}"),nameList);
            if(index1 < 0){
                nameList.push({"name":"${name["target"]}"});
            }
            </c:forEach>

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
                    'echarts/chart/chord',
                    'echarts/chart/force'
                ],
                function (ec) {
                    // 基于准备好的dom，初始化echarts图表
                    var myChart = ec.init(document.getElementById('main'));
                    var ecConfig = require('echarts/config');
                    var option = {
                        title : {
                            text: '文件传输',
                            x:'right',
                            y:'bottom'
                        },
                        tooltip : {
                            trigger: 'item',
                            formatter: function (params) {
                                if (params.indicator2) {    // is edge
                                    return '生产方：'+params.indicator + ' 消费方：' + params.indicator2 + ' 交易笔数：' + params.name;
                                } else {    // is node
                                    return params.name
                                }
                            }
                        },
                        legend: {
                            orient: 'vertical',
                            x: 'left',
                            y:'center',
                            data: sourceList,
                            selected:selectList
                        },
                        toolbox: {
                            show : true,
                            feature : {
                                restore : {show: true},
                                magicType: {show: true, type: ['force', 'chord']},
                                saveAsImage : {show: false}
                            }
                        },
                        series : [
                            {
                                name: '文件传输',
                                type:'chord',
                                sort : 'ascending',
                                sortSub : 'descending',
                                ribbonType: false,
                                radius: '60%',
                                itemStyle : {
                                    normal : {
                                        label : {
                                            rotate : true,
                                            show:true
                                        }
                                    }
                                },
                                minRadius: 7*max,
                                maxRadius: 20*max,
                                linkSymbol: 'arrow',
                                // 使用 nodes links 表达和弦图
                                nodes: nameList,
                                links: json,
                                formatter: function (params) {

                                }
                            }
                        ]
                    };

                    // 为echarts对象加载数据
                    myChart.setOption(option);
                    myChart.on(ecConfig.EVENT.CLICK, function (params) {
                        var popUp = document.getElementById("pop");
                        popUp.style.top = "200px";
                        popUp.style.left = "400px";
                        popUp.innerHTML=null;
                        popUp.innerHTML="<div id='statusbar'><input type='button' value='close' onclick='hidePopup();'>" +
                            "时间段：" + beginDate + "-" + endDate +"</div></br>"+
                            "<table  id='detail' border='1' width='100%'>" +
                            "<tr><th>交易码</th><th>生产方</th><th>消费方</th><th>交易笔数</th></tr></table>";
                        popUp.style.visibility = "visible";
                        if(typeof(params.special) != "undefined"){
                            $.ajax({
                                url:'${ctx}/client/clientGraphic/getDetail',
                                tpye:'post',
                                data:{'source':params.data.source,'target':params.data.target,'beginDate':beginDate,'endDate':endDate},
                                success:function(data){
                                    for (var j = 0; j < data.length; j++) {
                                            $('#detail').append("<tr><td>"+data[j].tranCode+"</td><td>"+data[j].source+"</td><td>"+data[j].target+"</td><td>"+data[j].weight+"</td></tr>");
                                    }

                                },
                                error:function(data){

                                }
                            })
                        }else{
                            $.ajax({
                                url:'${ctx}/client/clientGraphic/getDetail',
                                tpye:'post',
                                data:{'name':params.data.name,'beginDate':beginDate,'endDate':endDate},
                                success:function(data){
                                    for (var j = 0; j < data.length; j++) {
                                        $('#detail').append("<tr><td>"+data[j].tranCode+"</td><td>"+data[j].source+"</td><td>"+data[j].target+"</td><td>"+data[j].weight+"</td></tr>");
                                    }
                                },
                                error:function(data){
                                }
                            })
                        }
                    });
                }
            );
        }

        function hidePopup(){
            var popUp = document.getElementById("pop");
            popUp.style.visibility = "hidden";
        }

        $(document).ready(function(){
            drawEcharts2(${result});
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
        });



    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="graphicParam" action="${ctx}/client/clientGraphic/chord" method="post" class="breadcrumb form-search">

    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<div style="margin-left: 0px" class="content-wrapper">
    <!-- Main content -->
    <section class="content">
        <div class="row" style="background:#ffffff;">
            <div class="col-md-4" style="padding-left:2px;padding-right:2px;background:#ffffff;width: 98%">
                <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
                <div class="box box-primary" style="margin-bottom:2px;margin-left: 20px;">
                    <label>最小直径：</label><input type="text" id="ratio" value="3" style="width: 60px;"/>
                    <input type="button"  value="确定" style="width: 60px;" onclick="changRatio()"/>
                    <div id="main" style="height:700px;width:100%;float: left;" ></div>
                    <div id="pop" style="position: fixed;visibility: hidden;overflow: auto;border:1px solid #CCC;background-color:#F9F9F9;border:1px solid #333;padding:5px;width: 600px; height: 400px"></div>
                    <div id="style" style="position: absolute;top:120px;right:0;width: 120px;height: 200px; ">生产方图形选择：<br>
                        <input type="radio" value="circle" name="style" checked="checked" onclick="changRatio()"/>圆<br>
                        <input type="radio" value="star" name="style" onclick="changRatio()"/>星星<br>
                        <input type="radio" value="heart" name="style" onclick="changRatio()"/>心形<br>
                        <input type="radio" value="droplet" name="style" onclick="changRatio()"/>水滴<br>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>
</body>
</html>
