<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="/WEB-INF/tlds/shiros.tld" %>
<style>
    form.marlf-new{margin-left: 50px}
</style>
<script>
    function __getFormURL() {
        var path=window.location.pathname;
        if(path.lastIndexOf('/list')==path.length-5)path=path.substr(0,path.length-5);
        else if(path.lastIndexOf('/')==path.length-1)path=path.substr(0,path.length-1);
        return path+'/addPage';
    }
</script>
<div style="float: left;background-color: #f5f5f5;border-radius:4px;padding: 15px 15px;margin: 0 0 20px">
	<shiro:hasPermission name="servicenode:ftServiceNode:edit">
		<a href="javascript:void(0)" onclick="window.location=__getFormURL();">新增</a>
	</shiro:hasPermission>
</div>