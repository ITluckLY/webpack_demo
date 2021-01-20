package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.common.cons.EncodingCons;
import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.HostIpHelper;
import com.dcfs.esb.ftp.impls.context.ContextFactory;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.key.KeyManager;
import com.dcfs.esb.ftp.netty.ServiceNetty;
import com.dcfs.esb.ftp.server.client.ClientManage;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.file.EsbFileWorker;
import com.dcfs.esb.ftp.server.rule.EsbRuleManage;
import com.dcfs.esb.ftp.server.system.SystemManage;
import com.dcfs.esb.ftp.utils.InetAddressUtil;
import com.dcfs.esb.ftp.utils.MClassLoaderUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * Created by mocg on 2016/6/21.
 */
public class Cfg {
    public static final String SYS_CFG = "cfg.xml";
    public static final String COMPONENTS_CFG = "components.xml"; //默认的公共的 所有的流程组件信息
    public static final String CRONTAB_CFG = "crontab.xml";
    public static final String LB_CFG = "F5Status.xml";
    public static final String FILE_CFG = "file.xml";
    public static final String FILE_CLEAN_CFG = "file_clean.xml";
    public static final String FLOW_CFG = "flow.xml"; // 全部流程，包含API版本校验、IP校验
    public static final String PSFLOW_CFG = "file_process.xml"; // 校验流程
    public static final String NODES_CFG = "nodes.xml";
    public static final String ROUTE_CFG = "route.xml";
    public static final String RULE_CFG = "rule.xml";
    public static final String SERVICES_INFO_CFG = "services_info.xml";
    public static final String SYSTEM_CFG = "system.xml";
    public static final String USER_CFG = "user.xml";
    public static final String FILE_RENAME_CFG = "file_rename.xml";
    public static final String VSYS_MAP_CFG = "vsysmap.xml";
    public static final String FILE_VERSION = "version.properties";
    public static final String CLIENT_STATUS_CFG = "client_status.xml";

    public static final String NETTY_CFG = "netty.xml";// 流量控制
    public static final String KEY_CFG = "keys.xml";

    private static final Logger log = LoggerFactory.getLogger(Cfg.class);
    //存放一些临时的公共配置、参数
    private static final CachedContext commCfgContext = ContextFactory.createContext();
    private static String configPath = "./cfg/";
    private static NodeType nodeType = NodeType.UNDEFINED;
    private static String hostAddress;
    private static String hostName;
    private static String hostTrueIp;
    private static String apiVersion;
    //由nodeName转成过来, eg:FS001=>1001 FSN002=>2002 FSD003=>3003 FSL004=>4004 FSM005=>5005
    private static int nodeId = 0;

    // 返回一个路径 并设置utf8 的格式
    public static void initConfigPath() {
        log.info("初始化配置文件夹路径CONFIG_PATH...");
        String cfgPath = System.getProperty("ESB_FS_CFG_PATH"); // 获取这key 的值
        if (cfgPath != null && cfgPath.length() > 0) {
            //需要转码
            cfgPath = new String(cfgPath.trim().getBytes(EncodingCons.SPRING_READ_VALUE_CHARSET), EncodingCons.PROPERTIES_FILE_DEF_CHARSET);
        }
        if (cfgPath == null || cfgPath.length() == 0) {
            String clsPath = MClassLoaderUtil.getBaseResourcePath();// // 设置路径 的utf8
            cfgPath = clsPath + "cfg" + File.separator;
        }
        //测试此字符串是否以指定的后缀（File.separator）结尾。
        if (!cfgPath.endsWith(File.separator)) cfgPath = cfgPath + File.separator;
        configPath = cfgPath;
        log.info("初始化配置文件夹路径CONFIG_PATH成功#{}", configPath);
    }

    /**
     *  读取文件
     * @throws FtpException
     */
    public static void loadFtpConfig() throws FtpException {
        log.info("装载系统参数开始...");
        FtpConfig.setConfigFile(configPath + SYS_CFG); // 设置文件路口
        FtpConfig.reload(); // 读取配置文件
        log.info("装载系统参数成功...");
    }

    /**
     * @throws FtpException
     */
    public static void loadUserConfig() throws FtpException {
        log.info("开始装载用户参数...");
        UserInfoFactory.reload();
        UserInfoWorker.reload();
    }

    /**
     * @throws FtpException
     */
    public static void loadFileConfig() throws FtpException {//NOSONAR
        log.info("开始装载文件参数...");
        EsbFileManager.reload(); //文件系统管理工厂  file.xml
        EsbFileWorker.reload();
    }

    /**
     * @throws FtpException
     */
    public static void loadRuleConfig() throws FtpException {//NOSONAR
        log.info("开始装载规则参数...");
        EsbRuleManage.reload();
    }

    /**
     * @throws FtpException
     */
    public static void loadSystemConfig() throws FtpException {//NOSONAR
        log.info("开始装载系统信息参数...");
        SystemManage.reload();
    }
    /**
     * @throws FtpException
     */
    public static void loadClientStatusConfig() throws FtpException {//NOSONAR
        log.info("开始装载系统信息参数...");
        ClientManage.reload();
    }


    public static void loadNettyConfig() throws FtpException {//NOSONAR
        log.info("开始装载 Netty 信息参数...");
        ServiceNetty.getInstance().reload();
    }

    public static void loadKeyConfig() throws FtpException {//NOSONAR
        log.info("开始装载 Key 信息参数...");
        KeyManager.getInstance().reload();
    }


    public static String getConfigPath() {
        return configPath;
    }

    //getter configPath +

    public static String getSysCfg() {
        return configPath + SYS_CFG;
    }

    public static String getFileCfg() {
        return configPath + FILE_CFG;
    }

    public static String getUserCfg() {
        return configPath + USER_CFG;
    }

    public static String getSystemCfg() {
        return configPath + SYSTEM_CFG;
    }

    public static String getLbCfg() {
        return configPath + LB_CFG;
    }

    public static String getRuleCfg() {
        return configPath + RULE_CFG;
    }

    public static String getRouteCfg() {
        return configPath + ROUTE_CFG;
    }

    public static String getFileCleanCfg() {
        return configPath + FILE_CLEAN_CFG;
    }

    public static String getNodesCfg() {
        return configPath + NODES_CFG;
    }

    public static String getCrontabCfg() {
        return configPath + CRONTAB_CFG;
    }

    public static String getFlowCfg() {
        return configPath + FLOW_CFG;
    }

    public static String getNettyCfg() {
        return configPath + NETTY_CFG;
    }

    public static String getKeyCfg () {return configPath + KEY_CFG;}

    // 校验流程
    public static String getPsFlowCfg() {

        return configPath + PSFLOW_CFG;
    }

    public static String getComponentsCfg() {
        return configPath + COMPONENTS_CFG;
    }

    public static String getServicesInfoCfg() {
        return configPath + SERVICES_INFO_CFG;
    }

    public static NodeType getNodeType() {
        return nodeType;
    }

    public static void setNodeType(NodeType nodeType) {
        Cfg.nodeType = nodeType;
    }

    public static CachedContext getCommCfgContext() {
        return commCfgContext;
    }

    public static String getFileRenameCfg() {
        return configPath + FILE_RENAME_CFG;
    }

    public static String getVsysmapCfg() {
        return configPath + VSYS_MAP_CFG;
    }
    public static String getClientStatusCfg() {
        return configPath + CLIENT_STATUS_CFG;
    }

    public static String getApiVersion() {
        if (apiVersion != null) return apiVersion;
        String version = null;
        try {
            Properties prop = new Properties();
            PropertiesTool.load(prop, configPath + FILE_VERSION);
            version = prop.getProperty("api.version").trim();
        } catch (Exception e) {
            log.error("获取dataNode版本失败");
        }
        apiVersion = version;
        return version;
    }

    /**
     * 用于端口监听,外部不一定能访问,如 0.0.0.0
     *
     * @return
     */
    public static String getHostAddress() {
        hostAddress = HostIpHelper.getHostAddress(hostAddress);
        return hostAddress;
    }

    public static void setHostAddress(String hostAddress) {
        Cfg.hostAddress = hostAddress;
    }

    /**
     * 外部可以访问的具体的IP
     *
     * @return
     */
    public static String getHostTrueIp() {
        if (hostTrueIp != null) return hostTrueIp;
        hostTrueIp = HostIpHelper.getHostTrueIp(hostAddress);
        return hostTrueIp;
    }

    public static String getHostName() {
        if (hostName == null || hostName.isEmpty()) hostName = InetAddressUtil.getHostName();
        return hostName;
    }

    public static void setHostName(String hostName) {
        Cfg.hostName = hostName;
    }

    public static int getNodeId() {
        return nodeId;
    }

    public static void setNodeId(int nodeId) {
        Cfg.nodeId = nodeId;
    }

    protected Cfg() {
    }
}
