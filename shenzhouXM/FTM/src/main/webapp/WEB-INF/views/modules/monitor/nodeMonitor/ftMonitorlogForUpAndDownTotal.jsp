<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>文件上传下载汇总</title>
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
    <form:form id="searchForm" modelAttribute="bizFileUploadLog" action="${ctx}/monitor/FtNodeMonitor/monitorlogForUpAndDownTotal" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <label>开始时间：</label>

        <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
        <label>结束时间：</label>
        <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
    </form:form>
    <sys:message content="${message}"/>
    <div><a><h5>&nbsp;上传统计</h5></a></div>
    <table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;">
        <thead>
        <tr>
            <th>上传总数</th>
            <th>上传成功数</th>
            <th>上传失败数</th>
            <th>上传成功率</th>

        </tr>
        </thead>
        <tbody>
            <tr>
                <td>${totalListForUpload}</td>
                <td>${totalListForUploadSuss}</td>
                <td>${totalListForUploadFail}</td>
                <td>${totalListForUploadPerc}</td>
            </tr>
        </tbody>
    </table>
    <hr/>
    <div><a><h5>&nbsp;下载统计</h5></a></div>
    <table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;">
        <thead>
        <tr>
            <th>下载总数</th>
            <th>下载成功数</th>
            <th>下载失败数</th>
            <th>下载成功率</th>

        </tr>
        </thead>
        <tbody>
        <tr>
            <td>${totalListForDownload}</td>
            <td>${totalListForDownloadSuss}</td>
            <td>${totalListForDownloadFail}</td>
            <td>${totalListForDownloadPerc}</td>

        </tr>
        </tbody>
    </table>
    <hr/>

    <div class="pagination">${page}</div>
</body>
</html>