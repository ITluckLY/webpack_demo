<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>文件下载记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
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
        function showAll(a) {
            a.style.overflow = "visible";
            a.style.whiteSpace = "inherit";
        }

    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="bizFileQueryLog" action="${ctx}/monitor/FtNodeMonitor/monitorlogForQuery"
           method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
    </form:select>
    <label>上传用户名：</label>
    <form:select id="upuname" path="upuname" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftUserInfoList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
    </form:select>
    <label>下载用户名：</label>
    <form:select id="downuname" path="downuname" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftUserInfoList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
    </form:select>
    <label>成功标识：</label>
    <form:select id="transuss" path="transuss" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:option value="true" label="true"/>
        <form:option value="false" label="false"/>
    </form:select>
    <label>文件名：</label>
    <form:input path="oriFilename" htmlEscape="false" style="width:150px" class="input-medium"/>
    <br><br>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;table-layout: fixed;word-wrap:break-word;">
    <thead>
    <tr>
        <%--<th>编号</th>--%>
        <th>交易码</th>
        <%--<th>文件名</th>--%>
        <th>文件上传用户</th>
        <th width="30%">上传文件名</th>
        <th>文件下载用户</th>
        <%--<th>系统名</th>--%>
        <%--<th>节点名</th>--%>
        <%--<th>认证标志</th>--%>
        <%--<th>客户端文件名</th>--%>
        <th>下载客户端IP</th>
        <%--<th>压缩标识</th>--%>
        <%--<th>传输内容大小</th>--%>
        <%--<th>编码标志</th>--%>
        <th>上传记录创建时间</th>
        <th>下载记录创建时间</th>
        <%--<th>开始时间</th>--%>
        <%--<th>修改时间</th>--%>
        <%--<th>下载结束时间</th>--%>
        <%--<th>文件是否存在</th>--%>
        <%--<th>分片序号</th>--%>
        <%--<th>操作标志</th>--%>
        <%--<th>服务端文件重命名控制</th>--%>
        <%--<th>提示信息</th>--%>
        <%--<th>文件大小</th>--%>
        <%--<th>是否最后一片</th>--%>
        <%--<th>数据节点列表</th>--%>
        <%--<th>偏移量</th>--%>
        <%--<th>分片大小</th>--%>
        <%--<th>加密标志</th>--%>
        <%--<th>服务器IP地址</th>--%>
        <%--<th>服务器名称</th>--%>
        <th>成功标识</th>
        <%--<th>目标文件路径</th>--%>
        <%--<th>目标系统名</th>--%>
        <%--<th>交易码</th>--%>
        <th>错误信息</th>
        <th>同步标识</th>

        <%--<shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><th>操作</th></shiro:hasPermission>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="bizFileQueryLog">
        <%--${bizFileQueryLogTemp.downlastPiece eq 'true'}--%>
        <tr>
                <%--<td>${bizFileDownloadLogTemp.id}</td>--%>
                <%--<a href="${ctx}/monitor/FtNodeMonitor/monitorlogForUploadOne?id=${bizFileUploadLogTemp.id}">详情</a>--%>
            <td>${bizFileQueryLog.tranCode}</td>
                <%--<td>${bizFileDownloadLogTemp.fileName}</td>--%>
            <td>
                <a href="${ctx}/monitor/FtNodeMonitor/monitorlogForUploadOne?id=${bizFileQueryLog.upid}">${bizFileQueryLog.upuname}</a>
            </td>
            <td id="fileName" style="width: 20%;WORD-BREAK: break-all;WORD-WRAP:break-word;white-space:nowrap;overflow:hidden;text-overflow: ellipsis;" title="${bizFileQueryLog.oriFilename}" onclick="showAll(this);">${bizFileQueryLog.oriFilename}</td>
            <td>
                <%--<a href="${ctx}/monitor/FtNodeMonitor/monitorlogForDownloadOne?id=${bizFileQueryLog.downid}">${bizFileQueryLog.downuname}${bizFileQueryLog.NODENAME}</a>--%>
                    ${bizFileQueryLog.downuname} ${bizFileQueryLog.NODENAME}
            </td>
                <%--<td>${bizFileDownloadLogTemp.sysname}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.nodeNameTemp}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.authFlag}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.clientFileName}</td>--%>
            <td>${bizFileQueryLog.downclientIp}</td>
                <%--<td>${bizFileDownloadLogTemp.compressFlag}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.contLen}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.ebcdicFlag}</td>--%>
            <td><fmt:formatDate value="${bizFileQueryLog.upcreatedTime}" type="both"/></td>
            <td><fmt:formatDate value="${bizFileQueryLog.downcreatedTime}" type="both"/></td>
                <%--<td><fmt:formatDate value="${bizFileDownloadLogTemp.startTime}" type="both"/></td>--%>
                <%--<td><fmt:formatDate value="${bizFileDownloadLogTemp.modifiedTime}" type="both"/></td>--%>
                <%--<td><fmt:formatDate value="${bizFileQueryLogTemp.downendTime}" type="both"/></td>--%>
                <%--<td>${bizFileDownloadLogTemp.fileExists}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.fileIndex}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.fileMsgFlag}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.fileRenameCtrl}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.fileRetMsg}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.fileSize}</td>--%>
                <%--<td>${bizFileQueryLogTemp.downlastPiece}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.nodeList}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.offset}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.pieceNum}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.scrtFlag}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.serverIp}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.serverName}</td>--%>
            <td>${bizFileQueryLog.downlastPiece&&bizFileQueryLog.uplastPiece&&bizFileQueryLog.downsuss&&bizFileQueryLog.upsuss||bizFileQueryLog.STATE.equals("2")}</td>
                <%--<td>${bizFileDownloadLogTemp.tarFileName}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.tarSysName}</td>--%>
                <%--<td>${bizFileDownloadLogTemp.tranCode}</td>--%>
            <td>${bizFileQueryLog.uperrMsg}${bizFileQueryLog.downerrMsg}</td>
                <%--<shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><td>--%>
                <%--<a href="${ctx}/monitor/FtNodeMonitor/monitorlogForDownloadOne?id=${bizFileDownloadLogTemp.id}">详情</a>--%>
                <%--</td></shiro:hasPermission>--%>
                    <td><c:if test="${bizFileQueryLog.downuname==null||bizFileQueryLog.NODENAME!=null}">同步</c:if><c:if test="${bizFileQueryLog.downuname!=null&&bizFileQueryLog.NODENAME==null}">非同步</c:if></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>