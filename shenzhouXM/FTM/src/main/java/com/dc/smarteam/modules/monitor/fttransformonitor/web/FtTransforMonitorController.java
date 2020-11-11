package com.dc.smarteam.modules.monitor.fttransformonitor.web;

import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.file.service.BizFileService;
import com.dc.smarteam.modules.monitor.fttransformonitor.entity.FtTransforMonitor;
import com.dc.smarteam.modules.monitor.fttransformonitor.service.FtTransforMonitorService;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lvchuan on 2016/8/1.
 */
@Controller
@RequestMapping(value = "${adminPath}/monitor/ftTransforMonitor")
public class FtTransforMonitorController extends BaseController {

    @Autowired
    private FtTransforMonitorService ftTransforMonitorService;
    @Autowired
    private BizFileService bizFileService;


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

    @RequiresPermissions("TransforMonitor:ftTransforMonitor:view")
    @RequestMapping(value = "findTransDetail")
    @ResponseBody
    public JSONObject findTransDetail(String nodeName, String sysname) throws Exception {
        /*List<FtTransforMonitor> downloadList = this.ftTransforMonitorService.findDownload();
        List<FtTransforMonitor>  downloadTotalListForNS = new ArrayList<FtTransforMonitor>();
        List<FtTransforMonitor>  downloadTotalForSucc = new ArrayList<FtTransforMonitor>();
        List<FtTransforMonitor>  downloadTotalForFail = new ArrayList<FtTransforMonitor>();
        for(FtTransforMonitor ftm : downloadList){
            if(*//*sysname.equalsIgnoreCase(ftm.getSysname()) &&*//* nodeName.equalsIgnoreCase(ftm.getNodeName())){
                downloadTotalListForNS.add(ftm);
            }
            if(*//*sysname.equalsIgnoreCase(ftm.getSysname()) &&*//* nodeName.equalsIgnoreCase(ftm.getNodeName())
                    && (("1".equals(ftm.getSuss())&&"1".equals(ftm.getLastPiece()))
                    ||"000000".equals(ftm.getFileMsgFlag()))){
                downloadTotalForSucc.add(ftm);
            }else if(*//*sysname.equalsIgnoreCase(ftm.getSysname()) &&*//* nodeName.equalsIgnoreCase(ftm.getNodeName())
                    && !(("1".equals(ftm.getSuss())&&"1".equals(ftm.getLastPiece()))
                    ||"000000".equals(ftm.getFileMsgFlag()))){
                downloadTotalForFail.add(ftm);
            }
        }
        List<FtTransforMonitor> uploadList = ftTransforMonitorService.findUpload();
        List<FtTransforMonitor>  uploadTotalListForNS = new ArrayList<FtTransforMonitor>();
        List<FtTransforMonitor>  uploadTotalForSucc = new ArrayList<FtTransforMonitor>();
        List<FtTransforMonitor>  uploadTotalForFail = new ArrayList<FtTransforMonitor>();
        for(FtTransforMonitor ftm : uploadList){
            if(*//*sysname.equalsIgnoreCase(ftm.getSysname()) &&*//* nodeName.equalsIgnoreCase(ftm.getNodeName())){
                uploadTotalListForNS.add(ftm);
            }
            if(*//*sysname.equalsIgnoreCase(ftm.getSysname()) &&*//* nodeName.equalsIgnoreCase(ftm.getNodeName())
                    &&  (("1".equals(ftm.getSuss())&&"1".equals(ftm.getLastPiece()))
                    ||"000000".equals(ftm.getFileMsgFlag())||"finish-000000".equalsIgnoreCase(ftm.getFileMsgFlag()))){
                uploadTotalForSucc.add(ftm);
            }else if(*//*sysname.equalsIgnoreCase(ftm.getSysname()) &&*//* nodeName.equalsIgnoreCase(ftm.getNodeName())
                    && !(("1".equals(ftm.getSuss())&&"1".equals(ftm.getLastPiece()))
                    ||"000000".equals(ftm.getFileMsgFlag())||"finish-000000".equalsIgnoreCase(ftm.getFileMsgFlag()))){
                uploadTotalForFail.add(ftm);
            }
        }
        JSONObject statistic =new JSONObject();
        statistic.put("up",uploadTotalListForNS.size());
        statistic.put("upsucc",uploadTotalForSucc.size());
        statistic.put("upfail",uploadTotalForFail.size());
        statistic.put("down",downloadTotalListForNS.size());
        statistic.put("downsucc",downloadTotalForSucc.size());
        statistic.put("downfail",downloadTotalForFail.size());*/

        long l1 = System.currentTimeMillis();
        //long uploadTotal = ftTransforMonitorService.findUploadTotal(nodeName);
        long uploadTotalForSucc = ftTransforMonitorService.findUploadTotalForSucc(nodeName);
        long uploadTotalForFail = ftTransforMonitorService.findUploadTotalForFail(nodeName);
        //long downloadTotal = ftTransforMonitorService.findDownloadTotal(nodeName);
        long downloadTotalForSucc = ftTransforMonitorService.findDownloadTotalForSucc(nodeName);
        long downloadTotalForFail = ftTransforMonitorService.findDownloadTotalForFail(nodeName);
        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l1);

        JSONObject statistic = new JSONObject();
        //statistic.put("up", uploadTotal);
        statistic.put("upsucc", uploadTotalForSucc);
        statistic.put("upfail", uploadTotalForFail);
        //statistic.put("down", downloadTotal);
        statistic.put("downsucc", downloadTotalForSucc);
        statistic.put("downfail", downloadTotalForFail);
        return statistic;
    }

}
