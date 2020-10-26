/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sys.entity.Office;
import com.dc.smarteam.modules.sys.service.OfficeService;
import com.dc.smarteam.modules.sys.service.impl.OfficeServiceImpl;
import com.dc.smarteam.util.PublicRepResultTool;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 机构Controller
 *
 * @author ThinkGem
 * @version 2013-5-15
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

    @Resource(name = "OfficeServiceImpl")
    private OfficeService officeService;

    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return officeService.get(id);
        } else {
            return new Office();
        }
    }

//    @RequiresPermissions("sys:office:view")
//    @RequestMapping(value = {""})
//    public String index(Office office, Model model) {
////        model.addAttribute("list", officeService.findAll());
//        return "modules/sys/officeIndex";
//    }
//
//    @RequiresPermissions("sys:office:view")
//    @RequestMapping(value = {"list"})
//    public String list(Office office, Model model) {
//        model.addAttribute("list", officeService.findList(office));
//        return "modules/sys/officeList";
//    }
//
//    @RequiresPermissions("sys:office:view")
//    @RequestMapping(value = "form")
//    public String form(Office office, Model model) {
//        User user = UserUtils.getUser();
//        if (office.getParent() == null || office.getParent().getId() == null) {
//            office.setParent(user.getOffice());
//        }
//        office.setParent(officeService.get(office.getParent().getId()));
//        if (office.getArea() == null) {
//            office.setArea(user.getOffice().getArea());
//        }
//        // 自动获取排序号
//        if (StringUtils.isBlank(office.getId()) && office.getParent() != null) {
//            int size = 0;
//            List<Office> list = officeService.findAll();
//            for (int i = 0; i < list.size(); i++) {
//                Office e = list.get(i);
//                if (e.getParent() != null && e.getParent().getId() != null
//                        && e.getParent().getId().equals(office.getParent().getId())) {
//                    size++;
//                }
//            }
//            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
//        }
//        model.addAttribute("office", office);
//        return "modules/sys/officeForm";
//    }
//
//    @RequiresPermissions("sys:office:edit")
//    @RequestMapping(value = "save")
//    public String save(Office office, Model model, RedirectAttributes redirectAttributes) {
//        if (Global.isDemoMode()) {
//            addMessage(redirectAttributes, "演示模式，不允许操作！");
//            return "redirect:" + adminPath + "/sys/office/";
//        }
//        if (!beanValidator(model, office)) {
//            return form(office, model);
//        }
//        officeService.save(office);
//
//        if (office.getChildDeptList() != null) {
//            Office childOffice = null;
//            for (String id : office.getChildDeptList()) {
//                childOffice = new Office();
//                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
//                childOffice.setParent(office);
//                childOffice.setArea(office.getArea());
//                childOffice.setType("2");
//                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
//                childOffice.setUseable(Global.YES);
//                officeService.save(childOffice);
//            }
//        }
//
//        addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
//        String id = "0".equals(office.getParentId()) ? "" : office.getParentId();
//        return "redirect:" + adminPath + "/sys/office/list?id=" + id + "&parentIds=" + office.getParentIds();
//    }
//
//    @RequiresPermissions("sys:office:edit")
//    @RequestMapping(value = "delete")
//    public String delete(Office office, RedirectAttributes redirectAttributes) {
//        if (Global.isDemoMode()) {
//            addMessage(redirectAttributes, "演示模式，不允许操作！");
//            return "redirect:" + adminPath + "/sys/office/list";
//        }
////		if (Office.isRoot(id)){
////			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构或编号空");
////		}else{
//        officeService.delete(office);
//        addMessage(redirectAttributes, "删除机构成功");
////		}
//        return "redirect:" + adminPath + "/sys/office/list?id=" + office.getParentId() + "&parentIds=" + office.getParentIds();
//    }

    /**
     * 获取机构JSON数据。
     *
     * @param extId    排除的ID
     * @param type     类型（1：公司；2：部门/小组/其它：3：用户）
     * @param grade    显示级别
     * @param response
     * @return
     */
//    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public Object treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type,
                                              @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {
        log.debug("传入参数: extId: {}, type: {}, grade: {}, isAll: {}",extId, type, grade, isAll);
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.findList(isAll);
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                if (type != null && "3".equals(type)) {
                    map.put("isParent", true);
                }
                mapList.add(map);
            }
        }
        log.debug("机构JSON数据: "+mapList);
        return PublicRepResultTool.sendResult("0000","成功",mapList);
    }
}
