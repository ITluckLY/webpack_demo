/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sys.entity.Office;
import com.dc.smarteam.modules.sys.entity.Role;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.service.SystemService;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 *
 * @author ThinkGem
 * @version 2013-8-29
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

    @Resource(name = "SystemServiceImpl")
    private SystemService systemService;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getUser(id);
        } else {
            return new User();
        }
    }
//
//    @RequiresPermissions("sys:user:view")
//    @RequestMapping(value = {"index"})
//    public String index(User user, Model model) {
//        return "modules/sys/userIndex";
//    }

//    @RequiresPermissions("sys:user:view")1
    @RequestMapping(value = {"list", ""})
    public ResultDto<Map<String,Object>> list(User user, HttpServletRequest request, HttpServletResponse response) {
        log.debug("传入参数: user: {}", user);
        Page<User> page = new Page<User>();
        Map<String,Object>  resultMap = new HashMap<>();

        try {
            page = systemService.findUser(new Page<User>(request, response), user);
            resultMap.put("page", page);
        } catch (Exception e) {
            String message = "查询用户信息失败！详情：" + e.getMessage();
            log.debug("message:{}",message);
            return ResultDtoTool.buildError(message);
        }
        log.debug("用户信息列表:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }


//    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "form")
    public ResultDto<Map<String,Object>> form(String message, User user) {
        log.debug("参数 message:{} user:{}", message, user);
        if (user.getCompany() == null || user.getCompany().getId() == null) {
            user.setCompany(UserUtils.getUser().getCompany());
        }
        if (user.getOffice() == null || user.getOffice().getId() == null) {
            user.setOffice(UserUtils.getUser().getOffice());
        }

        Map<String,Object>  resultMap = new HashMap<>();
        resultMap.put("user", user);
        resultMap.put("allRoles", systemService.findAllRole());
        log.debug("用户信息:{}",resultMap);
        if(StringUtils.isNotBlank(message)){
            return ResultDtoTool.buildError(message);
        }
        return ResultDtoTool.buildSucceed("成功",resultMap);
    }

//    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "editForm")
    public ResultDto<Map<String,Object>> editForm(User user) {
        log.debug("参数 user:{}", user);
        if (user.getCompany() == null || user.getCompany().getId() == null) {
            user.setCompany(UserUtils.getUser().getCompany());
        }
        if (user.getOffice() == null || user.getOffice().getId() == null) {
            user.setOffice(UserUtils.getUser().getOffice());
        }
        Map<String,Object>  resultMap = new HashMap<>();
        resultMap.put("user", user);
        resultMap.put("allRoles", systemService.findAllRole());
        log.debug("用户信息:{}", JSON.toJSONString(resultMap));
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "save")
    public ResultDto save(User user, HttpServletRequest request) {
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            log.debug("message:{}",message);
            return ResultDtoTool.buildError(message);
        }
        // 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
        user.setCompany(new Office(request.getParameter("company.id")));
        user.setOffice(new Office(request.getParameter("office.id")));
        // 如果新密码为空，则不更换密码
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(systemService.entryptPassword(user.getNewPassword()));
        }
        if (!beanValidator(message, user)) {
            return form(message,user);
        }
        if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
            message = "保存用户'" + user.getLoginName() + "'失败，登录名已存在";
            log.debug("message:{}",message);
            return form(message,user);
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        List<String> roleIdList = user.getRoleIdList();
        for (Role r : systemService.findAllRole()) {
            if (roleIdList.contains(r.getId())) {
                roleList.add(r);
            }
        }
        user.setRoleList(roleList);
        // 保存用户信息
        systemService.saveUser(user);
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
        }
        message = "保存用户'" + user.getLoginName() + "'成功";
        log.debug("message:{}",message);
        return ResultDtoTool.buildSucceed(message,null);
    }

//    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "delete")
    public Object delete(User user) {
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            return ResultDtoTool.buildError(message,null);
        }
        if (UserUtils.getUser().getId().equals(user.getId())) {
            message = "删除用户失败, 不允许删除当前用户";
        } else if (User.isAdmin(user.getId())) {
            message = "删除用户失败, 不允许删除超级管理员用户";
        } else {
            systemService.deleteUser(user);
            message = "删除用户成功";
            return ResultDtoTool.buildSucceed(message,null);
        }
        log.debug("message:{}",message);
        return ResultDtoTool.buildError(message);
    }

    /**
     * 验证登录名是否有效          String username = (String)request.getSession().getAttribute("username");
     *
     * @param oldLoginName
     * @param loginName
     * @return
     */
//    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "checkLoginName")
    public String checkLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
            return "true";
        }
        if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
            return "true";
        }
        return "false";
    }

}
