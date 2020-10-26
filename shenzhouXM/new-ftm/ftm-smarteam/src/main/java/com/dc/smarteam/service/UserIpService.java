package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.user.entity.FtUser;
import java.util.List;

public interface UserIpService {

    String CFG_FILE_NAME = "user.xml";

    ResultDto<List<UserModel.UserInfo>> listAll();

    ResultDto<UserModel.UserInfo> selByName(FtUser ftUser) ;

    ResultDto<String> add(FtUserIp ftUserIp);

    ResultDto<String> update(FtUserIp ftUserIp);

    ResultDto<String> del(FtUserIp ftUserIp);

    UserModel loadModel();

    void save(UserModel model);

    String getCfgFileName();

    String getEntityXml(CfgData curr, boolean isNew);
}

