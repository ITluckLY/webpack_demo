<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>组件管理</title>
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
        function edit(id) {
            var formObj = $("#" + id);
            formObj.attr("action", "${ctx}/component/ftComponent/form");
            formObj.submit();
        }
        <%--function remove1(id) {--%>
        <%--var formObj = $("#" + id);--%>
        <%--formObj.attr("action", "${ctx}/component/ftComponent/delete");--%>
        <%--formObj.submit();--%>
        <%--}--%>
    </script>
</head>
<body>
<%--<ul class="nav nav-tabs nav-tabs-hidden">
    <li class="active"><a href="${ctx}/component/ftComponent/">组件列表</a></li>
    <shiro:hasPermission name="component:ftComponent:edit">
        <li><a href="${ctx}/component/ftComponent/form">组件添加</a></li>
    </shiro:hasPermission>
</ul>--%>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
    <shiro:hasPermission name="component:ftComponent:view">
        <a href="${ctx}/component/ftComponent/form">新增</a>
    </shiro:hasPermission>
</div>
<form:form id="searchForm" modelAttribute="ftComponent" action="${ctx}/component/ftComponent/" method="post"
           class="breadcrumb form-search marfl-new">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>组件名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
        </li>
        <li class="btns">
            <button id="btnSearch" class="btn btn-primary" type="submit"><i class="icon-search">&nbsp;&nbsp;查询</i>
            </button>
        </li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;TABLE-LAYOUT:fixed">
    <thead>
    <tr>
        <th width="15%">组件名称</th>
        <th width="35%">生成类</th>
        <th width="25%">参数</th>
        <th width="17%">组件描述</th>
        <shiro:hasPermission name="component:ftComponent:edit">
            <th width="8%">操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftComponent">
        <tr>
            <td style="WORD-WRAP:break-word"><a onclick="edit('form_${ftComponent.id}')">
                    ${ftComponent.name}
            </a></td>
            <td style="WORD-WRAP:break-word">
                    ${ftComponent.implement}
            </td>
            <td style="WORD-WRAP:break-word">
                    ${ftComponent.param}
            </td>
            <td style="WORD-WRAP:break-word">
                    ${ftComponent.des}
            </td>
            <shiro:hasPermission name="component:ftComponent:edit">
                <td style="WORD-WRAP:break-word">
                    <form id="form_${ftComponent.id}" method="post">
                        <input name="id" value="${ftComponent.id}" type="hidden">
                        <input name="name" value="${ftComponent.name}" type="hidden">
                        <input name="des" value="${ftComponent.des}" type="hidden">
                        <input name="implement" value="${ftComponent.implement}" type="hidden">
                        <input name="param" value='${ftComponent.param}' type="hidden">
                        <a href="javascript:void(0)" onclick="edit('form_${ftComponent.id}')">修改</a>
                            <%--<a href="javascript:void(0)" onclick="remove1('form_${ftComponent.id}')">删除</a>--%>
                        <a href="${ctx}/component/ftComponent/delete?id=${ftComponent.id}&name=${ftComponent.name}"
                           onclick="return confirmx('确认要删除该组件吗？', this.href)">删除</a>
                    </form>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>