package com.dc.smarteam.modules.monitor.ftnodemonitor.web;

import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeAlarmLine;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeAlarmLineService;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtTranCodeAlarmLineService;
import com.dc.smarteam.service.impl.ServiceInfoServiceImpl;
import com.dc.smarteam.util.PublicRepResultTool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 系统设置-告警配置-告警名单
 * <p>
 * Created by huangzbb on 2017/11/2.
 */
@RestController
@RequestMapping(value = "${adminPath}/monitor/FtNodeMonitor")
public class AlarmLineController extends BaseController {

    @Resource(name = "FtNodeAlarmLineServiceImpl")
    private FtNodeAlarmLineService ftNodeAlarmLineService;

    @Resource
    private FtTranCodeAlarmLineService ftTranCodeAlarmLineService;

    @Resource
    private ServiceInfoServiceImpl serviceInfoService;

//    @RequiresPermissions("NodeMonitor:alarmLine:view")
//    @RequestMapping(value = {"alarmLineList"})
//    public String alarmLineList(FtNodeAlarmLine ftNodeAlarmLine, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
//        List<FtNodeAlarmLine> list = new ArrayList<>();
//        try {
//            list = ftNodeAlarmLineService.findList(ftNodeAlarmLine);
//        } catch (Exception e) {
//            addMessage(redirectAttributes, "查询告警默认警戒线失败！详情：" + e.getMessage());
//            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
//        }
//        if (!list.isEmpty()) {
//            ftNodeAlarmLine = list.get(0);
//        }
//        model.addAttribute("ftNodeAlarmLine", ftNodeAlarmLine);
//        return "modules/monitor/nodeMonitor/ftNodeAlarmLineList";
//    }

    /***  -----  已改 start ----- ***/
    @RequestMapping(value = "saveAlarmLineList", produces = "application/json;charset=UTF-8")
    public Object saveAlarmLineList(FtNodeAlarmLine ftNodeAlarmLine) {
        String message = "";
        int change = ftNodeAlarmLineService.saveAlarmLineList(ftNodeAlarmLine);
        if(change>0){
            message = "保存成功";
        }
        return PublicRepResultTool.sendResult("0000",message,null);
    }
    /***  -----  已改 end ----- ***/

//    @RequestMapping(value = "tranCodeAlarmLineList")
//    public String tranCodeAlarmLineList(HttpServletRequest request,HttpServletResponse response,FtTranCodeAlarmLine ftTranCodeAlarmLine,RedirectAttributes redirectAttributes, Model model){
//        //List<FtTranCodeAlarmLine> list = null;
//        Page<FtTranCodeAlarmLine> page = null;
//        try{
//            //list = ftTranCodeAlarmLineService.findTranCodeAlarmLineList(ftTranCodeAlarmLine);
//            page = ftTranCodeAlarmLineService.findPage(new Page<FtTranCodeAlarmLine>(request,response),ftTranCodeAlarmLine);
//        }catch (Exception e){
//            model.addAttribute("message","获取交易码阈值列表失败");
//        }
//        model.addAttribute("page",page);
//        return "modules/monitor/tranCodeMonitor/ftTranCodeAlarmLineList";
//    }
//`
//    @RequestMapping(value = "addTranCodeAlarmLine")
//    public String addTranCodeAlarmLine(HttpServletRequest request,FtTranCodeAlarmLine ftTranCodeAlarmLine, Model model) {
//        //String tranCode = request.getParameter("tranCode");
//        String tranCode = ftTranCodeAlarmLine.getTranCode();
//        logger.info("addTranCodeAlarmLine tranCode is {}",tranCode);
//        List<String> tranCodeList = new ArrayList<>();
//        if((tranCode!=null&&!"".equals(tranCode)) &&(!"zero".equals(tranCode))){
//            ftTranCodeAlarmLine = ftTranCodeAlarmLineService.findTranCodeAlarmLine(tranCode);
//            model.addAttribute("tranCode",tranCode);
//            tranCodeList.add(tranCode);
//            model.addAttribute("tranCodeList",tranCodeList);
//            model.addAttribute("ftTranCodeAlarmLine",ftTranCodeAlarmLine);
//        }else{
//            model.addAttribute("ftTranCodeAlarmLine",new FtTranCodeAlarmLine());
//            ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
//            for(ServiceModel.Service service:resultDto.getData()){
//                tranCodeList.add(service.getTrancode());
//            }
//            model.addAttribute("tranCodeList",tranCodeList);
//        }
//
//        return "modules/monitor/tranCodeMonitor/ftEditTranCodeAlarmLine";
//    }
//
//    @RequestMapping(value = "saveTranCodeAlarmLine")
//    public String saveTranCodeAlarmLine(FtTranCodeAlarmLine ftTranCodeAlarmLine, RedirectAttributes redirectAttributes, Model model){
//        int res = ftTranCodeAlarmLineService.saveOneTranCodeAlarmLine(ftTranCodeAlarmLine);
//        if(res==1){
//            addMessage(redirectAttributes,"保存交易码告警阈值成功");
//        }else {
//            addMessage(redirectAttributes,"保存交易码告警阈值失败");
//        }
//        return "redirect:" + Global.getAdminPath() + "/monitor/FtNodeMonitor/tranCodeAlarmLineList";
//    }
//
//    @RequestMapping(value = "delTranCodeAlarmLine")
//    @ResponseBody
//    public String deleteTranCodeAlarmLine(FtTranCodeAlarmLine ftTranCodeAlarmLine){
//        logger.info("del {}",ftTranCodeAlarmLine.getTranCode());
//        if(ftTranCodeAlarmLineService.deleteTranCodeAlarmLine(ftTranCodeAlarmLine)==1){
//            return Boolean.toString(true);
//        }
//        return Boolean.toString(false);
//    }
//
//    @RequestMapping(value = "getTranCodeUser")
//    @ResponseBody
//    public Map<String,String> getTranCodeUser(FtServiceInfo ftServiceInfo){
//        logger.info("TranCode:{}",ftServiceInfo.getTrancode());
//        ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
//        Map<String,String> map = new HashMap<>();
//        try{
//            for(ServiceModel.Service service:resultDto.getData()){
//                if(service.getTrancode().equals(ftServiceInfo.getTrancode())){
//                    List<ServiceModel.AuthUser>  putAuths = service.getPutAuth().getUsers();
//                    List<String> userList1 = CollectionUtil.freeTurnList(putAuths,"getUser");
//                    String putUser = CollectionUtil.join(userList1,",");
//
//                    List<ServiceModel.AuthUser> getAuths = service.getGetAuth().getUsers();
//                    List<String> userList2 = CollectionUtil.freeTurnList(getAuths,"getUser");
//                    String getUser = CollectionUtil.join(userList2,",");
//
//                    map.put("putUser",putUser);
//                    map.put("getUser",getUser);
//                    break;
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            logger.error(e.getMessage());
//        }
//        logger.info("retrun mess putUser:{},getUser:{}",map.get("putUser"),map.get("getUser"));
//        return map;
//    }

}
