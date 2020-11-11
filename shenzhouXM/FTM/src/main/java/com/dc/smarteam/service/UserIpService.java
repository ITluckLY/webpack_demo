package com.dc.smarteam.service;

import com.dc.smarteam.aspectCfg.base.UpdateEntity;
import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.util.XmlBeanUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/3/17.
 */
@Service
public class UserIpService extends AbstractService {
    @Resource
    private CfgFileService cfgFileService;

    public ResultDto<List<UserModel.UserInfo>> listAll() {
        UserModel model = loadModel();
        return ResultDtoTool.buildSucceed(model.getUserInfos());
    }

    public ResultDto<UserModel.UserInfo> selByName(FtUser ftUser) {
        UserModel model = loadModel();
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

    @UpdateEntity
    public ResultDto<String> add(FtUserIp ftUserIp) {
        String userId = ftUserIp.getFtUserId();
        if (StringUtils.isEmpty(userId)) {
            return ResultDtoTool.buildError("用户不能为空");
        }
        if (StringUtils.isEmpty(ftUserIp.getIpAddress())) {
            return ResultDtoTool.buildError("IP不能为空");
        }

        UserModel model = loadModel();
        boolean exist = false;
        UserModel.UserInfo targetUserInfo = null;
        out:
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equals(ftUserIp.getFtUserId(), userInfo.getUid().getUid())) {
                targetUserInfo = userInfo;
                List<UserModel.IP> ips = userInfo.getIps();
                if (ips == null) break;
                for (UserModel.IP ip : ips) {
                    if (StringUtils.equalsIgnoreCase(ftUserIp.getIpAddress(), ip.getIp())) {
                        exist = true;
                        break out;
                    }
                }
            }
        }
        if (targetUserInfo == null) return ResultDtoTool.buildError("添加失败，没有此用户");
        if (exist) return ResultDtoTool.buildError("添加失败，用户已有此IP");

        UserModel.IP target = new UserModel.IP();
        CfgModelConverter.convertTo(ftUserIp, target);
        List<UserModel.IP> ips = targetUserInfo.getIps();
        if (ips == null) {
            ips = new ArrayList<>();
            targetUserInfo.setIps(ips);
        }
        ips.add(target);
        save(model);
        return ResultDtoTool.buildSucceed("添加成功");
    }

    public ResultDto<String> update(FtUserIp ftUserIp) {
        UserModel.UserInfo targetUserInfo = null;
        UserModel.IP target = null;
        UserModel model = loadModel();
        out:
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equals(ftUserIp.getFtUserId(), userInfo.getUid().getUid())) {
                targetUserInfo = userInfo;
                List<UserModel.IP> ips = userInfo.getIps();
                if (ips == null) break;
                for (UserModel.IP ip : ips) {
                    if (StringUtils.equalsIgnoreCase(ftUserIp.getIpAddress(), ip.getIp())) {
                        target = ip;
                        break out;
                    }
                }
            }
        }
        if (targetUserInfo == null) return ResultDtoTool.buildError("更新失败，没有此用户");
        if (target == null) return ResultDtoTool.buildError("更新失败，用户没有此IP");
        CfgModelConverter.convertTo(ftUserIp, target);
        save(model);
        return ResultDtoTool.buildSucceed("更新成功");
    }

    @UpdateEntity
    public ResultDto<String> del(FtUserIp ftUserIp) {
        UserModel model = loadModel();
        List<UserModel.UserInfo> userInfos = model.getUserInfos();
        out:
        for (UserModel.UserInfo userInfo : userInfos) {
            if (StringUtils.equals(ftUserIp.getFtUserId(), userInfo.getUid().getUid())) {
                List<UserModel.IP> ips = userInfo.getIps();
                if (ips == null) break;
                for (UserModel.IP ip : ips) {
                    if (StringUtils.equalsIgnoreCase(ftUserIp.getIpAddress(), ip.getIp())) {
                        ips.remove(ip);
                        break out;
                    }
                }
            }
        }
        save(model);
        return ResultDtoTool.buildSucceed("删除成功");
    }

    private static final String CFG_FILE_NAME = "user.xml";

    private UserModel loadModel() {
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
    public String getEntityXml(CfgData curr, boolean isNew) {
        FtUserIp ftUserIp = (FtUserIp) curr;
        if(isNew){
            return null;
        }
        UserModel.UserInfo targetUserInfo = null;
        UserModel model = loadModel();
        for (UserModel.UserInfo userInfo : model.getUserInfos()) {
            if (StringUtils.equals(ftUserIp.getFtUserId(), userInfo.getUid().getUid())) {
                targetUserInfo = userInfo;
            }
        }
        if (targetUserInfo == null) return null;
        return XmlBeanUtil.toXml(targetUserInfo);

    }
}
