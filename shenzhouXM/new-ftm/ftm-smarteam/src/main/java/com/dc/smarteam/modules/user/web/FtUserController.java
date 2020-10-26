/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.user.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.UserServiceI;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/user/ftUser")
public class FtUserController extends BaseController {
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;

//    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = {"list", ""})
    public Object list(FtUser ftUser, HttpServletRequest request, HttpServletResponse response,Model model, RedirectAttributes redirectAttributes) {
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        log.debug("ftServiceNode:{}", ftServiceNode);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return ResultDtoTool.buildError(ResultDtoTool.MISS_NODE_DATA,"系统名称数据缺失");
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
           return ResultDtoTool.buildError(resultDto.getMessage());
        }
        Map<String,Object>  resultMap = new HashMap<>();
        PageHelper.getInstance().getPage(FtUser.class, request, response,resultMap, list);
        log.debug("resultMap:{}", resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("user0:ftUser:view")
//    @RequestMapping(value = "addPage")
//    public String addPage(FtUser ftUser, Model model) {
////        model.addAttribute("ftUser", ftUser);
//        return "modules/user/ftUserForm";
//    }

//    @RequiresPermissions("user0:ftUser:view")
    @RequestMapping(value = "form")
    public Object form(FtUser ftUser) {
        log.debug("ftUser:{}",ftUser);
        ResultDto<UserModel.UserInfo> resultDto = userService.selByName(ftUser);
        if (ResultDtoTool.isSuccess(resultDto)) {
            UserModel.UserInfo userInfo = resultDto.getData();
            CfgModelConverter.convertTo(userInfo, ftUser);
            if (ftUser.getName() != null) {
                ftUser.setId("111111");
            }
        } else {
            String message = resultDto.getMessage();
            log.error("message:{}",message);
            return ResultDtoTool.buildError(message);
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("ftUser", ftUser);
        log.debug("resultMap:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("user0:ftUser:edit")
    @RequestMapping(value = "save")
    public Object save(FtUser ftUser) {
//        if (!beanValidator(model, ftUser)) {
//            return form(ftUser, model, request);
//        }
        log.debug("ftUser:{}", ftUser);
        String message = "";

        ftUser.setUserDir("/" + ftUser.getName());
        ftUser.setPermession("A");

        ResultDto<String> resultDto;
        if (StringUtils.isEmpty(ftUser.getId())) {
            resultDto = userService.add(ftUser);
        } else {
            resultDto = userService.update(ftUser);
        }
        if (ResultDtoTool.isSuccess(resultDto)) {
            message = "保存用户管理成功";
            log.debug("message:{}", message);
        } else {
            message = resultDto.getMessage();
            log.debug("message:{}", message);
            ResultDtoTool.buildError(message);
        }
        return ResultDtoTool.buildSucceed(message,null);
    }

//    @RequiresPermissions("user0:ftUser:edit")
    @RequestMapping(value = "delete")
    public Object delete(FtUser ftUser) {
        log.debug("ftUser:{}", ftUser);
        String message = "";
        ResultDto<String> resultDto = userService.del(ftUser);
        if (ResultDtoTool.isSuccess(resultDto)) {
            message = "删除用户管理成功";
            log.debug("message:{}", message);
        } else {
            message = resultDto.getMessage();
            log.debug("message:{}", message);
            ResultDtoTool.buildError(message);
        }
        return ResultDtoTool.buildSucceed(message,null);
    }


//    @RequiresPermissions("servicenode:ftServiceNode:view")
//    @RequestMapping(value = "otherConf")
//    public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        String getAllStr = MessageFactory.getInstance().user(new FtUser(), "print");//生成查询报文
//
//        FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
//        return "modules/user/ftUserOtherConf";
//    }
//
//
//    @RequiresPermissions("user0:ftUser:view")
//    @RequestMapping(value = "confComp")
//    public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
//        FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
//        String getAllStr = MessageFactory.getInstance().cfgSync("user", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
//        String getOtherAllStr = MessageFactory.getInstance().user(new FtUser(), "print");//生成查询报文
//        FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
//        return "modules/user/ftUserConfComp";
//    }
//
//    @RequiresPermissions("user0:ftUser:view")
//    @RequestMapping(value = {"catchFileCfg"})
//    @ResponseBody
//    public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
//        return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
//    }
}