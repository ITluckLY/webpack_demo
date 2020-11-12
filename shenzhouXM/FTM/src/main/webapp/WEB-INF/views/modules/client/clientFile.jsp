<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>数据文件</title>
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
        <div class="accordion-heading">
        <a class="accordion-toggle">文件路径<i class="icon-refresh pull-right" onclick="refreshTree();"></i></a>
        </div>
        <div id="ztree1" class="ztree"></div>
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
                var nodeId='<%=request.getAttribute("name")%>';
                $('#fileContent').attr("src", "${ctx}/client/clientFile/getNodeContent?path=" + treeNode.id + "&treeName="+treeNode.name);
                },

            onExpand:function(event, treeId, treeNode){
                var nodeId='<%=request.getAttribute("name")%>';
                $.fn.zTree.getZTreeObj("ztree1").removeChildNodes(treeNode);
                $.getJSON("${ctx}/client/clientFile/getSubDir?treeName=" + treeNode.name + "&path=" + treeNode.id, function (data) {
                    if(data){
                        if(data){
                            for(var i = 0; i < data.length; i ++){
                                $.fn.zTree.getZTreeObj("ztree1").addNodes(treeNode, data[i], false);
                            }
                        }
                    }
                });
            }
        }
    };

    function refreshTree1() {
        var nodeId='<%=request.getAttribute("name")%>';
        var treeName='<%=request.getAttribute("treeName")%>';
        var allPath='<%=request.getAttribute("allPath")%>';
        $.ajax({
            type:"post",
            url:"${ctx}/client/clientFile/getFileTree",
            data:{"allPath":allPath},
            dataType:"json",
            traditional:true,
            success:function (data) {
                $.fn.zTree.init($("#ztree1"), setting, data);
                if (data.length == 0) showTip("找不到文件");
                $('#fileContent').attr("src", "${ctx}/client/clientFile/query?path=" + treeName+"?nodeId=" + nodeId);//默认显示所有文件
            }
        });
    }

    refreshTree1();


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