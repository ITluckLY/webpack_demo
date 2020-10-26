package com.dc.smarteam.modules.clientstatus.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ClientStatusModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtClientStatusHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.client.entity.FtClientStatus;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.ClientStatusService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchuang on 2018/6/13.
 */

@Controller
@RequestMapping(value = "${adminPath}/client/clientStatus")
public class ClientStatusController extends BaseController {

    @Resource
    private ClientStatusService clientStatusService;


    @RequestMapping(value = {"","list"})
    public String indexList(FtClientStatus ftClientStatus, HttpServletRequest request, HttpServletResponse response, Model model){
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        List<FtClientStatus> list = new ArrayList<>();

        ResultDto<List<ClientStatusModel.Client>> resultDto = null;
        if(ftClientStatus==null){
            logger.info("listAll");
            resultDto = clientStatusService.listAll();
        }else {
            logger.info("listBySel");
            resultDto = clientStatusService.listBySel(ftClientStatus);
        }
        if(ResultDtoTool.isSuccess(resultDto)){
            for(ClientStatusModel.Client client:resultDto.getData()){
                FtClientStatus ftClientStatus0 = new FtClientStatus();
                logger.info("clinet_status:{}",client.getId());
                CfgModelConverter.convertTo(client,ftClientStatus0);
                list.add(ftClientStatus0);
            }
        }else{
            addMessage(model,resultDto.getMessage());
        }
        model.addAttribute("ftClientStatus", new FtClientStatus());
        PageHelper.getInstance().getPage(FtClientStatus.class,request,response,model,list);
        return "/modules/clientstatus/clientStatusList";
    }

    @RequestMapping(value = "newClientStatus")
    public String addClientStatus(HttpServletRequest request,Model model){
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        model.addAttribute("ftClientStatus",new FtClientStatus());
        return "modules/clientstatus/addClientStatus";
    }


    @RequestMapping(value = "addClientStatus")
    public String addClientStatus(FtClientStatus ftClientStatus, RedirectAttributes redirectAttributes){

        ResultDto<String> resultDto = clientStatusService.add(ftClientStatus);
        if(ResultDtoTool.isSuccess(resultDto)){
            addMessage(redirectAttributes,"添加客户端状态信息成功");
        }else {
            addMessage(redirectAttributes,"添加客户端状态信息失败");
        }
        return "redirect:"+ Global.getAdminPath()+"/client/clientStatus/list";
    }

    @RequestMapping(value = "editClientStatus")
    public String editClientStatus(FtClientStatus ftClientStatus, Model model, RedirectAttributes redirectAttributes){
        ResultDto<ClientStatusModel.Client> resultDto = clientStatusService.selById(ftClientStatus);
        if(ResultDtoTool.isSuccess(resultDto)){
            CfgModelConverter.convertTo(resultDto.getData(),ftClientStatus);
            model.addAttribute("ftClientStatus",ftClientStatus);
        }else{
            addMessage(model,"未找到信息");
            model.addAttribute("ftClientStatus",ftClientStatus);
        }
        return "/modules/clientstatus/editClientStatus";

    }

    @RequestMapping(value = "updateClientStatus")
    public String updateClientStatus(FtClientStatus ftClientStatus, Model model, RedirectAttributes redirectAttributes){
        logger.info("mess:{}",ftClientStatus.toString());
        ResultDto<String> resultDto = clientStatusService.update(ftClientStatus);
        if(ResultDtoTool.isSuccess(resultDto)){
            addMessage(redirectAttributes,"修改客户端状态信息成功");
        }else {
            addMessage(redirectAttributes,"修改客户端状态信息失败");
        }
        return "redirect:"+ Global.getAdminPath()+"/client/clientStatus/list";
    }

    @ResponseBody
    @RequestMapping(value = "delClientStatus")
    public String delClientStatus(HttpServletRequest request, FtClientStatus ftClientStatus){
        ResultDto<String> resultDto = clientStatusService.del(ftClientStatus);
        if(ResultDtoTool.isSuccess(resultDto)){
            return Boolean.toString(true);
        } else {
            return Boolean.toString(false);
        }
    }

    @RequiresPermissions("servicenode:ftServiceNode:view")
    @RequestMapping(value = "otherConf")
    public String otherConf(HttpServletRequest request, Model model){
        String getAllStr = MessageFactory.getInstance().clientStatus(new FtClientStatus(),"print");
        FtClientStatusHelper.getOtherConf(request,model,getAllStr);
        return "/modules/clientstatus/ftClientStatusOtherConf";
    }

}
