<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>标签</title>
    <meta name="decorator" content="default"/>
    <link href="${ctxStatic}/bootstrap/2.3.1/css_default/bootstrap.min.css" rel="stylesheet"/>
    <script href="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript" ></script>
    <script type="text/javascript">
        $(function () {
           $("#dictSel").change(function(){
               var dictId = $("#dictSel").val();
               $.ajax({
                   url:"${ctx}/sys/tagsetting/getTags",
                   type:"post",
                   dataType:"json",
                   data:{'dictId':dictId},
                   success:function(data){
                       var list = data.data;
                       $("#tagSel").empty();
                       for(var i=0;i<list.length;i++){
                           var option = "<option value='"+list[i].id+"'>"+list[i].name+"</option>";
                           $("#tagSel").append(option);
                       }
                   }
               });
           }) ;

            $("button").click(function () {
                var id = $("#tagSel").val();
                if(id===null||id===''||id===undefined){
                    alert("请选择类别和标签");
                    return;
                }
                $.ajax({
                    url:"${ctx}/sys/tagsetting/setGlobal",
                    type:"post",
                    data:{'id':id},
                    dataType:"json",
                    success:function(data){
                        if(data.flag=="true"){
                            $("#_tag span").text(data.val);
                        }
                    }

                });
            });

        });
    </script>
    <style>
        .content{
            padding-left:5%;
        }
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sys/tagsetting/list" >标签管理</a></li>
    <li><a href="${ctx}/sys/tagsetting/addTag">新增标签</a></li>
    <li class="active"><a href="#">设置全局标签</a></li>
</ul>
<div class="content breadcrumb">
    <p id="_tag" style="color: grey;">当前标签 :&nbsp; <span>${globalTag.name}</span></p>
        <div class="control-group">
            <label for="dictSel">类型</label>
            <select id="dictSel" style="width: 220px;">
                <option value="">--请选择--</option>
                <c:forEach items="${dicts}" var="dict">
                    <option value="${dict.id}">${dict.label}</option>
                </c:forEach>
            </select>
        </div>
        <div class="control-group">
            <label for="tagSel">标签</label>
            <select id="tagSel" style="width: 220px;">
                <option value="">--请选择--</option>
            </select>
        </div>
        <br>
    <button class="btn btn-primary">确认</button>&nbsp;&nbsp;
    <input id="btnCancel" class="btn " type="button" value="返 回" onclick="history.go(-1)"/>
</div>
</body>

</html>