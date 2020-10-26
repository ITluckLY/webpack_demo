package com.dc.smarteam.service;

import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import java.io.File;
import java.util.HashMap;

public interface UserImportsService {

     String ERROR_IMPORT = "error_import";
     String ERROR_BAK = "数据还原失败[请查看日志]";
     String USERNAME_REX = "[a-z][a-z0-9_]{2,19}"; //用户名
     String ADDRESS_REX = "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)";//ip
     String PORT_REX = "(\\d+)";//端口
     String PASS_REX = "[^\\u4e00-\\u9fa5]{6,20}";//密码
     String CFG_SYS_FILE_NAME = "system.xml";
     String CFG_USER_FILE_NAME = "user.xml";

     ResultDto<String> userImports(File file, String nodesystemname, String banksystemname);
    
     ResultDto addsysProtocol(SysProtocol sysProtocol);
    
     ResultDto addFtuser(String username, String password, String usersysname);
    
     ResultDto<HashMap<String, String[]>> getDataMap(File file, String nodesystemname, String banksystemname);
    
     HashMap<String, String[]> addonekeyMap(HashMap<String, String[]> userMap, String[] datas, int i);

     ResultDto checkeff(String[] importdata, String banksystemname);
    
     ResultDto backbakfile(String nodesystemname, ResultDto resultDto);

     SystemModel loadSystemModel();

     void saveSystemModel(SystemModel model);

     UserModel loadUserModel();
    
     void saveUserModel(UserModel model);

}
