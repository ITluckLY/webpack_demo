package com.dc.smarteam.common.msggenerator;

import com.dc.smarteam.common.json.SendEntity;
import com.dc.smarteam.modules.auth.entity.FtAuth;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.client.entity.FtClientStatus;
import com.dc.smarteam.modules.client.entity.FtsApiParam;
import com.dc.smarteam.modules.component.entity.FtComponent;
import com.dc.smarteam.modules.file.entity.FtFile;
import com.dc.smarteam.modules.file.entity.FtFileRename;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import com.dc.smarteam.modules.protocol.entity.FTPUser;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sysinfo.entity.FtSysToBack;
import com.dc.smarteam.modules.sysinfo.entity.Vsysmap;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;
import com.dc.smarteam.modules.user.entity.FtUser;
import net.sf.json.JSONObject;


/**
 * Created by Administrator on 2016/3/5.
 */
public class MessageFactory {
    private  static String FTSAPICFG = "ftsApiCfg";
    private static String NODE = "node";
    private static String NODES = "nodes";
    private static String FILE = "file";
    private static String USER = "user";
    private static String AUTH = "auth";
    private static String IPCONFIG = "user";
    private static String COMPONENT = "component";
    private static String FLOW = "flow";
    private static String TASK = "crontab";
    private static String SERVICEINFO = "service";
    private static String SYSINFO = "sysInfo";
    private static String ROUTE = "route";
    private static String FILE_CLEAN = "fileClean";
    private static String FILE_RENAME = "fileRename";
    private static String RULE = "rule";
    private static String DATANODE = "datanode";
    private static String CLIENT = "client";
    private static String FTPSERVER = "ftpServer";
    private static String CLIENT_STATUS = "clientStatusInfo";
    private static MessageFactory instance;

    public static MessageFactory getInstance() {
        if (instance == null) {
            instance = new MessageFactory();
        }
        return instance;
    }

    //获取节点
    public String nodes(FtServiceNode ftServiceNode, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(NODES);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftServiceNode.getName());
        data.put("system", ftServiceNode.getSystemName());
        data.put("ip", ftServiceNode.getIpAddress());
        data.put("cmd_port", ftServiceNode.getCmdPort());
        data.put("ftp_serv_port", ftServiceNode.getFtpServPort());
        data.put("ftp_manage_port", ftServiceNode.getFtpManagePort());
        data.put("receive_port", ftServiceNode.getReceivePort());
        data.put("state", ftServiceNode.getState());
        data.put("isolState", ftServiceNode.getIsolState());
        data.put("type", ftServiceNode.getType());
        data.put("nodeModel", ftServiceNode.getNodeModel());
        data.put("sysNodeModel", ftServiceNode.getSysNodeModel());
        data.put("switchModel", ftServiceNode.getSwitchModel());
        data.put("storeModel", ftServiceNode.getStoreModel());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //获取节点
    public String nodesAddUpdate(FtServiceNode ftServiceNode, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(NODES);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftServiceNode.getName());
        data.put("system", ftServiceNode.getSystemName());
        data.put("ip", ftServiceNode.getIpAddress());
        data.put("cmd_port", ftServiceNode.getCmdPort());
        data.put("ftp_serv_port", ftServiceNode.getFtpServPort());
        data.put("ftp_manage_port", ftServiceNode.getFtpManagePort());
        data.put("receive_port", ftServiceNode.getReceivePort());
        data.put("state", ftServiceNode.getState());
        data.put("isolState", ftServiceNode.getIsolState());
        data.put("type", ftServiceNode.getType());

//        data.put("sysNodeModel",ftServiceNode.getSysNodeModel());
        data.put("nodeModel", ftServiceNode.getNodeModel());
        data.put("storeModel", ftServiceNode.getStoreModel());
        data.put("switchModel", ftServiceNode.getSwitchModel());

        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //获取节点
    public String nodesNoDataNodeUpdate(FtServiceNode ftServiceNode, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(NODES);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftServiceNode.getName());
        data.put("type", ftServiceNode.getType());
        data.put("ip", ftServiceNode.getIpAddress());
        data.put("cmd_port", ftServiceNode.getCmdPort());
        data.put("ftp_serv_port", ftServiceNode.getFtpServPort());
        data.put("ftp_manage_port", ftServiceNode.getFtpManagePort());
        data.put("receive_port", ftServiceNode.getReceivePort());
        data.put("state", ftServiceNode.getState());
        data.put("isolState", ftServiceNode.getIsolState());

        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }


    //节点参数
    public String nodeParam(FtNodeParam ftNodeParam, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(NODE);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("key", ftNodeParam.getName());
        data.put("describe", ftNodeParam.getDes());
        data.put("text", ftNodeParam.getValue());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //用户操作
    public String user(FtUser ftUser, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(USER);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("uid", ftUser.getName());
        data.put("home", ftUser.getUserDir());
        data.put("sysname", ftUser.getSystemName());
        data.put("describe", ftUser.getDes());
        data.put("passwd", ftUser.getPwd());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //ip控制
    public String userIp(FtUserIp ftUserIp, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(IPCONFIG);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("uid", ftUserIp.getFtUserId());
        data.put("IP", ftUserIp.getIpAddress());
        data.put("IPDescribe", ftUserIp.getDes());
        data.put("status", ftUserIp.getState());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //权限控制
    public String auth(FtAuth ftAuth, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(AUTH);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftAuth.getName());
        data.put("path", ftAuth.getPath());
        data.put("permession", ftAuth.getPermession());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //文件管理
    public String file(FtFile ftFile, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FILE);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("path", ftFile.getParentPath());
        data.put("fileName", ftFile.getFileName());
//        data.put("rename", "");
        data.put("codeSet", "");
        data.put("newCodeSet", "");
        data.put("cryptogramType", "");
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }


    //组件操作
    public String component(FtComponent ftComponent, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(COMPONENT);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftComponent.getName());
        data.put("param", ftComponent.getParam());
        data.put("comment", ftComponent.getRemarks());
        data.put("describe", ftComponent.getDes());
        data.put("implement", ftComponent.getImplement());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //流程操作
    public String flow(FtFlow ftFlow, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FLOW);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftFlow.getName());
        data.put("components", ftFlow.getComponents());
        data.put("describe", ftFlow.getDes());
        data.put("sysName", ftFlow.getSystemName());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //定时任务
    public String timingTask(FtTimingTask ftTimingTask, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(TASK);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("ID", ftTimingTask.getSeq());
        data.put("nodeName", ftTimingTask.getNodeNameTemp());
        data.put("description", ftTimingTask.getDescription());
        data.put("status", ftTimingTask.getState());
        data.put("timeExp", ftTimingTask.getTimeExp());
        data.put("params", ftTimingTask.getParams());
        data.put("mission", ftTimingTask.getFlowId());
        data.put("count", ftTimingTask.getCount());
        data.put("taskName", ftTimingTask.getTaskName());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    // 服务信息管理
    public String serviceInfo(FtServiceInfo ftServiceInfo, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(SERVICEINFO);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("trancode", ftServiceInfo.getTrancode());
        data.put("flow", ftServiceInfo.getFlow());
        data.put("describe", ftServiceInfo.getDescribe());
        data.put("sysName", ftServiceInfo.getSystemName());
        data.put("rename", ftServiceInfo.getRename());
        data.put("filePeriod", ftServiceInfo.getFilePeriod());
        data.put("putAuth", ftServiceInfo.getPutAuth());
        data.put("getAuth", ftServiceInfo.getGetAuth());
        data.put("priority", ftServiceInfo.getPriority());
        data.put("size", ftServiceInfo.getSize());
        data.put("cross", ftServiceInfo.getCross());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }


    // 服务信息管理
    public String serviceInfoAddPut(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(SERVICEINFO);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("trancode", ftServiceInfo.getTrancode());
        data.put("flow", ftServiceInfo.getFlow());
        data.put("describe", ftServiceInfo.getDescribe());
        data.put("sysName", ftServiceInfo.getSystemName());
        data.put("rename", ftServiceInfo.getRename());
        data.put("filePeriod", ftServiceInfo.getFilePeriod());
        data.put("uname", putAuthEntity.getUserName());
        data.put("directoy", putAuthEntity.getDirectoy());
        data.put("cross", ftServiceInfo.getCross());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    // 服务信息管理
    public String serviceInfoAddGet(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(SERVICEINFO);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("trancode", ftServiceInfo.getTrancode());
        data.put("flow", ftServiceInfo.getFlow());
        data.put("describe", ftServiceInfo.getDescribe());
        data.put("sysName", ftServiceInfo.getSystemName());
        data.put("rename", ftServiceInfo.getRename());
        data.put("filePeriod", ftServiceInfo.getFilePeriod());
        data.put("uname", getAuthEntity.getUserName());
        data.put("cross", ftServiceInfo.getCross());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    // 系统信息
   /* public String sysInfo(FtSysInfo ftSysInfo, String optType){
        SendEntity  entity = new SendEntity();
        entity.setTarget(SYSINFO);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftSysInfo.getName());
        data.put("username", ftSysInfo.getAdmin());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }*/

    // 路由管理
    public String route(FtRoute ftRoute, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(ROUTE);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("user", ftRoute.getUser());
        data.put("tran_code", ftRoute.getTran_code());
        data.put("type", ftRoute.getType());
        data.put("mode", ftRoute.getMode());
        data.put("destination", ftRoute.getDestination());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }


    //文件清理
    public String fileClean(FtFileClean ftFileClean, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FILE_CLEAN);
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("id", ftFileClean.getId());
        data.put("srcPath", ftFileClean.getTargetDir());
        data.put("keepTime", ftFileClean.getKeepTime());
        data.put("isBackup", ftFileClean.getIsBackup());
        data.put("backupPath", ftFileClean.getBackupPath());
        data.put("desc", ftFileClean.getRemarks());
        data.put("state", ftFileClean.getState());
        data.put("system", ftFileClean.getSystem());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //同步配置
    public String cfgSync(String fileName, String optType, String sysName) {
        SendEntity entity = new SendEntity();
        entity.setTarget("cfgSync");
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("sysName", sysName);
        data.put("cfgNames", fileName);
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }


    //文件重命名
    public String fileRename(FtFileRename ftFileRename, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FILE_RENAME);
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("id", ftFileRename.getId());
        data.put("type", ftFileRename.getType());
        data.put("path", ftFileRename.getPath());
        data.put("sysName", ftFileRename.getSysname());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //文件重命名
    public String sysToBack(FtSysToBack ftSysToBack, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(SYSINFO);
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("name", ftSysToBack.getName());
        data.put("protocol", ftSysToBack.getProtocol());
        data.put("ip", ftSysToBack.getIp());
        data.put("port", ftSysToBack.getPort());
        data.put("username", ftSysToBack.getUsername());
        data.put("password", ftSysToBack.getPassword());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //系统
    public String system(SysProtocol sysProtocol, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(SYSINFO);
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("name", sysProtocol.getName());
        data.put("protocol", sysProtocol.getProtocol());
        data.put("ip", sysProtocol.getIp());
        data.put("port", sysProtocol.getPort());
        data.put("username", sysProtocol.getUsername());
        data.put("password", sysProtocol.getPassword());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //系统名称映射
    public String vsysmap(Vsysmap vsysmap, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget("vsysmap");
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("key", vsysmap.getKey());
        data.put("val", vsysmap.getVal());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }
    //规则
//    public String rule(Rule rule, String optType){
//        SendEntity  entity = new SendEntity();
//        entity.setTarget(RULE);
//        entity.setOperateType(optType);
//        JSONObject data = new JSONObject();
//        data.put("name", rule.getName());
//        data.put("onput", rule.getOnPut());
//        data.put("class", rule.getClassName());
//        entity.setData(data);
//        JSONObject jsonObject = JSONObject.fromObject(entity);
//        return jsonObject.toString();
//    }

    //监控端操作dataNode回滚
    public String dataNodeRollback(String bakFileName, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(DATANODE);
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("bakFileName", bakFileName);
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //监控端实时查询硬件消息，启停datanode
    public String dataNode(String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(DATANODE);
        entity.setOperateType(optType);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }
    public String client(String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(CLIENT);
        entity.setOperateType(optType);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    //FTP操作
    public String ftpServer(FTPUser ftpUser, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FTPSERVER);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("name", ftpUser.getName());
        data.put("password", ftpUser.getPassword());
        data.put("maxIdleTimeSec", ftpUser.getMaxIdleTimeSec());
        data.put("homeDir", ftpUser.getHomeDir());
        data.put("isEnabled", ftpUser.isEnabled());
        data.put("isWriteable", ftpUser.isWriteable());
        data.put("maxDownloadRate", ftpUser.getMaxDownloadRate());
        data.put("maxUploadRate", ftpUser.getMaxUploadRate());
        data.put("maxConcurrentLogins", ftpUser.getMaxConcurrentLogins());
        data.put("maxConcurrentLoginsPerIP", ftpUser.getMaxConcurrentLoginsPerIP());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    public String ftsApiCfg(FtsApiParam ftsApiParam, String optType) {
        SendEntity entity = new SendEntity();
        entity.setTarget(FTSAPICFG);
        entity.setOperateType(optType);

        JSONObject data = new JSONObject();
        data.put("uid", ftsApiParam.getUid());
        data.put("sysname", ftsApiParam.getSystemName());
        data.put("passwd", ftsApiParam.getPasswd());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

    public String clientStatus(FtClientStatus ftClientStatus, String optType){
        SendEntity entity = new SendEntity();
        entity.setTarget(CLIENT_STATUS);
        entity.setOperateType(optType);
        JSONObject data = new JSONObject();
        data.put("id",ftClientStatus.getId());
        data.put("name",ftClientStatus.getName());
        data.put("mode",ftClientStatus.getMode());
        data.put("status",ftClientStatus.getStatus());
        data.put("type",ftClientStatus.getType());
        entity.setData(data);
        JSONObject jsonObject = JSONObject.fromObject(entity);
        return jsonObject.toString();
    }

}
