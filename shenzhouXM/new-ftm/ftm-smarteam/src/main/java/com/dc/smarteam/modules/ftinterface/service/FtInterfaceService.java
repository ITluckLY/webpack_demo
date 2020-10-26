package com.dc.smarteam.modules.ftinterface.service;

import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.zk.CfgZkService;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.ftinterface.enity.FtFiles;
import com.dc.smarteam.modules.ftinterface.enity.FtInterface;
import com.dc.smarteam.modules.keys.entity.FtKey;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.*;
import com.dc.smarteam.service.impl.RouteServiceImpl;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import com.dc.smarteam.service.impl.SysServiceImpl;
import com.dc.smarteam.service.impl.UserServiceImpl;
import com.dc.smarteam.tool.ResultDtoTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2019/12/10.
 */
@Service
public class FtInterfaceService {
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;

    @Resource
    private CfgFileService cfgFileService;

    @Resource
    private CfgZkService cfgZkService;

    @Resource
    KeyService keyService;

    @Resource
    ServiceInfoServiceImpl serviceInfoService;

    @Resource
    SysServiceImpl sysService;

    @Resource
    FtGetFileService ftGetFileService;

    @Resource
    RouteServiceImpl routeService;

    private static final Logger log = LoggerFactory.getLogger(FtInterfaceService.class);

    private static final String ADD = "add";
    private static final String UPDATE = "update";
    private static final String DEL = "del";
    private static final String USER = "user.xml";
    private static final String SERVICE_INFO = "services_info.xml";
    private static final String KEY = "key.xml";
    private static final String SYSTEM = "system.xml";
    private static final String PUT = "put";
    private static final String GET = "get";
    private static final String ROUTE = "route.xml";

    public ResultDto<String> user(FtInterface ftInterface) throws IOException {
        ResultDto resul = null;
        String operateType = ftInterface.getOperateType();
        if (null == operateType) {
            log.error("操作类型为空");
            resul = ResultDtoTools.buildError("操作类型不能为空");
        }
        FtUser ftUser = new FtUser();
        ftUser.setUserDir("/" + ftUser.getName());
        ftUser.setPermession("A");
        ftUser.setClientAddress(ftInterface.getData().getClientAddress());
        ftUser.setDes(ftInterface.getData().getDes());
        ftUser.setName(ftInterface.getData().getName());
        ftUser.setPwd(ftInterface.getData().getPwd());
        ftUser.setSystemName(ftInterface.getData().getSystemName());
        ResultDto<String> resultDto = null;
        if (operateType.equalsIgnoreCase(ADD)) {
            resultDto = userService.adduser(ftUser);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
            resultDto = userService.updateuser(ftUser);
        } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = userService.del(ftUser);
        }
        log.info(String.valueOf(resultDto));
        String sysname = ftInterface.getData().getSystemName();
        String content = getCurrCfgContent(sysname, USER, true);
        cfgZkService.write(sysname, USER, content.trim());

        return resultDto;
    }

    public ResultDto<String> service(FtInterface ftInterface) throws IOException {
        ResultDto resul = null;
        String operateType = ftInterface.getOperateType();
        if (null == operateType) {
            log.error("操作类型为空");
            resul = ResultDtoTools.buildError("操作类型不能为空");
        }
//        FtServiceInfo ftServiceInfo = ftInterface.getFtServiceInfo();
//        PutAuthEntity putAuthEntity = ftInterface.getPutAuthEntity();
        FtServiceInfo ftServiceInfo = new FtServiceInfo();
        ftServiceInfo.setSystemName(ftInterface.getData().getSystemName());
        PutAuthEntity putAuthEntity = new PutAuthEntity();
        putAuthEntity.setUserName(ftInterface.getData().getUserName());
        putAuthEntity.setTrancode(ftInterface.getData().getTrancode());
        putAuthEntity.setDirectoy(ftInterface.getData().getDirectoy());
        GetAuthEntity getAuthEntity = new GetAuthEntity();
        getAuthEntity.setTrancode(ftInterface.getData().getTrancode());
        getAuthEntity.setUserName(ftInterface.getData().getUserName());
        String mode =ftInterface.getData().getMode();
        ResultDto<String> resultDto = null;
        if (operateType.equalsIgnoreCase(ADD)) {
            if (mode.equalsIgnoreCase(PUT)) {
                resultDto = serviceInfoService.savePutAuth(ftServiceInfo, putAuthEntity);
            }else if (mode.equalsIgnoreCase(GET)){
                resultDto = serviceInfoService.saveGetAuth(ftServiceInfo,getAuthEntity);
            }
        } else if (operateType.equalsIgnoreCase(DEL)) {
            if (mode.equalsIgnoreCase(PUT)) {
                resultDto = serviceInfoService.delPutAuth(ftServiceInfo,putAuthEntity);
            }else if (mode.equalsIgnoreCase(GET)){
                resultDto = serviceInfoService.delGetAuth(ftServiceInfo,getAuthEntity);
            }
        }
        String sysname = ftInterface.getData().getSystemName();
        String content = getCurrCfgContent(sysname, SERVICE_INFO, true);
        cfgZkService.write(sysname, SERVICE_INFO, content.trim());

        return resultDto;
    }


    public ResultDto<String> rsa(FtInterface ftInterface) throws IOException {
        ResultDto resul = null;
        String operateType = ftInterface.getOperateType();
        if (null == operateType) {
            log.error("操作类型为空");
            resul = ResultDtoTools.buildError("操作类型不能为空");
        }
        FtKey ftKey = new FtKey();
        ftKey.setUser(ftInterface.getData().getUser());
        ftKey.setType(ftInterface.getData().getType());
        ftKey.setContent(ftInterface.getData().getContent());
        ftKey.setSystemName(ftInterface.getData().getSystemName());
        ResultDto<String> resultDto = null;
        if (operateType.equalsIgnoreCase(ADD)) {
            resultDto = keyService.add(ftKey);
        } else if (operateType.equalsIgnoreCase(UPDATE)) {
            resultDto = keyService.update(ftKey);
        } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = keyService.del(ftKey);
        }
        log.info(String.valueOf(resultDto));
        String sysname = ftInterface.getData().getSystemName();
        String content = getCurrCfgContent(sysname, SYSTEM, true);
        cfgZkService.write(sysname, SYSTEM, content.trim());


        return resultDto;
    }

//    public  ResultDto<String> msg(FtInterface ftInterface) throws IOException {
//        ResultDto resultDto = null;
//        String operateType = ftInterface.getOperateType();
//        if (null == operateType) {
//            log.error("操作类型为空");
//            resultDto = ResultDtoTools.buildError("操作类型不能为空");
//        }
//        SysProtocol sysProtocol = new SysProtocol();
//        sysProtocol.setName(ftInterface.getData().getName());
//        sysProtocol.setProtocol(ftInterface.getData().getProtocol());
//        sysProtocol.setPort(ftInterface.getData().getPort());
//        sysProtocol.setIp(ftInterface.getData().getIp());
//        sysProtocol.setUsername(ftInterface.getData().getUsername());
//        sysProtocol.setPassword(ftInterface.getData().getPassword());
//        String mode = ftInterface.getData().getMode();
////        ResultDto<String> resultDto = null;
//        if (operateType.equalsIgnoreCase(ADD)) {
//            if (mode.equalsIgnoreCase(PUT)) {
//                sysProtocol.setUploadPath(ftInterface.getData().getServiceurl());
//                resultDto = sysService.add(sysProtocol);
//            }else if (mode.equalsIgnoreCase(GET)){
//                sysProtocol.setDownloadPath(ftInterface.getData().getServiceurl());
//                resultDto = sysService.add(sysProtocol);
//            }
//        } else if (operateType.equalsIgnoreCase(DEL)) {
//            if (mode.equalsIgnoreCase(GET)||mode.equalsIgnoreCase(PUT)){
//                resultDto = sysService.del(sysProtocol);
//            }
//        } else if (operateType.equalsIgnoreCase(UPDATE)){
//            if (mode.equalsIgnoreCase(PUT)){
//                sysProtocol.setUploadPath(ftInterface.getData().getServiceurl());
//                resultDto = sysService.update(sysProtocol);
//            }else if (mode.equalsIgnoreCase(GET)){
//                sysProtocol.setDownloadPath(ftInterface.getData().getServiceurl());
//                resultDto = sysService.update(sysProtocol);
//            }
//
//        }
public ResultDto<String> msg(FtInterface ftInterface) throws IOException {
    ResultDto resultDto = null;
    String operateType = ftInterface.getOperateType();
    if (null == operateType) {
        log.error("操作类型为空");
        resultDto = ResultDtoTools.buildError("操作类型不能为空");
    }
    SysProtocol sysProtocol = new SysProtocol();
    sysProtocol.setName(ftInterface.getData().getUserName());
    sysProtocol.setProtocol("HTTP");
    String serviceurl = ftInterface.getData().getServiceurl();
//    Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
//    Matcher m = p.matcher(serviceurl);
    sysProtocol.setPort("18001");
    sysProtocol.setIp("127.0.0.1");
    sysProtocol.setUsername(ftInterface.getData().getUserName());
    sysProtocol.setPassword(ftInterface.getData().getPassword());
//        ResultDto<String> resultDto = null;
    if (operateType.equalsIgnoreCase(ADD)) {
            sysProtocol.setUploadPath(ftInterface.getData().getServiceurl());
            sysProtocol.setDownloadPath(ftInterface.getData().getServiceurl());
            resultDto = sysService.add(sysProtocol);
        if (resultDto.getCode().equals("0000")){
            FtRoute ftRoute = new FtRoute();
            ftRoute.setTran_code(ftInterface.getData().getTrancode());
            ftRoute.setDestination(ftInterface.getData().getUserName());
            ftRoute.setUser(ftInterface.getData().getUserName());
            ftRoute.setType("s");
            ftRoute.setMode("asyn");
            resultDto = routeService.add(ftRoute);
        }
        String sysname = ftInterface.getData().getSystemName();
        String content1 = getCurrCfgContent(sysname, ROUTE, true);
        cfgZkService.write(sysname, ROUTE, content1.trim());

    } else if (operateType.equalsIgnoreCase(DEL)) {
            resultDto = sysService.del(sysProtocol);
        if (resultDto.getCode().equals("0000")){
            FtRoute ftRoute = new FtRoute();
            ftRoute.setTran_code(ftInterface.getData().getTrancode());
            resultDto = routeService.del(ftRoute);
        }
        String sysname = ftInterface.getData().getSystemName();
        String content1 = getCurrCfgContent(sysname, ROUTE, true);
        cfgZkService.write(sysname, ROUTE, content1.trim());
    } else if (operateType.equalsIgnoreCase(UPDATE)){
            sysProtocol.setUploadPath(ftInterface.getData().getServiceurl());
            sysProtocol.setDownloadPath(ftInterface.getData().getServiceurl());
            resultDto = sysService.update(sysProtocol);
    }
        String sysname = ftInterface.getData().getSystemName();
        String content = getCurrCfgContent(sysname, SERVICE_INFO, true);
        cfgZkService.write(sysname, SERVICE_INFO, content.trim());
        return resultDto;
    }

    public ResultDto<String> serviceInfo(FtInterface ftInterface) throws IOException{

        ResultDto rep = null;
        FtServiceInfo ftServiceInfo = new FtServiceInfo();
        ftServiceInfo.setTrancode(ftInterface.getData().getServiceid());
        ftServiceInfo.setSystemName(ftInterface.getData().getUserid());
        ResultDto<ServiceModel.Service> resultDto = serviceInfoService.selByTrancodeAndGetAuth(ftServiceInfo);
        if (resultDto.getCode().equals("0000")) {
            FtFiles ftFile = new FtFiles();
            ftFile.setTranCode(ftInterface.getData().getServiceid());
            List<FtFiles> file = ftGetFileService.findFileList(ftFile);

            rep = ResultDtoTools.buildSucceed(file);

        }else rep = ResultDtoTools.buildError("用户无此交易码权限");

        return rep;
    }


    private String getCurrCfgContent(String sysname, String fileName, boolean hasTimestamp) throws IOException {
        return cfgFileService.getCurrCfgContent(sysname, fileName, hasTimestamp);
    }
}
