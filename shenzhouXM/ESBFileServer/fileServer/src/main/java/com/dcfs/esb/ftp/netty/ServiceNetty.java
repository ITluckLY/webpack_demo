package com.dcfs.esb.ftp.netty;

import com.dcfs.esb.ftp.common.cons.EmptyCons;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.impls.flow.ServiceFlowBean;
import com.dcfs.esb.ftp.netty.nettyUtil.FtUserNetty;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ServiceNetty {

  private static final Logger log = LoggerFactory.getLogger(ServiceNetty.class);
  private static final Object lock = new Object();
  private static ServiceNetty instance;
  private Map<String, FtUserNetty> flows = new HashMap<>();
  private FtUserNetty defaultServiceFlowBean = null;

  private ServiceNetty() {
    load();
  }

  /**
   * 实例化流程工厂
   * flow信息
   *
   * @return
   */
  public static ServiceNetty getInstance() {
    if (instance != null) return instance;
    else {
      synchronized (lock) {
        if (instance == null) instance = new ServiceNetty();
        return instance;
      }
    }
  }

  public void reload() throws FtpException {
    synchronized (lock) {
      CachedCfgDoc.getInstance().loadNetty();
      instance = new ServiceNetty();
    }
  }

  /**
   * 初始化流程管理
   */
  public void load() {
    Map<String, FtUserNetty> nettyMap = new HashMap<>();
    try {
      Document doc = CachedCfgDoc.getInstance().loadNetty();
      List p = doc.selectNodes("/baseConfig/nettyConfig/prarm");
      for (Object aL : p) {
        Element e = (Element) aL; // 此处获取flow。xml 文件类的所有数据
        String name = e.attributeValue("maxSpeed");// 获取每个流程的 流程名称
        if (StringUtils.isEmpty(name)) continue;
        try { //NOSONAR
          addP(nettyMap, name, createUserNettyBean(e));
          if (log.isInfoEnabled()) log.info("加载处理流程" + name + ":" + e.asXML());
        } catch (Exception e2) {
          log.error("加载处理流程失败" + name + ":" + e.asXML(), e2);
        }
      }

      List c = doc.selectNodes("/baseConfig/nettyConfig/channelSpeed/channel");
      for (Object aL : c) {
        Element e = (Element) aL; // 此处获取flow。xml 文件类的所有数据
        String name = e.attributeValue("userName");// 获取每个流程的 流程名称
        if (StringUtils.isEmpty(name)) continue;
        try { //NOSONAR
          addFlow(nettyMap, name, createUserBean(e));
          if (log.isInfoEnabled()) log.info("加载处理流程" + name + ":" + e.asXML());
        } catch (Exception e2) {
          log.error("加载处理流程失败" + name + ":" + e.asXML(), e2);
        }
      }
      Map<String, FtUserNetty> temp = this.flows;
      this.flows = nettyMap;
      temp.clear();
    } catch (Exception e) {
      log.error("加载处理流程失败", e);
    }
  }


  public FtUserNetty createUserNettyBean(Element e) {
    FtUserNetty sfb = new FtUserNetty();
    sfb.setMaxSpeed(e.attributeValue("maxSpeed"));
    sfb.setSleepTime(e.attributeValue("sleepTime"));
    sfb.setScanTime(e.attributeValue("scanTime"));
    return sfb;
  }
  /**
   * 创建流程对象，并初始化
   *
   * @param e
   * @return
   */
  public FtUserNetty createUserBean(Element e) {
    FtUserNetty sfb = new FtUserNetty();
    sfb.setUserName(e.attributeValue("userName"));
    sfb.setReadLimit(e.attributeValue("readLimit"));
    sfb.setWriteLimit(e.attributeValue("writeLimit"));


    return sfb;
  }

  public void addP(Map<String, FtUserNetty> flows, String flowName, FtUserNetty flowBean) {
    if (flowBean == null) return;
    flows.put(flowName, flowBean);
  }

  /**
   * 添加流程
   *
   * @param flowName
   * @param flowBean
   * @return
   */
  public void addFlow(Map<String, FtUserNetty> flows, String flowName, FtUserNetty flowBean) {
    if (flowBean == null) return;
    flows.put(flowName, flowBean);
  }

  /**
   * 获取流程
   *
   * @param name
   * @return
   */
  public FtUserNetty getFlow(String name) { // ****************
    FtUserNetty flowBean = flows.get(name); // 前端页面指定的流程名称。
    if (flowBean != null) return flowBean;
    log.error("未获取到名称为{}的处理流程，返回默认处理流程", name);
    return getDefaultServiceFlowBean();
  }

  /**
   * ///////  返回流程节点。
   *
   * @return
   */
  private FtUserNetty getDefaultServiceFlowBean() {
    if (defaultServiceFlowBean != null) return defaultServiceFlowBean;
    FtUserNetty flowBean = new FtUserNetty();

    defaultServiceFlowBean = flowBean;
    return defaultServiceFlowBean;
  }
}

