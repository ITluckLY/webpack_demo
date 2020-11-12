package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.common.cons.EncodingCons;
import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.spring.SpringContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置xml文件缓存
 * Created by mocg on 2016/7/29.
 */
public class CachedCfgDoc {
  private static CachedCfgDoc ourInstance = new CachedCfgDoc();
  //key为文件名，可以在Cfg中获取
  private Map<String, Document> docMap = new HashMap<>();

  private CachedCfgDoc() {
  }

  public static CachedCfgDoc getInstance() {
    return ourInstance;
  }

  private synchronized Document load0(final String cfgFileName) throws DocumentException, IOException {//NOSONAR
    Document doc = null;
    NodeType nodeType = Cfg.getNodeType();
    CfgDocServiceFace cfgDocServiceFace = SpringContext.getInstance().getCfgDocServiceFace();
    if (NodeType.NAMENODE.equals(nodeType)) {
      if (Cfg.SYS_CFG.equals(cfgFileName)) doc = loadLocalXML(cfgFileName);
      else if (Cfg.COMPONENTS_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.COMPONENTS_CFG, null);
      else if (Cfg.CRONTAB_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.CRONTAB_CFG, null);
      else if (Cfg.LB_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.LB_CFG, null);
      else if (Cfg.FILE_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.FILE_CFG, null);
      else if (Cfg.FILE_CLEAN_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.FILE_CLEAN_CFG, null);
      else if (Cfg.FLOW_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.FLOW_CFG, null);
      else if (Cfg.PSFLOW_CFG.equals(cfgFileName))
        doc = cfgDocServiceFace.getCfgDoc(Cfg.PSFLOW_CFG, null); /*配置缓存 校验 流程xml 文件*/
      else if (Cfg.NODES_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.NODES_CFG, null);
      else if (Cfg.ROUTE_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.ROUTE_CFG, null);
      else if (Cfg.RULE_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.RULE_CFG, null);
      else if (Cfg.SERVICES_INFO_CFG.equals(cfgFileName))
        doc = cfgDocServiceFace.getCfgDoc(Cfg.SERVICES_INFO_CFG, null);
      else if (Cfg.SYSTEM_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.SYSTEM_CFG, null);
      else if (Cfg.USER_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.USER_CFG, null);
      else if (Cfg.FILE_RENAME_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.FILE_RENAME_CFG, null);
      else if (Cfg.VSYS_MAP_CFG.equals(cfgFileName)) doc = cfgDocServiceFace.getCfgDoc(Cfg.VSYS_MAP_CFG, null);
      else if (Cfg.CLIENT_STATUS_CFG.equals(cfgFileName))
        doc = cfgDocServiceFace.getCfgDoc(Cfg.CLIENT_STATUS_CFG, null);
    } else {
      doc = loadLocalXML(cfgFileName);
    }
    return doc;
  }

  private Document loadLocalXML(final String cfgFileName) throws DocumentException, IOException {
    Document doc;
    String filePath = Cfg.getConfigPath() + cfgFileName;
    SAXReader reader = new SAXReader();
    try (FileInputStream fis = new FileInputStream(filePath);
         InputStreamReader fr = new InputStreamReader(fis, EncodingCons.CFG_CONTENT_CHARSET)) {

      doc = reader.read(fr);
      XMLDealTool.withoutTimestamp(doc);
    }
    return doc;
  }

  public Document load(final String cfgFileName) throws DocumentException, IOException {
    //如果是NameNode总是直接读取数据的数据
    if (NodeType.NAMENODE.equals(Cfg.getNodeType())) {
      return reload(cfgFileName);
    }
    Document doc = docMap.get(cfgFileName);
    if (doc == null) {
      doc = load0(cfgFileName);
      docMap.put(cfgFileName, doc);
    }
    return doc;
  }

  public Document reload(final String cfgFileName) throws DocumentException, IOException {
    Document doc = load0(cfgFileName);
    docMap.put(cfgFileName, doc);
    return doc;
  }

  private Document load2(final String cfgFileName) throws FtpException {
    try {
      return load(cfgFileName);
    } catch (DocumentException | IOException e) {
      throw new FtpException("加载配置文件err", e);
    }
  }

  private Document reload2(final String cfgFileName) throws FtpException {
    try {
      return reload(cfgFileName);
    } catch (DocumentException | IOException e) {
      throw new FtpException("重新加载配置文件err", e);
    }
  }

  public Document loadCfg() throws FtpException {
    return load2(Cfg.SYS_CFG);
  }

  public Document reloadCfg() throws FtpException {
    return reload2(Cfg.SYS_CFG);
  }

  public Document loadComponents() throws FtpException {
    return load2(Cfg.COMPONENTS_CFG);
  }

  public Document reloadComponents() throws FtpException {
    return reload2(Cfg.COMPONENTS_CFG);
  }

  public Document loadCrontab() throws FtpException {
    return load2(Cfg.CRONTAB_CFG);
  }

  public Document reloadCrontab() throws FtpException {
    return reload2(Cfg.CRONTAB_CFG);
  }

  public Document loadFile() throws FtpException {
    return load2(Cfg.FILE_CFG);
  }

  public Document reloadFile() throws FtpException {
    return reload2(Cfg.FILE_CFG);
  }

  public Document loadFileClean() throws FtpException {
    return load2(Cfg.FILE_CLEAN_CFG);
  }

  public Document reloadFileClean() throws FtpException {
    return reload2(Cfg.FILE_CLEAN_CFG);
  }

  public Document loadFlow() throws FtpException {
    return load2(Cfg.FLOW_CFG);
  }

  public Document reloadFlow() throws FtpException {
    return reload2(Cfg.FLOW_CFG);
  }

  // 新增psflow 的文件
  public Document loadPsFlow() throws FtpException {
    return load2(Cfg.PSFLOW_CFG);
  }

  public Document reloadPsFlow() throws FtpException {
    return reload2(Cfg.PSFLOW_CFG);
  }


  public Document loadNodes() throws FtpException {
    return load2(Cfg.NODES_CFG);
  }

  public Document reloadNodes() throws FtpException {
    return reload2(Cfg.NODES_CFG);
  }

  public Document loadServicesInfo() throws FtpException {
    return load2(Cfg.SERVICES_INFO_CFG);
  }

  public Document reloadServicesInfo() throws FtpException {
    return reload2(Cfg.SERVICES_INFO_CFG);
  }

  public Document loadSystem() throws FtpException {
    return load2(Cfg.SYSTEM_CFG);
  }

  public Document reloadSystem() throws FtpException {
    return reload2(Cfg.SYSTEM_CFG);
  }

  public Document loadUser() throws FtpException {
    return load2(Cfg.USER_CFG);
  }

  public Document reloadUser() throws FtpException {
    return reload2(Cfg.USER_CFG);
  }

  public Document loadRoute() throws FtpException {
    return load2(Cfg.ROUTE_CFG);
  }

  public Document reloadRoute() throws FtpException {
    return reload2(Cfg.ROUTE_CFG);
  }

  public Document loadRule() throws FtpException {
    return load2(Cfg.RULE_CFG);
  }

  public Document reloadRule() throws FtpException {
    return reload2(Cfg.RULE_CFG);
  }

  public Document loadFileRename() throws FtpException {
    return load2(Cfg.FILE_RENAME_CFG);
  }

  public Document reloadFileRename() throws FtpException {
    return reload2(Cfg.FILE_RENAME_CFG);
  }

  public Document loadVsysmap() throws FtpException {
    return load2(Cfg.VSYS_MAP_CFG);
  }

  public Document reloadVsysmap() throws FtpException {
    return reload2(Cfg.VSYS_MAP_CFG);
  }

  public Document loadClientStatus() throws FtpException {
    return load2(Cfg.CLIENT_STATUS_CFG);
  }

  public Document reloadClientStatus() throws FtpException {
    return reload2(Cfg.CLIENT_STATUS_CFG);
  }

}
