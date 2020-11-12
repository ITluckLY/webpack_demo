<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>文件上传记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n,s){
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

    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="bizFileUploadLog" action="${ctx}/monitor/FtNodeMonitor/monitorlogForUpload" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>交易码：</label>
    <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
    </form:select>
    <label>节点组名称：</label>
    <form:select id="sysname" path="sysname" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${systemNameList}" htmlEscape="false"/>
    </form:select>
    <label>节点名称：</label>
    <form:select id="nodeNameTemp" path="nodeNameTemp" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${nodeNameList}" htmlEscape="false"/>
    </form:select>
    <label>上传成功标识：</label>
    <form:select id="uploadSuss" path="uploadSuss" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:option value="true" label="true" />
        <form:option value="false" label="false"/>
    </form:select>
    <br><br>
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <th>编号</th>
        <th>交易码</th>
        <th>文件名</th>
        <th>用户名称</th>
        <th>节点组名</th>
        <th>节点名</th>
        <%--<th>认证标志</th>--%>
        <th>客户端文件名</th>
        <th>客户端IP</th>
        <%--<th>压缩标识</th>--%>
        <%--<th>传输内容大小</th>--%>
        <%--<th>编码标志</th>--%>
        <th>上传开始时间</th>
        <%--<th>开始时间</th>--%>
        <%--<th>修改时间</th>--%>
        <%--<th>结束时间</th>--%>
        <%--<th>文件是否存在</th>--%>
        <%--<th>分片序号</th>--%>
        <%--<th>操作标志</th>--%>
        <%--<th>服务端文件重命名控制</th>--%>
        <%--<th>提示信息</th>--%>
        <%--<th>文件大小</th>--%>

        <%--<th>数据节点列表</th>--%>
        <%--<th>偏移量</th>--%>
        <%--<th>分片大小</th>--%>
        <%--<th>加密标志</th>--%>
        <%--<th>服务器IP地址</th>--%>
        <%--<th>服务器名称</th>--%>
        <th>上传成功标识</th>
        <%--<th>目标文件路径</th>--%>
        <%--<th>目标系统名</th>--%>
        <%--<th>交易码</th>--%>
        <th>错误信息</th>
        <th>操作</th>
        <%--<shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><th>操作</th></shiro:hasPermission>--%>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="bizFileUploadLogTemp">
        <tr>
            <td>${bizFileUploadLogTemp.id}</td>
            <td>${bizFileUploadLogTemp.tranCode}</td>
            <td>${bizFileUploadLogTemp.fileName}</td>
            <td>${bizFileUploadLogTemp.uname}</td>
            <td>${bizFileUploadLogTemp.sysname}</td>
            <td>${bizFileUploadLogTemp.nodeNameTemp}</td>
                <%--<td>${bizFileUploadLogTemp.authFlag}</td>--%>
            <td>${bizFileUploadLogTemp.clientFileName}</td>
            <td>${bizFileUploadLogTemp.clientIp}</td>
                <%--<td>${bizFileUploadLogTemp.compressFlag}</td>--%>
                <%--<td>${bizFileUploadLogTemp.contLen}</td>--%>
                <%--<td>${bizFileUploadLogTemp.ebcdicFlag}</td>--%>
            <td><fmt:formatDate value="${bizFileUploadLogTemp.startTime}" type="both"/></td>
                <%--<td><fmt:formatDate value="${bizFileUploadLogTemp.startTime}" type="both"/></td>--%>
                <%--<td><fmt:formatDate value="${bizFileUploadLogTemp.modifiedTime}" type="both"/></td>--%>
                <%--<td><fmt:formatDate value="${bizFileUploadLogTemp.endTime}" type="both"/></td>--%>
                <%--<td>${bizFileUploadLogTemp.fileExists}</td>--%>
                <%--<td>${bizFileUploadLogTemp.fileIndex}</td>--%>
                <%--<td>${bizFileUploadLogTemp.fileMsgFlag}</td>--%>
                <%--<td>${bizFileUploadLogTemp.fileRenameCtrl}</td>--%>
                <%--<td>${bizFileUploadLogTemp.fileRetMsg}</td>--%>
                <%--<td>${bizFileUploadLogTemp.fileSize}</td>--%>

                <%--<td>${bizFileUploadLogTemp.nodeList}</td>--%>
                <%--<td>${bizFileUploadLogTemp.offset}</td>--%>
                <%--<td>${bizFileUploadLogTemp.pieceNum}</td>--%>
                <%--<td>${bizFileUploadLogTemp.scrtFlag}</td>--%>
                <%--<td>${bizFileUploadLogTemp.serverIp}</td>--%>
                <%--<td>${bizFileUploadLogTemp.serverName}</td>--%>
            <td>${bizFileUploadLogTemp.uploadSuss}</td>
                <%--<td>${bizFileUploadLogTemp.tarFileName}</td>--%>
                <%--<td>${bizFileUploadLogTemp.tarSysName}</td>--%>
                <%--<td>${bizFileUploadLogTemp.tranCode}</td>--%>
            <td>${bizFileUploadLogTemp.uploadErrMsg}</td>
            <td>
                <a href="${ctx}/monitor/FtNodeMonitor/monitorlogForUploadOne?id=${bizFileUploadLogTemp.id}">详情</a>
            </td>
           <%-- <shiro:hasPermission name="NodeMonitor:ftNodeMonitor:edit"><td>
                <a href="${ctx}/monitor/FtNodeMonitor/monitorlogForUploadOne?id=${bizFileUploadLogTemp.id}">详情</a>
            </td></shiro:hasPermission>--%>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="pagination">${page}</div>
</body>
</html>