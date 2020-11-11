/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sysmanager.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sysinfo.service.FtSysInfoService;
import com.dc.smarteam.modules.sysmanager.entity.FtSysManager;
import com.dc.smarteam.modules.sysmanager.service.FtSysManagerService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统管理员Controller
 *
 * @author lvchuan
 * @version 2016-06-24
 */
@Controller
@RequestMapping(value = "${adminPath}/sysManager/ftSysManager")
public class FtSysManagerController extends BaseController {

    @Autowired
    private FtSysManagerService ftSysManagerService;
    @Autowired
    private FtSysInfoService ftSysInfoService;

    @ModelAttribute
    public FtSysManager get(@RequestParam(required = false) String id) {
        FtSysManager entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = ftSysManagerService.get(id);
        }
        if (entity == null) {
            entity = new FtSysManager();
        }
        return entity;
    }


    public String listToString(List list, char separator) {
        return org.apache.commons.lang.StringUtils.join(list.toArray(), separator);
    }

    @RequiresPermissions("sysManager:ftSysManager:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtSysManager ftSysManager, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<FtSysManager> page = ftSysManagerService.findPage(new Page<FtSysManager>(request, response), ftSysManager);
        model.addAttribute("page", page);
        List<FtSysManager> list = ftSysManagerService.findList(new FtSysManager());
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSystems(listToString(ftSysManagerService.findSystem(list.get(i)), ','));
            ftSysManagerService.save(list.get(i));
        }
        boolean name = false;
        boolean phone = false;
        boolean department = false;
        List<FtSysManager> list2 = new ArrayList<FtSysManager>();
        if (ftSysManager.getName() != null && !"".equals(ftSysManager.getName())) {
            name = true;
        }
        if (ftSysManager.getPhone() != null && !"".equals(ftSysManager.getPhone())) {
            phone = true;
        }
        if (ftSysManager.getDepartment() != null && !"".equals(ftSysManager.getDepartment())) {
            department = true;
        }
        if (name || phone || department) {
            for (int i = 0; i < list.size(); i++) {
                FtSysManager fsm = list.get(i);
                if (name && !fsm.getName().toLowerCase().contains(ftSysManager.getName().toLowerCase())) {
                    continue;
                }
                if (phone && !fsm.getPhone().toLowerCase().contains(ftSysManager.getPhone().toLowerCase())) {
                    continue;
                }
                if (department && !fsm.getDepartment().toLowerCase().contains(ftSysManager.getDepartment().toLowerCase())) {
                    continue;
                }
                list2.add(list.get(i));
            }
        } else {
            list2 = list;
        }
        page.setList(list2);
        page.setCount(list2.size());
        return "modules/sysmanager/ftSysManagerList";
    }

    @RequestMapping(value = "addPage")
    public String addPage(FtSysManager FtSysManager, Model model) {

        FtSysManager = new FtSysManager();
        model.addAttribute("FtSysManager", FtSysManager);
        return "modules/sysmanager/ftSysManagerForm";
    }

    @RequiresPermissions("sysManager:ftSysManager:view")
    @RequestMapping(value = "form")
    public String form(FtSysManager FtSysManager, Model model, HttpServletRequest request) {
//		List<FtServiceNode> nodes = ftServiceNodeService.findList(new FtServiceNode());//获取节点

//		FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
//
//		String selectOneStr = MessageFactory.getInstance().sysManager(FtSysManager, "selByName"); //生成查询报文
//		TCPAdapter tcpAdapter = new TCPAdapter();
//		String returnMsg = tcpAdapter.invoke(selectOneStr.getBytes(),ftServiceNode);//发送报文
//		JSONObject obj = JSONObject.fromObject(returnMsg);
//		FtSysManager = JsonToEntityFactory.getInstance().getFtSysManager(obj);
        model.addAttribute("FtSysManager", FtSysManager);
        return "modules/sysmanager/ftSysManagerEditForm";
    }

    @RequiresPermissions("sysManager:ftSysManager:edit")
    @RequestMapping(value = "save")
    public String save(FtSysManager FtSysManager, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, FtSysManager)) {
            return form(FtSysManager, model, request);
        }
//		ftAuthService.save(ftAuth);
//		FtServiceNode ftServiceNode = (FtServiceNode)request.getSession().getAttribute("ftServiceNode");
//
//		String addStr = MessageFactory.getInstance().sysManager(FtSysManager, "add");
//		List<FtServiceNode> nodes = ftServiceNodeService.findList(new FtServiceNode());//获取节点
//
//		TCPAdapter tcpAdapter = new TCPAdapter();
//		String returnMsg = tcpAdapter.invoke(addStr.getBytes(), ftServiceNode);//发送报文
        ftSysManagerService.save(FtSysManager);
        addMessage(redirectAttributes, "保存用户管理成功");
        return "redirect:" + Global.getAdminPath() + "/sysManager/ftSysManager/?repage";
    }

    @RequiresPermissions("sysManager:ftSysManager:edit")
    @RequestMapping(value = "saveEdit")
    public String saveEdit(FtSysManager FtSysManager, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, FtSysManager)) {
            return form(FtSysManager, model, request);
        }
//		ftAuthService.save(ftAuth);

//		String editStr = MessageFactory.getInstance().sysManager(FtSysManager, "update");
//		List<FtServiceNode> nodes = ftServiceNodeService.findList(new FtServiceNode());//获取节点

//		FtServiceNode ftServiceNode = (FtServiceNode)request.getSession().getAttribute("ftServiceNode");
//		TCPAdapter tcpAdapter = new TCPAdapter();
//		String returnMsg = tcpAdapter.invoke(editStr.getBytes(), ftServiceNode);//发送报文
        ftSysManagerService.save(FtSysManager);
        addMessage(redirectAttributes, "修改用户管理成功");
        return "redirect:" + Global.getAdminPath() + "/sysManager/ftSysManager/?repage";
    }

    @RequiresPermissions("sysManager:ftSysManager:edit")
    @RequestMapping(value = "delete")
    public String delete(FtSysManager ftSysManager, HttpServletRequest request, RedirectAttributes redirectAttributes) {
//		ftAuthService.delete(ftAuth);
//        FtServiceNode ftServiceNode = (FtServiceNode)request.getSession().getAttribute("ftServiceNode");
//
//        String deleteStr = MessageFactory.getInstance().sysManager(FtSysManager, "del");
//        TCPAdapter tcpAdapter = new TCPAdapter();
//        String returnMsg = tcpAdapter.invoke(deleteStr.getBytes(), ftServiceNode);//发送报文
//----------------------------------------------------------------------------------------------------
        //删除系统对应的管理员
//        List<FtSysInfo> sysList = ftSysInfoService.findList(new FtSysInfo());
//        StringBuilder sb = new StringBuilder(100);
//        for(FtSysInfo fsi : sysList){
//            if(fsi.getAdmin().contains(ftSysManager.getName())){
//                String[] split = fsi.getAdmin().split(",");
//                if(split.length > 1){
//                    for(int i=0;i<split.length;i++){
//                        if(!split[i].equalsIgnoreCase(fsi.getAdmin())&&(i<split.length-1)){
//                            sb.append(split[i]).append(",");
//                        }else if(!split[i].equalsIgnoreCase(fsi.getAdmin())&&(i==split.length-1)){
//                            sb.append(split[i]);
//                        }
//                    }
//                    fsi.setAdmin(sb.toString());
//                }else{
//                    fsi.setAdmin("");
//                }
//            }
//            ftSysInfoService.save(fsi);
//        }
        ftSysManagerService.delete(ftSysManager);
        addMessage(redirectAttributes, "删除用户管理成功");
        return "redirect:" + Global.getAdminPath() + "/sysManager/ftSysManager/?repage";
    }
}