<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>文件上传记录详情</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function() {

        });
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a>文件上传记录详情</a></li>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed" width="99%">
        <thead>
        <tr>
            <th>文件上传列表名</th>
            <th>列表值</th>
        </tr>
        </thead>
        <tbody>
        <tr><td>编号</td><td>${bizFileUploadLogTemp.id}</td></tr>
        <tr><td>文件名</td><td>${bizFileUploadLogTemp.fileName}</td></tr>
        <tr><td>用户名称</td><td>${bizFileUploadLogTemp.uname}</td></tr>
        <tr><td>节点组名</td><td>${bizFileUploadLogTemp.sysname}</td></tr>
        <tr><td>节点名</td><td>${bizFileUploadLogTemp.nodeNameTemp}</td></tr>
        <tr><td>认证标志</td><td>${bizFileUploadLogTemp.authFlag}</td></tr>
        <tr><td>客户端文件名</td><td>${bizFileUploadLogTemp.clientFileName}</td></tr>
        <tr><td>客户端IP</td><td>${bizFileUploadLogTemp.clientIp}</td></tr>
        <%--<tr><td>压缩标识</td><td>${bizFileUploadLogTemp.compressFlag}</td></tr>--%>
        <tr><td>传输内容大小</td><td>${bizFileUploadLogTemp.contLen}</td></tr>
        <tr><td>编码标志</td><td>${bizFileUploadLogTemp.ebcdicFlag}</td></tr>
        <tr><td>上传开始时间</td><td><fmt:formatDate value="${bizFileUploadLogTemp.startTime}" type="both"/></td></tr>
        <tr><td>上传结束时间</td><td><fmt:formatDate value="${bizFileUploadLogTemp.endTime}" type="both"/></td></tr>
        <tr><td>记录创建时间</td><td><fmt:formatDate value="${bizFileUploadLogTemp.createdTime}" type="both"/></td></tr>
        <tr><td>记录修改时间</td><td><fmt:formatDate value="${bizFileUploadLogTemp.modifiedTime}" type="both"/></td></tr>
        <%--<tr><td>文件是否存在</td><td>${bizFileUploadLogTemp.fileExists}</td></tr>--%>
        <tr><td>分片序号</td><td>${bizFileUploadLogTemp.fileIndex}</td></tr>
        <tr><td>操作标志</td><td>${bizFileUploadLogTemp.fileMsgFlag}</td></tr>
        <tr><td>服务端文件重命名控制</td><td>${bizFileUploadLogTemp.fileRenameCtrl}</td></tr>
        <%--<tr><td>提示信息</td><td>${bizFileUploadLogTemp.fileRetMsg}</td></tr>--%>
        <tr><td>文件大小</td><td>${bizFileUploadLogTemp.fileSize}</td></tr>
        <%--<tr><td>数据节点列表</td><td>${bizFileUploadLogTemp.nodeList}</td></tr>--%>
        <tr><td>偏移量</td><td>${bizFileUploadLogTemp.offset}</td></tr>
        <tr><td>分片大小</td><td>${bizFileUploadLogTemp.pieceNum}</td></tr>
        <tr><td>加密标志</td><td>${bizFileUploadLogTemp.scrtFlag}</td></tr>
        <tr><td>服务器IP地址</td><td>${bizFileUploadLogTemp.serverIp}</td></tr>
        <%--<tr><td>服务器名称</td><td>${bizFileUploadLogTemp.serverName}</td></tr>--%>
        <tr><td>是否最后一片</td><td>${bizFileUploadLogTemp.lastPiece}</td></tr>
        <tr><td>本流程是否执行成功</td><td>${bizFileUploadLogTemp.suss}</td></tr>
        <tr><td>目标文件路径</td><td>${bizFileUploadLogTemp.tarFileName}</td></tr>
        <%--<tr><td>目标系统名</td><td>${bizFileUploadLogTemp.tarSysName}</td></tr>--%>
        <tr><td>交易码</td><td>${bizFileUploadLogTemp.tranCode}</td></tr>
        <tr><td>流水号</td><td>${bizFileUploadLogTemp.flowNo}</td></tr>
        </tbody>
    </table>

    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</body>
</html>