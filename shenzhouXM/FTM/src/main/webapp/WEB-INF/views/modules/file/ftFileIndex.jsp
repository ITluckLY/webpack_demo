<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文件管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/treeview.jsp" %>
    <style type="text/css">
        .ztree {
            overflow: auto;
            margin: 0;
            _margin-top: 10px;
            padding: 10px 0 0 10px;
        }
    </style>
</head>
<body>
<sys:message content="${message}"/>
<div id="content" class="row-fluid">
    <div id="left" class="accordion-group">
            <%--<div class="controls">
                    节点：
                <form:select id="nodeId" path="ftFile.id" style="width:125px" onclick="refreshTree()"><form:options items="${nodeList}" itemLabel="name" itemValue="id" htmlEscape="false"/></form:select>
            </div>--%>
        <div class="accordion-heading">
        <a class="accordion-toggle">文件路径<i class="icon-refresh pull-right" onclick="refreshTree();"></i></a>
        </div>
        <div id="ztree" class="ztree"></div>
    </div>
    <div id="openClose" class="close">&nbsp;</div>
    <div id="right">
        <iframe id="fileContent"  width="100%" height="91%" frameborder="0"></iframe>
    </div>
</div>
<script type="text/javascript">
    var setting = {
        data: {simpleData: {enable: true, idKey: "id", pIdKey: "pid", rootPId: '0'}},
        callback: {
            onClick: function (event, treeId, treeNode) {
                var nodeId='<%=request.getAttribute("nodeName")%>';
//                var nodeId = $("#nodeId").val()
                <%--var nodeId = ${ftServiceNode.name};--%>
                <%--var nodeId=eval('${ftServiceNode.name}');--%>
                var id = treeNode.id == '0' ? '' : treeNode.id;
                <%--$('#fileContent').attr("src", "${ctx}/file/ftFile/getContent?path=" + treeNode.path);--%>
                $('#fileContent').attr("src", "${ctx}/file/ftFile/getNodeContent?path=" + treeNode.id + "&nodeId="+nodeId);
                //$('#fileContent').attr("src", "${ctx}/file/ftFile/getNodeContent?path=sendfile2" + "&nodeId="+nodeId);

            },
            onExpand:function(event, treeId, treeNode){
                var nodeId='<%=request.getAttribute("nodeName")%>';
//                var nodeId = $("#nodeId").val()
                <%--var nodeId = ${ftServiceNode.name};--%>
                <%--var nodeId=eval('${ftServiceNode.name}');--%>
                $.fn.zTree.getZTreeObj("ztree").removeChildNodes(treeNode);
                    $.getJSON("${ctx}/file/ftFile/getSubDir?nodeId=" + nodeId + "&path=" + treeNode.id, function (data) {
                    //$.getJSON("${ctx}/file/ftFile/getSubDir?nodeId=" + nodeId + "&path=sendfile2", function (data) {
                    if(data){
                        if(data){
                            for(var i = 0; i < data.length; i ++){
                                $.fn.zTree.getZTreeObj("ztree").addNodes(treeNode, data[i], false);
                            }
                        }
                    }
                });
            }
        }
    };

    function refreshTree() {
        var nodeId='<%=request.getAttribute("nodeName")%>';
//        var nodeId = $("#nodeId").val();
        <%--var nodeId = ${ftServiceNode.name};--%>
        <%--var nodeId=eval('${ftServiceNode.name}');--%>
        $.getJSON("${ctx}/file/ftFile/getNodeFileTree?nodeId=" + nodeId, function (data) {
            $.fn.zTree.init($("#ztree"), setting, data);
//            var showNode = data[data.length -1];
            if (data.length == 0) showTip("找不到文件");
            $('#fileContent').attr("src", "${ctx}/file/ftFile/query?nodeId=" + nodeId);//默认显示所有文件
        });
    }
    refreshTree();

    var leftWidth = 180; // 左侧窗口大小
    var htmlObj = $("html"), mainObj = $("#main");
    var frameObj = $("#left, #openClose, #right, #right iframe");
    function wSize() {
        var strs = getWindowSize().toString().split(",");
        htmlObj.css({"overflow-x": "hidden", "overflow-y": "hidden"});
        mainObj.css("width", "auto");
        frameObj.height(strs[0] - 5);
        var leftWidth = ($("#left").width() < 0 ? 0 : $("#left").width());
        $("#right").width($("#content").width() - leftWidth - $("#openClose").width() - 5);
        $(".ztree").width(leftWidth - 10).height(frameObj.height() - 46);
    }
</script>
<script src="${ctxStatic}/common/wsize.min.js" type="text/javascript"></script>
</body>
</html>
