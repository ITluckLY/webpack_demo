<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>节点监控</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/selfDefine/strContains.js" type="text/javascript"></script>
    <script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
    <script type="text/javascript">
        $(document).ready(function () {
            var state = document.getElementsByName('state');
            for(i=0;i<state.length;i++){
                if(state[i].innerHTML==1){
                    state[i].innerHTML="连接中"
                }else {
                    state[i].innerHTML="未连接"
                }
            }
            var storage = document.getElementsByName('storage');
            for(i=0;i<storage.length;i++) {
                if (contains(storage[i].innerHTML, "/")){
                    var sto = storage[i].innerHTML.split("/");
                    var used = parseInt(sto[0]) / 1024 /1024;
                    var total = parseInt(sto[1]) / 1024 /1024;
                    var storageMB = parseInt(used) + "/" + parseInt(total);
                    storage[i].innerHTML=storageMB;
                }
            }
        })
    </script>
    <style type="text/css">
        #container {
            height: 400px;
            min-width: 310px;
            max-width: 800px;
            margin: 0 auto;
        }
    </style>

</head>
<body>
<form:form id="searchForm" modelAttribute="ftNodeMonitor" action="${ctx}/monitor/FtNodeMonitor/" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <%--<li><label>系统名称：</label>
            <form:input path="system" htmlEscape="false" maxlength="256" class="input-medium"/>
        </li>--%>
        <li><label>节点名称：</label>
            <form:input path="node" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>

        <li class="btns"><button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i></button></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table  table-bordered table-condensed" <%--style="width: 99%;TABLE-LAYOUT:fixed"--%>>
    <thead>
    <tr>
        <th>节点名称</th>
        <th>节点状态</th>
        <th>节点组名称</th>
        <th>文件目录</th>
        <th>文件数</th>
        <th>存储空间(单位:M)</th>
        <th>CPU</th>
        <%--<th>磁盘</th>--%>
        <th>内存</th>
        <th>流量</th>
        <th>阀值</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftNodeMonitorTemp">
        <tr>
            <td>
                    ${ftNodeMonitorTemp.node}
            </td>
            <td name="state" ${ftNodeMonitorTemp.state=='1'?"bgColor=lightgreen":"bgColor=red"}>
                    ${ftNodeMonitorTemp.state}
            </td>
            <td>
                    ${ftNodeMonitorTemp.system}
            </td>
            <td>
                    ${ftNodeMonitorTemp.catalog}
            </td>
            <td>
                    ${ftNodeMonitorTemp.filenumber}
            </td>
            <td name="storage">
                    ${ftNodeMonitorTemp.storage}
            </td>
            <td><a href="${ctx}/monitor/FtNodeMonitor/cpu?id=${ftNodeMonitorTemp.id}&node=${ftNodeMonitorTemp.node}">
                    ${ftNodeMonitorTemp.cpu}
            </a></td>
            <%--<td><a href="${ctx}/monitor/FtNodeMonitor/disk?id=${ftNodeMonitorTemp.id}&node=${ftNodeMonitorTemp.node}">
                    &lt;%&ndash;${ftNodeMonitorTemp.disk}&ndash;%&gt;
                    <c:if test="${ftNodeMonitorTemp.disk == null}">
                        <a style="color:red">无记录</a>
                    </c:if>
                    <c:if test="${ftNodeMonitorTemp.disk != null}">
                        详情请点击
                    </c:if>
            </a></td>--%>
            <td><a href="${ctx}/monitor/FtNodeMonitor/memory?id=${ftNodeMonitorTemp.id}&node=${ftNodeMonitorTemp.node}">
                    ${ftNodeMonitorTemp.memory}
            </a></td>
            <td><a href="${ctx}/monitor/FtNodeMonitor/flowrate?id=${ftNodeMonitorTemp.id}&node=${ftNodeMonitorTemp.node}">
                    ${ftNodeMonitorTemp.flowrate}
            </a></td>
            <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:view">
                <td>
                    <c:if test="${ftNodeMonitorTemp.system != null}">
                        <a href="${ctx}/monitor/FtNodeMonitor/form?id=${ftNodeMonitorTemp.id}">设置</a>
                    </c:if>
                    <c:if test="${ftNodeMonitorTemp.system == null}">
                        <a>设置</a>
                    </c:if>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
<script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
<script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts-3d.js"></script>
<script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>
<div class="pagination">${page}</div>
<%--<script type="text/javascript">--%>
    <%--$(document).ready(function() {--%>
        <%--var nodename=[];--%>
        <%--var total=[];--%>
        <%--var use=[];--%>
        <%--var left=[];--%>
        <%--$.ajax({--%>
            <%--type: "get",--%>
            <%--dataType: "json",--%>
            <%--url: '${ctx}/monitor/FtNodeMonitor/allnodeInfo',--%>
            <%--success: function (data) {--%>
                <%--$.each(data, function(i,item){--%>
                  <%--nodename[i]=item.system+"-"+item.node;--%>
                   <%--var sto=item.storage.split("/");--%>
                    <%--use[i]=parseInt(sto[0]);--%>
                    <%--total[i]=parseInt(sto[1]);--%>
                    <%--left[i]=parseInt(total[i]-use[i]);--%>
                <%--});--%>

                <%--chart = new Highcharts.Chart({--%>
                    <%--chart: {--%>
                        <%--renderTo: 'container',--%>
                        <%--type: 'column',--%>
                    <%--},--%>
                    <%--credits: {--%>
                        <%--enabled: false--%>
                    <%--},--%>
                    <%--exporting: {--%>
                        <%--enabled: false--%>
                    <%--},--%>
                    <%--title: {--%>
                        <%--text: '各节点系统的存储情况'--%>
                    <%--},--%>

                    <%--xAxis: {--%>
                        <%--categories:nodename--%>
                    <%--},--%>

                    <%--yAxis: {--%>
                        <%--allowDecimals: false,--%>
                        <%--min: 0,--%>
                        <%--title: {--%>
                            <%--text: '存储空间（G）'--%>
                        <%--},--%>
                    <%--},--%>

                    <%--tooltip: {--%>
                        <%--headerFormat: '<b>{point.key}</b><br>',--%>
                        <%--pointFormat: '<span style="color:{series.color}">\u25CF</span> {series.name}: {point.y} / {point.stackTotal}'--%>
                    <%--},--%>

                    <%--plotOptions: {--%>
                        <%--column: {--%>
                            <%--stacking: 'normal',--%>
                            <%--depth: 40--%>
                        <%--}--%>
                    <%--},--%>

                    <%--series: [{--%>
                        <%--name: '剩余',--%>
                        <%--data: left,--%>
                        <%--stack: 'male'--%>
                    <%--}, {--%>
                        <%--name: '已使用',--%>
                        <%--data: use,--%>
                        <%--stack: 'male'--%>
                    <%--}]--%>
                <%--});--%>
            <%--}--%>
        <%--})--%>
    <%--});--%>
<%--</script>--%>
<div id="container" style="height: 400px"></div>
</body>
</html>