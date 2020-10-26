/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.user.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.model.UserAuthModel;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.FileAuthService;
import com.dc.smarteam.service.UserServiceI;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础配置-用户管理-用户权限
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/user/ftUser")
public class FtUserDirAuthController extends BaseController {
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;
    @Resource
    private FileAuthService fileAuthService;

    @RequiresPermissions("user0:ftUserDirAuth:view")
    @RequestMapping(value = {"dirList"})
    public String dirList(UserAuthModel userAuth, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null || ftServiceNode.getSystemName() == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";//NOSONAR
        }

        ResultDto<List<FileModel.BaseFile>> resultDto = fileAuthService.listAll();
        List<UserAuthModel> userAuthList = new LinkedList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<FileModel.BaseFile> baseFiles = resultDto.getData();
            for (FileModel.BaseFile baseFile : baseFiles) {
                if (StringUtils.isNoneEmpty(userAuth.getPath()) && !StringUtils.containsIgnoreCase(baseFile.getName(), userAuth.getPath()))
                    continue;
                List<FileModel.Grant> grants = baseFile.getGrants();
                for (FileModel.Grant grant : grants) {
                    if (StringUtils.isNoneEmpty(userAuth.getUserName()) && !StringUtils.containsIgnoreCase(grant.getUser(), userAuth.getUserName()))
                        continue;
                    UserAuthModel userAuthModel = new UserAuthModel();
                    userAuthModel.setPath(baseFile.getName());
                    userAuthModel.setUserName(grant.getUser());
                    userAuthModel.setAuth(grant.getType());
                    userAuthList.add(userAuthModel);
                }
            }
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        PageHelper.getInstance().getPage(userAuth.getClass(), request, response, model, userAuthList);
        return "modules/user/ftUserDirList";
    }

    @RequiresPermissions("user0:ftUserDirAuth:edit")
    @RequestMapping(value = {"addUserDirAuth"})
    public String addUserDirAuth(UserAuthModel userAuth, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        ResultDto<List<UserModel.UserInfo>> resultDto = userService.listAll();
        List<FtUser> ftUserList = new ArrayList<>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<UserModel.UserInfo> userInfos = resultDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                FtUser ftUser2 = new FtUser();
                CfgModelConverter.convertTo(userInfo, ftUser2);
                ftUserList.add(ftUser2);
            }
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/user/ftUser/dirList";//NOSONAR
        }
        model.addAttribute("ftUserList", ftUserList);
        model.addAttribute("userAuth", userAuth);
        return "modules/user/addUserDirAuth";
    }

    @RequiresPermissions("user0:ftUserDirAuth:edit")
    @RequestMapping(value = {"saveUserDirAuth"})
    public String saveUserDirAuth(UserAuthModel userAuth, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        ResultDto<String> resultDto = fileAuthService.add(userAuth);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存目录权限成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/user/ftUser/dirList";
    }

    @RequiresPermissions("user0:ftUserDirAuth:edit")
    @RequestMapping(value = {"delUserAuth"})
    public String delUserAuth(UserAuthModel userAuth, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, userAuth)) {
            addMessage(redirectAttributes, "无权限操作");
            return "redirect:" + Global.getAdminPath() + "/user/ftUser/dirList";
        }

        ResultDto<String> resultDto = fileAuthService.del(userAuth);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除目录权限成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/user/ftUser/dirList";
    }

}