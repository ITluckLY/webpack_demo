package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.cfgmodel.NettyModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.client.service.FtOperationLogService;
import com.dc.smarteam.modules.serviceinfo.entity.BankSystem;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.util.XmlBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class UserService extends AbstractService {

    @Resource
    private CfgFileService cfgFileService;
    @Resource
    private FtOperationLogService ftOperationLogService;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    public ResultDto<List<UserModel.UserInfo>> listAll() {
        UserModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getUserInfos());
    }


    public ResultDto<UserModel.UserInfo> selByName(FtUser ftUser) {
        UserModel model = loadModel();
        return selloadedByName(ftUser,model);
    }
    public ResultDto<UserModel.UserInfo> selloadedByName(FtUser ftUser,UserModel model) {
        UserModel.UserInfo target = null;
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equals(userInfo.getUid().getUid(), ftUser.getName())) {
                target = userInfo;
                break;
            }
        }
        if (target == null) return ResultDtoTool.buildError("对象不存在");
        return ResultDtoTool.buildSucceed(target);
    }

    public ResultDto<String> add(FtUser ftUser) {
        String name = ftUser.getName();
        if (StringUtils.isEmpty(name)) {
            return ResultDtoTool.buildError("用户名不能为空");
        }
       /* if (StringUtils.isEmpty(ftUser.getSystemName())) {
            return ResultDtoTool.buildError("系统名称不能为空");
        }*/

        UserModel model = loadModel();
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equalsIgnoreCase(userInfo.getUid().getUid(), ftUser.getName())) {
                return ResultDtoTool.buildError("添加失败，已有此用户");
            }
        }

        UserModel.UserInfo target = new UserModel.UserInfo();
        CfgModelConverter.convertTo(ftUser, target);
        model.getUserInfos().add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> adduser(FtUser ftUser) {
        String name = ftUser.getName();
        if (StringUtils.isEmpty(name)) {
            return ResultDtoTool.buildError("用户名不能为空");
        }
       /* if (StringUtils.isEmpty(ftUser.getSystemName())) {
            return ResultDtoTool.buildError("系统名称不能为空");
        }*/

        UserModel model = loadModel();
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equalsIgnoreCase(userInfo.getUid().getUid(), ftUser.getName())) {
                return ResultDtoTool.buildError("添加失败，已有此用户");
            }
        }

        UserModel.UserInfo target = new UserModel.UserInfo();
        CfgModelConverter.convertToreplace(ftUser, target);
        model.getUserInfos().add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtUser ftUser) {
        UserModel.UserInfo target = null;
        UserModel model = loadModel();
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equals(userInfo.getUid().getUid(), ftUser.getName())) {
                target = userInfo;
                break;
            }
        }
        if (target == null) {
            return ResultDtoTool.buildError("没有找到指定的用户信息");
        }
        CfgModelConverter.convertTo(ftUser, target);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> updateuser(FtUser ftUser) {
        UserModel.UserInfo target = null;
        UserModel model = loadModel();
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equals(userInfo.getUid().getUid(), ftUser.getName())) {
                target = userInfo;
                break;
            }
        }
        if (target == null) {
            return ResultDtoTool.buildError("没有找到指定的用户信息");
        }
        CfgModelConverter.convertToreplace(ftUser, target);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    public ResultDto<String> del(FtUser ftUser) {
        UserModel model = loadModel();
        List<UserModel.UserInfo> userInfos = model.getUserInfos();
        for (UserModel.UserInfo userInfo : userInfos) {
            if (StringUtils.equals(userInfo.getUid().getUid(), ftUser.getName())) {
                userInfos.remove(userInfo);
                break;
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    public List<BankSystem> getBanksystemlist() {
        ResultDto<List<UserModel.UserInfo>> userDto = listAll();
        List<BankSystem> bankSystemlist = new ArrayList<>();
        if (ResultDtoTool.isSuccess(userDto)) {
            List<UserModel.UserInfo> userInfos = userDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                FtUser user = new FtUser();
                CfgModelConverter.convertTo(userInfo, user);
                BankSystem bankSystem = new BankSystem();
                bankSystem.setBankname(user.getName().substring(0, 3));
                bankSystemlist = addOneList(bankSystemlist, bankSystem);
            }
        }
        return bankSystemlist;
    }

    public List<BankSystem> addOneList(List<BankSystem> bankSystemList, BankSystem bankSystem) {
        for (BankSystem bankSystemmin : bankSystemList) {
            if (StringUtils.equals(bankSystemmin.getBankname(), bankSystem.getBankname())) {
                return bankSystemList;
            }
        }
        bankSystemList.add(bankSystem);
        return bankSystemList;
    }

    private static final String CFG_FILE_NAME = "user.xml";
    public UserModel loadModel() {
        return cfgFileService.loadModel4Name(CFG_FILE_NAME, UserModel.class);
    }
    private void save(UserModel model) {
        cfgFileService.saveModel4Name(CFG_FILE_NAME, model);
    }


    @Override
    public String getCfgFileName() {
        return CFG_FILE_NAME;
    }

    @Override
    public String getEntityXml(CfgData curr, boolean isNew){
        FtUser ftUser = (FtUser)curr;
        if(isNew){
            UserModel.UserInfo userInfo = new UserModel.UserInfo();
            CfgModelConverter.convertTo(ftUser,userInfo);
            return XmlBeanUtil.toXml(userInfo);
        }
        UserModel.UserInfo target = null;
        UserModel userModel = loadModel();
        for (UserModel.UserInfo userInfo : userModel.getUserInfos()) {
            if (StringUtils.equals(userInfo.getUid().getUid(), ftUser.getName())) {
                target = userInfo;
                //logger.info("old user is {}",XmlBeanUtil.toXml(userInfo));
                break;
            }
        }
        if(target==null)return null;
        logger.info("old user is{},{}",target.getUid().getUid(),target.getUid().getDescribe());
        return XmlBeanUtil.toXml(target);

    }

}
