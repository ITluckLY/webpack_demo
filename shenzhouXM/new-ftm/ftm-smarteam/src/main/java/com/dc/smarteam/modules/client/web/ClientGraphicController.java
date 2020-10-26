package com.dc.smarteam.modules.client.web;

import com.dc.smarteam.cfgmodel.RouteModel;
import com.dc.smarteam.cfgmodel.SystemModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.DateHelper;
import com.dc.smarteam.modules.client.entity.GraphicParam;
import com.dc.smarteam.modules.file.service.BizFileQueryLogService;
import com.dc.smarteam.modules.file.service.BizFileUploadLogServiceI;
import com.dc.smarteam.modules.file.service.impl.BizFileDownloadLogServiceImpl;
import com.dc.smarteam.service.RouteServiceI;
import com.dc.smarteam.service.SysServiceI;
import com.dc.smarteam.service.UserServiceI;
import net.sf.json.JSONArray;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/client/clientGraphic")
public class ClientGraphicController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(ClientGraphicController.class);
    @Resource(name = "BizFileUploadLogServiceImpl")
    private BizFileUploadLogServiceI bizFileUploadLogService;
    @Autowired
    private BizFileDownloadLogServiceImpl bizFileDownloadLogService;
    @Autowired
    private BizFileQueryLogService bizFileQueryLogService;
    @Resource(name = "SysServiceImpl")
    private SysServiceI sysService;
    @Resource(name = "UserServiceImpl")
    private UserServiceI userService;
    @Resource(name = "RouteServiceImpl")
    private RouteServiceI routeService;

    //add 20170904 单位换算1024*1024

    private static final int BASE_BYTE = 1048576;

    @RequiresPermissions("NodeMonitor:ftNodeMonitor:view")
    @RequestMapping(value = {"viewPage"})
    public String viewPage(GraphicParam graphicParam, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<SystemModel.System> sysModels = sysService.loadModel().getSystems();
        List<String> clientList = new ArrayList<>();
        for(SystemModel.System system:sysModels){
            clientList.add(system.getName());
        }
        List<UserModel.UserInfo> userInfos = userService.loadModel().getUserInfos();
        List<String> user = new ArrayList<>();
        List<String> sys = new ArrayList<>();
        for(UserModel.UserInfo userInfo:userInfos){
            String uid = userInfo.getUid().getUid();
            user.add(uid);
            String sysName = uid.substring(0,3);
            if(!sys.contains(sysName)){
                sys.add(sysName);
            }
        }
        List<RouteModel.Route> routes = routeService.loadModel().getRoutes();
        List<String> transCode = new ArrayList<>();
        for(RouteModel.Route route:routes){
            transCode.add(route.getTranCode());
        }
        List<String> all = new ArrayList<>();
        all.add("全部");
        Map<String,List> type = new HashMap<>();
        type.put("sys",sys);
        type.put("user",user);
        type.put("tranCode",transCode);
        type.put("all",all);
        type.put("client",clientList);
        model.addAttribute("type",type);
        search(graphicParam,request,response,model);
        return "modules/client/graphic2";
    }

    @RequestMapping(value = {"search"})
    public String search(GraphicParam graphicParam, HttpServletRequest request, HttpServletResponse response, Model model) {
        String type = request.getParameter("type");
        String detail = request.getParameter("detail");
        model.addAttribute("selectType",type);
        model.addAttribute("selectDetail",detail);
        if (graphicParam.getBeginDate() == null && graphicParam.getEndDate() == null) {
            graphicParam.setBeginDate(DateHelper.getStartDate());
            graphicParam.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(graphicParam.getBeginDate());
        calendar2.setTime(graphicParam.getEndDate());
        List<String> timeList = new ArrayList<>();
        while (calendar1.getTimeInMillis() <= calendar2.getTimeInMillis()) {
            String time1 = "'"+sdf1.format(calendar1.getTime())+"'";
            timeList.add(time1);
            calendar1.add(Calendar.DATE,1);
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String beginDate = null;
        String endDate = null;
        if (graphicParam.getBeginDate() != null) {
            beginDate = sdf2.format(graphicParam.getBeginDate());
            model.addAttribute("beginDate", beginDate);
        }
        if (graphicParam.getEndDate() != null) {
            endDate = sdf2.format(graphicParam.getEndDate());
            model.addAttribute("endDate", endDate);
        }
        Map<String, Object> map2 = new HashMap<>();
        map2.put("beginDate", beginDate);
        map2.put("endDate", endDate);
        List<Long> findUploadTotal = new ArrayList<>();
        List<Long> findDownloadTotal = new ArrayList<>();
        if(type != null && type.equals("sys") && !detail.equals("all")){
            map2.put("sys",detail);
            findUploadTotal = bizFileUploadLogService.findListBySysTime(map2);
            findDownloadTotal = bizFileDownloadLogService.findListBySysTime(map2);
        }else if(type != null && type.equals("user") && !detail.equals("all")){
            map2.put("user",detail);
            findUploadTotal = bizFileUploadLogService.findListBySysAndTime(map2);
            findDownloadTotal = bizFileDownloadLogService.findListBySysAndTime(map2);
        }else if(type != null && type.equals("client") && !detail.equals("all")){
            map2.put("client",detail);
            findUploadTotal = bizFileQueryLogService.findUploadListByRouteAndTime(map2);
            findDownloadTotal = bizFileQueryLogService.findDownloadListByRouteAndTime(map2);
        }else if(type != null && type.equals("tranCode") && !detail.equals("all")){
            map2.put("tranCode",detail);
            findUploadTotal = bizFileUploadLogService.findListByTranCodeAndTime(map2);
            findDownloadTotal = bizFileDownloadLogService.findListByTranCodeAndTime(map2);
        }else {
            findUploadTotal = bizFileUploadLogService.findListByTime(map2);
            findDownloadTotal = bizFileDownloadLogService.findListByTime(map2);
        }
        List<Long> findTotal = new ArrayList<>();
        for(int i = 0;i < timeList.size();i++){
            Long total = findUploadTotal.get(i) + findDownloadTotal.get(i);
            findTotal.add(total);
        }
        model.addAttribute("timeList", timeList);
        model.addAttribute("findUploadTotal", findUploadTotal);
        model.addAttribute("findDownloadTotal", findDownloadTotal);
        model.addAttribute("findTotal", findTotal);
        return "modules/client/graphic2";
    }


    @RequestMapping(value = {"pie"})
    public String pie(GraphicParam graphicParam, HttpServletRequest request, HttpServletResponse response, Model model) {
        String type = graphicParam.getType();
        if (graphicParam.getBeginDate() == null && graphicParam.getEndDate() == null) {
            graphicParam.setBeginDate(DateHelper.getStartDate());
            graphicParam.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (graphicParam.getBeginDate() != null) {
            String beginDate = sdf2.format(graphicParam.getBeginDate());
            model.addAttribute("beginDate", beginDate);
        }
        if (graphicParam.getEndDate() != null) {
            String endDate = sdf2.format(graphicParam.getEndDate());
            model.addAttribute("endDate", endDate);
        }
        Map<String, Object> map2 = new HashMap<>();
        Date beginDate = graphicParam.getBeginDate();
        Date endDate = graphicParam.getEndDate();
        map2.put("beginDate", beginDate);
        map2.put("endDate", endDate);
        List<Map<String,Long>> pie = new ArrayList<>();
        if(type == null || type.equals("sys")){
            pie = bizFileUploadLogService.findListByTimeBySysName(map2);
        } else if(type == null || type.equals("user")){
            pie = bizFileUploadLogService.findListByTimeBySys(map2);
        }else if(type == null || type.equals("client")){
            pie = bizFileUploadLogService.findListByTimeByClient(map2);
        }else if(type == null || type.equals("tranCode")){
            pie = bizFileUploadLogService.findListByTimeByTranCode(map2);
        }

        JSONArray result = JSONArray.fromObject(pie);
        model.addAttribute("result",result);
        return "modules/client/sysPie";
    }


    @RequestMapping(value = {"chord"})
    public String chord(GraphicParam graphicParam, HttpServletRequest request, HttpServletResponse response, Model model) {
        String type = graphicParam.getType();
        if (graphicParam.getBeginDate() == null && graphicParam.getEndDate() == null) {
            graphicParam.setBeginDate(DateHelper.getStartDate());
            graphicParam.setEndDate(DateHelper.getEndDate());
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if (graphicParam.getBeginDate() != null) {
            String beginDate = sdf2.format(graphicParam.getBeginDate());
            model.addAttribute("beginDate", beginDate);
        }
        if (graphicParam.getEndDate() != null) {
            String endDate = sdf2.format(graphicParam.getEndDate());
            model.addAttribute("endDate", endDate);
        }
        Map<String, Object> map2 = new HashMap<>();
        Date beginDate = graphicParam.getBeginDate();
        Date endDate = graphicParam.getEndDate();
        map2.put("beginDate", beginDate);
        map2.put("endDate", endDate);
        List<Map> chord = new ArrayList<>();
        List<String> uname = new ArrayList<>();
        if(type == null || type.equals("sys")){
            chord = bizFileUploadLogService.findListByTranCode(map2);
            for(Map m: chord){
                if(!uname.contains(m.get("source"))){
                    uname.add((String)m.get("source"));
                }
                if(!uname.contains(m.get("target"))){
                    uname.add((String)m.get("target"));
                }
            }
        }
        JSONArray result = JSONArray.fromObject(chord);
        JSONArray selected = JSONArray.fromObject(uname);
        model.addAttribute("result",result);
        model.addAttribute("selected",selected);
        return "modules/client/chord";
    }

    @RequestMapping(value = {"getDetail"})
    @ResponseBody
    public JSONArray getDetail(HttpServletRequest request, HttpServletResponse response, Model model) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Map<String, Object> map = new HashMap<>();
        List<Map> detail = new ArrayList<>();
        try {
            map.put("beginDate",sdf.parse(request.getParameter("beginDate")));
            map.put("endDate",sdf.parse(request.getParameter("endDate")));
            map.put("source",request.getParameter("source"));
            map.put("target",request.getParameter("target"));
            map.put("name",request.getParameter("name"));
            detail = bizFileUploadLogService.getDetail(map);
        } catch (ParseException e) {
            log.error("日期格式化失败",e);
        }
        return JSONArray.fromObject(detail);
    }

}
