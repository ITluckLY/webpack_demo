<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文件清理管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }


        $(function () {
            var arr = document.getElementsByName("radio");
            var temp = [];
            temp[0] = "";
            var z = 1;

            $("#btnStart").click(function () {
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i].checked) {
                        var hiddenId = document.getElementsByName("hiddenId")[i].defaultValue;
                        temp[0] = hiddenId;
                        var trVal = arr[i].parentNode.parentNode;
//                        var tdList = trVal.childNodes;
                        var tdList = trVal.children;

                        for (var j = 1; j < tdList.length; j++) {
                            if (tdList[j].localName == "td") {
                                temp[z] = tdList[j].innerHTML.trim();
                                z = z + 1;
                            }
                        }
                    }
                }
                var id = temp[0];
                var state = temp[7];
                if(state == "停止"){
                    $.ajax(
                            {
                                url: "${ctx}/fileclean/ftFileClean/startFileClean",
                                type: "post",
                                dataType: "html",
                                data: {
                                    id: id
                                },
                                success: function (msg) {
                                    window.location.reload();
                                },
                                error: function () {
                                    alert("请求页面异常");
                                }
                            }
                    )
                } else if (state == null){
                    showTip("未选中任何记录");
                } else {
                    showTip("请勿重复操作");
                }
            });


            $("#btnStop").click(function () {
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i].checked) {
                        var hiddenId = document.getElementsByName("hiddenId")[i].defaultValue;
                        temp[0] = hiddenId;
                        var trVal = arr[i].parentNode.parentNode;
//                        var tdList = trVal.childNodes;
                        var tdList = trVal.children;

                        for (var j = 1; j < tdList.length; j++) {
                            if (tdList[j].localName == "td") {
                                temp[z] = tdList[j].innerHTML.trim();
                                z = z + 1;
                            }
                        }
                    }
                }
                var id = temp[0];
                var state = temp[7];
                if(state == "启动"){
                    $.ajax(
                            {
                                url: "${ctx}/fileclean/ftFileClean/stopFileClean",
                                type: "post",
                                dataType: "html",
                                data: {
                                    id: id
                                },
                                success: function (msg) {
                                    window.location.reload();
                                },
                                error: function () {
                                    alert("请求页面异常");
                                }
                            }
                    )
                } else if (state == null){
                    showTip("未选中任何记录");
                } else {
                    showTip("请勿重复操作");
                }
            });


        });



    </script>
    <%-- <style>
         .form-search .ul-form li label {
             width: auto
         }
     </style>--%>
</head>
<body>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/fileclean/ftFileClean/">文件清理列表</a></li>
    <shiro:hasPermission name="fileclean:ftFileClean:edit">
        <li><a href="${ctx}/fileclean/ftFileClean/form">文件清理添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="fileclean:ftFileClean:view">
        <a href="${ctx}/fileclean/ftFileClean/form">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftFileClean" action="${ctx}/fileclean/ftFileClean/" method="post"
           class="breadcrumb form-search marfl-new">
    <ul class="ul-form ">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <li>
        <label>目标目录：</label>
            <form:input path="targetDir" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li>
        <label>状态：</label>
            <form:select path="state" htmlEscape="false" maxlength="50" class="input-medium">
                <form:option value="">——请选择——</form:option>
                <form:option value="0">停止</form:option>
                <form:option value="1">启动</form:option>
            </form:select>
        </li>
        <li style="width: 1%"/>
        <li>
            <button id="btnSubmit" class="btn btn-primary" type="submit"><i style="margin-right: 0.5em"
                                                                            class="icon icon-search"></i>查询
            </button>
        <shiro:hasPermission name="fileclean:ftFileClean:edit">
            <input id="btnStart" type="button" value="启动" class="btn btn-primary"/>
            <input id="btnStop" type="button" value="停止" class="btn btn-primary"/>
        </shiro:hasPermission>
        </li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%">
    <thead>
    <tr>
        <th></th>
        <th width="20%">目标目录</th>
        <th>保留时间(分钟)</th>
        <th>文件归档</th>
        <th  width="20%">归档路径</th>
        <th  width="20%">备注</th>
        <th>节点组名称</th>
        <th>状态</th>
        <shiro:hasPermission name="fileclean:ftFileClean:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftFileClean">
        <tr id="fFClean" name="fFClean">
            <td>
                <input name="radio" type="radio"/>
                    <%--${ftFileCleanTemp.id}--%>
                <input type="hidden" name="hiddenId" id="hiddenId" value="${ftFileClean.id}">
            </td>
            <td>
                <a href="${ctx}/fileclean/ftFileClean/form?id=${ftFileClean.id}&state=${ftFileClean.state}">
                        ${ftFileClean.targetDir}
                </a></td>
            <td>
                    ${ftFileClean.keepTime}
            </td>
            <td>
                    ${ftFileClean.isBackup=="true"?"是":""}
                    ${ftFileClean.isBackup=="false"?"否":""}
            </td>
            <td>
                    ${ftFileClean.backupPath}
            </td>
            <td>
                    ${ftFileClean.remarks}
            </td>
            <td>
                    ${ftFileClean.system}
            </td>
            <td>
                    ${ftFileClean.state=="0"?"停止":""}
                    ${ftFileClean.state=="1"?"启动":""}
            </td>
            <shiro:hasPermission name="fileclean:ftFileClean:edit">
                <td>
                    <a href="${ctx}/fileclean/ftFileClean/form?id=${ftFileClean.id}&state=${ftFileClean.state}">修改</a>
                    <a href="${ctx}/fileclean/ftFileClean/delete?id=${ftFileClean.id}&state=${ftFileClean.state}"
                       onclick="return confirmx('确认要删除该文件清理吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>