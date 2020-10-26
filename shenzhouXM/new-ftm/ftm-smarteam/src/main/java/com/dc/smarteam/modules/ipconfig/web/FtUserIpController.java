/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.ipconfig.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.UserIpService;
import com.dc.smarteam.service.UserServiceI;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IP控制Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/ipconfig/ftUserIp")
public class FtUserIpController{

    @Resource(name = "UserIpServiceImpl")
    private UserIpService userIpService;
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;

//    @RequiresPermissions("ipconfig:ftUserIp:view")
    @RequestMapping(value = {"list", ""})
    public ResultDto list(FtUserIp ftUserIp, HttpServletRequest request, HttpServletResponse response) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            log.debug("节点信息失效，重新获取节点信息");
            return ResultDtoTool.buildError(ResultDtoTool.MISS_NODE_DATA,"节点信息失效，重新获取节点信息");
        }
        List<FtUserIp> list = new ArrayList<FtUserIp>();
        ResultDto<List<UserModel.UserInfo>> resultDto = userIpService.listAll();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<UserModel.UserInfo> userInfos = resultDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                List<UserModel.IP> ips = userInfo.getIps();
                if (ips == null) continue;
                for (UserModel.IP ip : ips) {
                    String status = ip.getStatus();
                    if ("forbidden".equalsIgnoreCase(status)) status = "0";
                    else if ("allowed".equalsIgnoreCase(status)) status = "1";
                    if (StringUtils.isNoneEmpty(ftUserIp.getIpAddress()) && !StringUtils.containsIgnoreCase(ip.getIp(), ftUserIp.getIpAddress())) continue;
                    if (StringUtils.isNoneEmpty(ftUserIp.getFtUserId()) && !StringUtils.containsIgnoreCase(userInfo.getUid().getUid(), ftUserIp.getFtUserId())) continue;
                    if (StringUtils.isNoneEmpty(ftUserIp.getState()) && !StringUtils.equals(ftUserIp.getState(), status)) continue;
                    FtUserIp userIp = new FtUserIp();
                    userIp.setIpAddress(ip.getIp());
                    userIp.setState(status);
                    userIp.setDes(ip.getDescribe());
                    userIp.setFtUserId(userInfo.getUid().getUid());
                    userIp.setId(String.valueOf(list.size()));
                    list.add(userIp);
                }
            }
        } else {
            log.error("error Message:{}",resultDto.getMessage());
            return ResultDtoTool.buildError(resultDto.getMessage());
        }
        Map<String,Object> resultMap = new HashMap<>();
        PageHelper.getInstance().getPage(ftUserIp.getClass(), request, response, resultMap, list);
        log.debug("resultMap:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("ipconfig:ftUserIp:view")
    @RequestMapping(value = "form")
    public ResultDto<Map<String,Object>> form() {
        List<FtUser> ftUserList = new ArrayList<FtUser>();
        ResultDto<List<UserModel.UserInfo>> userDto = userService.listAll();
        if (ResultDtoTool.isSuccess(userDto)) {
            List<UserModel.UserInfo> userInfos = userDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                FtUser ftUser = new FtUser();
                CfgModelConverter.convertTo(userInfo, ftUser);
                ftUserList.add(ftUser);
            }
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("ftUserList", ftUserList);
        log.debug("resultMap:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("ipconfig:ftUserIp:view")
    @RequestMapping(value = "editForm")
    public ResultDto editForm(FtUserIp ftUserIp) {
        ResultDto<List<UserModel.UserInfo>> resultDto = userIpService.listAll();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<UserModel.UserInfo> userInfos = resultDto.getData();
            out:
            for (UserModel.UserInfo userInfo : userInfos) {
                if (!StringUtils.equals(ftUserIp.getFtUserId(), userInfo.getUid().getUid())) continue;
                List<UserModel.IP> ips = userInfo.getIps();
                if (ips == null) continue;
                for (UserModel.IP ip : ips) {
                    if (StringUtils.equals(ftUserIp.getIpAddress(), ip.getIp())) {
                        String status = ip.getStatus();
                        if ("forbidden".equalsIgnoreCase(status)) status = "0";
                        else if ("allowed".equalsIgnoreCase(status)) status = "1";
                        ftUserIp.setIpAddress(ip.getIp());
                        ftUserIp.setState(status);
                        ftUserIp.setDes(ip.getDescribe());
                        ftUserIp.setFtUserId(userInfo.getUid().getUid());
                        ftUserIp.setId("1111");
                        break out;
                    }
                }
            }
        } else {
            log.error(resultDto.getMessage());
            return ResultDtoTool.buildError(resultDto.getMessage());
        }
        log.debug("ftUserIp:{}",ftUserIp);
        return ResultDtoTool.buildSucceed(ftUserIp);
    }

//    @RequiresPermissions("ipconfig:ftUserIp:edit")
    @RequestMapping(value = "save")
    public ResultDto save(FtUserIp ftUserIp) {
        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftUserIp.getId())) {
            resultDto = userIpService.add(ftUserIp);
        } else {
            resultDto = userIpService.update(ftUserIp);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            return ResultDtoTool.buildError(resultDto.getMessage());
        }
        return ResultDtoTool.buildSucceed("保存IP控制成功",null);
    }

//    @RequiresPermissions("ipconfig:ftUserIp:edit")
    @RequestMapping(value = "delete")
    public ResultDto delete(FtUserIp ftUserIp) {
        ResultDto<String> resultDto = userIpService.del(ftUserIp);
        if (!ResultDtoTool.isSuccess(resultDto)) {
            return ResultDtoTool.buildError(resultDto.getMessage());
        }
        return ResultDtoTool.buildSucceed("删除IP控制成功",null);
    }

}