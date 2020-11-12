<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>路由配置同步对比列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

            var mes = '${message}';
            if (mes != null && mes != "") {
                alert(mes);
            }
        });
        var route = "route";
        function synConf(data) {
            $.ajax({
                url: "${ctx}/route/ftRoute/catchFileCfg",
                type: "post",
                data: {fileName: data},
                dataType: "text",
                success: function (msg) {
                    if(msg == "true"){
                        alert("文件同步成功");
                    }
                    if(msg == "false"){
                        alert("文件同步失败");
                    }
                    if(msg != 'true' && msg != 'false'){
                        alert(msg);
                    }
                    window.location.reload();
                },
                error:function(msg){
                    alert("文件同步出错");
                }
            });
        }

    </script>
</head>
<%--${message}--%>
<body>
<tr>
    <ul class="nav nav-tabs">
        <li><a>当前节点：${ftServiceNode.name}</a></li>
        <li><a onclick="synConf(route)" id="route">配置同步</a></li>
        <li><a href="${ctx}/servicenode/ftServiceNode/synConf">返回上一页</a></li>
        <li><a><font color="red">${resultSyn}</font></a></li>
    </ul>
    <td>
        <a>当前节点配置：</a>
        <pre>
            <xmp>
                ${returnMsg}
            </xmp>
        </pre>
    </td>


</tr>
<hr>

<c:forEach items="${ftServiceNodeList}" var="fsn" varStatus="loop">
    <tr>
        <td>
                节点：${fsn.name}
        </td>
        <td>
            <pre>
                <xmp>
                    ${otherFsnConf[loop.count-1]}
                </xmp>
             </pre>
        </td>
    </tr>
    <hr/>
</c:forEach>


</body>
</html>