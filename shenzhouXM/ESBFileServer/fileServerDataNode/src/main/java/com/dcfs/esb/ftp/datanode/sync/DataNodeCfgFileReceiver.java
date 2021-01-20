package com.dcfs.esb.ftp.datanode.sync;

import com.dcfs.esb.ftp.impls.flow.ServiceFlowManager;
import com.dcfs.esb.ftp.key.KeyManager;
import com.dcfs.esb.ftp.netty.ServiceNetty;
import com.dcfs.esb.ftp.server.client.ClientManage;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import com.dcfs.esb.ftp.server.file.EsbFileWorker;
import com.dcfs.esb.ftp.server.invoke.fileclean.FileCleanManager;
import com.dcfs.esb.ftp.server.invoke.node.NodeManager;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import com.dcfs.esb.ftp.server.invoke.node.VsysmapWorker;
import com.dcfs.esb.ftp.server.invoke.nodesync.AbstractCfgFileReceiver;
import com.dcfs.esb.ftp.server.route.RouteManager;
import com.dcfs.esb.ftp.server.schedule.ScheduleManager;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;


/**
 * Created by mocg on 2016/10/11.
 */
public class DataNodeCfgFileReceiver extends AbstractCfgFileReceiver {
  private static final Logger log = LoggerFactory.getLogger(DataNodeCfgFileReceiver.class);
  private String cfgDir = Cfg.getConfigPath();
  private String sysName = NodeManager.getInstance().getSysName();

  @Override
  public boolean receive(String sysName, String cfgFileName, String cfgContent) throws IOException {//NOSONAR
    log.debug("receive#sysName:{}\ncfgFileName:{}\ncfgContent:{}", sysName, cfgFileName, cfgContent);
    if (StringUtils.isEmpty(cfgContent)) return false;
    //系统名称不相同不同步，本地配置不同步
    if (this.sysName != null && !this.sysName.equals(sysName)) return false;
    if (Cfg.SYS_CFG.equals(cfgFileName)) return false;
    //先备份，再更新，更新失败回滚
    String timeFlag = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
    File cfgFileBak = new File(cfgDir, cfgFileName + timeFlag + ".bak");
    File cfgFile = new File(cfgDir, cfgFileName);
    FileUtils.copyFile(cfgFile, cfgFileBak);
    log.debug("成功备份配文件#备份文件:{}", cfgFileBak.getPath());
    Document doc = XMLDealTool.readXml(cfgContent);
    if (doc == null) {
      log.error("配置文本不是xml#{}", cfgFileName);
      return false;
    }
    boolean b = XMLDealTool.writerXml(doc, cfgFile);
    log.debug("成功覆盖配置文件?{}#文件:{}", b, cfgFile.getPath());
    try {
      if (Cfg.COMPONENTS_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadComponents();
        ServiceContainer.reload();
      } else if (Cfg.CRONTAB_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadCrontab();
        ScheduleManager.getInstance().reInit();
      } else if (Cfg.FILE_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadFile();
        Cfg.loadFileConfig();
        EsbFileWorker.reload();
      } else if (Cfg.FILE_CLEAN_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadFileClean();
        FileCleanManager.getInstance().reload();
      } else if (Cfg.FLOW_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadFlow();
        ServiceFlowManager.getInstance().reload(); // 初始化 flow 下面的流程
      } else if (Cfg.PSFLOW_CFG.equals(cfgFileName)) {  /* 校验流程*/
        CachedCfgDoc.getInstance().loadPsFlow();
        ServiceFlowManager.getInstance().reload();
      } else if (Cfg.NODES_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadNodes();
        NodesWorker.reload();
      } else if (Cfg.ROUTE_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadRoute();
        RouteManager.reload();
      } else if (Cfg.RULE_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadRule();
        Cfg.loadRuleConfig();
      } else if (Cfg.SERVICES_INFO_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadServicesInfo();
        ServiceContainer.reload();
      } else if (Cfg.SYSTEM_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadSystem();
        Cfg.loadSystemConfig();
      } else if (Cfg.USER_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadUser();
        Cfg.loadUserConfig();
        UserInfoWorker.reload();
      } else if (Cfg.FILE_RENAME_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadFileRename();
      } else if (Cfg.VSYS_MAP_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadVsysmap();
        VsysmapWorker.reload();
      } else if (Cfg.CLIENT_STATUS_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadClientStatus();
        ClientManage.reload();
      } else if (Cfg.NETTY_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().loadNetty();
        ServiceNetty.getInstance().reload();
      } else if (Cfg.KEY_CFG.equals(cfgFileName)) {
        CachedCfgDoc.getInstance().reloadKey();
        KeyManager.getInstance().reload();
      }
      log.info("配置同步重新加载完成#{}", cfgFileName);
    } catch (Exception e) {
      log.error("配置同步err", e);
      FileUtils.copyFile(cfgFileBak, cfgFile);
    }
    return true;
  }
}
