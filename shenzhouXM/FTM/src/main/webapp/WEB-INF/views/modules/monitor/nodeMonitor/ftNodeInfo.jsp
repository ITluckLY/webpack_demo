<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>节点状态监控</title>
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

        })
    </script>
</head>
<body>
<table id="contentTable" class="nodeinfotable">
    <thead>
    <tr>
        <th>总节点数</th>
        <th>已连接节点</th>
        <th>未连接节点</th>
        <th>备注</th>
    </tr>
    </thead>
    <tbody>
        <tr>
            <td name='total' width="170px"></td>
            <td name='valid' width="170px"></td>
            <td name='invalid' width="170px"></td>
            <td width="170px">请点击节点，查看详情！</td>
        </tr>
    </tbody>
</table>
<style type="text/css">
    table.nodeinfotable {
        font-family: verdana,arial,sans-serif;
        font-size:11px;
        color:#333333;
        border-width: 1px;
        border-color: #666666;
        border-collapse: collapse;
        width:90%;
    }
    table.nodeinfotable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #dedede;
    }
    table.nodeinfotable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #ffffff;
        text-align: center;
    }
</style>

</body>
</html>