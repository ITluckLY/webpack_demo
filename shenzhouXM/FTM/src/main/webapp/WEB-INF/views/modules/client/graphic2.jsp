<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>文件传输监控</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/echarts/echarts.js"></script>
    <script type="text/javascript">
        function drawEcharts1(){
            var timeArray = ${timeList};
            var totalArray = ${findTotal};
            var uploadArray = ${findUploadTotal};
            var downloadArray = ${findDownloadTotal};
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
                    'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                    'echarts/chart/line',
                ],
                function (ec) {
                    // 基于准备好的dom，初始化echarts图表
                    var myChart = ec.init(document.getElementById('main'));
                    var name = $("#detail option:selected").val();
                    var option = {
                        title : {
                            text: '文件交易量('+name+')'
                        },
                        tooltip : {
                            trigger: 'axis'
                        },
                        legend: {
                            data:['总量','上传','下载'],
                            y:'bottom'
                        },
                        toolbox: {
                            show : true,
                            feature : {
                                mark : {show: false},
                                dataView : {show: false, readOnly: false},
                                magicType : {show: true, type: ['line', 'bar']},
                                restore : {show: true},
                                saveAsImage : {show: false}
                            }
                        },
                        calculable : true,
                        xAxis : [
                            {
                                type : 'category',
                                boundaryGap : false,
                                data :timeArray
                            }
                        ],
                        yAxis : [
                            {
                                type : 'value',
                                axisLabel : {
                                    formatter: '{value}'
                                }
                            }
                        ],
                        series : [
                            {
                                name:'总量',
                                type:'line',
                                data:totalArray,
                                itemStyle:{
                                    normal:{
                                        label:{
                                            show:true
                                        }
                                    }
                                }
                            },
                            {
                                name:'上传',
                                type:'line',
                                data:uploadArray,
                                itemStyle:{
                                    normal:{
                                        label:{
                                            show:true
                                        }
                                    }
                                }
                            },
                            {
                                name:'下载',
                                type:'line',
                                data:downloadArray,
                                itemStyle:{
                                    normal:{
                                        label:{
                                            show:true
                                        }
                                    }
                                }
                            },

                        ]
                    };

                    // 为echarts对象加载数据
                    myChart.setOption(option);
                }
            );
        }

        function getValue() {
            var str = $("#type option:selected").val();
            var options = document.getElementById("detail").options;
            var selected = "${selectDetail}";
            options.length = 1;
            <c:forEach items="${type}" var="item">
            var name = "${item.key}";
            if(name.indexOf(str)>=0){
                <c:forEach items="${item.value}" var="choose">
                var option = new Option("${choose}","${choose}");
                options.add(option);
                </c:forEach>
            }
            </c:forEach>
            if(selected != null){
                $("#detail").find("option[value=${selectDetail}]").attr("selected",true);
            }
        }
        $(document).ready(function(){
            drawEcharts1();
            getValue();
        });


        function page(n, s) {
            loading();
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
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
<form:form id="searchForm" modelAttribute="graphicParam" action="${ctx}/client/clientGraphic/viewPage" method="post" class="breadcrumb form-search">
    <form:select id="type" path="type" name="type" class="input-medium" style="width:100px" onchange="getValue()" autocomplete="off">
        <form:option value="all" label="请选择"/>
        <form:option value="sys" label="系统"/>
        <form:option value="user" label="用户"/>
        <form:option value="client" label="客户端"/>
        <form:option value="tranCode" label="交易码"/>
    </form:select>
    <form:select id="detail" path="detail" name="detail" class="input-medium" style="width:100px" autocomplete="off">
        <form:option value="all" label="请选择"/>
    </form:select>
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
                <div class="box box-primary" style="margin-bottom:2px;">
                    <div id="main" style="height:700px;width:100%;float: left;margin-left: 20px;" ></div>
                </div>
            </div>
        </div>
    </section>
</div>
<div class="pagination">${page}</div>
</body>
</html>