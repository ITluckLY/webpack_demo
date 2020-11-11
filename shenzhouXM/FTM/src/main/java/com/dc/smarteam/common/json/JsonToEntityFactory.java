package com.dc.smarteam.common.json;

import com.dc.smarteam.common.vo.ZTreeNode;
import com.dc.smarteam.modules.auth.entity.FtAuth;
import com.dc.smarteam.modules.client.entity.ClientFile;
import com.dc.smarteam.modules.component.entity.FtComponent;
import com.dc.smarteam.modules.file.entity.FtFile;
import com.dc.smarteam.modules.file.entity.FtFileRename;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.nodeparam.entity.FtNodeParam;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;
import com.dc.smarteam.modules.user.entity.FtUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/3/2.
 */
public class JsonToEntityFactory {
    private static final Logger log = LoggerFactory.getLogger(JsonToEntityFactory.class);
    private static JsonToEntityFactory instance;
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String IP = "ip";
    private static final String CMD_PORT = "cmd_port";
    private static final String FTP_SERV_PORT = "ftp_serv_port";
    private static final String STATE = "state";
    private static final String DESCRIBE = "describe";
    private static final String SYSNAME = "sysname";
    private static final String STATUS = "status";
    private static final String GRANT = "grant";
    private static final String FALSE = "false";
    private static final String PARAMS = "params";

    public static JsonToEntityFactory getInstance() {
        if (null == instance) {
            instance = new JsonToEntityFactory();
        }
        return instance;
    }

    //根据key获取json中字符串
    public String getString(JSONObject json, String key) {
        try {
            if (json.containsKey(key)) {
                return json.getString(key);
            }
        } catch (Exception e) {
            log.error("数据中不包含查询参数[" + key + "]", e);
        }
        return null;
    }

    //    根据json生成节点对象
    public FtServiceNode getNode(JSONObject json) {
        FtServiceNode ftServiceNode = new FtServiceNode();
        ftServiceNode.setName(getString(json, NAME));
        ftServiceNode.setType(getString(json, TYPE));
        ftServiceNode.setIpAddress(getString(json, IP));
        ftServiceNode.setCmdPort(getString(json, CMD_PORT));
        ftServiceNode.setFtpServPort(getString(json, FTP_SERV_PORT));
        ftServiceNode.setFtpManagePort(getString(json, "ftp_manage_port"));
        ftServiceNode.setReceivePort(getString(json, "receive_port"));
        ftServiceNode.setNodeModel(getString(json, "model"));
        ftServiceNode.setState(getString(json, STATE));
        ftServiceNode.setIsolState(getString(json, "isolState"));
        ftServiceNode.setSystemName(getString(json, "system"));
        return ftServiceNode;
    }

    //生成节点数组
    public List<FtServiceNode> getNodes(String message) {
        List<FtServiceNode> list = new ArrayList<FtServiceNode>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        for (int i = 0; i < jsonArray.size(); i++) {
            FtServiceNode ftServiceNode = getNode(jsonArray.getJSONObject(i));
            list.add(ftServiceNode);
        }
        return list;
    }


    //根据json生成节点参数
    public FtNodeParam getFtNodeParam(JSONObject json, FtServiceNode node) {
        FtNodeParam ftNodeParam = new FtNodeParam();
        if (json == null) {
            return ftNodeParam;
        }
        ftNodeParam.setId(UUID.randomUUID().toString());
        ftNodeParam.setNodeId(node.getId());
        ftNodeParam.setName(getString(json, "key"));
        ftNodeParam.setValue(getString(json, "text"));
        ftNodeParam.setDes(getString(json, DESCRIBE));
        return ftNodeParam;
    }

    //生成节点参数数组
    public List<FtNodeParam> getNodeParamList(String message, FtServiceNode node) {
        List<FtNodeParam> list = new ArrayList<FtNodeParam>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        for (int i = 0; i < jsonArray.size(); i++) {
            FtNodeParam param = getFtNodeParam(jsonArray.getJSONObject(i), node);
            list.add(param);
        }
        return list;
    }

    //根据json生成用户
    public FtUser getFtUser(JSONObject json) {
        FtUser entity = new FtUser();
        if (json == null) {
            return entity;
        }
        JSONObject userJson = json.getJSONObject("Uid");
        entity.setId(getString(userJson, "text"));
        entity.setName(getString(userJson, "text"));//用户名称
        entity.setSystemName(getString(userJson, SYSNAME));//系统名称
        entity.setDes(getString(userJson, DESCRIBE));//系统名称
        entity.setUserDir(getString(userJson, "home"));
        entity.setPwd(getString(json, "Passwd"));//用户密码
        JSONObject grant = json.getJSONObject("Grant");
        if (null != grant) {
            entity.setPermession(getString(grant, TYPE));
        }
        return entity;
    }

    //根据json生成ip配置
    public List<FtUserIp> getFtUserIp(JSONObject json) {
        List<FtUserIp> list = new ArrayList<FtUserIp>();
        if (json == null) {
            return list;
        }
        try {
            if (json.containsKey("IP")) {
                JSONArray ipJsonArray = new JSONArray();
                if (!(json.toString()).contains("[")) {
                    JSONObject ipJson = json.getJSONObject("IP");
                    ipJsonArray.add(ipJson);
                } else {
                    ipJsonArray = json.getJSONArray("IP");
                }
                for (int i = 0; i < ipJsonArray.size(); i++) {
                    FtUserIp entity = new FtUserIp();
                    FtUser ftUser = getFtUser(json);
                    entity.setFtUserId(ftUser.getId());//用户id
                    //20160718新增
                    entity.setSystemName(ftUser.getSystemName());

                    JSONObject ipJson = ipJsonArray.getJSONObject(i);
//                    entity.setSystemName(getString(ipJson,SYSNAME));//系统名称
                    entity.setIpAddress(getString(ipJson, "text"));//ip地址
                    entity.setState(getString(ipJson, STATUS));//状态
                    entity.setDes(getString(ipJson, "IPDescribe"));//描述
                    list.add(entity);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return list;
    }


    //生成用户数组
    public List<FtUser> getFtUserList(String message) {
        List<FtUser> list = new ArrayList<FtUser>();
        if (message == null) return list;
        if (StringUtils.isNotEmpty(message)) {
            JSONArray jsonArray = JSONArray.fromObject(message);
            for (int i = 0; i < jsonArray.size(); i++) {
                FtUser entity = getFtUser(jsonArray.getJSONObject(i));
                list.add(entity);
            }
        }
        return list;
    }

    //生成ip配置数组
    public List<FtUserIp> getFtUserIpList(String message) {
        List<FtUserIp> list = new ArrayList<FtUserIp>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        for (int i = 0; i < jsonArray.size(); i++) {
            List<FtUserIp> subList = getFtUserIp(jsonArray.getJSONObject(i));
            if (null != subList) {
                list.addAll(subList);
            }
        }
        return list;
    }

    //生成ip配置数组
    public List<SysProtocol> getSystemList(String message) {
        List<SysProtocol> list = new ArrayList<SysProtocol>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    SysProtocol entity = new SysProtocol();
                    entity.setName(getString(json, NAME));//名称
                    entity.setProtocol(getString(json, "protocol"));//名称
                    entity.setIp(getString(json, IP));//名称
                    entity.setPort(getString(json, "port"));//名称
                    entity.setUsername(getString(json, "username"));//名称
                    entity.setPassword(getString(json, "password"));//名称
                    list.add(entity);
                }
            }
        }
        return list;
    }

    //生成目录权限数组 yangxcc
    public List<FtAuth> getFtAuthList(String message) {

        List<FtAuth> list = new ArrayList<FtAuth>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtAuth entity = new FtAuth();
                    entity.setName(getString(json, NAME));//名称
                    entity.setPath(getString(json, "path"));//名称
                    entity.setPermession(getString(json, GRANT));//中文描述
                    list.add(entity);
                }
            }
        }
        return list;
    }

    //生成目录权限数组 yangxcc
    public List<FtAuth> getUserAuthList(String message) {

        List<FtAuth> list = new ArrayList<FtAuth>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtAuth entity = new FtAuth();
                    entity.setName(getString(json, NAME));
                    entity.setPath(getString(json, "path"));//名称
                    entity.setPermession(getString(json, GRANT));
                    list.add(entity);
                }
            }
        }
        return list;
    }


    //生成目录权限对象 yangxcc
    public FtAuth getFtAuth(JSONObject j) {

        FtAuth ftAuth = new FtAuth();
        if (j == null) {
            return ftAuth;
        }
        ftAuth.setName(getString(j, NAME));
        if (j.toString().contains("[")) {
            JSONArray arr = j.getJSONArray(GRANT);
            String str = "";
            String strPath = "";
            for (int i = 0; i < arr.size(); i++) {
                JSONObject itm = arr.getJSONObject(i);

                str = str + getString(itm, "user") + "=" + getString(itm, TYPE);
                strPath = strPath + getString(itm, SYSNAME);
                if (i < (arr.size() - 1)) {
                    str += ",";
                    strPath += ",";
                }
            }

            ftAuth.setPath(strPath);
            ftAuth.setPermession(str);
        } else {
            JSONObject o = j.getJSONObject(GRANT);
            String str = getString(o, "user") + "=" + getString(o, TYPE);
            String strPath = getString(o, SYSNAME);
            ftAuth.setPermession(str);
            ftAuth.setPath(strPath);
        }
        return ftAuth;
    }


    //生成服务信息管理对象数组 yangxcc
    public List<FtServiceInfo> getFtServiceInfoList(String message) {
        List<FtServiceInfo> list = new ArrayList<FtServiceInfo>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtServiceInfo entity = new FtServiceInfo();
                    entity.setTrancode(getString(json, "trancode"));//名称
                    entity.setFlow(getString(json, "flow"));//名称
                    entity.setDescribe(getString(json, DESCRIBE));//中文描述
                    entity.setSystemName(getString(json, SYSNAME));
                    entity.setRename(getString(json, "rename"));
                    entity.setFilePeriod(getString(json, "filePeriod"));
                    entity.setPutAuth(getString(json, "putAuth"));
                    entity.setGetAuth(getString(json, "getAuth"));
                    entity.setPriority(getString(json, "priority"));
                    entity.setSize(getString(json, "size"));
                    entity.setCross(getString(json, "cross"));
                    list.add(entity);
                }
            }
        }
        return list;
    }

    //生成服务信息管理对象 yangxcc
    public FtServiceInfo getFtserviceInfo(JSONObject j) {

        FtServiceInfo ftServiceInfo = new FtServiceInfo();
        if (j == null) {
            return ftServiceInfo;
        }
        ftServiceInfo.setTrancode(getString(j, "trancode"));
        ftServiceInfo.setFlow(getString(j, "flow"));
        ftServiceInfo.setDescribe(getString(j, DESCRIBE));
        ftServiceInfo.setSystemName(getString(j, SYSNAME));
        ftServiceInfo.setRename(getString(j, "rename"));
        ftServiceInfo.setFilePeriod(getString(j, "filePeriod"));
        ftServiceInfo.setPutAuth(getString(j, "putAuth"));
        ftServiceInfo.setGetAuth(getString(j, "getAuth"));
        ftServiceInfo.setPriority(getString(j, "priority"));
        ftServiceInfo.setSize(getString(j, "size"));
        ftServiceInfo.setCross(getString(j, "cross"));
        return ftServiceInfo;
    }

    public List<PutAuthEntity> getPutAuthList(String message) {
        List<PutAuthEntity> list = new ArrayList<PutAuthEntity>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    PutAuthEntity entity = new PutAuthEntity();
                    entity.setDirectoy(getString(json, "directoy"));//名称
                    entity.setUserName(getString(json, "text"));//中文描述
                    list.add(entity);
                }
            }
        }
        return list;
    }

    public List<GetAuthEntity> getGetAuthList(String message) {
        List<GetAuthEntity> list = new ArrayList<GetAuthEntity>();
        if (message == null) return list;
        String messageTemp = message.replaceAll(" |\\n|\\t|\\[|\\]|\"|\'", "");
        String[] split = messageTemp.split(",");
        for (String sp : split) {
            GetAuthEntity gae = new GetAuthEntity();
            gae.setUserName(sp);
            list.add(gae);
        }
        return list;
    }


    //生成路由管理对象数组 yangxcc
    public List<FtRoute> getFtRouteList(String message) {

        List<FtRoute> list = new ArrayList<FtRoute>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtRoute entity = new FtRoute();
                    entity.setUser(getString(json, "user"));//名称
                    entity.setTran_code(getString(json, "tran_code"));//名称
                    entity.setType(getString(json, TYPE));//中文描述
                    entity.setMode(getString(json, "mode"));//名称
                    entity.setDestination(getString(json, "destination"));//中文描述
                    list.add(entity);
                }
            }
        }
        return list;
    }

    //生成路由管理对象 yangxcc
    public FtRoute getFtRoute(JSONObject j) {

        FtRoute ftRoute = new FtRoute();
        ftRoute.setUser(getString(j, "user"));
        ftRoute.setTran_code(getString(j, "tran_code"));
        ftRoute.setType(getString(j, TYPE));
        ftRoute.setMode(getString(j, "mode"));
        ftRoute.setDestination(getString(j, "destination"));
        return ftRoute;
    }


    //根据返回json字符串生成文件树形
    public List<ZTreeNode> getFileTree(String message) {
        List<ZTreeNode> list = new ArrayList<ZTreeNode>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        String rootPath = "";
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (null != obj) {
                String path = getString(obj, "path");
                if (StringUtils.isNotEmpty(path)) {
                    ZTreeNode child = new ZTreeNode();
                    child.setId(rootPath + "/" + path);//以文件路径作为id
                    child.setName(path);
                    child.setPid("0");
                    child.setIsParent("true");
                    child.setOpen(FALSE);
                    list.add(child);
                }
            }
        }
        return list;
    }

    //根据返回json字符串生成文件树形
    public List<ZTreeNode> getFileTreeByTimestamp(String message) {
        List<ZTreeNode> list = new ArrayList<ZTreeNode>();
        if (message == null) {
            return list;
        }

        JSONArray jsonArr = JSONArray.fromObject(message);
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject jsonObject = jsonArr.getJSONObject(i);
            String path = jsonObject.getString("path");
            if (StringUtils.isNotEmpty(path)) {
                ZTreeNode child = new ZTreeNode();
                child.setId("/" + path);//以文件路径作为id
                child.setName(path);
                child.setPid("0");
                child.setIsParent("true");
                child.setOpen(FALSE);
                list.add(child);
            }
        }
        return list;
    }


    //查询目录下子目录
    public List<ZTreeNode> getSubDir(String message) {
        List<ZTreeNode> list = new ArrayList<ZTreeNode>();
        if (message == null) return list;
        JSONArray array = JSONArray.fromObject(message);
        if (null != array) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (null != json) {
                    ZTreeNode node = new ZTreeNode();
                    node.setId(getString(json, "id"));
                    node.setName(getString(json, NAME));
                    node.setPid(getString(json, "pid"));
                    node.setPath(getString(json, "path"));
                    node.setIsParent(getString(json, "isParent"));
                    node.setOpen(getString(json, "open"));
                    list.add(node);
                }
            }
        }
        return list;
    }

    //查询文件目录下子文件
    public List<FtFile> getContent(String message) {
        List<FtFile> list = new ArrayList<FtFile>();
        if (message == null) return list;
        JSONArray array = JSONArray.fromObject(message);
        if (null != array) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (null != json) {
                    FtFile ftFile = new FtFile();
                    ftFile.setId(getString(json, "id"));
                    ftFile.setFileName(getString(json, NAME));
                    ftFile.setParentPath(getString(json, "path"));
                    ftFile.setFileSize(getString(json, "size"));
                    ftFile.setSystemName(getString(json, "systemName"));
                    ftFile.setLastModifiedDate(getString(json, "lastModifiedDate"));
                    list.add(ftFile);
                }
            }
        }
        return list;
    }

    public List<ClientFile> getCliContent(String message) {
        List<ClientFile> list = new ArrayList<ClientFile>();
        if (message == null) return list;
        JSONArray array = JSONArray.fromObject(message);
        if (null != array) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (null != json) {
                    ClientFile ftFile = new ClientFile();
                    ftFile.setId(getString(json, "id"));
                    ftFile.setFileName(getString(json, NAME));
                    ftFile.setParentPath(getString(json, "path"));
                    ftFile.setFileSize(getString(json, "size"));
                    ftFile.setSystemName(getString(json, "systemName"));
                    ftFile.setLastModifiedDate(getString(json, "lastModifiedDate"));
                    list.add(ftFile);
                }
            }
        }
        return list;
    }

    //获取组件列表
    public List<FtComponent> getComponents(String message) {//NOSONAR
        List<FtComponent> list = new ArrayList<FtComponent>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtComponent ftComponent = new FtComponent();
                    ftComponent.setId(getString(json, NAME));//名称
                    ftComponent.setName(getString(json, NAME));//名称
                    ftComponent.setDes(getString(json, DESCRIBE));//中文描述
                    ftComponent.setImplement(getString(json, "implement"));//实现类
                    if ("[]".equals(ftComponent.getImplement())) {
                        ftComponent.setImplement("");
                    }
                    //参数
                    if (json.containsKey(PARAMS)) {
                        JSONArray paramArray = json.getJSONArray(PARAMS);
                        int size = paramArray.size();
                        StringBuilder param = new StringBuilder(50);
                        for (int j = 0; j < size; j++) {
                            JSONObject jo = paramArray.getJSONObject(j);
                            param.append(jo.get(NAME) + "=" + jo.get("text"));
                            if (j < size - 1) {
                                param.append(",");
                            }
                        }
                        ftComponent.setParam(param.toString());
                    }
                    ftComponent.setRemarks(getString(json, "comment"));//备注

                    list.add(ftComponent);
                }
            }
        }

        return list;
    }

    public List<ClientFile> getClientContent(String message) {
        List<ClientFile> list = new ArrayList<ClientFile>();
        if (message == null) return list;
        JSONArray array = JSONArray.fromObject(message);
        if (null != array) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (null != json) {
                    ClientFile ftFile = new ClientFile();
                    ftFile.setId(getString(json, "id"));
                    ftFile.setFileName(getString(json, NAME));
                    ftFile.setParentPath(getString(json, "path"));
                    ftFile.setFileSize(getString(json, "fileSize"));
                    ftFile.setSystemName(getString(json, "systemName"));
                    ftFile.setLastModifiedDate(getString(json, "lastModifiedDate"));
                    list.add(ftFile);
                }
            }
        }
        return list;
    }

    public List<String> getClientPath(String message) {
        List<String> list = new ArrayList<>();
        if (message == null) return list;
        JSONArray array = JSONArray.fromObject(message);
        if (null != array) {
            for (int i = 0; i < array.size(); i++) {
                    list.add(array.get(i).toString());
            }
        }
        return list;
    }

    //获取流程列表
    public List<FtFlow> getFlows(String message) {
        List<FtFlow> list = new ArrayList<FtFlow>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtFlow entity = new FtFlow();
                    entity.setId(getString(json, NAME));//名称
                    entity.setName(getString(json, NAME));//名称
                    entity.setDes(getString(json, DESCRIBE));//中文描述
                    entity.setSystemName(getString(json, "text"));//系统
                    entity.setComponents(getString(json, "components"));//流程调用组件
                    list.add(entity);
                }
            }
        }

        return list;
    }

    //获取任务列表
    public List<FtTimingTask> getTasks(String message) {

        List<FtTimingTask> list = new ArrayList<FtTimingTask>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            if (null != json) {
                FtTimingTask entity = new FtTimingTask();
                entity.setId(getString(json, "ID"));//名称
                entity.setSeq(getString(json, "ID"));
                entity.setCount(getString(json, "count"));
                entity.setNodeNameTemp(getString(json, "nodeName"));
                entity.setDescription(getString(json, "description"));
                entity.setState(getString(json, STATUS));
                entity.setTimeExp(getString(json, "timeExp"));
                entity.setParams(getString(json, PARAMS));
                entity.setFlowId(getString(json, "mission"));
                entity.setTaskName(getString(json, "taskName"));
                list.add(entity);
            }
        }
        return list;
    }

    //根据id获取任务
    public FtTimingTask getTaskById(String message) {
        FtTimingTask entity = new FtTimingTask();
        if (message == null) {
            return entity;
        }
        JSONObject json = JSONObject.fromObject(message);
        if (null != json) {
            entity.setId(getString(json, "ID"));//名称
            entity.setSeq(getString(json, "ID"));//名称
            entity.setState(getString(json, STATUS));//状态
            entity.setTimeExp(getString(json, "timeExp"));//表达式
            entity.setFlowId(getString(json, "mission"));
            entity.setParams(getString(json, PARAMS));
            entity.setDescription(getString(json, "description"));
            entity.setNodeNameTemp(getString(json, "nodeName"));
            entity.setCount(getString(json, "count"));
            entity.setTaskName(getString(json, "taskName"));
        }
        return entity;
    }


    //根据id获取文件清理任务
    public FtFileClean getFileClean(String message) {
        FtFileClean entity = new FtFileClean();
        if (message == null) {
            return entity;
        }
        JSONObject json = JSONObject.fromObject(message);
        if (null != json) {
            entity.setId(getString(json, "id"));//名称
            entity.setTargetDir(getString(json, "srcPath"));
            entity.setKeepTime(getString(json, "keepTime"));
            entity.setIsBackup(getString(json, "isBackup"));
            entity.setBackupPath(getString(json, "backupPath"));
            entity.setRemarks(getString(json, "desc"));
            entity.setState(getString(json, STATE));
        }
        return entity;
    }


    //获取文件清理列表
    public List<FtFileClean> getFileCleans(String message) {
        List<FtFileClean> list = new ArrayList<FtFileClean>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtFileClean entity = new FtFileClean();
                    entity.setId(getString(json, "id"));//名称
                    entity.setTargetDir(getString(json, "srcPath"));
                    entity.setKeepTime(getString(json, "keepTime"));
                    entity.setIsBackup(getString(json, "isBackup"));
                    entity.setBackupPath(getString(json, "backupPath"));
                    entity.setRemarks(getString(json, "desc"));
                    entity.setState(getString(json, STATE));
                    entity.setSystem(getString(json, "system"));
                    list.add(entity);
                }
            }
        }
        return list;
    }

    //获取文件重命名路径列表
    public List<FtFileRename> getFileRename(String message) {
        List<FtFileRename> list = new ArrayList<FtFileRename>();
        if (message == null) return list;
        JSONArray jsonArray = JSONArray.fromObject(message);
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (null != json) {
                    FtFileRename entity = new FtFileRename();
                    entity.setId(getString(json, "id"));
                    entity.setType(getString(json, TYPE));
                    entity.setPath(getString(json, "path"));
                    entity.setSysname(getString(json, SYSNAME));
                    list.add(entity);
                }
            }
        }
        return list;
    }


}
