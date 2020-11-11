package com.dc.smarteam.modules.client.web;


import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;

import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.client.entity.FtOperationLog;
import com.dc.smarteam.modules.client.service.FtOperationLogService;
import com.dc.smarteam.modules.sys.entity.OptTag;
import com.dc.smarteam.modules.sys.service.OptTagService;
import com.dc.smarteam.util.StringToXmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyfal
 *
 * 20180528
 */
@Controller
@RequestMapping(value = "${adminPath}/client/operate")
public class ClientOperateController extends BaseController {

    @Autowired
    private FtOperationLogService ftOperationLogService;
    @Autowired
    private OptTagService optTagService;

    @RequestMapping(value = "view")
    public String view(FtOperationLog ftOperationLog, HttpServletRequest request, HttpServletResponse response, Model model) {
        if(ftOperationLog.getBeginDate() == null){
            ftOperationLog.setBeginDate(DateHelper.getStartDate());
        }
        if(ftOperationLog.getEndDate() == null){
            ftOperationLog.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (ftOperationLog.getBeginDate() != null) {
            String beginDate = sdf.format(ftOperationLog.getBeginDate());
            model.addAttribute("beginDate", beginDate);
        }
        if (ftOperationLog.getEndDate() != null) {
            String endDate = sdf.format(ftOperationLog.getEndDate());
            model.addAttribute("endDate", endDate);
        }
        String cleanFlag = "";
        if (ftOperationLog.getCleanFlag() == null) {
            cleanFlag = "0";
        }else if(ftOperationLog.getCleanFlag().equals("1")){
            cleanFlag = "1";
        }
        model.addAttribute("cleanFlag", cleanFlag);
        List<FtOperationLog> list = ftOperationLogService.findList(ftOperationLog);
        List<FtOperationLog> fileList = ftOperationLogService.findFileName();
        List<OptTag> tagList = optTagService.findList(new OptTag());
        model.addAttribute("fileList",fileList);
        model.addAttribute("tagList",tagList);
        PageHelper.getInstance().getPage(ftOperationLog.getClass(), request, response, model, list);
        return "modules/client/operate";
    }

    @RequestMapping(value = "compare")
    public String compare(String id,HttpServletRequest request, HttpServletResponse response,Model model) {
            FtOperationLog ftOperationLog = ftOperationLogService.findById(id);
            String before = StringToXmlUtil.format(ftOperationLog.getBeforeModifyValue());
            String after = StringToXmlUtil.format(ftOperationLog.getAfterModifyValue());
            model.addAttribute("before",before.substring(before.indexOf(">")+1));
            model.addAttribute("after",after.substring(after.indexOf(">")+1));
        return "modules/client/compare";
    }

    @RequestMapping(value = "allCompare")
    public String allCompare(HttpServletRequest request, HttpServletResponse response,Model model) {
        String[] all = request.getParameter("all").split(",");
//        StringBuilder beStr = new StringBuilder("");
//        StringBuilder afStr = new StringBuilder("");
        List<String> result = new ArrayList<>();
        for(int i=1;i<all.length;i++){
            FtOperationLog ftOperationLog = ftOperationLogService.findById(all[i]);
            String before = StringToXmlUtil.format(ftOperationLog.getBeforeModifyValue());
            String after = StringToXmlUtil.format(ftOperationLog.getAfterModifyValue());
            String beCom = StringUtils.toHtml(before.substring(before.indexOf(">")+1));
            String afCom = StringUtils.toHtml(after.substring(after.indexOf(">")+1));
//            beStr.append(i+"."+ftOperationLog.getCfgFileName()+beCom+"<br/>");
//            afStr.append(i+"."+ftOperationLog.getCfgFileName()+afCom+"<br/>");
            String beStr = i+"."+ftOperationLog.getCfgFileName()+beCom+"<br/>";
            String afStr = i+"."+ftOperationLog.getCfgFileName()+afCom+"<br/>";
            result.add(beStr);
            result.add(afStr);
        }
//        model.addAttribute("allBefore",beStr.toString());
//        model.addAttribute("allAfter",afStr.toString());
        model.addAttribute("result",result);
        return "modules/client/allCompare2";
    }

    @RequestMapping(value = "storage")
    @ResponseBody
    public String storage(String id,String tag) {
        FtOperationLog ftOperationLog = new FtOperationLog();
        ftOperationLog.setId(Long.parseLong(id));
//        ftOperationLog.setRemark1(StringEscapeUtils.unescapeHtml4(tag));
        ftOperationLog.setRemark1(tag);
        ftOperationLogService.addTag(ftOperationLog);
        return "{\"message\":true}";
    }

}
