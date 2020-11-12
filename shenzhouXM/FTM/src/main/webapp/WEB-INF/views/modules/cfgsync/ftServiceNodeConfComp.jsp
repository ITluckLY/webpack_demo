<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>节点配置同步对比列表</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

            var mes = '${message}';
            if (mes != null && mes != "") {
                alert(mes);
            }
        });
        var All = "all";
        var cfg = "cfg";
        var nodes = "nodes";
        var user = "user";
        var file = "file";
        var fileClean = "file_clean";
        var components = "components";
        var services = "services_info";
        var crontab = "crontab";
        var fileReName = "file_rename";
        function synConf(data) {
            $.ajax({
                url: "${ctx}/servicenode/ftServiceNode/catchFileCfg",
                type: "post",
                data: {fileName: data},
                dataType: "text",
                success: function (msg) {
                    if(msg == "true"){
                        showTip("文件同步成功");
                    }
                    if(msg == "false"){
                        showTip("文件同步失败");
                    }
                    if(msg != 'true' && msg != 'false'){
                        showTip(msg);
                    }
                    window.location.reload();
                },
                error:function(msg){
                    showTip("文件同步出错");
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
        <li><a onclick="synConf(nodes)" id="nodes">配置同步</a></li>
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