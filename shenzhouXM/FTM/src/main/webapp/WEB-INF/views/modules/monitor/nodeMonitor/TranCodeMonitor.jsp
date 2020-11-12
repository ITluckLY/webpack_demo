<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>交易码动态</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>

<form:form id="searchForm" modelAttribute="tranCodeMonitor" action="${ctx}/monitor/FtNodeMonitor/trancodemonitor/trancodelist" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <%--<li><label>交易码：</label>
            <form:input path="tranCode" htmlEscape="false" maxlength="40" class="input-medium"/>
        </li>--%>
        <li><label>交易码：</label>
        <form:select id="tranCode" path="tranCode" class="input-medium" style="width:150px">
            <form:option value="" label="——请选择——"/>
            <form:options items="${ftServiceInfoList}" itemLabel="trancode" itemValue="trancode" htmlEscape="false"/>
        </form:select></li>

        <%--<li><label class="controls" >服务端文件：</label>
            <form:input path="fileName" htmlEscape="false" maxlength="80" class="input-medium"/>
        </li>--%>
        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
        <li class="clearfix">
        </li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th width="8%">交易码</th>
        <th width="15%">交易码描述</th>
        <th width="8%">文件上传时间</th>
        <th width="8%">文件下载时间</th>
        <th width="15%">服务端文件名</th>
        <th width="15%">源文件名</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="trancodemonitor">
        <tr>
            <td  style="WORD-WRAP:break-word">
                  ${trancodemonitor.tranCode}
                </td>
            <td style="WORD-WRAP:break-word">
                    ${trancodemonitor.describe}
            </td>
            <td  style="WORD-WRAP:break-word">
                <fmt:formatDate value="${trancodemonitor.uploadTime}" type="both"/>
            </td>
            <td style="WORD-WRAP:break-word">
                <fmt:formatDate value="${trancodemonitor.downloadTime}" type="both"/>
            </td>
            <td style="WORD-WRAP:break-word">
                ${trancodemonitor.fileName}
            </td>
            <td style="WORD-WRAP:break-word">
                ${trancodemonitor.oriFilename}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>