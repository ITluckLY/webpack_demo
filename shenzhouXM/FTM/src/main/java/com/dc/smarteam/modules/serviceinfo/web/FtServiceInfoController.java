/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.web;//NOSONAR

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.FlowModel;
import com.dc.smarteam.cfgmodel.ServiceModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.MessageFactory;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.helper.FtServiceNodeHelper;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.flow.entity.FtFlow;
import com.dc.smarteam.modules.route2.entity.FtRoute;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.GetAuthEntity;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.service.FlowService;
import com.dc.smarteam.service.RouteService;
import com.dc.smarteam.service.ServiceInfoService;
import com.dc.smarteam.service.UserService;
import com.dc.smarteam.util.EmptyUtils;
import com.dc.smarteam.util.NullSafeUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理Controller
 *
 * @author liwang
 * @version 2016-01-12
 */
@Controller
@RequestMapping(value = "${adminPath}/serviceinfo/ftServiceInfo")
public class FtServiceInfoController extends BaseController {

  @Resource
  private ServiceInfoService serviceInfoService;
  @Resource
  private FlowService flowService;
  @Resource
  private UserService userService;
  @Autowired
  private RouteService routeService;
  public static final String REDIRECT = "redirect:";

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = {"list", ""})
  public String list(FtServiceInfo ftServiceInfo, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {// NOSONAR
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    if (null == ftServiceNode || null == ftServiceNode.getSystemName()) {
      return REDIRECT + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
    }
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());

    ResultDto<List<ServiceModel.Service>> resultDto = serviceInfoService.listAll();
    List<FtServiceInfo> list = new ArrayList();
    if (ResultDtoTool.isSuccess(resultDto)) {
      List<ServiceModel.Service> services = resultDto.getData();
      for (ServiceModel.Service service : services) { //NOSONAR
        if (!StringUtils.containsIgnoreCase(service.getSysname(), ftServiceInfo.getSystemName())) continue;
        if (StringUtils.isNoneEmpty(ftServiceInfo.getTrancode()) && !StringUtils.containsIgnoreCase(service.getTrancode(), ftServiceInfo.getTrancode()))
          continue;
        if (StringUtils.isNoneEmpty(ftServiceInfo.getFlow()) && !StringUtils.containsIgnoreCase(service.getFlow(), ftServiceInfo.getFlow()))
          continue;
        if (StringUtils.isNoneEmpty(ftServiceInfo.getDescribe()) && !StringUtils.containsIgnoreCase(service.getDescribe(), ftServiceInfo.getDescribe()))
          continue;
        FtServiceInfo info = new FtServiceInfo();
        CfgModelConverter.convertToWithoutAuth(service, info);
        info.setId(String.valueOf(list.size()));
        list.add(info);
      }
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
      return REDIRECT + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
    }
    PageHelper.getInstance().getPage(FtServiceInfo.class, request, response, model, list);

    return "modules/serviceinfo/ftServiceInfoList";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "addPage")
  public String addPage(FtServiceInfo ftServiceInfo, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    ResultDto<List<FlowModel.Flow>> resultDto = flowService.selBySysname(ftServiceNode.getSystemName());
    List<FtFlow> ftFlowList = new ArrayList<>();
    List<FtFlow> ftPSFlowList = new ArrayList<>();
    if (ResultDtoTool.isSuccess(resultDto)) {
      List<FlowModel.Flow> flows = resultDto.getData();
      for (FlowModel.Flow flow : flows) {
        FtFlow ftFlow = new FtFlow();
        if (flow.getName().equals("FilesProcesFlow")) {
          CfgModelConverter.convertTo(flow, ftFlow);
          ftPSFlowList.add(ftFlow);
        } else {
          CfgModelConverter.convertTo(flow, ftFlow);
          ftFlowList.add(ftFlow);
        }

      }
    }
    model.addAttribute("ftPSFlowList", ftPSFlowList);
    model.addAttribute("ftFlowList", ftFlowList);
    return "modules/serviceinfo/ftServiceInfoForm";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "form")
  public String form(FtServiceInfo ftServiceInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);

    ResultDto<ServiceModel.Service> resultDto = serviceInfoService.selByTrancodeAndSysname(ftServiceInfo);
    if (ResultDtoTool.isSuccess(resultDto)) {
      ServiceModel.Service service = resultDto.getData();
      FtServiceInfo info = new FtServiceInfo();
      CfgModelConverter.convertToWithoutAuth(service, info);
      info.setId(ftServiceInfo.getId());
      model.addAttribute("ftServiceInfo", info);

      ResultDto<List<FlowModel.Flow>> flowDto = flowService.selBySysname(ftServiceNode.getSystemName());
      List<FtFlow> ftFlowList = new ArrayList<>();
      List<FtFlow> ftPSFlowList = new ArrayList<>();

      if (ResultDtoTool.isSuccess(flowDto)) {
        List<FlowModel.Flow> flows = flowDto.getData();
        for (FlowModel.Flow flow : flows) {
          FtFlow ftFlow = new FtFlow();
          if (flow.getName().equals("FilesProcesFlow")) {
            CfgModelConverter.convertTo(flow, ftFlow);
            ftPSFlowList.add(ftFlow);
          } else {
            CfgModelConverter.convertTo(flow, ftFlow);
            ftFlowList.add(ftFlow);
          }
        }
      }

      model.addAttribute("ftPSFlowList", ftPSFlowList);
      model.addAttribute("ftFlowList", ftFlowList);
      return "modules/serviceinfo/ftServiceInfoEditForm";
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
      return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/?repage";//NOSONAR
    }
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:edit")
  @RequestMapping(value = "save")
  public String save(FtServiceInfo ftServiceInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    ResultDto<String> resultDto;
    if (EmptyUtils.isEmpty(ftServiceInfo.getId())) {
      resultDto = serviceInfoService.add(ftServiceInfo);
    } else {
      resultDto = serviceInfoService.update(ftServiceInfo);
    }
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "保存服务管理成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/?repage";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "addPutAuth")
  public String addPutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    String trancodeTemp = ftServiceInfo.getTrancode();
    if (null != trancodeTemp) {
      putAuthEntity.setTrancode(trancodeTemp);
    }
    List<FtUser> ftUserList = new ArrayList<>();
    ResultDto<List<UserModel.UserInfo>> userDto = userService.listAll();
    if (ResultDtoTool.isSuccess(userDto)) {
      List<UserModel.UserInfo> userInfos = userDto.getData();
      for (UserModel.UserInfo userInfo : userInfos) {
        FtUser ftUser = new FtUser();
        CfgModelConverter.convertTo(userInfo, ftUser);
        ftUserList.add(ftUser);
      }
    }
    model.addAttribute("ftUserList", ftUserList);

    return "modules/serviceinfo/ftServiceInfoAddPutAuth";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "addGetAuth")
  public String addGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    List<FtUser> ftUserList = new ArrayList<>();
    ResultDto<List<UserModel.UserInfo>> resultDto = userService.listAll();
    if (ResultDtoTool.isSuccess(resultDto)) {
      List<UserModel.UserInfo> userInfos = resultDto.getData();
      for (UserModel.UserInfo userInfo : userInfos) {
        FtUser ftUser = new FtUser();
        CfgModelConverter.convertTo(userInfo, ftUser);
        ftUserList.add(ftUser);
      }
    }
    model.addAttribute("ftUserList", ftUserList);
    return "modules/serviceinfo/ftServiceInfoAddGetAuth";
  }


  @RequiresPermissions("serviceinfo:ftServiceInfo:edit")
  @RequestMapping(value = "savePutAuth")
  public String savePutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    ResultDto<String> resultDto = serviceInfoService.savePutAuth(ftServiceInfo, putAuthEntity);
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "保存上传权限成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/putGetTotal?trancode=" + ftServiceInfo.getTrancode() + "&systemName=" + ftServiceInfo.getSystemName();//NOSONAR
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:edit")
  @RequestMapping(value = "delPutAuth")
  public String delPutAuth(FtServiceInfo ftServiceInfo, PutAuthEntity putAuthEntity, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    ResultDto<String> resultDto = serviceInfoService.delPutAuth(ftServiceInfo, putAuthEntity);
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "删除上传权限成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/putGetTotal?trancode=" + ftServiceInfo.getTrancode() + "&systemName=" + ftServiceInfo.getSystemName();
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:edit")
  @RequestMapping(value = "saveGetAuth")
  public String saveGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    ResultDto<String> resultDto = serviceInfoService.saveGetAuth(ftServiceInfo, getAuthEntity);
    ResultDto<String> resultDto2 = routeService.addRouteByGetAuth(getAuthEntity);
    if (ResultDtoTool.isSuccess(resultDto) && ResultDtoTool.isSuccess(resultDto2)) {
      addMessage(redirectAttributes, "保存下载权限成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage() + resultDto2.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/putGetTotal?trancode=" + ftServiceInfo.getTrancode() + "&systemName=" + ftServiceInfo.getSystemName();
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:edit")
  @RequestMapping(value = "delGetAuth")
  public String delGetAuth(FtServiceInfo ftServiceInfo, GetAuthEntity getAuthEntity, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
    ftServiceInfo.setSystemName(ftServiceNode.getSystemName());
    ResultDto<String> resultDto = serviceInfoService.delGetAuth(ftServiceInfo, getAuthEntity);
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "删除下载权限成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/putGetTotal?trancode=" + ftServiceInfo.getTrancode() + "&systemName=" + ftServiceInfo.getSystemName();
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:edit")
  @RequestMapping(value = "delete")
  public String delete(FtServiceInfo ftServiceInfo, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    ResultDto<String> resultDto = serviceInfoService.del(ftServiceInfo);
    ResultDto<String> dto = routeService.del(ftServiceInfo);
    if (ResultDtoTool.isSuccess(resultDto) && ResultDtoTool.isSuccess(dto)) {
      addMessage(redirectAttributes, "删除服务管理成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    FtRoute ftRoute = new FtRoute();
    ftRoute.setTran_code(ftServiceInfo.getTrancode());
    routeService.delByTranscode(ftRoute);
    return REDIRECT + Global.getAdminPath() + "/serviceinfo/ftServiceInfo/?repage";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "putGetTotal")
  public String putGetTotal(FtServiceInfo ftServiceInfo, HttpServletRequest request, Model model) {
    List<PutAuthEntity> putAuthList = new ArrayList<>();
    List<GetAuthEntity> getAuthList = new ArrayList<>();
    ResultDto<ServiceModel.Service> resultDto = serviceInfoService.selByTrancodeAndSysname(ftServiceInfo);
    if (ResultDtoTool.isSuccess(resultDto)) {
      ServiceModel.Service service = resultDto.getData();
      ServiceModel.PutAuth putAuth = service.getPutAuth();
      if (putAuth != null) {
        for (ServiceModel.AuthUser authUser : NullSafeUtil.null2Empty(putAuth.getUsers())) {
          PutAuthEntity entity = new PutAuthEntity();
          entity.setTrancode(ftServiceInfo.getTrancode());
          entity.setUserName(authUser.getUser());
          entity.setDirectoy(authUser.getDirectoy());
          putAuthList.add(entity);
        }
      }

      ServiceModel.GetAuth getAuth = service.getGetAuth();
      if (getAuth != null) {
        for (ServiceModel.AuthUser authUser : NullSafeUtil.null2Empty(getAuth.getUsers())) {
          GetAuthEntity entity = new GetAuthEntity();
          entity.setTrancode(ftServiceInfo.getTrancode());
          entity.setUserName(authUser.getUser());
          getAuthList.add(entity);
        }
      }
    }

    model.addAttribute("paeList", putAuthList);
    model.addAttribute("gaeList", getAuthList);
    return "modules/serviceinfo/transcodeWithList";
  }


  @RequiresPermissions("servicenode:ftServiceNode:view")
  @RequestMapping(value = "otherConf")
  public String otherConf(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
    String getAllStr = MessageFactory.getInstance().serviceInfo(new FtServiceInfo(), "print");//生成查询报文
    FtServiceNodeHelper.getOtherConf(request, model, getAllStr);
    return "modules/serviceinfo/ftServiceInfoOtherConf";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "confComp")
  public String confComp(FtServiceNode ftServiceNode, HttpServletRequest request, HttpServletResponse response, Model model) {
    FtServiceNode ftServiceNodeNameNode = (FtServiceNode) request.getSession().getAttribute("ftServiceNodeNameNode");
    String getAllStr = MessageFactory.getInstance().cfgSync("services_info", "generateSyncCfgXml", ftServiceNodeNameNode.getSystemName());//生成查询报文
    String getOtherAllStr = MessageFactory.getInstance().serviceInfo(new FtServiceInfo(), "print");//生成查询报文
    FtServiceNodeHelper.getConfComp(ftServiceNode, ftServiceNodeNameNode, getAllStr, getOtherAllStr, request, model);
    return "modules/serviceinfo/ftServiceInfoConfComp";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = "export")
  public String exportcsv(FtServiceInfo ftServiceInfo, HttpServletRequest request,    //NOSONAR
                          @RequestParam("filename") String filename,
                          Model model, HttpServletResponse response) {
    try {
      filename = URLDecoder.decode(filename, "UTF-8"); //NOSONAR
      response.reset();
      response.setContentType("application/oct-stream");
      response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("utf8"), "utf8"));
      response.setCharacterEncoding("utf-8");
      response.setContentType("text/plain; charset=utf-8");
      PrintWriter pw = response.getWriter();

      ResultDto<String> resultDto = serviceInfoService.serviceInfoExport();
      if (!ResultDtoTool.isSuccess(resultDto)) {
        return "modules/serviceinfo/ftServiceInfoList";
      }
      String sb = resultDto.getData();
      //添加UTF-8 BOM文件头
      pw.print(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
      pw.println(sb);
      pw.flush();
      pw.close();
    } catch (Exception e) {
      logger.error("导出异常" + e);
    }
    return "modules/serviceinfo/ftServiceInfoList";
  }

  @RequiresPermissions("serviceinfo:ftServiceInfo:view")
  @RequestMapping(value = {"catchFileCfg"})
  @ResponseBody
  public String catchFileCfg(FtServiceNode ftServiceNode, String fileName, HttpServletRequest request) {
    return FtServiceNodeHelper.getCachtFileCfg(fileName, request);
  }
}