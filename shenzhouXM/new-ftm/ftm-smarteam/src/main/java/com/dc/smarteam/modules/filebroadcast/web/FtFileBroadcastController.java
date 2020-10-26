/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.filebroadcast.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.filebroadcast.entity.FtFileBroadcast;
import com.dc.smarteam.modules.filebroadcast.service.FtFileBroadcastService;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
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

/**
 * 文件广播Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/filebroadcast/ftFileBroadcast")
public class FtFileBroadcastController extends BaseController {

    @Autowired
    private FtFileBroadcastService ftFileBroadcastService;

    @ModelAttribute
    public FtFileBroadcast get(@RequestParam(required = false) String id) {
        FtFileBroadcast entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = ftFileBroadcastService.get(id);
        }
        if (entity == null) {
            entity = new FtFileBroadcast();
        }
        return entity;
    }

    @RequiresPermissions("filebroadcast:ftFileBroadcast:view")
    @RequestMapping(value = {"list", ""})
    public String list(FtFileBroadcast ftFileBroadcast, HttpServletRequest request, HttpServletResponse response, Model model) {

        //20160708新增
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }

        Page<FtFileBroadcast> page = ftFileBroadcastService.findPage(new Page<FtFileBroadcast>(request, response), ftFileBroadcast);
        model.addAttribute("page", page);
        return "modules/filebroadcast/ftFileBroadcastList";
    }

    @RequiresPermissions("filebroadcast:ftFileBroadcast:view")
    @RequestMapping(value = {"target"})
    public String target(FtFileBroadcast ftFileBroadcast, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<FtFileBroadcast> page = ftFileBroadcastService.findPage(new Page<FtFileBroadcast>(request, response), ftFileBroadcast);
        model.addAttribute("page", page);
        return "modules/filebroadcast/ftFileBroadcastTarget";
    }

    @RequiresPermissions("filebroadcast:ftFileBroadcast:view")
    @RequestMapping(value = "form")
    public String form(FtFileBroadcast ftFileBroadcast, Model model) {
        model.addAttribute("ftFileBroadcast", ftFileBroadcast);
        return "modules/filebroadcast/ftFileBroadcastForm";
    }

    @RequiresPermissions("filebroadcast:ftFileBroadcast:edit")
    @RequestMapping(value = "save")
    public String save(FtFileBroadcast ftFileBroadcast, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, ftFileBroadcast)) {
            return form(ftFileBroadcast, model);
        }
        ftFileBroadcastService.save(ftFileBroadcast);
        addMessage(redirectAttributes, "保存文件广播成功");
        return "redirect:" + Global.getAdminPath() + "/filebroadcast/ftFileBroadcast/?repage";
    }

    @RequiresPermissions("filebroadcast:ftFileBroadcast:edit")
    @RequestMapping(value = "delete")
    public String delete(FtFileBroadcast ftFileBroadcast, RedirectAttributes redirectAttributes) {
        ftFileBroadcastService.delete(ftFileBroadcast);
        addMessage(redirectAttributes, "删除文件广播成功");
        return "redirect:" + Global.getAdminPath() + "/filebroadcast/ftFileBroadcast/?repage";
    }

}