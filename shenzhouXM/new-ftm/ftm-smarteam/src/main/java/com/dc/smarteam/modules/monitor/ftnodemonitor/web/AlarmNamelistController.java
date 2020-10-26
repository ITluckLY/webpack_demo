package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitorMsgEmail;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorMsgEmailService;
import com.dc.smarteam.util.PublicRepResultTool;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置-告警配置-告警名单
 * <p>
 * Created by huangzbb on 2017/11/2.
 */
@Log4j2
@RestController
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor")
public class AlarmNamelistController {

    @Resource(name = "FtNodeMonitorMsgEmailServiceImpl")
    private FtNodeMonitorMsgEmailService ftNodeMonitorMsgEmailService;

    @RequestMapping(value = "addMsgAndEmailList", produces = "application/json;charset=UTF-8")
    public Object addMsgAndEmailList(HttpServletRequest request, HttpServletResponse response){
        List<FtNodeMonitorMsgEmail> list;
        int total = 0;
        Map<String,Object>  resultMap = new HashMap<>();
        try {
            int pageNo  = Integer.valueOf(request.getParameter("pageNo"));
            int pageSize = Integer.valueOf(request.getParameter("pageSize"));
            list = ftNodeMonitorMsgEmailService.getAddMsgAndEmailList(pageNo, pageSize);
            total = ftNodeMonitorMsgEmailService.getAddMsgAndEmailTotal();
            resultMap.put("list",list);
            resultMap.put("total",total);
            if(log.isDebugEnabled()){
                log.debug("查询的告警名单列表: "+resultMap);
            }
        } catch (Exception e) {
            return "查询告警名单失败！详情：" + e.getMessage();
        }

        return PublicRepResultTool.sendResult("0000","成功",resultMap);
    }

    @RequestMapping(value = "addMsgAndEmailForm", produces = "application/json;charset=UTF-8")
    public Object addMsgAndEmailForm(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail) throws Exception {
        if (ftNodeMonitorMsgEmail.getId() != null) {
            ftNodeMonitorMsgEmail = ftNodeMonitorMsgEmailService.getFtNodeMonitorMsgEmailById(ftNodeMonitorMsgEmail.getId());
        }
        if(log.isDebugEnabled()){
            log.debug("查询的告警名单信息: "+ftNodeMonitorMsgEmail);
        }
        return PublicRepResultTool.sendResult("0000","成功",ftNodeMonitorMsgEmail);
    }


    @RequestMapping(value = "saveMsgEmail", produces = "application/json;charset=UTF-8")
    public Object addMsgAndEmailSave(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail) throws Exception {
        String message = "";
        if (ftNodeMonitorMsgEmail.getId() == null) {
            ftNodeMonitorMsgEmail.setId(IdGen.uuid());
            int change = ftNodeMonitorMsgEmailService.save(ftNodeMonitorMsgEmail);
            if(change>0) {
                message = "保存告警名单成功";
            }
        } else {
            int change = ftNodeMonitorMsgEmailService.save(ftNodeMonitorMsgEmail);
            if(change>0) {
                message = "更新告警名单成功";
            }
        }
        return PublicRepResultTool.sendResult("0000",message,null);
    }

    @RequestMapping(value = "deleteMsgEmail", produces = "application/json;charset=UTF-8")
    public Object deleteMsgEmail(FtNodeMonitorMsgEmail ftNodeMonitorMsgEmail) throws Exception {
        String message = "";
        if (ftNodeMonitorMsgEmail.getId() != null) {
            int change = ftNodeMonitorMsgEmailService.delete(ftNodeMonitorMsgEmail);
            if(change>0){
                message = "删除告警名单成功";
            }
        }
        return PublicRepResultTool.sendResult("0000",message,null);
    }
}
