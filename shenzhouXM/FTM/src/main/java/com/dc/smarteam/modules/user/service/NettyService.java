package com.dc.smarteam.modules.user.service;

import com.dc.smarteam.aspectCfg.base.UpdateEntity;
import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.cfgmodel.*;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.modules.cfgfile.service.CfgFileService;
import com.dc.smarteam.modules.client.service.FtOperationLogService;
import com.dc.smarteam.modules.protocol.entity.SysProtocol;
import com.dc.smarteam.modules.serviceinfo.entity.FtServiceInfo;
import com.dc.smarteam.modules.serviceinfo.entity.PutAuthEntity;
import com.dc.smarteam.modules.user.entity.FtUser;
import com.dc.smarteam.modules.user.entity.FtUserNetty;
import com.dc.smarteam.service.AbstractService;
import com.dc.smarteam.service.UserService;
import com.dc.smarteam.util.XmlBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.nio.ch.Net;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class NettyService extends AbstractService {

  @Resource
  private CfgFileService cfgFileService;
  @Resource
  private UserService userService;

  private Logger logger = LoggerFactory.getLogger(NettyService.class);

  private static final String NETTY_FILE_NAME = "netty.xml";

  @Override
  public String getCfgFileName() {
    return NETTY_FILE_NAME;
  }

  public NettyModel nettyLoadModel() {
    return cfgFileService.loadModel4Name(NETTY_FILE_NAME, NettyModel.class);
  }
  private static final String INITNettyLoadModel  = "initNetty.xml";
  private static final String INITP_NETTY_FILE_NAME = "initPNetty.xml";
  private static final String INITC_NETTY_FILE_NAME = "initCNetty.xml";

  public static String getInitcNettyFileName() {
    return INITC_NETTY_FILE_NAME;
  }
  public static String getInitPNettyFileName() {
    return INITP_NETTY_FILE_NAME;
  }

  public NettyModel initNettyLoadModel() {
    return cfgFileService.loadModel4Name(INITNettyLoadModel, NettyModel.class);
  }
  public NettyModel initPNettyLoadModel() {
    return cfgFileService.loadModel4Name(INITP_NETTY_FILE_NAME, NettyModel.class);
  }
  public NettyModel initCNettyLoadModel() {
    return cfgFileService.loadModel4Name(INITC_NETTY_FILE_NAME, NettyModel.class);
  }


  public ResultDto<List<NettyModel.NettyConfig>> nettyListAll() {
    NettyModel model = nettyLoadModel();
    return ResultDtoTool.buildSucceed(model.getNettyConfig());
  }

  // 添加 addPrarm
  public ResultDto<String> addP(FtUserNetty ftUserNetty) {
    if (StringUtils.isEmpty(ftUserNetty.getMaxSpeed())) {
      return ResultDtoTool.buildError("MaxsPeed不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getSleepTime()))) {
      return ResultDtoTool.buildError("SleepTime不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getScanTime()))) {
      return ResultDtoTool.buildError("ScanTime不能为空");
    }

    NettyModel model = nettyLoadModel();
    // NettyModel model = initPNettyLoadModel();
    NettyModel.NettyConfig initP = null;
    boolean exist = false;

    for (NettyModel.NettyConfig userInfo : model.getNettyConfig()) {
      initP = userInfo;
      List<NettyModel.Prarm> pm = userInfo.getPrarm();
      if (pm == null) break;
      for (NettyModel.Prarm np : pm) {
        if  (np.getMaxSpeed() != null) {
          exist = true;
          return ResultDtoTool.buildError("添加失败，已经设置了公共属性！！！");
        }
      }
    }
    //if (initP == null) return ResultDtoTool.buildError("添加失败!");

    NettyModel.Prarm prm = new NettyModel.Prarm();
    CfgModelConverter.convertToP2(ftUserNetty, prm);

    List<NettyModel.Prarm> ips = initP.getPrarm();
    if (ips == null) {
      ips = new ArrayList<>();
      initP.setPrarm(ips);
    }
    ips.add(prm);

    save(model);
    return ResultDtoTool.buildSucceed("添加成功");
  }


  // 更新  Prarm
  public ResultDto<String> updateSavaP(FtUserNetty ftUserNetty) {
    if (StringUtils.isEmpty(ftUserNetty.getMaxSpeed())) {
      return ResultDtoTool.buildError("MaxsPeed不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getSleepTime()))) {
      return ResultDtoTool.buildError("SleepTime不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getScanTime()))) {
      return ResultDtoTool.buildError("ScanTime不能为空");
    }

    NettyModel model = nettyLoadModel();
    NettyModel.NettyConfig target = null;

    for (NettyModel.NettyConfig  userInfo : model.getNettyConfig()) {
      //if (StringUtils.equals(userInfo.getPrarm().get(0).getMaxSpeed(), ftUserNetty.getMaxSpeed())) {
      if (userInfo.getPrarm().get(0).getMaxSpeed() !=null && ftUserNetty.getMaxSpeed()!=null) {
        target = userInfo;
        break;
      }
    }
    if (target == null) {
      return ResultDtoTool.buildError("没有找到指定的用户信息");
    }
    CfgModelConverter.convertToNettyP(ftUserNetty, target);

    save(model);
    return ResultDtoTool.buildSucceed("修改成功！！！");
  }


  // 更新  updateC
  public ResultDto<String> updateSavaC(FtUserNetty ftUserNetty) {
    if (StringUtils.isEmpty(ftUserNetty.getUserName())) {
      return ResultDtoTool.buildError("用户名称不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getWriteLimit()))) {
      return ResultDtoTool.buildError("Write不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getReadLimit()))) {
      return ResultDtoTool.buildError("Read不能为空");
    }

    NettyModel model = nettyLoadModel();
    NettyModel.NettyConfig target = null;
    NettyModel.NettyChannel cs = null;

    for (NettyModel.NettyConfig  userInfo : model.getNettyConfig()) {
      for(NettyModel.NettyChannel nettyModel :userInfo.getChannelSpeed().getChannelList())
      if (StringUtils.equals(nettyModel.getUserName(), ftUserNetty.getUserName())) {
        target = userInfo;
        cs = nettyModel;
        nettyModel.setUserName(ftUserNetty.getUserName());
        nettyModel.setReadLimit(ftUserNetty.getReadLimit());
        nettyModel.setWriteLimit(ftUserNetty.getWriteLimit());
        break;
      }
    }
    if (target == null) {
      return ResultDtoTool.buildError("没有找到指定的用户信息");
    }

    //CfgModelConverter.convertToNettyC(ftUserNetty, target);

    save(model);
    return ResultDtoTool.buildSucceed("修改成功！！！");
  }



  // 添加 ChannelSpeed
  public ResultDto<String> addC(FtUserNetty ftUserNetty) {
    if (StringUtils.isEmpty(ftUserNetty.getUserName())) {
      return ResultDtoTool.buildError("用户名称不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getWriteLimit()))) {
      return ResultDtoTool.buildError("Write不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getReadLimit()))) {
      return ResultDtoTool.buildError("Read不能为空");
    }

    NettyModel.NettyConfig asd = new NettyModel.NettyConfig();
    NettyModel model = nettyLoadModel();
    for (NettyModel.NettyConfig system : model.getNettyConfig()) {
      for (NettyModel.NettyChannel nclist : system.getChannelSpeed().getChannelList()) {
        if (StringUtils.equalsIgnoreCase(nclist.getUserName(), ftUserNetty.getUserName())) {
          return ResultDtoTool.buildError("添加失败，已有此用户！！！");
        }
      }
    }
    CfgModelConverter.convertToC(ftUserNetty, asd);
    model.getNettyConfig().add(asd);
    save(model);
    return ResultDtoTool.buildSucceed("添加成功");
  }

  // 添加 ChannelSpeed
  public ResultDto<String> addC2(FtUserNetty ftUserNetty) {
    if (StringUtils.isEmpty(ftUserNetty.getUserName())) {
      return ResultDtoTool.buildError("用户名称不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getWriteLimit()))) {
      return ResultDtoTool.buildError("Write不能为空");
    }
    if (StringUtils.isEmpty(String.valueOf(ftUserNetty.getReadLimit()))) {
      return ResultDtoTool.buildError("Read不能为空");
    }
    NettyModel model = nettyLoadModel();
    // NettyModel model = initCNettyLoadModel();
    NettyModel.NettyConfig initC = null;
    for (NettyModel.NettyConfig system : model.getNettyConfig()) {
      initC = system;
      if (system.getChannelSpeed().getChannelList() !=null) {
        for (NettyModel.NettyChannel nclist : system.getChannelSpeed().getChannelList()) {
          if (StringUtils.equalsIgnoreCase(nclist.getUserName(), ftUserNetty.getUserName())) {
            return ResultDtoTool.buildError("添加失败，已有此用户！！！");
          }
        }
      }
    }
    NettyModel.ChannelSpeed getCS = initC.getChannelSpeed();
    if (getCS == null) {
      getCS = new NettyModel.ChannelSpeed();
      initC.setChannelSpeed(getCS);
    }
    List<NettyModel.NettyChannel> users = getCS.getChannelList();
    if (users == null) {
      users = new ArrayList<>();
      getCS.setChannelList(users);
    }
    NettyModel.NettyChannel authUser = new NettyModel.NettyChannel();
    authUser.setUserName(ftUserNetty.getUserName());
    authUser.setReadLimit(ftUserNetty.getReadLimit());
    authUser.setWriteLimit(ftUserNetty.getWriteLimit());

    if (!users.contains(authUser)) users.add(authUser);
    save(model);
    return ResultDtoTool.buildSucceed("添加成功");
  }


  private void save(NettyModel model) {
    cfgFileService.saveModel4Name(NETTY_FILE_NAME, model);
  }


  // 列表数据
  public ResultDto<NettyModel.NettyConfig> selByPC(FtUserNetty ftUserNetty) {
    NettyModel model = nettyLoadModel();

    NettyModel.NettyConfig target = null;
    for (NettyModel.NettyConfig service : model.getNettyConfig()) {
      target = service;
    }
    if (target == null) return ResultDtoTool.buildError("对象不存在");
    return ResultDtoTool.buildSucceed(target);
  }


  // 修改prarm
  public ResultDto<NettyModel.NettyConfig> getPrarm(FtUserNetty ftUserNetty) {
    NettyModel model = nettyLoadModel();
    return selloadedByName(ftUserNetty,model);
  }
  public ResultDto<NettyModel.NettyConfig> selloadedByName(FtUserNetty ftUserNetty, NettyModel model) {
    NettyModel.NettyConfig target = null;
    for (NettyModel.NettyConfig userInfo : model.getNettyConfig()) {
      if (StringUtils.equals(userInfo.getPrarm().get(0).getMaxSpeed(), ftUserNetty.getMaxSpeed())) {
        target = userInfo;
        break;
      }
    }
    if (target == null) return ResultDtoTool.buildError("对象不存在");
    return ResultDtoTool.buildSucceed(target);
  }

  // 修改 ChannelSpeed
  public ResultDto<NettyModel.NettyConfig> getChannelSpeed(FtUserNetty ftUserNetty) {
    NettyModel model = nettyLoadModel();
    return selByName(ftUserNetty,model);
  }
  public ResultDto<NettyModel.NettyConfig> selByName(FtUserNetty ftUserNetty, NettyModel model) {
    NettyModel.NettyConfig target = null;
    for (NettyModel.NettyConfig userInfo : model.getNettyConfig()) {
      for(NettyModel.NettyChannel nclist : userInfo.getChannelSpeed().getChannelList()){
        if (StringUtils.equals(nclist.getUserName(), ftUserNetty.getUserName())) {
          target = userInfo;
          break;
        }
      }
    }
    if (target == null) return ResultDtoTool.buildError("对象不存在");
    return ResultDtoTool.buildSucceed(target);
  }



  @UpdateEntity
  public ResultDto<String> delPrarm(FtUserNetty ftUserNetty) {
    NettyModel model = nettyLoadModel();
    List<NettyModel.NettyConfig> services = model.getNettyConfig();
    NettyModel.NettyConfig targetService = null;
    for (NettyModel.NettyConfig service : services) {
        targetService = service;
    }
    List<NettyModel.Prarm> prarms = targetService.getPrarm();
    if (prarms == null) {
      prarms = new ArrayList<>();
      targetService.setPrarm(prarms);
    }

    NettyModel.Prarm authUser = new NettyModel.Prarm();
    authUser.setMaxSpeed(ftUserNetty.getMaxSpeed());
    authUser.setScanTime(ftUserNetty.getScanTime());
    authUser.setSleepTime(ftUserNetty.getSleepTime());
    for(NettyModel.Prarm ll : prarms){
      if(StringUtils.equals(ll.getMaxSpeed(),authUser.getMaxSpeed())) {
        prarms.remove(ll);
        break;
      }
    }

    save(model);
    return ResultDtoTool.buildSucceed("操作成功");
  }

  @UpdateEntity
  public ResultDto<String> delPrarm2(FtUserNetty ftUserNetty) {
    NettyModel model = nettyLoadModel();
    List<NettyModel.NettyConfig> services = model.getNettyConfig();
    List<NettyModel.Prarm> prarms = services.get(0).getPrarm();
    for(NettyModel.Prarm np : prarms){
      if(StringUtils.equals(np.getMaxSpeed(),ftUserNetty.getMaxSpeed())){
        prarms.remove(np);
        break;
      }
    }

    save(model);
    return ResultDtoTool.buildSucceed("操作成功");
  }


  @UpdateEntity
  public ResultDto<String> delChannel(FtUserNetty ftUserNetty) {
    NettyModel model = nettyLoadModel();
    NettyModel.ChannelSpeed userInfos = model.getNettyConfig().get(0).getChannelSpeed();
    List<NettyModel.NettyChannel> nm = userInfos.getChannelList();
    for (NettyModel.NettyChannel nnc : nm) {
      if (StringUtils.equals(nnc.getUserName(), ftUserNetty.getUserName())) {
        nm.remove(nnc);
        break;
      }
    }
    save(model);
    return ResultDtoTool.buildSucceed("操作成功");
  }


  @Override
  public String getEntityXml(CfgData curr, boolean isNew) {
    FtUserNetty ftUserNetty = (FtUserNetty) curr;
    if (isNew) {
      NettyModel.NettyConfig target = new NettyModel.NettyConfig();
      // 此处设置 nettyConfig 里面的值
      CfgModelConverter.convertToNetty(ftUserNetty, target);
      return XmlBeanUtil.toXml(target);
    }
    NettyModel.NettyConfig target = null;
    NettyModel model = nettyLoadModel();
    for (NettyModel.NettyConfig userInfo : model.getNettyConfig()) {
      target = userInfo;
    }
    if (target == null) return null;
    logger.info("target  is{},{}", target.getPrarm(), target.getChannelSpeed());
    return XmlBeanUtil.toXml(target);

  }
}
