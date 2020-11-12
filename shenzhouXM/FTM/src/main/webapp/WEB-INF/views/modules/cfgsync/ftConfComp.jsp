<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>节点配置同步对比列表</title>
    <meta name="decorator" content="default"/>
</head>
<%--${message}--%>
<body>
<ul class="nav nav-tabs">
    <shiro:hasPermission name="cfg:cfgsync:edit">
    <li><a href="javascript:void(0)" onclick="synConf()">配置同步</a></li>
    </shiro:hasPermission>
    <li><a href="javascript:void(0)" onclick="refreshPage()">刷新</a></li>
    <li><a href="${ctx}/cfgsync/index">返回上一页</a></li>
    <li><a><font color="red">${resultSyn}</font></a></li>
</ul>
<table style="width: 99%">
    <tr>
        <td>
            <span>当前配置：</span>
            <pre>
            <xmp id="currCfgContent">
                ${currCfgContent}
            </xmp>
        </pre>
        </td>
    </tr>
    <c:forEach items="${ftServiceNodeList}" var="fsn" varStatus="loop">
        <tr>
            <td>
                节点：${fsn.name}
                <pre>
                <xmp>
                        ${otherFsnConfMap[fsn.name]}
                </xmp>
             </pre>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${inactiveNodeList!= null && fn:length(inactiveNodeList) > 0}">
        <tr>
            <td>
                未启用节点列表：
                <div style="width:100%;margin-left: 20px;color: #00A6C7">
                    <c:forEach items="${inactiveNodeList}" var="fsn" varStatus="loop">
                        <c:if test="${loop.index>0}">、</c:if>${fsn.name}
                    </c:forEach>
                </div>
            </td>
        </tr>
    </c:if>
</table>
<script type="text/javascript">
    var fileName = '${fileName}';

    function synConf() {
        $.ajax({
            url: "${ctx}/cfgsync/sync",
            type: "post",
            data: {fileName: fileName},
            dataType: "text",
            success: function (msg) {
                if (msg === "true") {
                    showTip("配置文件写入zk成功");
                } else if (msg === "false") {
                    showTip("文件同步失败");
                } else {
                    showTip(msg);
                }
                window.location.reload();
            },
            error: function (msg) {
                showTip("文件同步出错");
            }
        });
    }
    function refreshPage() {
        window.location.reload(true);
    }

</script>
</body>
</html>