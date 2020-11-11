package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorMsgEmail;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorMsgEmailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * 系统设置-告警配置-告警名单
 * <p>
 * Created by huangzbb on 2017/11/2.
 */
@Controller
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor")
public class AlarmNamelistController extends BaseController {

    @Resource
    private FtNodeMonitorMsgEmailService ftNodeMonitorMsgEmailService;

    @RequiresPermissions("NodeMonitor:alarmNamelist:view")
    @RequestMapping(value = {"addMsgAndEmailList"})
    public String addMsgAndEmailList(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) throws Exception {
        Page<FtNodeMonitorMsgEmail> page = new Page<>();
        List<FtNodeMonitorMsgEmail> list;
        try {
            ftNodeMonitorMsgEmailService.findPage(new Page<FtNodeMonitorMsgEmail>(request, response), ftNodeMonitorMsgEmail);
            ftNodeMonitorMsgEmail.setPage(page);
            list = ftNodeMonitorMsgEmailService.findList(ftNodeMonitorMsgEmail);
            page.setList(list);
        } catch (Exception e) {
            addMessage(redirectAttributes, "查询告警名单失败！详情：" + e.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        model.addAttribute("page", page);

        return "modules/monitor/nodeMonitor/ftAddMsgAndEmailList";
    }

    @RequiresPermissions("NodeMonitor:alarmNamelist:view")
    @RequestMapping(value = {"addMsgAndEmailForm"})
    public String addMsgAndEmailForm(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        if (ftNodeMonitorMsgEmail.getId() != null) {
            ftNodeMonitorMsgEmail = ftNodeMonitorMsgEmailService.get(ftNodeMonitorMsgEmail.getId());
        }
        model.addAttribute("ftNodeMonitorMsgEmail", ftNodeMonitorMsgEmail);
        return "modules/monitor/nodeMonitor/ftAddMsgAndEmailForm";
    }


    @RequiresPermissions("NodeMonitor:alarmNamelist:edit")
    @RequestMapping(value = {"saveMsgEmail"})
    public String addMsgAndEmailSave(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {

        if (ftNodeMonitorMsgEmail.getId() == null) {
            ftNodeMonitorMsgEmail.setId(UUID.randomUUID().toString());
            ftNodeMonitorMsgEmailService.save(ftNodeMonitorMsgEmail);
            addMessage(redirectAttributes, "保存告警名单成功");
        } else {
            ftNodeMonitorMsgEmailService.save(ftNodeMonitorMsgEmail);
            addMessage(redirectAttributes, "更新告警名单成功");
        }
        return "redirect:" + Global.getAdminPath() + "/monitor/FtNodeMonitor/addMsgAndEmailList";
    }

    @RequiresPermissions("NodeMonitor:alarmNamelist:edit")
    @RequestMapping(value = {"deleteMsgEmail"})
    public String deleteMsgEmail(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        if (ftNodeMonitorMsgEmail.getId() != null) {
            ftNodeMonitorMsgEmailService.delete(ftNodeMonitorMsgEmail);
            addMessage(redirectAttributes, "删除告警名单成功");
        }
        return "redirect:" + Global.getAdminPath() + "/monitor/FtNodeMonitor/addMsgAndEmailList";
    }
}
