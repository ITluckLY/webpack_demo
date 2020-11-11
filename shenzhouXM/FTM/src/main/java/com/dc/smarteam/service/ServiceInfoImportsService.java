package com.dc.smarteam.service;

import com.csvreader.CsvReader;
import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.cons.NodeType;
import com.dc.smarteam.helper.EmptyCfgXmlHelper;
import com.dc.smarteam.modules.cfgfile.entity.CfgFile;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.cfgsync.service.NodeCfgSyncService;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceFlowVo;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.util.EmptyUtils;
import com.dc.smarteam.util.ImportsHelpUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hudja on 2017/7/24.
 */
@Service
public class ServiceInfoImportsService {

    private static Logger log = LoggerFactory.getLogger(ServiceInfoImportsService.class);
    @Resource
    private ServiceInfoService serviceInfoService;
    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private RouteService routeService;
    @Resource
    private NodeCfgSyncService nodeCfgSyncService;
    @Autowired
    private SysService sysService;
    @Autowired
    private UserService userService;

    @Value("${localUploadFilePath}")
    private String localUploadFilePath;
    @Value("${localUploadFile}")
    private String localUploadFile;

    public static final String ERROR_IMPORT = "error_import";
    public static final String ERROR_BAK = "数据还原失败[请查看日志]";


    public ResultDto serviceImports(File file, String nodesystemname, String banksystem) {//NOSONAR
        ResultDto<List<String[]>> resultDtodata = getDataList(file, nodesystemname, banksystem);

        if (!ResultDtoTool.isSuccess(resultDtodata)) {
            return ResultDtoTool.buildError(resultDtodata.getCode(), resultDtodata.getMessage());
        }
        List<String[]> importdatalist = resultDtodata.getData();
        for (String[] importdata : importdatalist) {
            String trancode = importdata[0]; //交易码
            String fromsystem = importdata[1];      //上传系统
            String tosystems = importdata[2]; //下载系统
            String rename = importdata[3];//文件重命名
            String cross = importdata[4];//文件跨地域
            String des = importdata[5] + " 文件[" + importdata[6] + "]";
            FtServiceInfo ftServiceInfo = new FtServiceInfo();
            ftServiceInfo.setSystemName(nodesystemname);
            ftServiceInfo.setTrancode(trancode);
            ftServiceInfo.setPriority("3");
            ftServiceInfo.setFlow("DefStandardFlow");
            ftServiceInfo.setDescribe(des);
//            ftServiceInfo.setRename("0");
//            ftServiceInfo.setCross("0");
            ftServiceInfo.setRename(rename);
            ftServiceInfo.setCross(cross);
            ftServiceInfo.setFilePeriod("0");
            ftServiceInfo.setSize("10");
            //导入服务
            ResultDto resultDto = importsServiceInfo(ftServiceInfo);
            if (!ResultDtoTool.isSuccess(resultDto)) {
                return backbakfile(nodesystemname, resultDto);
            }
            String[] users = fromsystem.split(",");
            for (String user : users) {
                //导入上传权限
                resultDto = importsputAuth(ftServiceInfo, user, trancode);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return backbakfile(nodesystemname, resultDto);
                }
                //保存路由管理
                resultDto = saveftRoute(user, trancode, tosystems);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return backbakfile(nodesystemname, resultDto);
                }
            }
            //导入下载权限
            String[] tosystemarr = tosystems.split(",");
            for (String system : tosystemarr) {
                resultDto = importsgetAuth(ftServiceInfo, trancode, system);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return backbakfile(nodesystemname, resultDto);
                }
            }

        }

        return ResultDtoTool.buildSucceed("文件导入完成");
    }

    /**
     * 添加交易码服务流程
     * @param ftServiceFlowVo
     * @param nodesystemname
     * @return
     */
    @Transactional
    public ResultDto addServiceFlow(FtServiceFlowVo ftServiceFlowVo, String nodesystemname) throws Exception{//NOSONAR

        String trancode = ftServiceFlowVo.getTransCode(); //交易码
        String fromsystem = ftServiceFlowVo.getProducer();      //上传系统
        String tosystems = ftServiceFlowVo.getCustomer(); //下载系统
        String des = ftServiceFlowVo.getDes() + "[" + ftServiceFlowVo.getFileName() + "]";
        FtServiceInfo ftServiceInfo = new FtServiceInfo();
        ftServiceInfo.setSystemName(nodesystemname);
        ftServiceInfo.setTrancode(trancode);
        ftServiceInfo.setPriority(ftServiceFlowVo.getFtServiceInfo().getPriority());
        ftServiceInfo.setFlow(ftServiceFlowVo.getFtServiceInfo().getFlow());
        ftServiceInfo.setDescribe(des);
        ftServiceInfo.setRename(ftServiceFlowVo.getFtServiceInfo().getRename());
        ftServiceInfo.setFilePeriod(ftServiceFlowVo.getFtServiceInfo().getFilePeriod());
        ftServiceInfo.setSize(ftServiceFlowVo.getFtServiceInfo().getSize());
        ftServiceInfo.setCross(ftServiceFlowVo.getFtServiceInfo().getCross());

        log.info("ftServiceInfo:{}",ftServiceInfo.toString());
        //添加服务
        ResultDto resultDto = this.addServiceInfo(ftServiceInfo);
        if (!ResultDtoTool.isSuccess(resultDto)) {
            //抛出运行时异常是因为业务流程没完成需要回滚事务，下同
            throw new RuntimeException("添加交易码服务失败");
        }
        String[] userList = fromsystem.split(",");
        for (String user : userList) {
            //添加上传权限
            resultDto = this.addPutAuth(ftServiceInfo, user, trancode);
            if (!ResultDtoTool.isSuccess(resultDto)) {
                throw new RuntimeException("添加上传权限失败:"+resultDto.getMessage());
            }
            //保存路由管理
            resultDto = this.saveftRouteNoBatch(user, trancode, tosystems);
            if (!ResultDtoTool.isSuccess(resultDto)) {
                throw new RuntimeException("添加交易码路由失败:"+resultDto.getMessage());
            }
        }
        //添加下载权限
        String[] tosystemarr = tosystems.split(",");
        for (String system : tosystemarr) {
            resultDto = this.addGetAuth(ftServiceInfo, trancode, system);
            if (!ResultDtoTool.isSuccess(resultDto)) {
                throw new RuntimeException("添加下载权限失败:"+resultDto.getMessage());
            }
        }
        return ResultDtoTool.buildSucceed("添加交易码服务流程完成");
    }

    /**
     * 根据excle文件获取有效数据
     */
    public ResultDto<List<String[]>> getDataList(File file, String nodesystemname, String banksystem) {
        List<String[]> importdatalist = new ArrayList<>();

        try {
            CsvReader csvreader = new CsvReader(new FileInputStream(file), Charset.forName("gbk"));
            if (!bakfiledata(nodesystemname, CFG_SERVICE_INFO_FILE_NAME) || !bakfiledata(nodesystemname, CFG_ROUTE_FILE_NAME)) {
                return ResultDtoTool.buildError(ERROR_IMPORT, "数据备份失败");
            }

            while (csvreader.readRecord()) {
                if (csvreader.getValues().length != 7) {
                    log.error("csv格式有误请查看{}行{}列不等于7", (csvreader.getCurrentRecord() + 1), csvreader.getValues().length);
                    return ResultDtoTool.buildError(ERROR_IMPORT, "csv格式有误请查看" + (csvreader.getCurrentRecord() + 1) + "行的列为" + csvreader.getValues().length + "列正确为7列");
                }
                if (csvreader.getCurrentRecord() == 0) {
                    continue;
                }
                ResultDto resultDtocheff = checkeff(csvreader.getValues(), banksystem);
                if (!ResultDtoTool.isSuccess(resultDtocheff)) {
                    log.error("数据有误请查看{}行", (csvreader.getCurrentRecord() + 1));
                    return ResultDtoTool.buildError(ERROR_IMPORT, "数据有误请查看" + (csvreader.getCurrentRecord() + 1) + "行" + resultDtocheff.getMessage());
                }
                importdatalist.add(csvreader.getValues());
            }
            //更新系统下交易码，执行批量删除
            ServiceModel serviceModel = loadModelService();
            List<ServiceModel.Service> services = serviceModel.getServices();
            Iterator<ServiceModel.Service> serviceIterator = services.iterator();
            while (serviceIterator.hasNext()) {
                ServiceModel.Service service = serviceIterator.next();
                if (StringUtils.equals(service.getTrancode().substring(0, 3), banksystem)) {
                    serviceIterator.remove();
                }
            }
            saveServiceModel(serviceModel);
            csvreader.close();
        } catch (IOException e) {
            log.error("文件读取失败", e);
            return ResultDtoTool.buildError(ERROR_IMPORT, "文件读取失败");
        }
        return ResultDtoTool.buildSucceed(importdatalist);
    }


    /**
     * 批量导入服务
     *
     * @param ftServiceInfo
     */
    public ResultDto<String> importsServiceInfo(FtServiceInfo ftServiceInfo) {
        if (EmptyUtils.isEmpty(ftServiceInfo.getId())) {
            ResultDtoTool.buildError("导入服务码不能为空");
        }
        ServiceModel model = loadModelService();
        for (ServiceModel.Service service : model.getServices()) {
            if (StringUtils.equalsIgnoreCase(service.getTrancode(), ftServiceInfo.getTrancode())) {
                ResultDto<String> resultDto = serviceInfoService.del(ftServiceInfo);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return resultDto;
                }
            }
        }
        return serviceInfoService.add(ftServiceInfo);
    }

    /**
     * 新增一条交易码服务
     * @param ftServiceInfo
     * @return
     */
    public ResultDto<String> addServiceInfo(FtServiceInfo ftServiceInfo) {
        if (EmptyUtils.isEmpty(ftServiceInfo.getId())) {
            ResultDtoTool.buildError("导入服务码不能为空");
        }
        ServiceModel model = loadModelService();
        for (ServiceModel.Service service : model.getServices()) {
            if (StringUtils.equalsIgnoreCase(service.getTrancode(), ftServiceInfo.getTrancode())) {
               return ResultDtoTool.buildError("交易码已存在");
            }
        }
        return serviceInfoService.add(ftServiceInfo);
    }

    /**
     * 批量导入上传授权
     *
     * @param ftServiceInfo
     * @param user
     * @param trancode
     */
    public ResultDto<String> importsputAuth(FtServiceInfo ftServiceInfo, String user, String trancode) {
        FtUser ftUser = new FtUser();
        ftUser.setName(user);
        if (!ResultDtoTool.isSuccess(userService.selByName(ftUser))) {
//            return ResultDtoTool.buildError(user+"用户不存在"); //NOSONAR
        }

        PutAuthEntity putAuthEntity = new PutAuthEntity();
        putAuthEntity.setDirectoy("/" + trancode + "/" + user);
        putAuthEntity.setUserName(user);
        putAuthEntity.setTrancode(trancode);
        return serviceInfoService.savePutAuth(ftServiceInfo, putAuthEntity);

    }

    /**
     * 添加上传用户
     * @param ftServiceInfo
     * @param user
     * @param trancode
     * @return
     */
    public ResultDto<String> addPutAuth(FtServiceInfo ftServiceInfo, String user, String trancode) {
        FtUser ftUser = new FtUser();
        ftUser.setName(user);
        if (!ResultDtoTool.isSuccess(userService.selByName(ftUser))) {
            return ResultDtoTool.buildError(user+"用户不存在"); //NOSONAR
        }
        PutAuthEntity putAuthEntity = new PutAuthEntity();
        putAuthEntity.setDirectoy("/" + trancode + "/" + user);
        putAuthEntity.setUserName(user);
        putAuthEntity.setTrancode(trancode);
        return serviceInfoService.savePutAuth(ftServiceInfo, putAuthEntity);

    }

    /**
     * 批量导入下载授权
     *
     * @param ftServiceInfo
     * @param trancode
     * @param system
     */
    public ResultDto<String> importsgetAuth(FtServiceInfo ftServiceInfo, String trancode, String system) {
        FtUser ftUser = new FtUser();
        ftUser.setName(system);
        if (!ResultDtoTool.isSuccess(userService.selByName(ftUser))) {
//            return ResultDtoTool.buildError(system+"用户不存在 导入失败  "); //NOSONAR
        }


        GetAuthEntity getAuthEntity = new GetAuthEntity();
        getAuthEntity.setUserName(system);
        getAuthEntity.setTrancode(trancode);
        return serviceInfoService.saveGetAuth(ftServiceInfo, getAuthEntity);
    }

    /**
     * 添加下载用户
     * @param ftServiceInfo
     * @param trancode
     * @param system
     * @return
     */
    public ResultDto<String> addGetAuth(FtServiceInfo ftServiceInfo, String trancode, String system) {
        FtUser ftUser = new FtUser();
        ftUser.setName(system);
        if (!ResultDtoTool.isSuccess(userService.selByName(ftUser))) {
            return ResultDtoTool.buildError(system+"用户不存在 导入失败  "); //NOSONAR
        }

        GetAuthEntity getAuthEntity = new GetAuthEntity();
        getAuthEntity.setUserName(system);
        getAuthEntity.setTrancode(trancode);
        return serviceInfoService.saveGetAuth(ftServiceInfo, getAuthEntity);
    }

    /**
     * 添加路由管理方法
     * --对于已经存在的交易码路由 执行更新操作
     *
     * @param user
     * @param trancode
     * @param tosystems
     * @return
     */
    public ResultDto<String> saveftRoute(String user, String trancode, String tosystems) {
        FtRoute ftRoute = new FtRoute();
        ftRoute.setUser(user);
        ftRoute.setTran_code(trancode);
        ftRoute.setType("s");
        ftRoute.setMode("asyn");
        RouteModel model = loadModelRoute();
        if (StringUtils.isEmpty(trancode)) {
            ResultDtoTool.buildError("交易码为空");
        }
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equalsIgnoreCase(route.getTranCode(), trancode)
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                log.debug("已经存在交易码路由执行删除操作");
                ResultDto resultDto = routeService.del(ftRoute);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return resultDto;
                }
            }
        }
        String tosysrouts = getsysroutmax(tosystems); //获取目标路由
        ftRoute.setDestination(tosysrouts);
        if ("".equals(tosysrouts)) {
            log.error("用户{}交易码{}路由{}为空", user, trancode, tosysrouts);
            ResultDtoTool.buildError("用户路由为空");
        }
        return routeService.add(ftRoute);
    }

    /**
     * 添加路由(非批量)
     * @param user
     * @param trancode
     * @param tosystems
     * @return
     */
    public ResultDto<String> saveftRouteNoBatch(String user, String trancode, String tosystems) {
        FtRoute ftRoute = new FtRoute();
        ftRoute.setUser(user);
        ftRoute.setTran_code(trancode);
        ftRoute.setType("s");
        ftRoute.setMode("asyn");
        RouteModel model = loadModelRoute();
        if (StringUtils.isEmpty(trancode)) {
            ResultDtoTool.buildError("交易码为空");
        }
        for (RouteModel.Route route : model.getRoutes()) {
            if (StringUtils.equalsIgnoreCase(route.getTranCode(), trancode)
                    && StringUtils.equalsIgnoreCase(route.getUser(), ftRoute.getUser())) {
                log.error("已经存在交易码");
                return ResultDtoTool.buildError("交易码已存在");
            }
        }
        String tosysrouts = getsysroutmax(tosystems); //获取目标路由
        ftRoute.setDestination(tosysrouts);
        if ("".equals(tosysrouts)) {
            log.error("用户{}交易码{}路由{}为空", user, trancode, tosysrouts);
            ResultDtoTool.buildError("用户路由为空");
        }
        return routeService.add(ftRoute);
    }

    /**
     * 根据 , 分割检查后在进行拼接目标路由
     *
     * @param tosystemsmax
     * @return
     */
    public String getsysroutmax(String tosystemsmax) {
        StringBuilder sb = new StringBuilder("");
        if (!tosystemsmax.contains(",")) {
            return getsysrouts(tosystemsmax);
        }
        String[] sysrouts = tosystemsmax.split(",");
        for (int i = 0; i < sysrouts.length; i++) {
            if ("".equals(getsysrouts(sysrouts[i]))) {
                continue;
            }
            if ("".equals(sb.toString())) {
                sb.append(getsysrouts(sysrouts[i]));
            } else {
                sb.append(",").append(getsysrouts(sysrouts[i]));
            }
        }
        return sb.toString();
    }

    /**
     * 获取单个用户的路由存在用,拼接
     *
     * @param tosystems
     * @return
     */
    public String getsysrouts(String tosystems) {
        StringBuilder sb = new StringBuilder("");
        SysProtocol sys = new SysProtocol();
        sys.setUsername(tosystems);
        List<SystemModel.System> listsyss = sysService.selByuserName(sys).getData();
        for (int i = 0; i < listsyss.size(); i++) {
            if (null == listsyss.get(i) || "".equals(listsyss.get(i).getName())) {
                continue;
            }
            if ("".equals(sb.toString())) {
                sb.append(listsyss.get(i).getName());
            } else {
                sb.append(",").append(listsyss.get(i).getName());
            }
        }
        return sb.toString();
    }

    /**
     * 备份批量导入数据前的数据
     *
     * @param sysname
     * @param fileName
     * @return
     */
    public boolean bakfiledata(String sysname, String fileName) {

        try {
            String cfgxml = cfgFileService.getCurrCfgContent(sysname, fileName, false);
            CfgFile cfgFile = new CfgFile();
            cfgFile.setFileName(fileName);
            cfgFile.setNodeType("bak");
            cfgFile.setFileType(FilenameUtils.getExtension(fileName));
            cfgFile.setContent(cfgxml);
            if (null == cfgFileService.findOne(cfgFile)) cfgFileService.save(cfgFile);
            cfgFile = cfgFileService.findOne(cfgFile);
            cfgFile.setContent(cfgxml);
            cfgFileService.updateByNameAndNodeType(cfgFile);
        } catch (IOException e) {
            log.error("服务备份失败", e);
            return false;

        }
        return true;
    }

    /**
     * 还原配置文件数据
     */
    public ResultDto backbakfile(String nodesystemname, ResultDto resultDto) {
        if (!backbakfiledata(nodesystemname, CFG_SERVICE_INFO_FILE_NAME) || !backbakfiledata(nodesystemname, CFG_ROUTE_FILE_NAME)) {
            return ResultDtoTool.buildError(ERROR_BAK);
        }
        return resultDto;
    }

    /**
     * 备份数据还原
     *
     * @param sysname
     * @param fileName
     * @return
     */
    public boolean backbakfiledata(String sysname, String fileName) {
        String cfgXml = "";
        if (log.isDebugEnabled()) {
            log.debug("进入数据还原{}{}", NodeType.NAMENODE.name(), fileName);
        }
        CfgFile queryCfgFile = new CfgFile();
        queryCfgFile.setFileName(fileName);
        queryCfgFile.setNodeType("bak");
        queryCfgFile.setFileType(FilenameUtils.getExtension(fileName));
        CfgFile cfgFile = cfgFileService.findOne(queryCfgFile);
        if (cfgFile != null) {
            cfgXml = cfgFile.getContent();
        }

        try {

            cfgXml = cfgXml.equals(EmptyCfgXmlHelper.getEmptyXml(fileName)) || cfgXml.equals("") ? "" :
                    nodeCfgSyncService.generateSyncCfgXml(sysname, fileName, cfgXml, false);

        } catch (IOException e) {
            log.error("数据还原失败", e);
            return false;
        }
        if (null == cfgFile) {
            cfgFile = new CfgFile();
        }
        cfgFile.setContent(cfgXml);
        cfgFile.setNodeType(NodeType.NAMENODE.name());
        cfgFile.setSystem("UNDEFINED");
        cfgFileService.updateByNameAndNodeType(cfgFile);
        return true;

    }

    /**
     * 导入数据可用性检查
     *
     * @param csvdata
     * @param banksystem 行内系统名效验交易码前3位
     * @return
     */
    public ResultDto checkeff(String[] csvdata, String banksystem) {
        String trancode = csvdata[0];
        String fromsystem = csvdata[1];
        String tosystem = csvdata[2];
        if ("".equals(trancode) || "".equals(fromsystem) || "".equals(tosystem)) {
            log.error("交易码[{}]文件生产方[]{}文件消费方[{}]", trancode, fromsystem, tosystem);
            return ResultDtoTool.buildError("交易码[" + trancode + "]文件生产方[" + fromsystem + "]文件消费方[" + tosystem + "]");
        }
        if (!ImportsHelpUtil.rexMatch(trancode, "[a-z]{3}00[0-9a-z]{5}")) {
            log.error("交易码[{}]格式有误", trancode);
            return ResultDtoTool.buildError("交易码[" + trancode + "]格式有误");
        }
        if (null != banksystem && !StringUtils.equals("", banksystem) && !trancode.startsWith(banksystem)) {
            log.error("交易码[{}]格式有误不以系统名称为前3位", trancode);
            return ResultDtoTool.buildError("交易码[" + trancode + "]格式有误不以系统名称为前3位");
        }
        if (!checksysuser(tosystem)) {
            return ResultDtoTool.buildError("消费方格式有误");
        }
        if (!checksysuser(fromsystem)) {
            return ResultDtoTool.buildError("生产方格式有误");
        }
        log.info("IMPORT SERVICEDATA IS SUCCESS");
        return ResultDtoTool.buildSucceed("数据效验成功");
    }

    public boolean checksysuser(String system) {
        boolean checkeff = true;
        if (!system.contains(",")) {
            checkeff = ImportsHelpUtil.rexMatch(system, "[a-z]{3}[0-9]{2}");
        } else {
            String[] systems = system.split(",");
            for (String systemmin : systems) {
                if (!ImportsHelpUtil.rexMatch(systemmin, "[a-z]{3}[0-9]{2}")) {
                    checkeff = false;
                    log.error("文件消费方[{}]格式有误", systemmin);
                    break;
                }
            }
        }
        return checkeff;
    }

    public static final String CFG_ROUTE_FILE_NAME = "route.xml";
    public static final String CFG_SERVICE_INFO_FILE_NAME = "services_info.xml";

    public ServiceModel loadModelService() {
        return cfgFileService.loadModel4Name(CFG_SERVICE_INFO_FILE_NAME, ServiceModel.class);
    }

    private void saveServiceModel(ServiceModel model) {
        cfgFileService.saveModel4Name(CFG_SERVICE_INFO_FILE_NAME, model);
    }

    public RouteModel loadModelRoute() {
        return cfgFileService.loadModel4Name(CFG_ROUTE_FILE_NAME, RouteModel.class);
    }

    public void save(RouteModel model) {
        cfgFileService.saveModel4Name(CFG_ROUTE_FILE_NAME, model);
    }

}
