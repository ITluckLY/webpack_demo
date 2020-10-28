/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.flow.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.ComponentModel;
import com.dc.smarteam.cfgmodel.FlowModel;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.component.entity.FtComponent;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.service.ComponentService;
import com.dc.smarteam.service.FlowServiceI;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程管理Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/flow/ftFlow")
public class FtFlowController extends BaseController {

  @Resource(name = "FlowServiceImpl")
  private FlowServiceI flowService;
  @Resource
  private ComponentService componentService;

  @GetMapping(value =  {"list", ""})
  public Object list(FtFlow ftFlow, HttpServletRequest request, HttpServletResponse response, Map map) {

    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
      return ResultDtoTool.buildError("请先设置节点组！！！");
    }
    ftFlow.setSystemName(ftServiceNode.getSystemName());

    ResultDto<List<FlowModel.Flow>> resultDto = flowService.listAll();
    List<FtFlow> list = new ArrayList<>();
    if (ResultDtoTool.isSuccess(resultDto)) {
      List<FlowModel.Flow> flows = resultDto.getData();
      for (FlowModel.Flow flow : flows) {
        //按查询条件筛选
        String flowSysname = flow.getSysname().replaceAll("\n", "").trim();
        flow.setSysname(flowSysname);
        if (!"*".equals(flowSysname) && !StringUtils.equalsIgnoreCase(flowSysname, ftFlow.getSystemName())) continue;
        if (StringUtils.isNoneEmpty(ftFlow.getName()) && !StringUtils.containsIgnoreCase(flow.getName(), ftFlow.getName()))
          continue;
        if (StringUtils.isNoneEmpty(ftFlow.getDes()) && !StringUtils.containsIgnoreCase(flow.getDescribe(), ftFlow.getDes()))
          continue;
        FtFlow ftFlow2 = new FtFlow();
        CfgModelConverter.convertTo(flow, ftFlow2);
        ftFlow2.setId(String.valueOf(list.size()));
        list.add(ftFlow2);
      }
    } else {
      return ResultDtoTool.buildError(resultDto.getMessage());

    }
    PageHelper.getInstance().getPage(ftFlow.getClass(), request, response, map, list);
    return ResultDtoTool.buildSucceed(list);

  }

  @RequestMapping(value = "form")
  public Object form(FtFlow ftFlow) {

    if (StringUtils.isEmpty(ftFlow.getName())) {
      return ResultDtoTool.buildError("请先添加名称！！！");
    }

    Map<String, Object> resultmap = new HashMap<>();
    FtFlow entity = new FtFlow();
    ResultDto<FlowModel.Flow> resultDto = flowService.selByName(ftFlow);
    if (ResultDtoTool.isSuccess(resultDto)) {
      FlowModel.Flow flow = resultDto.getData();
      CfgModelConverter.convertTo(flow, entity);
      entity.setId("1");
    } else {
      return ResultDtoTool.buildError(resultDto.getMessage());
    }
    resultmap.put("ftFlow", entity);
    return ResultDtoTool.buildSucceed(resultmap);
  }

  @RequestMapping(value = "save")
  public Object save(FtFlow ftFlow, HttpServletRequest request) {

    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    if (ftServiceNode == null) {
      return ResultDtoTool.buildError("请先设置节点组！！！");
    }
    ftFlow.setSystemName("*".equals(ftFlow.getSystemName()) ? "*" : ftServiceNode.getSystemName());
    ResultDto<String> resultDto;
    if (StringUtils.isEmpty(ftFlow.getId())) {
      resultDto = flowService.add(ftFlow);
    } else {
      resultDto = flowService.update(ftFlow);
    }
    String mes = "";
    if (ResultDtoTool.isSuccess(resultDto)) {
        mes ="保存流程管理成功";
    } else {
        mes =resultDto.getMessage();
    }
      return  ResultDtoTool.buildSucceed(mes);
  }

  @RequestMapping(value = "delete")
  public Object delete(FtFlow ftFlow, HttpServletRequest request) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    if (ftServiceNode == null) {
      return  ResultDtoTool.buildError("请先选择数据！！！");
    }

    ResultDto<String> resultDto = flowService.del(ftFlow);
    String  mss = "";
    if (ResultDtoTool.isSuccess(resultDto)) {
        mss ="删除流程管理成功";
    } else {
        mss =resultDto.getMessage();
    }
    return ResultDtoTool.buildSucceed(mss);
  }

  @RequestMapping(value = "component")
  public Object component(FtFlow ftFlow) {
    ResultDto<List<ComponentModel.Service>> dto = componentService.listAll();
    List<FtComponent> list = new ArrayList<>();
    Map<String,Object> result=new HashMap<>();
    if (ResultDtoTool.isSuccess(dto)) {
      List<ComponentModel.Service> compservices = dto.getData();
      for (ComponentModel.Service compservice : compservices) {
        FtComponent ftComponent = new FtComponent();
        CfgModelConverter.convertTo(compservice, ftComponent);
        list.add(ftComponent);
      }
    } else {
        return  ResultDtoTool.buildError(dto.getMessage());
    }
      result.put("ftFlow", ftFlow);
      result.put("ftComponentList", list);
    return ResultDtoTool.buildSucceed(result);
  }

  @RequestMapping(value = "confComp")
  public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request,   Model model) {
    FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
    String getAllStr = MessageFactory.getInstance().cfgSync("flow", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
    String getOtherAllStr = MessageFactory.getInstance().flow(new FtFlow(), "print");//生成查询报文
    FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
    return "modules/flow/ftFlowConfComp";
  }

  @RequiresPermissions("servicenode:ftServiceNode:view")
  @RequestMapping(value = {"catchFileCfg"})
  @ResponseBody
  public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
    return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
  }


  @RequiresPermissions("servicenode:ftServiceNode:view")
  @RequestMapping(value = "otherConf")
  public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
    String getAllStr = MessageFactory.getInstance().flow(new FtFlow(), "print");//生成查询报文
    FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
    return "modules/flow/ftFlowOtherConf";
  }

}