<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>客户端状态</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function() {
            //console.log("${page.list}");
        });
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function del(id,removeId) {
            if(confirm("确认删除？")){
                //console.log("ID:"+id+"removeId:"+removeId);
                $.ajax({
                    url:"${ctx}/client/clientStatus/delClientStatus",
                    type:"post",
                    data:{'id':id},
                    dataType:"text",
                    success:function(result){
                        if(result=="true"){
                            alert("删除成功");
                            var removed = "#"+removeId;
                            //console.log("removeID:"+removed);
                            $(removed).remove();
                        }else{
                            alert('删除失败');
                        }
                    }

                });
            }

        }

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="#">客户端状态信息</a></li>
    <li><a href="${ctx}/client/clientStatus/newClientStatus" >添加</a></li>
</ul>
<form:form id="searchForm" modelAttribute="ftClientStatus" action="${ctx}/client/clientStatus/list" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>客户端ID：</label>
            <form:input path="id" htmlEscape="false" maxlength="256" class="input-medium"/>
        </li>
        <li><label>客户端名称：</label>
            <form:input path="name" htmlEscape="false" maxlength="256" class="input-medium" />
        </li>
        <li><label>类型：</label>
            <form:select path="type" class="input-medium" >
                <form:option value="" />
                <form:option value="enable"/>
                <form:option value="disable" />
            </form:select>
        </li>

        <li><label>模式：</label>
            <form:select path="mode" class="input-medium" >
                <form:option value="" />
                <form:option value="auto"/>
                <form:option value="manual" />
            </form:select>
        </li>
        <li><label>状态：</label>
            <form:select path="status" class="input-medium" >
                <form:option value="" />
                <form:option value="running"/>
                <form:option value="stopped" />
            </form:select>
        </li>
        <li class="btns" style="right:500px "><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>客户端ID</th>
        <th>客户端名称</th>
        <th>类型</th>
        <th>模式</th>
        <th>状态</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="clientStatus" varStatus="status">
        <tr id="${status.index}">
            <td>
                    ${clientStatus.id}
            </td>
            <td>
                    ${clientStatus.name}
            </td>
            <td>
                    ${clientStatus.type}
            </td>
            <td>
                    ${clientStatus.mode}
            </td>
            <td>
                    ${clientStatus.status}
            </td>
            <td>
               <%-- <shiro:hasPermission name="client:clientStatus:edit">--%>
                    <a href="${ctx}/client/clientStatus/editClientStatus?id=${clientStatus.id}" >修改</a>
                    <a href="javascript:del('${clientStatus.id}','${status.index}')">删除</a>
                <%--</shiro:hasPermission>--%>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>