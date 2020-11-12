<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
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
  <link href="${ctxStatic}/AdminLTE/css/AdminLTE.min.css" rel="stylesheet" type="text/css"/>
  <link href="${ctxStatic}/AdminLTE/css/skins/_all-skins.min.css" rel="stylesheet" type="text/css"/>
  <script src="${ctxStatic}/html5/html5.js"></script>
  <script type="text/javascript" src="${ctxStatic}/excanvas/excanvas.min.js"></script>
  <script src="${ctxStatic}/respond/respond.min.js" type="text/javascript"></script>
  <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
  <script src="${ctxStatic}/bootstrap/alte/js/bootstrap.min.js" type="text/javascript"></script>
  <script src='${ctxStatic}/fastclick/fastclick.min.js'></script>
  <script src="${ctxStatic}/AdminLTE/js/app.min.js" type="text/javascript"></script>
  <script src="${ctxStatic}/AdminLTE/js/demo.js" type="text/javascript"></script>
  <script src="${ctxStatic}/echarts/echarts.js"></script>
  <script type="text/javascript">

    $(document).ready(function(){

      /*var typeArray = ["01","02","04"];*/
      var typeArray = ["01","02"];
      var interval = 60000;
      function startMonitor(){
        for(var i=0;i<typeArray.length;i++){
          var type = typeArray[i];
          getMonitorData("${ctx}/file/BizFileStatistics/"+type,type);
        }
      };
      //立即执行一次后，再设置定时器
      startMonitor();
      window.setInterval(startMonitor,interval);
    });

    function getMonitorData(actionName,type){
      var cfg = {
        type: 'POST',
        data: JSON.stringify({id:type}),
        dataType: 'text',
        contentType:'application/json;charset=UTF-8',
        success: function(result) {
          if (type == '04') {
            refreshNodeStateTable(result);
          } else {
            var json = eval('(' + result + ')');
            if (type == '01') {
              drawEcharts1(json);
            } else if (type == '02') {
              drawEcharts2(json);
            }
          }
        },
        error:function (error){
          //alert('返回异常');
        }
      };
      cfg.url = actionName;
      $.ajax(cfg);
    }

    //刷新平台运行情况表格
    function refreshNodeStateTable(result) {
      $('#nodeStatusBody').empty();
      $('#nodeStatusBody').append(result);
    }
    function drawEcharts1(json){

      var timeArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        timeArray[i]=json[i].time;
      }
      var totalArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        totalArray[i]=json[i].Total;
      }
      var uploadArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        uploadArray[i]=json[i].UploadTotal;
      }
      var downloadArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        downloadArray[i]=json[i].DownloadTotal;
      }

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
                'echarts/chart/line'
              ],
              function (ec) {
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(document.getElementById('main'));

                var option = {
                  title : {
                    text: '实时交易量'
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
                    },
                    {
                      name:'上传',
                      type:'line',
                      data:uploadArray,
                    },
                    {
                      name:'下载',
                      type:'line',
                      data:downloadArray,
                    }
                  ]
                };

                // 为echarts对象加载数据
                myChart.setOption(option);
              }
      );
    }

    function drawEcharts2(json){
      var timeArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        timeArray[i]=json[i].time;
      }
      var totalArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        totalArray[i]=json[i].TotalFlow;
      }
      var uploadArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        uploadArray[i]=json[i].UploadTotalFlow;
      }
      var downloadArray = new Array(json.length);
      for(var i=0;i<json.length;i++){
        downloadArray[i]=json[i].DownloadTotalFlow;
      }
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
                'echarts/chart/line'
              ],
              function (ec) {
                // 基于准备好的dom，初始化echarts图表
                var myChart2 = ec.init(document.getElementById('main2'));

                var option = {
                  title : {
                    text: '实时交易流量(M)'
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
                      data : timeArray
                    }
                  ],
                  yAxis : [
                    {
                      type : 'value',
                      axisLabel : {
                        formatter: '{value}M'
                      }
                    }
                  ],
                  series : [
                    {
                      name:'总量',
                      type:'line',
                      data:totalArray,
                    },
                    {
                      name:'上传',
                      type:'line',
                      data:uploadArray,
                    },
                    {
                      name:'下载',
                      type:'line',
                      data:downloadArray,
                    }
                  ]
                };

                // 为echarts对象加载数据
                myChart2.setOption(option);
              }


      );
    }
  </script>
</head>
<body>
<div style="margin-left: 0px" class="content-wrapper">


  <!-- Main content -->
  <section class="content">
    <div class="row" style="background:#ffffff;">

      <!-- /.col (LEFT) -->
      <div class="col-md-4" style="padding-left:2px;padding-right:2px;background:#ffffff;width: 98%">
        <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
        <div class="box box-primary" style="margin-bottom:2px;">
          <div id="main" style="height:57%;width:100%;float: left;height: 280px" ></div>
        </div>
      </div>

      <div class="col-md-4" style="padding-left:2px;padding-right:2px;background:#ffffff;width: 98%">
        <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
        <div class="box box-primary" style="margin-bottom:2px;">
          <div id="main2" style="height:57%;width:100%;float: left;height: 280px" ></div>
        </div>
      </div>
    </div>

<!--
    <div class="row" style="background:#ffffff;">
      <div class="col-md-12" style="padding-left:2px;padding-right:2px;width: 60%">
        <div class="box box-info" style="margin-bottom:2px;">
          <div class="box-header with-border">
            <h3 class="box-title">文件传输平台运行情况</h3>
          </div>
          <div class="box-body">
            <div class="table-responsive">
              <table class="table no-margin">
                <thead>
                <tr>
                  <th>总数</th>
                  <th>上传总数</th>
                  <th>下载总数</th>
                  <th>成功总数</th>
                  <th>失败总数</th>
                  <th>总流量(M)</th>
                  <th>上传流量(M)</th>
                  <th>下载流量(M)</th>
                </tr>
                </thead>
                <tbody id="nodeStatusBody" style="max-height: 250px; overflow:auto;">
                <tr>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>


    -->
  </section>
</div>
</body>
</html>
