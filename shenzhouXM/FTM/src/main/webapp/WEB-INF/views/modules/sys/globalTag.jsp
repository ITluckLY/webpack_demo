<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>标签管理</title>
    <meta name="decorator" content="default"/>
    <link href="${ctxStatic}/bootstrap/2.3.1/css_default/bootstrap.min.css" rel="stylesheet"/>
    <script href="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript" ></script>
    <script type="text/javascript">
        $(function(){

            $("#dictSel").change(function(){
                var dictId = $("#dictSel").val();
                $.ajax({
                    url:"${ctx}/sys/tagsetting/getTags",
                    type:"post",
                    dataType:"json",
                    data:{'dictId':dictId},
                    success:function(data){
                        var list = data.data;
                        console.log(list);
                        $("#tagSel").empty();
                        for(var i=0;i<list.length;i++){
                            console.log(list[i]);
                            var option = "<option value='"+list[i].id+"'>"+list[i].name+"</option>";
                            $("#tagSel").append(option);
                        }
                    }
                });
            });
        }) ;

        function del(id){
            if(!confirm("确定删除?")){
                return;
            }
            $.ajax({
                url:"${ctx}/sys/tagsetting/del",
                type:"post",
                dataType:"text",
                data:{'id':id},
                success:function(flag){
                    if(flag=="true"){
                        $("#"+id).remove();
                        alert("删除完成");
                    }
                }
            })
        }

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="#" >标签管理</a></li>
    <li><a href="${ctx}/sys/tagsetting/addTag">新增标签</a></li>
    <li><a href="${ctx}/sys/tagsetting/setGlobalTag">设置全局标签</a></li>
</ul>
<div>
    <form:form id="searchForm" modelAttribute="optTag" action="${ctx}/sys/tagsetting/list" method="post"
               class="breadcrumb form-search marfl-new">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <ul class="ul-form">
            <li><label>标签名：</label>
                <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
            </li>
            <li><label>所属字典：</label>
                <form:select path="dictId" class="input-xlarge" cssStyle="width: 200px">
                    <form:option value="" label="--请选择--" />
                    <form:options items="${dicts}" itemLabel="label" itemValue="id" htmlEscape="false" />
                </form:select>
            </li>
                <%--<li><label>状态：</label>--%>
                <%--<form:input path="status" htmlEscape="false" maxlength="100" class="input-medium"/>--%>
                <%--</li>--%>
            <li class="btns" style="right:500px "><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="clearfix"></li>

        </ul>
    </form:form>
</div>

<sys:message content="${message}"/>

<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>标签名称</th>
        <th>类别</th>
        <th>备注</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="tag">
        <tr id="${tag.id}">
            <td>
                    ${tag.name}
            </td>
            <td>
                    ${tag.dict.label}
            </td>
            <td>
                    ${tag.remark}
            </td>
            <td>
                    <%-- <shiro:hasPermission name="client:clientStatus:edit">--%>
                <a href="javascript:del('${tag.id}')">删除</a>
                    <%--</shiro:hasPermission>--%>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>

</html>