<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<html>
<head>
    <title>对比</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript" src="${ctxStatic}/compare/codemirror.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/compare/searchcursor.js"></script>
    <script type="text/javascript" src="${ctxStatic}/compare/mergely.min.js"></script>
    <link rel="stylesheet" href="${ctxStatic}/compare/codemirror.css">
    <link rel="stylesheet" media="all" href="${ctxStatic}/compare/mergely.css" />
    <style>
        h2, ul {
            margin: .3em 0;
        }

        .container {
            display: flex;
            flex-direction: column;
            height: 100vh;
            margin: 0 .5em;
            width:100%;
        }

        .diffs {
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
        }
        .diffs header * {
            display: inline-block;
            vertical-align: middle;
        }
        .diffs .compare-wrapper {
            flex: 1 1 auto;
            position: relative;
        }
        .diffs .compare-wrapper #compare {
            position: absolute;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
        }

        /* Auto-height fix */
        .mergely-column .CodeMirror {
            height: 100%;
        }
    </style>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/client/operate/view?cleanFlag=1">返回上一页</a></li>
</ul>
<div class="container">

    <div class="diffs">
        <header>
            <h2>对比</h2>
        </header>
        <div class="compare-wrapper" id="compare-wrapper">
        </div>
    </div>
</div>
<script type="text/javascript">
    function escape2Html(str) {
        var arrEntities={'lt':'<','gt':'>','nbsp':' ','amp':'&','quot':'"'};
        return str.replace(/&(lt|gt|nbsp|amp|quot);/ig,function(all,t){return arrEntities[t];});
    }

    function changebr(str) {
        return str.replace(/<br\/>/ig,"\n");
    }

    <c:forEach items="${result}" step='2' var="bef" varStatus="status">
    $("#compare-wrapper").append('<div id="${status.index}"></div>');
    var comp = $("#${status.index}");
    comp.mergely({
        cmsettings: {
            readOnly: true,
            lineWrapping: true
        },
        wrap_lines: true,

        editor_width: 'calc(50% - 25px)',
        editor_height: '100%',

        lhs: function(setValue) {
            setValue(changebr(escape2Html('${bef}')));
        },
        rhs: function(setValue) {
            setValue(changebr(escape2Html('${result[status.index+1]}')));
        }
    });
    </c:forEach>
</script>
</body>
</html>
