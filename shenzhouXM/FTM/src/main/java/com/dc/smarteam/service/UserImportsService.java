package com.dc.smarteam.service;

import com.csvreader.CsvReader;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.util.ImportsHelpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hudja on 2017/7/24.
 */
@Service
public class UserImportsService {
    private static Logger log = LoggerFactory.getLogger(UserImportsService.class);
    @Resource
    private SysService sysService;
    @Resource
    private UserService userService;
    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private ServiceInfoImportsService serviceInfoImportsService;


    @Value("${localUploadFilePath}")
    private String localUploadFilePath;
    @Value("${localUploadFile}")
    private String localUploadFile;

    public static final String ERROR_IMPORT = "error_import";
    public static final String ERROR_BAK = "数据还原失败[请查看日志]";
    public static final String USERNAME_REX = "[a-z][a-z0-9_]{2,19}"; //用户名
    public static final String ADDRESS_REX = "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)";//ip
    public static final String PORT_REX = "(\\d+)";//端口
    public static final String PASS_REX = "[^\\u4e00-\\u9fa5]{6,20}";//密码

    public ResultDto<String> userImports(File file, String nodesystemname, String banksystemname) {
        ResultDto<HashMap<String, String[]>> mapResultDto = getDataMap(file, nodesystemname, banksystemname);
        if (!ResultDtoTool.isSuccess(mapResultDto)) {
            return ResultDtoTool.buildError(mapResultDto.getCode(), mapResultDto.getMessage());
        }
        HashMap<String, String[]> importdatamap = mapResultDto.getData();
        if (importdatamap.isEmpty()) {
            return ResultDtoTool.buildError("导入数据为空，csv数据文件!");
        }


        for (String key : importdatamap.keySet()) { //NOSONAR
            String[] importdata = importdatamap.get(key);
            String usersysname = importdata[0]; //系统名称
            String username = importdata[1];      //用户名称
            String ip = importdata[2]; //ip地址
            String port = importdata[3];//端口
            String password = importdata[4];//密码
            ResultDto resultDtouser = addFtuser(username, password, usersysname);
            if (!ResultDtoTool.isSuccess(resultDtouser)) {

                return backbakfile(nodesystemname, resultDtouser);
            }

            SysProtocol sysProtocol = new SysProtocol();
            sysProtocol.setName(key);
            sysProtocol.setProtocol("FtsFileMsg"); //设置默认值
            sysProtocol.setIp(ip);
            sysProtocol.setPort(port);
            sysProtocol.setUsername(username);
            sysProtocol.setPassword(password);

            ResultDto resultDtosys = addsysProtocol(sysProtocol);
            if (!ResultDtoTool.isSuccess(resultDtosys)) {
                return backbakfile(nodesystemname, resultDtosys);
            }


        }

        return ResultDtoTool.buildSucceed("用户管理批量导入完成");
    }

    /**
     * 添加目标路由
     *
     * @param sysProtocol
     * @return
     */
    public ResultDto addsysProtocol(SysProtocol sysProtocol) {
        SystemModel model = loadSystemModel();
        for (SystemModel.System system : model.getSystems()) {
            if (StringUtils.equalsIgnoreCase(system.getName(), sysProtocol.getName())) {
                log.debug("已有此系统,执行删除更新操作");
                ResultDto resultDto = sysService.del(sysProtocol);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return resultDto;
                }
                break;
            }
        }
        return sysService.add(sysProtocol);

    }

    /**
     * 添加用户
     *
     * @param username
     * @param password
     * @param usersysname
     * @return
     */
    public ResultDto addFtuser(String username, String password, String usersysname) {
        FtUser ftUser = new FtUser();
        ftUser.setName(username);

        UserModel userModel = loadUserModel();
        for (UserModel.UserInfo userInfo : userModel.getUserInfos()) {
            if (StringUtils.equalsIgnoreCase(userInfo.getUid().getUid(), ftUser.getName())) {
                log.debug("已有此用户,执行删除更新操作!");
                ResultDto resultDto = userService.del(ftUser);
                if (!ResultDtoTool.isSuccess(resultDto)) {
                    return resultDto;
                }
                break;
            }
        }
        ftUser.setDes(usersysname);
        ftUser.setPwd(password);

        return userService.add(ftUser);
    }

    /**
     * 获取csv数据
     *
     * @param file
     * @param nodesystemname
     * @return
     */
    private ResultDto<HashMap<String, String[]>> getDataMap(File file, String nodesystemname, String banksystemname) {//NOSONAR

        HashMap<String, String[]> userMap = new HashMap();
        try {
            CsvReader csvreader = new CsvReader(new FileInputStream(file), Charset.forName("gbk"));
            if (!serviceInfoImportsService.bakfiledata(nodesystemname, CFG_SYS_FILE_NAME) || !serviceInfoImportsService.bakfiledata(nodesystemname, CFG_USER_FILE_NAME)) {
                return ResultDtoTool.buildError("备份文件失败");

            }
            while (csvreader.readRecord()) {

                if (csvreader.getCurrentRecord() == 0) {
                    continue;
                }
                if (csvreader.getValues().length != 8) {
                    log.error("csv格式有误请查看" + csvreader.getCurrentRecord() + "行 ");
                    return ResultDtoTool.buildError(ERROR_IMPORT, "csv格式有误" + csvreader.getCurrentRecord() + "行的列为" + csvreader.getValues().length + "列 正确为8列");
                }
                ResultDto checkffdto = checkeff(csvreader.getValues(), banksystemname);
                if (!ResultDtoTool.isSuccess(checkffdto)) {
                    log.error("数据有误请查看" + csvreader.getCurrentRecord() + "行");
                    return ResultDtoTool.buildError(checkffdto.getCode(), "数据有误请查看" + csvreader.getCurrentRecord() + "行" + checkffdto.getMessage());
                }
                userMap = addonekeyMap(userMap, csvreader.getValues(), 1);
            }
            if (null != banksystemname && banksystemname.length() == 3) {
                //更新系统下用户，执行批量删除
                UserModel userModel = loadUserModel();
                List<UserModel.UserInfo> userInfos = userModel.getUserInfos();
                Iterator<UserModel.UserInfo> userInfoIterator = userInfos.iterator();
                while (userInfoIterator.hasNext()) {
                    UserModel.UserInfo userInfo = userInfoIterator.next();
                    if (StringUtils.equals(userInfo.getUid().getUid().substring(0, 3), banksystemname)) {
                        userInfoIterator.remove();
                    }
                }
                saveUserModel(userModel);
                //批量删除路由目标
                SystemModel systemmodel = loadSystemModel();
                List<SystemModel.System> systems = systemmodel.getSystems();
                Iterator<SystemModel.System> systemIterator = systems.iterator();
                while (systemIterator.hasNext()) {
                    SystemModel.System system = systemIterator.next();
                    if (StringUtils.equals(system.getName().substring(0, 3), banksystemname)) {
                        systemIterator.remove();
                    }
                }
                saveSystemModel(systemmodel);
            }
            csvreader.close();
        } catch (IOException e) {
            log.error("文件读取失败", e);
            return ResultDtoTool.buildError("文件读取失败");
        }

        return ResultDtoTool.buildSucceed(userMap);
    }

    public HashMap<String, String[]> addonekeyMap(HashMap<String, String[]> userMap, String[] datas, int i) {
        if (userMap.get(datas[1]) == null) {
            userMap.put(datas[1], datas);
            return userMap;
        } else if (userMap.get(datas[1] + String.valueOf(i)) == null) {
            userMap.put(datas[1] + String.valueOf(i), datas);
            return userMap;
        } else {
            int add = i + 1;
            return addonekeyMap(userMap, datas, add);
        }

    }

    /**
     * 检查数据有效性
     *
     * @param importdata
     * @return
     */
    private ResultDto checkeff(String[] importdata, String banksystemname) {
        final String errorrex = "不符合规范";
        String systemchname = importdata[0];//系统中文名
        String username = importdata[1]; //用户名
        String address = importdata[2];//ip
        String port = importdata[3];//端口
        String password = importdata[4];//密码
        if (StringUtils.equals(systemchname, "") || StringUtils.equals(username, "") || StringUtils.equals(address, "") || StringUtils.equals(port, "") || StringUtils.equals(password, "")) {
            log.error("系统中文名[{}]用户名[{}]系统中文名[{}]IP地址[{}]端口[{}]密码[@@]{}", systemchname, username, address, port, errorrex);
            return ResultDtoTool.buildError("系统中文名[" + systemchname + "]用户名[" + username + "]IP地址[" + address + "]端口[" + port + "]密码[@@]{}" + errorrex);
        }
        if (!ImportsHelpUtil.rexMatch(username, USERNAME_REX)) {
            log.error("用户名[{}]{}", username, errorrex);
            return ResultDtoTool.buildError("用户名[" + username + "]" + errorrex);
        }
        if (null != banksystemname && banksystemname.length() == 3 && !StringUtils.equals(username.substring(0, 3), banksystemname)) {
            log.error("用户名[{}]格式有误不以系统名称为前3位", username);
            return ResultDtoTool.buildError("用户名[" + username + "]格式有误不以系统名称为前3位");
        }
//        if (!ImportsHelpUtil.rexMatch(address, ADDRESSREX)) {
//            log.error("address[{}]", address, errorrex);
//            return ResultDtoTool.buildError("IP地址[" + address + "]" + errorrex);
//        }
        if (!ImportsHelpUtil.rexMatch(port, PORT_REX) || Integer.parseInt(port) >= 65536 || Integer.parseInt(port) <= 0) {
            log.error("port[{}]", port, errorrex);
            return ResultDtoTool.buildError("port[" + port + "]" + errorrex);
        }
        if (!ImportsHelpUtil.rexMatch(password, PASS_REX)) {
            log.error("password[{}]{}", password, errorrex);
            return ResultDtoTool.buildError("password[" + password + "]" + errorrex);
        }

       /* String[] addmins = address.split("\\.");
        for (String addmin : addmins) {
            if (Integer.parseInt(addmin) >255 || Integer.parseInt(addmin) < 0) {
                log.error("ip[{}]{}", address, errorrex);
                return ResultDtoTool.buildError("address[" + address + "]" + errorrex);
            }
        }*/
        return ResultDtoTool.buildSucceed("数据效验成功");
    }

    /**
     * 还原配置文件数据
     */
    public ResultDto backbakfile(String nodesystemname, ResultDto resultDto) {
        if (!serviceInfoImportsService.backbakfiledata(nodesystemname, CFG_USER_FILE_NAME) || !serviceInfoImportsService.backbakfiledata(nodesystemname, CFG_SYS_FILE_NAME)) {
            return ResultDtoTool.buildError(ERROR_BAK);
        }
        return resultDto;
    }

    private static final String CFG_SYS_FILE_NAME = "system.xml";

    private SystemModel loadSystemModel() {
        return cfgFileService.loadModel4Name(CFG_SYS_FILE_NAME, SystemModel.class);
    }

    private void saveSystemModel(SystemModel model) {
        cfgFileService.saveModel4Name(CFG_SYS_FILE_NAME, model);
    }

    private static final String CFG_USER_FILE_NAME = "user.xml";

    private UserModel loadUserModel() {
        return cfgFileService.loadModel4Name(CFG_USER_FILE_NAME, UserModel.class);
    }

    private void saveUserModel(UserModel model) {
        cfgFileService.saveModel4Name(CFG_USER_FILE_NAME, model);
    }

}
