<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>

<html>
<head>
    <title>修改记录</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript" src="${ctxStatic}/jQuery-ui/jQuery1.11.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jQuery-ui/jquery-ui.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-cookie/jquery.cookie.js"></script>
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

        function allCompare() {
             var chk_value =$.cookie("checkValue");
            var url = ctx + "/client/operate/allCompare?all=" + chk_value;
            openWin(url);
            resetValue();
        }

        function openWin(url) {
            window.open(url, "_self");
        }

        $().ready(function(e) {
            $("td.tag").each(function(i,obj){
                var text = $(obj).text();
                $(obj).html(text);
                $(obj).find("td:eq(0)").mouseenter(function () {
                    $(this).find("img").show();
                });
                $(obj).find("td:eq(0)").mouseleave(function () {
                    $(this).find("img").hide();
                });
                $(obj).find("td:eq(0)").find("img").click(function() {
                    $(obj).html("");
                    storage(obj,"del");
                });
                $(obj).find("td:eq(0)").attr("id","new")
            });

            $('td[id^="draggable"]').draggable({
                helper:"clone",
                cursor: "move"
            });
            //释放后
            $('td[id^="target"]').droppable({
                drop:function(event,ui){
                    $(this).children().remove();
                    var source = ui.draggable.clone();
                    $('<img/>', {
                        src: '${ctxStatic}/jQuery-ui/btn_delete.png',
                        style:'display:none',
                        click: function() {
                            storage(source[0].parentNode,"del");
                            source.remove();
                        }
                    }).appendTo(source);
                    source.mouseenter(function () {
                        $(this).find("img").show();
                    });
                    source.mouseleave(function () {
                        $(this).find("img").hide();
                    });
                    $(this).append(source);
                    storage(this,"add");
                }

            });
        });

        function storage(a,b){
            var id = a.attributes.name.value;
            var str = a.childNodes;
            var text = "";
            if( b == "add"){
                for(var i = 0; i < str.length;i++){
                    text = text + str[i].outerHTML;
                }
            }
            $.ajax({
                type:'post',
                url:'${ctx}/client/operate/storage',
                data:{"id":id,"tag":text},
                dataType:'json',
                success:function(data){
                },
                error:function(data){
                }
            });
        }

        $(function(){
            if($('#cleanFlag').val() == 0){
                resetValue();
                $('#cleanFlag').val("1")
            }
            //判断cookie是否存在，不存在则创建cookie，有效期1天
            if($.cookie("checkValue")==undefined) {
                $.cookie('checkValue','', { expires: 1 });
            } else {
                //勾选cookie中保留的项
                var checkedItem = $.cookie("checkValue").split(",");
                checkedItem.forEach(function(item,index) {
                    $("input[name='selbox']").each(function () {
                        if($(this).data("id")==item) {
                            $(this).prop('checked', true);
                        }
                    });
                });
            }

            /*复选框选中的值存入cookie*/
            $("input[name='selbox']").click(function(){
                /*先不管这个checkbx在不在cookie中，遍历数组去掉当前这个值*/
                var val = $(this).data("id");
                var array = $.cookie("checkValue").split(",");
                array.forEach(function(item,index){
                    if(val==item){
                        array.splice(index,1);
                    }
                });
                /*判断当前的checkbox是不是选中，选中的话在将这个值加进去*/
                if($(this).prop('checked')){
                    array.push($(this).data("id"));
                }
                /*最后把处理完的值存入cookie*/
                $.cookie("checkValue",array);
            });
        });
        
        function resetValue() {
            $.cookie("checkValue",null);
            $("input[name='selbox']").each(function () {
                $(this).prop('checked', false);
            });
            return true;
        }

    </script>
</head>
<body>
<form:form id="searchForm" modelAttribute="ftOperationLog" action="${ctx}/client/operate/view" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <label>操作文件：</label>
    <form:select id="cfgFileName" path="cfgFileName" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${fileList}" itemLabel="cfgFileName" itemValue="cfgFileName" htmlEscape="false"/>
    </form:select>
    <label>标签：</label>
    <form:select id="remark1" path="remark1" class="input-medium" style="width:150px">
        <form:option value="" label="——请选择——"/>
        <form:options items="${tagList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
    </form:select>
    <%--<table border = "2" style="float: right">--%>
        <%--<tr height="40">--%>
            <%--<td id="draggable1" width = "90" bgcolor="#adff2f">发版</td>--%>
            <%--<td id="draggable2" width = "90" bgcolor="#00bfff">配合</td>--%>
        <%--</tr>--%>
        <%--<tr height="40">--%>
            <%--<td id="draggable3" width = "90" bgcolor="red">消费方未上线</td>--%>
            <%--<td id="draggable4" width="90" bgcolor="#a9a9a9">下线</td>--%>
        <%--</tr>--%>
    <%--</table>--%>
    <br/><br/>
    <input type="hidden" name="cleanFlag" id="cleanFlag" value="${cleanFlag}">
    <label>开始时间：</label>
    <input class="Wdate" type="text" name="beginDate" id="beginDate" value="${beginDate}"> -
    <label>结束时间：</label>
    <input class="Wdate" type="text" name="endDate" id="endDate" value="${endDate}">
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="resetValue();"/>
</form:form>
<sys:message content="${message}"/>
<div style="margin-left: 20px">
    <a href="javascript:void(0)" onclick="allCompare()">批量对比</a>
    <a href="javascript:void(0)" style="margin-left: 5px;" onclick="resetValue()">取消选中</a>
</div>
<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 99%;table-layout: fixed;word-wrap:break-word;">
    <thead>
    <tr>
        <th width="25px">选择</th>
        <th>操作类型</th>
        <th width="25%">修改前</th>
        <th width="25%">修改后</th>
        <th>操作对象</th>
        <th>操作文件</th>
        <th>操作时间</th>
        <th>操作人</th>
        <th  width = "120">标签</th>
        <th>备注</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="ftOperationLog" varStatus="status">
        <tr>
            <td nowrap>
                    <input type="checkbox" class="click_checkbox" name="selbox" data-id="${ftOperationLog.id}"/>
            </td>
            <td>${ftOperationLog.paramUpdateType}</td>
            <td><c:out value="${ftOperationLog.beforeModifyValue}" escapeXml="true"/></td>
            <td><c:out value="${ftOperationLog.afterModifyValue}" escapeXml="true"/></td>
            <td>${ftOperationLog.paramName}</td>
            <td>${ftOperationLog.cfgFileName}</td>
            <td><fmt:formatDate value="${ftOperationLog.modifiedDate}" type="both"/></td>
            <td>${ftOperationLog.opeName}</td>
            <td id="target" name="${ftOperationLog.id}" class="tag">${ftOperationLog.remark1}</td>
            <td>${ftOperationLog.remarks}</td>
            <td><a href="${ctx}/client/operate/compare?id=${ftOperationLog.id}">对比</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>