/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.ipconfig.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.ipconfig.entity.FtUserIp;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.UserIpService;
import com.dc.smarteam.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * IP控制Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/ipconfig/ftUserIp")
public class FtUserIpController extends BaseController {

    @Resource
    private UserIpService userIpService;
    @Resource
    private UserService userService;

    @RequiresPermissions("ipconfig:ftUserIp:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtUserIp ftUserIp, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
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
            addMessage(redirectAttributes, resultDto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(ftUserIp.getClass(), request, response, model, list);
        return "modules/ipconfig/ftUserIpList";
    }

    @RequiresPermissions("ipconfig:ftUserIp:view")
    @RequestMapping(value = "form")
    public String form(FtUserIp ftUserIp, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
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
        model.addAttribute("ftUserList", ftUserList);
        return "modules/ipconfig/ftUserIpForm";
    }

    @RequiresPermissions("ipconfig:ftUserIp:view")
    @RequestMapping(value = "editForm")
    public String editForm(FtUserIp ftUserIp, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
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
            addMessage(redirectAttributes, resultDto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/ipconfig/ftUserIp/?repage";
        }
        return "modules/ipconfig/ftUserIpEditForm";
    }

    @RequiresPermissions("ipconfig:ftUserIp:edit")
    @RequestMapping(value = "save")
    public String save(FtUserIp ftUserIp, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//        if (!beanValidator(model, ftUserIp)) {
//            return form(ftUserIp, model, request);
//        }
        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftUserIp.getId())) {
            resultDto = userIpService.add(ftUserIp);
        } else {
            resultDto = userIpService.update(ftUserIp);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存IP控制成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/ipconfig/ftUserIp/?repage";
    }

    @RequiresPermissions("ipconfig:ftUserIp:edit")
    @RequestMapping(value = "delete")
    public String delete(FtUserIp ftUserIp, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        ResultDto<String> resultDto = userIpService.del(ftUserIp);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除IP控制成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/ipconfig/ftUserIp/?repage";
    }

}