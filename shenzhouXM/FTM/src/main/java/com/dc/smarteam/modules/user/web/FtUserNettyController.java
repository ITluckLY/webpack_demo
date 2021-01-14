package com.dc.smarteam.modules.user.web;


import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.NettyModel;
import com.dc.smarteam.cfgmodel.UserModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.service.DictService;
import com.dc.smarteam.modules.user.entity.FtUserNetty;
import com.dc.smarteam.service.FileAuthService;
import com.dc.smarteam.modules.user.service.NettyService;
import com.dc.smarteam.service.UserService;
import com.dc.smarteam.util.NullSafeUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * netty 流控
 */
@Controller
@RequestMapping(value = "${adminPath}/user/ftUserNetty")
public class FtUserNettyController extends BaseController {

  @Resource
  private UserService userService;
  @Resource
  private FileAuthService fileAuthService;

  @Resource
  private NettyService nettyService;

  @Autowired
  private DictService dictService;

  public static final String REDIRECT = "redirect:";


  @RequiresPermissions("user0:ftUserDirAuth:view")
  @RequestMapping(value = {"nettyList", ""})
  public String nettyList(FtUserNetty ftUserNetty, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

    List<NettyModel.Prarm> prarmList = new ArrayList<>();
    List<NettyModel.NettyChannel> channelSpeedList = new ArrayList<>();
    List<NettyModel.NettyChannel> channelSpeedList2 = new ArrayList<>();
    ResultDto<NettyModel.NettyConfig> resultDto = nettyService.selByPC(ftUserNetty);
    if (ResultDtoTool.isSuccess(resultDto)) {
      NettyModel.NettyConfig service = resultDto.getData();

      // 获取prarm 列表数据
      List<NettyModel.Prarm> pram = service.getPrarm();
      if (pram != null) {
        for (NettyModel.Prarm prarmUser : NullSafeUtil.null2Empty(pram)) {
          NettyModel.Prarm entity = new NettyModel.Prarm();
          entity.setMaxSpeed(prarmUser.getMaxSpeed());
          entity.setSleepTime(prarmUser.getSleepTime());
          entity.setScanTime(prarmUser.getScanTime());
          prarmList.add(entity);
        }
      }
      // 获取 Chennel 列表数据

      if (service.getChannelSpeed() != null) {
        List<NettyModel.NettyChannel> nnc = service.getChannelSpeed().getChannelList();
        if (nnc != null) {
          NettyModel.ChannelSpeed getCS = service.getChannelSpeed();
          for (NettyModel.NettyChannel authUser : NullSafeUtil.null2Empty(getCS.getChannelList())) {
            NettyModel.NettyChannel entity = new NettyModel.NettyChannel();
            if (!StringUtils.equals(ftUserNetty.getUserName(), null) ) {
              if(StringUtils.equals(ftUserNetty.getUserName(), "")){
                entity.setUserName(authUser.getUserName());
                entity.setReadLimit(authUser.getReadLimit());
                entity.setWriteLimit(authUser.getWriteLimit());
                channelSpeedList.add(entity);
              }
              //if (StringUtils.equals(ftUserNetty.getUserName(), authUser.getUserName())) {
              if ( authUser.getUserName().contains(ftUserNetty.getUserName())) {
                entity.setUserName(authUser.getUserName());
                entity.setReadLimit(authUser.getReadLimit());
                entity.setWriteLimit(authUser.getWriteLimit());
                channelSpeedList2.add(entity);
                channelSpeedList = channelSpeedList2;
                break;

              } else {


              }
            } else {

              entity.setUserName(authUser.getUserName());
              entity.setReadLimit(authUser.getReadLimit());
              entity.setWriteLimit(authUser.getWriteLimit());
              channelSpeedList.add(entity);
            }

          }
        }
      }
    }
    model.addAttribute("prarmList", prarmList);
    model.addAttribute("channelSpeedList", channelSpeedList);

    return "modules/user/nettyList";
  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = "addPrarm")
  public String addPrarm(FtUserNetty ftUserNetty, HttpServletRequest request, Model model) {

    request.getSession().setAttribute("new", "new");
    Dict dict = new Dict();
    dict.setType(GlobalCons.PROTOCAL_TYPE);
    List<Dict> dictList = this.dictService.findList(dict);
    model.addAttribute("dictList", dictList);
    model.addAttribute("ftUserNetty", ftUserNetty);

    return "modules/user/addPrarm";
  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = {"updateP"})
  public String updateP(FtUserNetty ftUserNetty, Model model, RedirectAttributes redirectAttributes) {

    ResultDto<NettyModel.NettyConfig> resultDto = nettyService.getPrarm(ftUserNetty);
    if (ResultDtoTool.isSuccess(resultDto)) {
      NettyModel.NettyConfig userInfo = resultDto.getData();
      CfgModelConverter.convertToUP(userInfo, ftUserNetty);
      if (ftUserNetty.getMaxSpeed() != null) {
        ftUserNetty.setId("111111");
      }
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
      return "redirect:" + Global.getAdminPath() + "/user/ftUserNetty/nettyList";
    }
    model.addAttribute("ftUserNetty", ftUserNetty);
    return "modules/user/addPrarm";

  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = {"savePrarm"})
  public String savePrarm(FtUserNetty ftUserNetty, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    // ResultDto<String> resultDto = nettyService.addP(ftUserNetty);
    ResultDto<String> resultDto;
    if (StringUtils.isEmpty(ftUserNetty.getId())) {
      resultDto = nettyService.addP(ftUserNetty);
    } else {
      resultDto = nettyService.updateSavaP(ftUserNetty);
    }
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "保存成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return "redirect:" + Global.getAdminPath() + "/user/ftUserNetty/nettyList";
  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = "delPrarm")
  public String delPrarm(FtUserNetty ftUserNetty, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    ResultDto<String> resultDto = nettyService.delPrarm2(ftUserNetty);
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "删除成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/user/ftUserNetty/nettyList";

  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = "addChannelSpeed")
  public String addChannelSpeed(FtUserNetty ftUserNetty, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
    request.getSession().setAttribute("new", "new");
    Dict dict = new Dict();
    dict.setType(GlobalCons.PROTOCAL_TYPE);
    List<Dict> dictList = this.dictService.findList(dict);
    model.addAttribute("dictList", dictList);
    model.addAttribute("ftUserNetty", ftUserNetty);
    return "modules/user/addChannel";
  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = {"updateC"})
  public String updateC(FtUserNetty ftUserNetty, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    ResultDto<NettyModel.NettyConfig> resultDto = nettyService.getChannelSpeed(ftUserNetty);
    if (ResultDtoTool.isSuccess(resultDto)) {
      NettyModel.NettyConfig userInfo = resultDto.getData();
      CfgModelConverter.convertToUP(userInfo, ftUserNetty);
      if (ftUserNetty.getUserName() != null) {
        ftUserNetty.setId("222222");
      }
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
      return "redirect:" + Global.getAdminPath() + "/user/ftUserNetty/nettyList";
    }

    model.addAttribute("ftUserNetty", ftUserNetty);
    return "modules/user/addChannel";

  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = {"saveChannel"})
  public String saveChannel(FtUserNetty ftUserNetty, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    // ResultDto<String> resultDto = nettyService.addC2(ftUserNetty);

    ResultDto<String> resultDto;
    if (StringUtils.isEmpty(ftUserNetty.getId())) {
      resultDto = nettyService.addC2(ftUserNetty);
    } else {

      resultDto = nettyService.updateSavaC(ftUserNetty);
    }

    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "保存用户宽带权限信息成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/user/ftUserNetty/nettyList";
  }


  @RequiresPermissions("user0:ftUserDirAuth:edit")
  @RequestMapping(value = "delChannel")
  public String delChannel(FtUserNetty ftUserNetty, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    ResultDto<String> resultDto = nettyService.delChannel(ftUserNetty);
    if (ResultDtoTool.isSuccess(resultDto)) {
      addMessage(redirectAttributes, "删除成功");
    } else {
      addMessage(redirectAttributes, resultDto.getMessage());
    }
    return REDIRECT + Global.getAdminPath() + "/user/ftUserNetty/nettyList";
  }


}
