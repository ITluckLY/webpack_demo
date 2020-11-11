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
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.model.UserAuthModel;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.FileAuthService;
import com.dc.smarteam.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 用户管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/user/ftUser")
public class FtUserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private FileAuthService fileAuthService;

    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtUser ftUser, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        ResultDto<List<UserModel.UserInfo>> resultDto = userService.listAll();
        List<FtUser> list = new ArrayList<FtUser>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<UserModel.UserInfo> userInfos = resultDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                if (StringUtils.isNoneEmpty(ftUser.getName()) && !StringUtils.containsIgnoreCase(userInfo.getUid().getUid(), ftUser.getName()))
                    continue;
                FtUser ftUser2 = new FtUser();
                CfgModelConverter.convertTo(userInfo, ftUser2);
                list.add(ftUser2);
            }
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        PageHelper.getInstance().getPage(FtUser.class, request, response, model, list);
        return "modules/user/ftUserList";
    }

    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = "addPage")
    public String addPage(FtUser ftUser, Model model) {
//        model.addAttribute("ftUser", ftUser);
        return "modules/user/ftUserForm";
    }

    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = "form")
    public String form(FtUser ftUser, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<UserModel.UserInfo> resultDto = userService.selByName(ftUser);
        if (ResultDtoTool.isSuccess(resultDto)) {
            UserModel.UserInfo userInfo = resultDto.getData();
            CfgModelConverter.convertTo(userInfo, ftUser);
            if (ftUser.getName() != null) {
                ftUser.setId("111111");
            }
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/user/ftUser/?repage";
        }
        model.addAttribute("ftUser", ftUser);
        return "modules/user/ftUserEditForm";
    }

    @RequiresPermissions("user0:ftUser:edit")
    @RequestMapping(value = "save")
    public String save(FtUser ftUser, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//        if (!beanValidator(model, ftUser)) {
//            return form(ftUser, model, request);
//        }
        ftUser.setUserDir("/" + ftUser.getName());
        ftUser.setPermession("A");

        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftUser.getId())) {
            resultDto = userService.add(ftUser);
        } else {
            resultDto = userService.update(ftUser);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存用户管理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/user/ftUser/?repage";
    }

    @RequiresPermissions("user0:ftUser:edit")
    @RequestMapping(value = "delete")
    public String delete(FtUser ftUser, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> resultDto = userService.del(ftUser);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除用户管理成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/user/ftUser/?repage";
    }


    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {

        String getAllStr = MessageFactory.getInstance().user(new FtUser(), "print");//生成查询报文

        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
        return "modules/user/ftUserOtherConf";
    }


    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = "confComp")
    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
        String getAllStr = MessageFactory.getInstance().cfgSync("user", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
        String getOtherAllStr = MessageFactory.getInstance().user(new FtUser(), "print");//生成查询报文
        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
        return "modules/user/ftUserConfComp";
    }

    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = {"catchFileCfg"})
    @ResponseBody
    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
    }
}