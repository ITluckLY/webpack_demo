package com.dc.smarteam.modules.monitor.fttransformonitor.web;

import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.file.service.BizFileService;
import com.dc.smarteam.modules.monitor.fttransformonitor.entity.FtTransforMonitor;
import com.dc.smarteam.modules.monitor.fttransformonitor.service.FtTransforMonitorService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lvchuan on 2016/8/1.
 */
@Slf4j
@RestController
@RequestMapping(value = "${adminPath}/monitor/ftTransforMonitor")
public class FtTransforMonitorController extends BaseController {

    @Autowired
    private FtTransforMonitorService ftTransforMonitorService;

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = {"transforstatistic"})
    public String transforstatistic(FtTransforMonitor ftTransforMonitor, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

//        Page<FtTransforMonitor> page = ftTransforMonitorService.findPage(new Page<FtTransforMonitor>(request, response),ftTransforMonitor);
//        List<FtTransforMonitor> list=ftTransforMonitorService.findTransforstatistic(new FtTransforMonitor());
//        page.setList(list);
//        model.addAttribute("page", page);
        return "modules/monitor/transforMonitor/ftTransforStatistic";
    }


    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = {"transforSuccess"})
    public String transforSuccess(FtTransforMonitor ftTransforMonitor, Model model) throws Exception {
        model.addAttribute("ftTransforMonitor", ftTransforMonitor);
        return "modules/monitor/transforMonitor/ftTransforSuccess";
    }

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = {"transforFial"})
    public String transforFial(FtTransforMonitor ftTransforMonitor, Model model) throws Exception {
        model.addAttribute("ftTransforMonitor", ftTransforMonitor);
        return "modules/monitor/transforMonitor/ftTransforFail";
    }

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = "getTransforDetail")
    @ResponseBody
    public List<FtTransforMonitor> getTransforDetail(String nodeName, String sysname) throws Exception {
        FtTransforMonitor ftTransforMonitor = new FtTransforMonitor();
        ftTransforMonitor.setNodeName(nodeName);
        ftTransforMonitor.setSysname(sysname);
        List<FtTransforMonitor> transdetail = ftTransforMonitorService.findTransfordetail(ftTransforMonitor);
        return transdetail;
    }

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = "findDownloadNumberPerHour")
    @ResponseBody
    public List<FtTransforMonitor> findDownloadNumberPerHour(String nodeName, String sysname) throws Exception {
        FtTransforMonitor ftTransforMonitor = new FtTransforMonitor();
        ftTransforMonitor.setNodeName(nodeName);
        ftTransforMonitor.setSysname(sysname);
        List<FtTransforMonitor> numberPerHour = ftTransforMonitorService.findDownloadNumberPerHour(ftTransforMonitor);
        return numberPerHour;
    }

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = "findUploadNumberPerHour")
    @ResponseBody
    public List<FtTransforMonitor> findUploadNumberPerHour(String nodeName, String sysname) throws Exception {
        FtTransforMonitor ftTransforMonitor = new FtTransforMonitor();
        ftTransforMonitor.setNodeName(nodeName);
        ftTransforMonitor.setSysname(sysname);
        List<FtTransforMonitor> numberPerHour = ftTransforMonitorService.findUploadNumberPerHour(ftTransforMonitor);
        return numberPerHour;
    }

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = "findUploadFailPerHour")
    @ResponseBody
    public List<FtTransforMonitor> findUploadFailPerHour(String nodeName, String sysname) throws Exception {
        FtTransforMonitor ftTransforMonitor = new FtTransforMonitor();
        ftTransforMonitor.setNodeName(nodeName);
        ftTransforMonitor.setSysname(sysname);
        List<FtTransforMonitor> numberPerHour = ftTransforMonitorService.findUploadFailPerHour(ftTransforMonitor);
        return numberPerHour;
    }

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = "findDownloadFailPerHour")
    @ResponseBody
    public List<FtTransforMonitor> findDownloadFailPerHour(String nodeName, String sysname) throws Exception {
        FtTransforMonitor ftTransforMonitor = new FtTransforMonitor();
        ftTransforMonitor.setNodeName(nodeName);
        ftTransforMonitor.setSysname(sysname);
        List<FtTransforMonitor> numberPerHour = ftTransforMonitorService.findDownloadFailPerHour(ftTransforMonitor);
        return numberPerHour;
    }

    /*@RequiresPermissions("TransforMonitor:ftTransforMonitor:view")*/
    @GetMapping(value = "/findTransDetail")
    public ResultDto<JSONObject> findTransDetail(@RequestParam(name = "nodeName", required = false) String nodeName,
                                                 @RequestParam(name = "nodeName", required = false) String sysname) throws Exception {

        long l1 = System.currentTimeMillis();
        long uploadTotalForSucc = ftTransforMonitorService.findUploadTotalForSucc(nodeName);
        long uploadTotalForFail = ftTransforMonitorService.findUploadTotalForFail(nodeName);
        long downloadTotalForSucc = ftTransforMonitorService.findDownloadTotalForSucc(nodeName);
        long downloadTotalForFail = ftTransforMonitorService.findDownloadTotalForFail(nodeName);
        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l1);

        JSONObject statistic = new JSONObject();
        statistic.put("upsucc", uploadTotalForSucc);
        statistic.put("upfail", uploadTotalForFail);
        statistic.put("downsucc", downloadTotalForSucc);
        statistic.put("downfail", downloadTotalForFail);
        return ResultDtoTool.buildSucceed("success", statistic);
    }

}
