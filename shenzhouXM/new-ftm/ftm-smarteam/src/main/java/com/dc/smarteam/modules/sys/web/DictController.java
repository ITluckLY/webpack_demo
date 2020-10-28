/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.service.DictService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.Model;
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
 * 字典Controller
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/sys/dict")
public class DictController extends BaseController {

    @Resource(name = "DictServiceImpl")
    private DictService dictService;

    @ModelAttribute
    public Dict get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return dictService.get(id);
        } else {
            return new Dict();
        }
    }

//    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = {"list", ""})
    public ResultDto list(Dict dict, HttpServletRequest request, HttpServletResponse response) {
        List<String> typeList = new ArrayList<String>();
        Page<Dict> page = new Page<Dict>();
        try {
            typeList = dictService.findTypeList();
            page = dictService.findPage(new Page<Dict>(request, response), dict);
        } catch (Exception e) {
            String message = "查询字典信息失败！详情：" + e.getMessage();
            log.debug("message:{}",message);
            return ResultDtoTool.buildError(message,null);
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("typeList", typeList);
        resultMap.put("page", page);
        log.debug("resultMap:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = "form")
    public ResultDto<Map<String,Object>> form(Dict dict, Model model) {
        log.debug("dict:{}",dict);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("dict", dict);
        log.debug("resultMap:{}",resultMap);
        return ResultDtoTool.buildSucceed(resultMap);
    }

//    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "save")//@Valid
    public ResultDto save(Dict dict, Model model, RedirectAttributes redirectAttributes) {
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            return ResultDtoTool.buildError(message);
        }
        if (!beanValidator(model, dict)) {
            return form(dict, model);
        }
        dictService.save(dict);
        message = "保存字典'" + dict.getLabel() + "'成功";
        return ResultDtoTool.buildSucceed(message,null);
    }

//    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "delete")
    public ResultDto delete(Dict dict, RedirectAttributes redirectAttributes) {
        String message = "";
        if (Global.isDemoMode()) {
            message = "演示模式，不允许操作！";
            return ResultDtoTool.buildError(message,null);
        }
        dictService.delete(dict);
        message = "删除字典成功";
        return ResultDtoTool.buildSucceed(message,null);
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String type, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Dict dict = new Dict();
        dict.setType(type);
        List<Dict> list = dictService.findList(dict);
        for (int i = 0; i < list.size(); i++) {
            Dict e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("pId", e.getParentId());
            map.put("name", StringUtils.replace(e.getLabel(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public List<Dict> listData(@RequestParam(required = false) String type) {
        Dict dict = new Dict();
        dict.setType(type);
        return dictService.findList(dict);
    }

}
