<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
  <title>流程管理</title>
  <meta name="decorator" content="default"/>
  <script type="text/javascript">
      function display() {
          var name = $("#query").val();
          var $trs = $('#display').find('tr');
          $trs.hide();
          $trs.each(function () {
              var $tr = $(this);
              var text = $tr.find("td:eq(1)").text();
              if (text.indexOf(name) >= 0) $tr.show();
          });
      }

      function GetQueryString(name) {
          var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
          var r = window.location.search.substr(1).match(reg);

          if (r != null) {
              return unescape(r[2]);
          }
          return null;
      }

      function arrContains(arr, ele) {
          for (var i = 0; i < arr.length; i++) {
              if (ele === arr[i])
                  return true;
          }
          return false;
      }

      $(function () {
          $("#inputForm").validate({
              submitHandler: function (form) {
                  loading('正在提交，请稍等...');
                  var comps = "";
                  $('#display3').find('tr').each(function () {
                      var text = $(this).find("td:eq(1)").text();
                      if (comps.length > 0) comps += ",";
                      comps += $.trim(text);
                  });
                  $('#componentsV1').val(comps);
                  form.submit();
              },
              errorContainer: "#messageBox",
              errorPlacement: function (error, element) {
                  $("#messageBox").text("输入有误，请先更正。");
                  if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                      error.appendTo(element.parent().parent());
                  } else {
                      error.insertAfter(element);
                  }
              }
          });
          display();

          $("#addComponent").click(function () {
              var alreadyList = [];
              var $display3 = $("#display3");
              $display3.find("tr").each(function () {
                  alreadyList.push($.trim($(this).find('td').eq(1).text()));
              });

              var $checkedBoxs = $('#allFlow').find(':checkbox:checked');
              $checkedBoxs.each(function () {
                  var text = $(this).parents("td:first").next().text();
                  text = $.trim(text);
                  if (!arrContains(alreadyList, text)) {
                      $display3.append("<tr id='rightTr'><td id='rightTd'><input type='checkbox'/></td><td>" + text + "</td></tr>");
                  }
              });
          });

          $("#removeComponent").click(function () {
              var $checkedBoxs = $('#display3').find(':checkbox:checked');
              $checkedBoxs.each(function () {
                  $(this).parents("tr:first").remove();
              });
          });

          $("#upComponent").click(function () {
              var $checkedBoxs = $('#display3').find(':checkbox:checked');
              $checkedBoxs.each(function () {
                  var $tr = $(this).parents("tr:first");
                  var prevTR = $tr.prev();
                  if (prevTR.length > 0) {
                      prevTR.insertAfter($tr);
                  }
              });
          });

          $("#downComponent").click(function () {
              var $checkedBoxs = $('#display3').find(':checkbox:checked');
              $checkedBoxs.each(function () {
                  var $tr = $(this).parents("tr:first");
                  var nextTR = $tr.next();
                  if (nextTR.length > 0) {
                      nextTR.insertBefore($tr);
                  }
              });
          });
      });
  </script>
</head>
<body>

<br/>
<br/>

<div style="width:100%;">
  <div style="float: left; width:45%">
    <div style="padding-top: 2px">
      可选组件列表：<br/>
    </div>
    <div style="padding-top: 2px">
      <input id="query" htmlEscape="false" maxlength="256" class="input-medium"/>&nbsp;&nbsp;
      <button class="btn btn-primary" onclick="display()"><i class="icon-search">&nbsp;&nbsp;查询</i>
      </button>
    </div>
    <div style="padding-top: 5px">
      <table id="allFlow" class="table table-striped table-bordered table-condensed"
             style="width:90%;TABLE-LAYOUT:fixed">
        <thead>
        <tr>
          <th width="5%"></th>
          <th width="40%">组件名称</th>
          <th width="55%">组件描述</th>
        </tr>
        </thead>
        <tbody id="display">
        <c:forEach items="${ftComponentList}" var="ftComponentTemp">
          <tr>
            <td>
              <input type="checkbox"/>
            </td>
            <td>
                ${ftComponentTemp.name}
            </td>
            <td style="WORD-WRAP:break-word">
                ${ftComponentTemp.des}
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="pagination"></div>
  </div>
  <div style="float: left; width:8%; padding-top: 20px">
    <br/>
    <br/>
    &nbsp;&nbsp;&nbsp;<input id="addComponent" class="btn btn-primary" type="button" value="增加>>"/><br/><br/>
    &nbsp;&nbsp;&nbsp;<input id="removeComponent" class="btn btn-primary" type="button" value="删除<<"/><br/><br/>
    &nbsp;&nbsp;&nbsp;<input id="upComponent" class="btn btn-primary" type="button" value="上移↑"/><br/><br/>
    &nbsp;&nbsp;&nbsp;<input id="downComponent" class="btn btn-primary" type="button" value="下移↓"/><br/>
  </div>

  <div style="float: left; width:45%">
    <br/>
    <div style="padding-top: 15px">
      已选组件列表：<br/>
    </div>
    <form:form id="inputForm" modelAttribute="ftFlow" action="${ctx}/flow/ftFlow/save" method="post"
               class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="systemName"/>
    <form:hidden path="name" id="nameV1"/>
    <form:hidden path="des" id="desV1"/>
    <form:hidden path="components" id="componentsV1"/>
      <%--  以下是获取右边数据的值 --%>
    <div style="padding-top: 5px">
      <table id="alreadyFlow" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
          <th width="25%"></th>
          <th width="75%">组件名称</th>
        </tr>
        </thead>
        <tbody id="display3">
        <c:forEach items='${ftFlow.components}' var="flowComponent">
          <tr id="rightTr">
            <td id="rightTd">
              <input type="checkbox">
            </td>
            <td>
                ${flowComponent}
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="pagination"></div>
  </div>
  <div style="clear: both"></div>
  <div class="form-actions">
    <shiro:hasPermission name="flow:ftFlow:edit">
      <%-- <c:if test="${ftFlow.systemName !='*'}">
           <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
       </c:if>--%>
      <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
    </shiro:hasPermission>
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
  </div>
  </form:form>
</body>
</html>