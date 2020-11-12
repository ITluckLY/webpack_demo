<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>基础配置-节点管理-配置同步   -节点配置同步</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<form:form method="post"
           class="breadcrumb form-search marfl-new">
    <ul class="ul-form">
        <li class="clearfix"></li>
    </ul>
</form:form>
<table style="border-collapse:separate; border-spacing:10px 20px;">
    <shiro:hasPermission name="cfg:cfgsync:edit">
    <tr>
        <td colspan="6"><a class="btn" onclick="synConfAll()" id="All">同步所有文件</a></td>
    </tr>
    </shiro:hasPermission>
    <tr>
        <td><a class="btn" id="nodes" href="${ctx}/cfgsync/confComp?fileName=nodes.xml">节点列表配置同步</a></td>
        <td><a class="btn" id="user" href="${ctx}/cfgsync/confComp?fileName=user.xml">用户配置同步</a></td>
        <td><a class="btn" id="file" href="${ctx}/cfgsync/confComp?fileName=file.xml">文件配置同步</a></td>
        <td><a class="btn" id="components" href="${ctx}/cfgsync/confComp?fileName=components.xml">组件配置同步</a></td>
        <td><a class="btn" id="fileRename" href="${ctx}/cfgsync/confComp?fileName=file_rename.xml">文件重命名配置同步</a></td>
        <td><a class="btn" id="fileClean" href="${ctx}/cfgsync/confComp?fileName=file_clean.xml">文件清理配置同步</a></td>
    </tr>
    <tr>
        <td><a class="btn" id="crontab" href="${ctx}/cfgsync/confComp?fileName=crontab.xml">定时任务配置同步</a></td>
        <td><a class="btn" id="services" href="${ctx}/cfgsync/confComp?fileName=services_info.xml">服务配置同步</a></td>
        <td><a class="btn" id="flow" href="${ctx}/cfgsync/confComp?fileName=flow.xml">流程配置同步</a></td>
        <td><a class="btn" id="route" href="${ctx}/cfgsync/confComp?fileName=route.xml">路由配置同步</a></td>
        <td><a class="btn" id="system" href="${ctx}/cfgsync/confComp?fileName=system.xml">路由目标配置同步</a></td>
        <td><a class="btn" id="vsysmap" href="${ctx}/cfgsync/confComp?fileName=vsysmap.xml">系统映射配置同步</a></td>
    </tr>
    <tr>
        <td><a class="btn" id="clientstatus" href="${ctx}/cfgsync/confComp?fileName=client_status.xml">客户端状态配置同步</a></td>
        <td><a class="btn" id="keys" href="${ctx}/cfgsync/confComp?fileName=keys.xml">证书同步</a></td>
    </tr>

</table>
<script type="text/javascript">
    function synConfAll() {
        if (confirm('确定同步所有文件?')) {
            $.ajax({
                url: "${ctx}/cfgsync/syncAll",
                type: "post",
                dataType: "text",
                success: function (msg) {
                    if (msg == "true") {
                        alert("文件同步成功");
                    } else {
                        alert(msg);
                    }
                },
                error: function (msg) {
                    alert("文件同步出错");
                }
            });
        }
    }
</script>
</body>
</html>