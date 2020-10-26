/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.auth.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FileModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.auth.entity.FtAuth;
import com.dc.smarteam.modules.auth.entity.FtAuthView;
import com.dc.smarteam.modules.auth.service.FtAuthService;
import com.dc.smarteam.modules.model.UserAuthModel;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.modules.user.service.FtUserService;
import com.dc.smarteam.service.FileAuthService;
import com.dc.smarteam.service.UserServiceI;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 用户管理Controller
 *
 * @author yangxc
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/auth/ftAuth")
public class FtAuthController extends BaseController {

    @Autowired
    private FtAuthService ftAuthService;
    @Autowired
    private FtUserService ftUserService;
    @Autowired
    private FileAuthService fileAuthService;

    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;


//    @ModelAttribute
//    public FtAuth get(@RequestParam(required = false) String d) {
//        FtAuth entity = null;
//        if (StringUtils.isNotBlank(id)) {
//            entity = ftAuthService.get(id);
//        }
//        if (entity == null) {
//            entity = new FtAuth();
//        }
//        return entity;
//    }

    @RequiresPermissions("auth:ftAuth:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtAuth ftAuth, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (ftServiceNode == null || ftServiceNode.getSystemName() == null) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        ResultDto<List<FileModel.BaseFile>> resultDto = fileAuthService.listAll();
        List<FtAuthView> authViewList = new ArrayList<FtAuthView>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String ftAuthName = ftAuth.getName();
            String permession = ftAuth.getPermession();
            List<FileModel.BaseFile> baseFiles = resultDto.getData();
            for (FileModel.BaseFile baseFile : baseFiles) {
                if (baseFile.getGrants() == null) continue;
                for (FileModel.Grant grant : baseFile.getGrants()) {
                    if (!StringUtils.isEmpty(ftAuthName) && !StringUtils.containsIgnoreCase(baseFile.getName(), ftAuthName))
                        continue;
                    if (!StringUtils.isEmpty(permession) && !StringUtils.containsIgnoreCase(grant.getUser(), permession))
                        continue;
                    FtAuthView authView = new FtAuthView();
                    authView.setName(baseFile.getName());
                    authView.setPath(baseFile.getPath());
                    authView.setUser(grant.getUser());
                    authView.setPermType(grant.getType());
                    authViewList.add(authView);
                }
            }
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(FtAuthView.class, request, response, model, authViewList);
        return "modules/auth/ftAuthList";
    }

    @RequiresPermissions("auth:ftAuth:view")
    @RequestMapping(value = "addPage")
    public String addPage(FtUser ftUser, FtAuth ftAuth, String username, String permissionTemp, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        ResultDto<List<UserModel.UserInfo>> resultDto = userService.listAll();
        List<FtUser> ftUserList = new ArrayList<FtUser>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            List<UserModel.UserInfo> userInfos = resultDto.getData();
            for (UserModel.UserInfo userInfo : userInfos) {
                FtUser ftUser2 = new FtUser();
                CfgModelConverter.convertTo(userInfo, ftUser2);
                ftUserList.add(ftUser2);
            }
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/auth/ftAuth/?repage";
        }

        ftAuth.setPath(ftServiceNode.getSystemName());
        model.addAttribute("permissionTemp", permissionTemp);
        model.addAttribute("ftUserList", ftUserList);
        model.addAttribute("username", username);
        model.addAttribute("ftAuth", ftAuth);
        return "modules/auth/ftAuthForm";
    }

    /**
     * 无修改操作，无用
     */
    @RequiresPermissions("auth:ftAuth:view")
    @RequestMapping(value = "form")
    public String form(FtAuth ftAuth, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        String selectOneStr = MessageFactory.getInstance().auth(ftAuth, "selByName"); //生成查询报文
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(selectOneStr, ftServiceNode, String.class);//发送报文
        JSONObject obj = null;
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            obj = JSONObject.fromObject(data);
            ftAuth = JsonToEntityFactory.getInstance().getFtAuth(obj);
        } else {
            String data = resultDto.getMessage();
            addMessage(redirectAttributes, data);
            return "redirect:" + Global.getAdminPath() + "/auth/ftAuth/?repage";
        }
        model.addAttribute("ftAuth", ftAuth);
        return "modules/auth/ftAuthEditForm";
    }

    @RequiresPermissions("auth:ftAuth:edit")
    @RequestMapping(value = "save")
    public String save(FtAuth ftAuth, String username, String permessionTemp, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!(ftAuth.getName().startsWith("/"))) {
            ftAuth.setName("/" + ftAuth.getName());
        }
        UserAuthModel userAuth = new UserAuthModel();
        userAuth.setUserName(username);
        userAuth.setPath(ftAuth.getName());
        userAuth.setAuth(permessionTemp);
        ResultDto<String> resultDto = fileAuthService.add(userAuth);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "保存用户目录成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/auth/ftAuth/?repage";
    }

    /**
     * 无修改操作，无用
     */
    @RequiresPermissions("auth:ftAuth:edit")
    @RequestMapping(value = "saveEdit")
    public String saveEdit(FtAuth ftAuth, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String editStr = MessageFactory.getInstance().auth(ftAuth, "update");
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(editStr, ftServiceNode, String.class);//发送报文
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "修改用户目录成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/auth/ftAuth/?repage";
    }

    @RequiresPermissions("auth:ftAuth:edit")
    @RequestMapping(value = "delete")
    public String delete(FtAuth ftAuth, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        UserAuthModel userAuth = new UserAuthModel();
        userAuth.setUserName(ftAuth.getName());
        userAuth.setPath(ftAuth.getPath());
        userAuth.setAuth(ftAuth.getPermession());
        ResultDto<String> resultDto = fileAuthService.del(userAuth);
        if (ResultDtoTool.isSuccess(resultDto)) {
            addMessage(redirectAttributes, "删除用户目录成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/auth/ftAuth/?repage";
    }

}