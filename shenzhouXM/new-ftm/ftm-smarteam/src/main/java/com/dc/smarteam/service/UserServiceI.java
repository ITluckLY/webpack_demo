package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.modules.serviceinfo.entity.BankSystem;
import com.dc.smarteam.modules.user.entity.FtUser;

import java.util.List;

public interface UserServiceI {

    String CFG_FILE_NAME = "user.xml";

    ResultDto<List<UserModel.UserInfo>> listAll();

    ResultDto<UserModel.UserInfo> selByName(FtUser ftUser);

    ResultDto<UserModel.UserInfo> selloadedByName(FtUser ftUser, UserModel model);

    ResultDto<String> add(FtUser ftUser);

    ResultDto<String> adduser(FtUser ftUser);

    ResultDto<String> update(FtUser ftUser);

    ResultDto<String> updateuser(FtUser ftUser);

    ResultDto<String> del(FtUser ftUser);

    List<BankSystem> getBanksystemlist();

    List<BankSystem> addOneList(List<BankSystem> bankSystemList, BankSystem bankSystem);

    UserModel loadModel();

    void save(UserModel model);

    String getCfgFileName();

    String getEntityXml(CfgData curr, boolean isNew);
}
