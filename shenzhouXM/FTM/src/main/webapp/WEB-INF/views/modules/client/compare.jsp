<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>对比</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">

        legend{
            width: 70px;
            margin-left: 30px;
            border-bottom: 0
        }

        pre{
            border: 0;
            background-color: #fff;
        }

        fieldset{
            width: 45%;
            height:100%;
            position:absolute;
            border:2px solid ;
        }

        #oldfile{
            left:3%;
        }

        #newfile{
            right:3%;
        }

    </style>

</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/client/operate/view">返回上一页</a></li>
</ul>
<fieldset id="oldfile"><legend>修改前</legend>
    <div ><pre id="left"><c:out value="${before}" escapeXml="true"/></pre></div>
</fieldset>
<fieldset id="newfile"><legend>修改后</legend>
    <div ><pre id="right"><c:out value="${after}" escapeXml="true"/></pre></div>
</fieldset>




<script src="${ctxStatic}/compare/compareTxt.js" type="text/javascript"></script>
<script type="text/javascript">


    var dom1 = document.getElementById("left");
    var dom2 = document.getElementById("right");

    CompareTxt(dom1,dom2);

</script>
</body>
</html>
