/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sys.entity.Menu;
import com.dc.smarteam.modules.sys.service.SystemService;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单Controller
 *
 * @author ThinkGem
 * @version 2013-3-23
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

    @Resource(name = "SystemServiceImpl")
    private SystemService systemService;

    @ModelAttribute("menu")
    public Menu get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getMenu(id);
        } else {
            return new Menu();
        }
    }

//    @RequiresPermissions("sys:menu:view")
    @RequestMapping(value = {"list", ""})
    public ResultDto<Map<String,Object>> list(HttpServletRequest request) {
        List<Menu> list = new ArrayList<>();
        List<Menu> sourcelist = new ArrayList<>();
        try {
            list = Lists.newArrayList();
            sourcelist = systemService.findAllMenu();
            Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        } catch (Exception e) {
            String message = "查询菜单信息失败！详情：" + e.getMessage();
            log.debug("message:{}",message);
            return ResultDtoTool.buildError(message);
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        log.debug("resultMap:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("sys:menu:view")
    @RequestMapping(value = "form")
    public ResultDto<Map<String,Object>> form(Menu menu,String msg) {
        if (menu.getParent() == null || menu.getParent().getId() == null) {
            menu.setParent(new Menu(Menu.getRootId()));
        }
        menu.setParent(systemService.getMenu(menu.getParent().getId()));
        // 获取排序号，最末节点排序号+30
        if (StringUtils.isBlank(menu.getId())) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = systemService.findAllMenu();
            Menu.sortList(list, sourcelist, menu.getParentId(), false);
            if (list.size() > 0) {
                menu.setSort(list.get(list.size() - 1).getSort() + 30);
            }
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("menu", menu);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "save")
    public ResultDto save(Menu menu) {
        String message = "";
        if (!UserUtils.getUser().isAdmin()) {
            message = "越权操作，只有超级管理员才能添加或修改数据！";
            log.debug("message:{}", message);
            return ResultDtoTool.buildError(message);
        }
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            log.debug("message:{}", message);
            return ResultDtoTool.buildError(message);
        }
        if (!beanValidator(message, menu)) {
//            return form(menu, message);
            return ResultDtoTool.buildError(message);
        }
        systemService.saveMenu(menu);
        message = "保存菜单'" + menu.getName() + "'成功";
        log.debug("message:{}", message);
        return ResultDtoTool.buildSucceed(message,null);
    }

//    @RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "delete")
    public ResultDto delete(Menu menu, RedirectAttributes redirectAttributes) {
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            return ResultDtoTool.buildError(message);
        }
//		if (Menu.isRoot(id)){
//			addMessage(redirectAttributes, "删除菜单失败, 不允许删除顶级菜单或编号为空");
//		}else{
        systemService.deleteMenu(menu);
        message = "删除菜单成功";
//		}
        return ResultDtoTool. buildSucceed(message,null);
    }


    /**
     * 批量修改菜单排序
     */
//    @RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "updateSort")
    public ResultDto updateSort(String[] ids, Integer[] sorts) {
        log.debug("ids:{}, sorts:{}", ids, sorts);
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            log.debug("message:{}",message);
            return ResultDtoTool.buildError(message);
        }
        for (int i = 0; i < ids.length; i++) {
            Menu menu = new Menu(ids[i]);
            menu.setSort(sorts[i]);
            systemService.updateMenuSort(menu);
        }
        message = "保存菜单排序成功!";
        log.debug("message:{}",message);
        return ResultDtoTool.buildSucceed(message,null);
    }

    /**
     * isShowHide是否显示隐藏菜单
     *
     * @param extId
     * @param isShowHidden
     * @param response
     * @return
     */
//    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public ResultDto<List<Map<String, Object>>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String isShowHide, HttpServletResponse response) {
        log.debug("传入参数: extId: {}, isShowHide: {}",extId, isShowHide);
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list = systemService.findAllMenu();
        for (int i = 0; i < list.size(); i++) {
            Menu e = list.get(i);
            if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                if (isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")) {
                    continue;
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        log.debug("mapList: {}", mapList);
        return  ResultDtoTool.buildSucceed("成功",mapList);
    }
}
