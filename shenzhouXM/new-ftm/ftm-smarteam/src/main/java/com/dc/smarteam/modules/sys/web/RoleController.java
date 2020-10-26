/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.Collections3;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sys.entity.Office;
import com.dc.smarteam.modules.sys.entity.Role;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.service.OfficeService;
import com.dc.smarteam.modules.sys.service.SystemService;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.dc.smarteam.util.PublicRepResultTool;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色Controller
 *
 * @author ThinkGem
 * @version 2013-12-05
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleController extends BaseController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private OfficeService officeService;

    @ModelAttribute("role")
    public Role get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getRole(id);
        } else {
            return new Role();
        }
    }

//    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = {"list", ""})
    public Object list(Role role) {
        List<Role> list = new ArrayList<Role>();
        String message = "";
        try {
            list = systemService.findAllRole();
        } catch (Exception e) {
            message = "查询角色信息失败！详情：" + e.getMessage();
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        log.debug("resultMap:{}",resultMap);
        return PublicRepResultTool.sendResult("0000","成功",resultMap);
    }

//    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "form")
    public Object form(Role role) {
        log.debug("role:{}",role);
        if (role.getOffice() == null) {
            role.setOffice(UserUtils.getUser().getOffice());
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("role", role);
        resultMap.put("menuList", systemService.findAllMenu());
        resultMap.put("officeList", officeService.findAll());
        log.debug("resultMap:{}",resultMap);
        return PublicRepResultTool.sendResult("0000","成功",resultMap);
    }

//    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "save")
    public Object save(Role role) {
        log.debug("role:{}",role);
        String message = "";
        if (!UserUtils.getUser().isAdmin() && role.getSysData().equals(Global.YES)) {
            message = "越权操作，只有超级管理员才能修改此数据！";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        if (!beanValidator(message, role)) {
//            return form(role, model);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        if (!"true".equals(checkName(role.getOldName(), role.getName()))) {
            message = "保存角色'" + role.getName() + "'失败, 角色名已存在";
            log.debug("message:{}",message);
//            return form(role, model);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        if (!"true".equals(checkEnname(role.getOldEnname(), role.getEnname()))) {
            message = "保存角色'" + role.getName() + "'失败, 英文名已存在";
            log.debug("message:{}",message);
//            return form(role, model);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        systemService.saveRole(role);
        message = "保存角色'" + role.getName() + "'成功";
        log.debug("message:{}",message);
        return PublicRepResultTool.sendResult("0000",message,null);
    }

    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "delete")
    public Object delete(Role role) {
        log.debug("role:{}",role);
        String message = "";
        if (!UserUtils.getUser().isAdmin() && role.getSysData().equals(Global.YES)) {
            message = "越权操作，只有超级管理员才能修改此数据！";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            log.debug("message:{}",message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
//		if (Role.isAdmin(id)){
//			addMessage(redirectAttributes, "删除角色失败, 不允许内置角色或编号空");
////		}else if (UserUtils.getUser().getRoleIdList().contains(id)){
////			addMessage(redirectAttributes, "删除角色失败, 不能删除当前用户所在角色");
//		}else{
        systemService.deleteRole(role);
        message = "删除角色成功";
//		}
        log.debug("message:{}",message);
        return PublicRepResultTool.sendResult("0000",message,null);
    }

    /**
     * 角色分配页面
     *
     * @param role
     * @return
     */
    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "assign")
    public Object assign(Role role) {
        log.debug("role:{}",role);
        List<User> userList = systemService.findUser(new User(new Role(role.getId())));
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("userList", userList);
        log.debug("resultMap:{}",resultMap);
        return PublicRepResultTool.sendResult("0000","成功",resultMap);
    }

    /**
     * 角色分配 -- 打开角色分配对话框
     *
     * @param role
     * @param model
     * @return
     */
    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "usertorole")
    public String selectUserToRole(Role role, Model model) {
        List<User> userList = systemService.findUser(new User(new Role(role.getId())));
        model.addAttribute("role", role);
        model.addAttribute("userList", userList);
        model.addAttribute("selectIds", Collections3.extractToString(userList, "name", ","));
        model.addAttribute("officeList", officeService.findAll());
        return "modules/sys/selectUserToRole";
    }

    /**
     * 角色分配 -- 根据部门编号获取用户列表
     *
     * @param officeId
     * @param response
     * @return
     */
    @RequiresPermissions("sys:role:view")
    @ResponseBody
    @RequestMapping(value = "users")
    public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        User user = new User();
        user.setOffice(new Office(officeId));
        Page<User> page = systemService.findUser(new Page<User>(1, -1), user);
        for (User e : page.getList()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("pId", 0);
            map.put("name", e.getName());
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 角色分配 -- 从角色中移除用户
     *
     * @param userId
     * @param roleId
     * @param redirectAttributes
     * @return
     */
//    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "outrole")
    public Object outrole(String userId, String roleId) {
        log.debug("userId:{}, roleId:{}",userId,roleId);
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        Role role = systemService.getRole(roleId);
        User user = systemService.getUser(userId);
        if (UserUtils.getUser().getId().equals(userId)) {
            message = "无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！";
        } else {
            if (user.getRoleList().size() <= 1) {
                message = "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。";
            } else {
                Boolean flag = systemService.outUserInRole(role, user);
                if (!flag) {
                    message =  "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！";
                } else {
                    message =  "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！";
                    log.debug("message:{}", message);
                    return PublicRepResultTool.sendResult("0000",message,null);
                }
            }
        }
        log.debug("message:{}", message);
        return PublicRepResultTool.sendResult("9999",message,null);
    }

    /**
     * 角色分配
     *
     * @param role
     * @param idsArr
     * @return
     */
//    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "assignrole")
    public Object assignRole(Role role, String[] idsArr) {
        log.debug("role:{}, idsArr:{}", role, idsArr);
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            log.debug("message:{}", message);
            return PublicRepResultTool.sendResult("9999",message,null);
        }
        StringBuilder msg = new StringBuilder();
        int newNum = 0;
        for (int i = 0; i < idsArr.length; i++) {
            User user = systemService.assignUserToRole(role, systemService.getUser(idsArr[i]));
            if (null != user) {
                msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
                newNum++;
            }
        }
        message = "已成功分配 " + newNum + " 个用户" + msg;
        log.debug("message:{}", message);
        return PublicRepResultTool.sendResult("0000",message,null);
    }

    /**
     * 验证角色名是否有效
     *
     * @param oldName
     * @param name
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "checkName")
    public String checkName(String oldName, String name) {
        if (name != null && name.equals(oldName)) {
            return "true";
        }
        if (name != null && systemService.getRoleByName(name) == null) {
            return "true";
        }
        return "false";
    }

    /**
     * 验证角色英文名是否有效
     *
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "checkEnname")
    public String checkEnname(String oldEnname, String enname) {
        if (enname != null && enname.equals(oldEnname)) {
            return "true";
        }
        if (enname != null && systemService.getRoleByEnname(enname) == null) {
            return "true";
        }
        return "false";
    }

}
