package com.dc.smarteam.cfgmodel;

import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.modules.client.entity.ClientSyn;
import com.dc.smarteam.modules.client.entity.FtClientStatus;
import com.dc.smarteam.modules.component.entity.FtComponent;
import com.dc.smarteam.modules.file.entity.FtFileRename;
import com.dc.smarteam.modules.fileclean.entity.FtFileClean;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.keys.entity.FtKey;
import com.dc.smarteam.modules.model.UserAuthModel;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.ScrtUtil;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sysinfo.entity.Vsysmap;
import com.dc.smarteam.modules.timingtask.entity.FtTimingTask;
import com.dc.smarteam.modules.user.entity.FtUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mocg on 2017/4/15.
 */
//逻辑实体<-->存储实体
public class CfgModelConverter {
    public static void convertTo(FtServiceNode ftServiceNode, NodesModel.Node modelNode) {
        modelNode.setName(ftServiceNode.getName());
        modelNode.setType(ftServiceNode.getType());
        modelNode.setIp(ftServiceNode.getIpAddress());
        modelNode.setCmdPort(ftServiceNode.getCmdPort());
        modelNode.setServPort(ftServiceNode.getFtpServPort());
        modelNode.setReceivePort(ftServiceNode.getReceivePort());
        modelNode.setManagePort(ftServiceNode.getFtpManagePort());
        modelNode.setState(ftServiceNode.getState());
        modelNode.setIsolState(ftServiceNode.getIsolState());
        modelNode.setModel(ftServiceNode.getNodeModel());

        if (StringUtils.isNoneEmpty(ftServiceNode.getSystemName())) {
            NodesModel.System system = new NodesModel.System();
            system.setName(ftServiceNode.getSystemName());
            system.setStoreModel(ftServiceNode.getStoreModel());
            system.setSwitchModel(ftServiceNode.getSwitchModel());
            modelNode.setSystem(system);
        }
    }

    public static void convertTo(NodesModel.Node modelNode, FtServiceNode ftServiceNode) {
        ftServiceNode.setName(modelNode.getName());
        ftServiceNode.setType(modelNode.getType());
        ftServiceNode.setIpAddress(modelNode.getIp());
        ftServiceNode.setState(modelNode.getState());
        ftServiceNode.setIsolState(modelNode.getIsolState());
        ftServiceNode.setCmdPort(modelNode.getCmdPort());
        ftServiceNode.setFtpServPort(modelNode.getServPort());
        ftServiceNode.setFtpManagePort(modelNode.getManagePort());
        ftServiceNode.setReceivePort(modelNode.getReceivePort());
        ftServiceNode.setNodeModel(modelNode.getModel());
        NodesModel.System nodeSystem = modelNode.getSystem();
        if (nodeSystem != null) {
            ftServiceNode.setSystemName(nodeSystem.getName());
            ftServiceNode.setSwitchModel(nodeSystem.getSwitchModel());
            ftServiceNode.setStoreModel(nodeSystem.getStoreModel());
        }
        String sysNodeModel = modelNode.getModel();
        if ("ms-m".equals(sysNodeModel) || "ms-s".equals(sysNodeModel)) sysNodeModel = "ms";
        ftServiceNode.setSysNodeModel(sysNodeModel);
        ftServiceNode.setNodeModel(modelNode.getModel());
        //更新状态
        FtServiceNodeHelper.updateStateByZK(ftServiceNode);
    }

    //ComponentModel
    public static void convertTo(ComponentModel.Service service, FtComponent ftComponent) {
        ftComponent.setName(service.getName());
        ftComponent.setImplement(service.getImplement());
        ftComponent.setDes(service.getDescribe());
        StringBuilder builder = new StringBuilder();
        for (ComponentModel.Param param : service.getParams()) {
            if (builder.length() > 0) builder.append(",");
            builder.append(param.getName()).append("=").append(param.getValue());
        }
        ftComponent.setParam(builder.toString());
        ftComponent.setRemarks(service.getComment());
    }

    public static void convertTo(FtComponent ftComponent, ComponentModel.Service service) {
        service.setName(ftComponent.getName());
        service.setImplement(ftComponent.getImplement());
        service.setDescribe(ftComponent.getDes());
        service.setComment(ftComponent.getRemarks());
        List<ComponentModel.Param> paramList = new ArrayList<>();
        String paramStr = ftComponent.getParam();
        if (StringUtils.isNoneEmpty(paramStr)) {
            String[] arr = paramStr.split(",");
            for (String s : arr) {
                String[] arr2 = s.split("=");
                ComponentModel.Param param = new ComponentModel.Param();
                param.setName(arr2[0]);
                param.setValue(arr2[1]);
                paramList.add(param);
            }
        }
        service.setParams(paramList);
    }

    //flow
    public static void convertTo(FtFlow ftFlow, FlowModel.Flow flow) {
        flow.setName(ftFlow.getName());
        //组件链接字符串  ftFlow.getComponents()
        if (ftFlow.getComponents() != null) flow.setComponents(ftFlow.getComponents());
        flow.setDescribe(ftFlow.getDes());
        flow.setSysname(ftFlow.getSystemName());
    }


    public static void convertTo(FlowModel.Flow flow, FtFlow ftFlow) {
        ftFlow.setName(flow.getName());
        ftFlow.setComponents(flow.getComponents());
        ftFlow.setDes(flow.getDescribe());
        ftFlow.setSystemName(flow.getSysname());
    }

    //serviceInfo
    public static void convertToWithoutAuth(FtServiceInfo ftServiceInfo, ServiceModel.Service service) {
        service.setSysname(ftServiceInfo.getSystemName());
        service.setTrancode(ftServiceInfo.getTrancode());
        service.setFlow(ftServiceInfo.getFlow());
        service.setPsflow(ftServiceInfo.getPsflow());
        service.setDescribe(ftServiceInfo.getDescribe());
        service.setRename(ftServiceInfo.getRename());
        service.setFilePeriod(ftServiceInfo.getFilePeriod());
        service.setPriority(ftServiceInfo.getPriority());
        service.setSize(ftServiceInfo.getSize());
        service.setCross(ftServiceInfo.getCross());



    /*ServiceModel.PutAuth putAuth = new ServiceModel.PutAuth();
        ftServiceInfo.getPutAuth();
        service.setPutAuth(putAuth);
        ServiceModel.GetAuth getAuth = new ServiceModel.GetAuth();
        ftServiceInfo.getGetAuth();
        service.setGetAuth(getAuth);*/

    }

    //serviceInfo
    public static void convertTo(ServiceModel.Service service, FtServiceInfo ftServiceInfo) {
        ftServiceInfo.setSystemName(service.getSysname());
        ftServiceInfo.setTrancode(service.getTrancode());
        ftServiceInfo.setFlow(service.getFlow());
        ftServiceInfo.setPsflow(service.getPsflow()); // 新增校验流程
        ftServiceInfo.setDescribe(service.getDescribe());
        ftServiceInfo.setRename(service.getRename());
        ftServiceInfo.setFilePeriod(service.getFilePeriod());
        ftServiceInfo.setPriority(service.getPriority());
        ftServiceInfo.setSize(service.getSize());
        ftServiceInfo.setCross(service.getCross());

        ServiceModel.PutAuth putAuth = service.getPutAuth();

        ftServiceInfo.setPutAuth(null);
        ServiceModel.GetAuth getAuth = service.getGetAuth();
        ftServiceInfo.setGetAuth(null);

    }


    public static void convertToWithoutAuth(ServiceModel.Service service, FtServiceInfo ftServiceInfo) {
        ftServiceInfo.setSystemName(service.getSysname());
        ftServiceInfo.setTrancode(service.getTrancode());
        ftServiceInfo.setFlow(service.getFlow());
        ftServiceInfo.setPsflow(service.getPsflow()); // 新增校验流程
        ftServiceInfo.setDescribe(service.getDescribe());
        ftServiceInfo.setRename(service.getRename());
        ftServiceInfo.setFilePeriod(service.getFilePeriod());
        ftServiceInfo.setPriority(service.getPriority());
        ftServiceInfo.setSize(service.getSize());
        ftServiceInfo.setCross(service.getCross());

        /*ServiceModel.PutAuth putAuth = service.getPutAuth();
        ftServiceInfo.setPutAuth(null);
        ServiceModel.GetAuth getAuth = service.getGetAuth();
        ftServiceInfo.setGetAuth(null);*/
    }

    //FtUser
    public static void convertTo(FtUser ftUser, UserModel.UserInfo userInfo) {
        UserModel.Uid uid = new UserModel.Uid();
        uid.setUid(ftUser.getName());
        uid.setHome(ftUser.getUserDir());
        uid.setDescribe(ftUser.getDes());
        userInfo.setUid(uid);
        String pwd = ftUser.getPwd();
        if (pwd != null) {
            if (!pwd.startsWith("${3DES}")) {
                pwd = "${3DES}" + ScrtUtil.encryptEsb(pwd);
            }
        }
        userInfo.setPasswd(pwd);
        userInfo.setClientAddress(ftUser.getClientAddress());
        //userInfo.setGrant(null);
        //userInfo.setIps(null);
    }

    public static void convertToreplace(FtUser ftUser, UserModel.UserInfo userInfo) {
        UserModel.Uid uid = new UserModel.Uid();
        uid.setUid(ftUser.getName());
        uid.setHome(ftUser.getUserDir());
        uid.setDescribe(ftUser.getDes());
        userInfo.setUid(uid);
        String pwd = ftUser.getPwd();
        if (pwd != null) {
            if (!pwd.startsWith("${3DES}")) {
                pwd = "${3DES}" + ScrtUtil.encryptEsb(pwd);
            }
        }
        userInfo.setPasswd(pwd.replaceAll("\r|\n", ""));
        userInfo.setClientAddress(ftUser.getClientAddress());
        //userInfo.setGrant(null);
        //userInfo.setIps(null);
    }

    public static void convertTo(UserModel.UserInfo userInfo, FtUser ftUser) {
        UserModel.Uid uid = userInfo.getUid();
        ftUser.setName(uid.getUid());
        ftUser.setPwd(userInfo.getPasswd());
        ftUser.setUserDir(uid.getHome());
        ftUser.setDes(uid.getDescribe());
        ftUser.setPermession(userInfo.getGrant() == null ? null : userInfo.getGrant().getType());
        ftUser.setClientAddress(userInfo.getClientAddress());
    }

    //FtUserIp
    public static void convertTo(FtUserIp ftUserIp, UserModel.IP ip) {
        ip.setIp(ftUserIp.getIpAddress());
        String state = ftUserIp.getState();
        if ("forbidden".equalsIgnoreCase(state)) state = "0";
        else if ("allowed".equalsIgnoreCase(state)) state = "1";
        ip.setStatus(state);
        ip.setDescribe(ftUserIp.getDes());
    }

    public static void convertTo(SysProtocol sysProtocol, SystemModel.System system) {
        system.setName(sysProtocol.getName());
        system.setProtocol(sysProtocol.getProtocol());
        system.setIp(sysProtocol.getIp());
        system.setPort(sysProtocol.getPort());
        system.setUsername(sysProtocol.getUsername());
        system.setPassword(sysProtocol.getPassword());
        system.setUploadPath(sysProtocol.getUploadPath());
        system.setDownloadPath(sysProtocol.getDownloadPath());
    }

    public static void convertTo(SystemModel.System system, SysProtocol sysProtocol) {
        sysProtocol.setName(system.getName());
        sysProtocol.setProtocol(system.getProtocol());
        sysProtocol.setIp(system.getIp());
        sysProtocol.setPort(system.getPort());
        sysProtocol.setUsername(system.getUsername());
        sysProtocol.setPassword(system.getPassword());
        sysProtocol.setUploadPath(system.getUploadPath());
        sysProtocol.setDownloadPath(system.getDownloadPath());
    }

    public static void convertTo(FtRoute ftRoute, RouteModel.Route route) {
        route.setDestination(ftRoute.getDestination());
        route.setMode(ftRoute.getMode());
        route.setTranCode(ftRoute.getTran_code());
        route.setType(ftRoute.getType());
        route.setUser(ftRoute.getUser());
    }

    public static void convertTo(RouteModel.Route route, FtRoute ftRoute) {
        ftRoute.setDestination(route.getDestination());
        ftRoute.setMode(route.getMode());
        ftRoute.setTran_code(route.getTranCode());
        ftRoute.setType(route.getType());
        ftRoute.setUser(route.getUser());
    }

    public static void convertTo(FtTimingTask ftTimingTask, CrontabModel.Task task) {
        task.setCount(ftTimingTask.getCount());
        task.setDescription(ftTimingTask.getDescription());
        task.setMission(ftTimingTask.getFlowId());
        task.setNodeName(ftTimingTask.getNodeNameTemp());
        task.setParams(ftTimingTask.getParams());
        task.setStatus(ftTimingTask.getState());
        task.setTaskName(ftTimingTask.getTaskName());
        task.setTimeExp(ftTimingTask.getTimeExp());
        task.setId(ftTimingTask.getId());
    }

    public static void convertTo(CrontabModel.Task task, FtTimingTask ftTimingTask) {
        ftTimingTask.setCount(task.getCount());
        ftTimingTask.setDescription(task.getDescription());
        ftTimingTask.setFlowId(task.getMission());
        ftTimingTask.setNodeNameTemp(task.getNodeName());
        ftTimingTask.setParams(task.getParams());
        ftTimingTask.setState(task.getStatus());
        ftTimingTask.setTaskName(task.getTaskName());
        ftTimingTask.setTimeExp(task.getTimeExp());
        ftTimingTask.setId(task.getId());
        ftTimingTask.setSeq(task.getId());
    }

    public static void convertTo(Vsysmap vsysmap, VsysmapModel.Map map) {
        map.setSource(vsysmap.getKey());
        map.setDest(vsysmap.getVal());
    }

    public static void convertTo(VsysmapModel.Map map, Vsysmap vsysmap) {
        vsysmap.setKey(map.getSource());
        vsysmap.setVal(map.getDest());
    }

    public static void convertTo(FtFileClean ftFileClean, FileCleanModel.FileClean fileClean) {
        fileClean.setId(ftFileClean.getId());
        fileClean.setKeepTime(ftFileClean.getKeepTime());
        fileClean.setIsBackup(ftFileClean.getIsBackup());
        fileClean.setBackupPath(ftFileClean.getBackupPath());
        fileClean.setState(ftFileClean.getState());
        if (ftFileClean.getSystem() != null) fileClean.setSystem(ftFileClean.getSystem());
        fileClean.setSrcPath(ftFileClean.getTargetDir());
        fileClean.setDesc(ftFileClean.getRemarks());
    }

    public static void convertTo(FileCleanModel.FileClean fileClean, FtFileClean ftFileClean) {
        ftFileClean.setId(fileClean.getId());
        ftFileClean.setKeepTime(fileClean.getKeepTime());
        ftFileClean.setIsBackup(fileClean.getIsBackup());
        ftFileClean.setBackupPath(fileClean.getBackupPath());
        ftFileClean.setState(fileClean.getState());
        ftFileClean.setSystem(fileClean.getSystem());
        ftFileClean.setTargetDir(fileClean.getSrcPath());
        ftFileClean.setRemarks(fileClean.getDesc());
    }

    public static void convertTo(FtFileRename ftFileRename, FileRenameModel.FileRename fileRename) {
        fileRename.setId(ftFileRename.getId());
        fileRename.setPath(ftFileRename.getPath());
        fileRename.setType(ftFileRename.getType());
        if (ftFileRename.getSysname() != null) fileRename.setSysname(ftFileRename.getSysname());
    }

    public static void convertTo(FileRenameModel.FileRename fileRename, FtFileRename ftFileRename) {
        ftFileRename.setId(fileRename.getId());
        ftFileRename.setPath(fileRename.getPath());
        ftFileRename.setType(fileRename.getType());
        ftFileRename.setSysname(fileRename.getSysname());
    }

    public static void convertTo(FileModel.BaseFile baseFile, UserAuthModel userAuthModel){
        userAuthModel.setPath(baseFile.getName());
        for(FileModel.Grant grant:baseFile.getGrants()){
            if (StringUtils.equals(grant.getUser(), userAuthModel.getUserName())) {
                userAuthModel.setUserName(grant.getUser());
                userAuthModel.setAuth(grant.getType());
                break;
            }
        }
    }

    public static void convertTo(UserAuthModel userAuthModel, FileModel.BaseFile baseFile){
        List<FileModel.Grant> list = new ArrayList<>();
        baseFile.setName(userAuthModel.getPath());
        baseFile.setPath(userAuthModel.getPath());
        FileModel.Grant grant = new FileModel.Grant();
        grant.setUser(userAuthModel.getUserName());
        grant.setType(userAuthModel.getAuth());
        list.add(grant);
        baseFile.setGrants(list);
    }

    /* 客户端状态 */
    public static void convertTo(FtClientStatus ftClientStatus, ClientStatusModel.Client client){
        client.setName(ftClientStatus.getName());
        client.setId(ftClientStatus.getId());
        client.setMode(ftClientStatus.getMode());
        client.setStatus(ftClientStatus.getStatus());
        client.setType(ftClientStatus.getType());
    }
    /* 客户端状态 */
    public static void convertTo(ClientStatusModel.Client client, FtClientStatus ftClientStatus){
        ftClientStatus.setName(client.getName());
        ftClientStatus.setType(client.getType());
        ftClientStatus.setStatus(client.getStatus());
        ftClientStatus.setId(client.getId());
        ftClientStatus.setMode(client.getMode());
    }

    /**gaona 公私钥管理*/
    public static void convertTo(FtKey ftKey,KeysModel.Key key ) {
        ftKey.setUser(key.getUser());
        ftKey.setType(key.getType());
        ftKey.setContent(key.getContent());
    }

    /**gaona 公私钥管理*/
    public static void convertTo(KeysModel.Key key,FtKey ftKey ) {
        key.setUser(ftKey.getUser());
        key.setType(ftKey.getType());
        key.setContent(ftKey.getContent().replaceAll("\r|\n", ""));
    }


}
