package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esc.ftp.comm.constant.SysCfg;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.constant.UnitCons;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件服务器配置参数类
 */
public class FtpConfig {
    public static final String NODE_NAME = "NODE_NAME";
    public static final String FILE_ROOT_PATH = "FILE_ROOT_PATH";
    public static final String FILE_BACKUP_ROOT_PATH = "FILE_BACKUP_ROOT_PATH";
    public static final String CMD_PORT = "CMD_PORT";
    public static final String FTP_SERV_PORT = "FTP_SERV_PORT";
    public static final String FTP_MANAGE_PORT = "FTP_MANAGE_PORT";
    public static final String DISTRIBUTE_FILE_RECEIVE_PORT = "DISTRIBUTE_FILE_RECEIVE_PORT";
    public static final String FTP_MAX_FILE_SIZE = "FTP_MAX_FILE_SIZE";
    public static final String FTP_PIECE_NUM = "FTP_PIECE_NUM";
    public static final String FTP_POOL_SIZE = "FTP_POOL_SIZE";
    public static final String TIME_OUT = "TIME_OUT";
    public static final String ROUTE_POOL_SIZE = "ROUTE_POOL_SIZE";
    public static final String MAX_CLIENT = "MAX_CLIENT";
    public static final String IP_CHECK = "IP_CHECK";
    public static final String HOST_IP = "HOST_IP";
    public static final String HOST_NAME = "HOST_NAME";
    public static final String MANAGER_IP = "MANAGER_IP";
    public static final String SYSTEM_NAME = "SYSTEM_NAME";
    public static final String TOKEN_POOL = "TOKEN_POOL";
    public static final String HTTP_SERV_PORT = "HTTP_SERV_PORT";//HTTP传输服务端口
    public static final String HTTP_POOL_SIZE = "HTTP_POOL_SIZE";//HTTP线程池大小
    public static final String STREAM_SERV_PORT = "STREAM_SERV_PORT";//文件块存储转发接口
    //net speed ctrl
    public static final String MAX_NETWORK_SPEED = "MAX_NETWORK_SPEED";//单位M
    public static final String NETWORK_CTRL_THRESHOLD = "NETWORK_CTRL_THRESHOLD";//单位M
    public static final String NETWORK_CTRL_SLEEP_TIME = "NETWORK_CTRL_SLEEP_TIME";//单位毫秒
    public static final String CFG_BAK_KEEP_TIME = "CFG_BAK_KEEP_TIME";//单位小时
    public static final String USE_LEGAL_NODE = "USE_LEGAL_NODE";//使用合法的节点
    public static final String MOUNT_FILE_PATH = "MOUNT_FILE_PATH";//挂载目录
    public static final String DEBUG_CAPABILITY = "DEBUG_CAPABILITY";//debug性能
    public static final String DEBUG_OUTBEAN = "DEBUG_OUTBEAN";//debug性能时输出bean
    private static final Logger log = LoggerFactory.getLogger(FtpConfig.class);
    private static final Object lock = new Object();
    //获取名称后面的数字串
    private static final Pattern nodeIdPattern = Pattern.compile("^.*[^0-9](\\d+)$");

    private static final int DEF_FTP_PIECE_NUM = 511;
    /*默认文件分片大小511K*/
    private static final int DEF_PIECE_NUM = DEF_FTP_PIECE_NUM * UnitCons.ONE_KB;
    /*最大文件分片大小1M*/
    private static final int MAX_PIECE_NUM = SysConst.MAX_PIECE_NUM;
    /*默认最大文件大小2048M*/
    private static final long DEF_MAX_FILE_SIZE = 2 * UnitCons.ONE_GB_LONG;
    /*默认异步路由处理线程池50*/
    private static final int DEF_ROUTE_POOL_SIZE = 50;
    /*文件推送消息延迟时间，避免kafka入库过慢*/
    private static final String FILE_MSG_PUSH_DELAY_TIME = "FILE_MSG_PUSH_DELAY_TIME";
    /*分发节点个数*/
    private static final String DISTRIBUTE_NODE_NUM = "DISTRIBUTE_NODE_NUM";

    private static FtpConfig instance;
    private static String configFile = null;
    private Properties prop = null;
    private int pieceNum = DEF_PIECE_NUM;
    private long maxFileSize = DEF_MAX_FILE_SIZE;
    private int routePoolSize = DEF_ROUTE_POOL_SIZE;

    private boolean ipCheck = true;
    private String systemName;
    private String nodeName;
    private int[] tokenPools;
    private long maxNetworkSpeed;
    private long networkCtrlThreshold;
    private long networkCtrlSleepTime;
    private long cfgBakKeepTime;
    private long fileMsgPushDelayTime;
    private int distributeNodeNum;
    private boolean useLegalNode = false;

    private String fileRootPath;
    private String fileBackupRootPath;
    private String mountFilePath;
    private String hostIp;
    private String hostName;
    private String managerIp;

    private int cmdPort;
    private int servPort;
    private int managePort;
    private int distributeFileReceivePort;
    private int poolSize;
    private int httpPoolSize;
    private int httpServPort;
    private int streamServPort;


    private FtpConfig() {
    }

    public static FtpConfig getInstance() {
        if (instance == null) {// 判断对象为空则创建对象
            synchronized (lock) {// 加锁
                if (instance == null) {// 再次判断，控制同步的其他线程能够马上返回
                    try {
                        reload();
                    } catch (FtpException fe) {
                        log.error("加载配置信息异常", fe);
                        throw new NestedRuntimeException("加载配置信息异常", fe);
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 重载文件的权限信息
     */
    public static void reload() throws FtpException {
        CachedCfgDoc.getInstance().reloadCfg();
        FtpConfig cf = new FtpConfig();
        cf.load();
        instance = cf;
    }

    /**
     * 加载配置文件信息
     *
     * @throws FtpException
     */
    public void load() throws FtpException {
        try {
            prop = loadFromXML();
            this.pieceNum = StringTool.toInt(prop.getProperty(FTP_PIECE_NUM), DEF_FTP_PIECE_NUM) * 1024;//NOSONAR
            this.maxFileSize = StringTool.toInt(prop.getProperty(FTP_MAX_FILE_SIZE), 2048) * 1024L * 1024;//NOSONAR
            this.routePoolSize = StringTool.toInt(prop.getProperty(ROUTE_POOL_SIZE), 50);//NOSONAR

            Cfg.setHostAddress(prop.getProperty(HOST_IP));
            Cfg.setHostName(prop.getProperty(HOST_NAME));
            Cfg.setNodeId(tranToNodeId(prop.getProperty(NODE_NAME)));
            //TOKEN_POOL
            String tokenPoolStr = prop.getProperty(TOKEN_POOL);
            if (StringUtils.isNotEmpty(tokenPoolStr)) {
                String[] priorityPoolArr = tokenPoolStr.split(",");
                int[] arr = new int[priorityPoolArr.length];
                for (int i = 0; i < priorityPoolArr.length; i++) {
                    arr[i] = Integer.parseInt(priorityPoolArr[i]);
                }
                this.tokenPools = arr;
            } else tokenPools = new int[0];
            //network speed
            maxNetworkSpeed = StringTool.toLong(prop.getProperty(MAX_NETWORK_SPEED), 8L) * 1024L * 1024L;//NOSONAR
            networkCtrlThreshold = StringTool.toLong(prop.getProperty(NETWORK_CTRL_THRESHOLD), 4L) * 1024L * 1024L;//NOSONAR
            networkCtrlSleepTime = StringTool.toLong(prop.getProperty(NETWORK_CTRL_SLEEP_TIME), 2000L);//NOSONAR
            cfgBakKeepTime = StringTool.toLong(prop.getProperty(CFG_BAK_KEEP_TIME, "168"), 168L) * 3600000L;//NOSONAR
            fileMsgPushDelayTime = StringTool.toLong(prop.getProperty(FILE_MSG_PUSH_DELAY_TIME), 2000L);//NOSONAR
            distributeNodeNum = StringTool.toInt(prop.getProperty(DISTRIBUTE_NODE_NUM), 1);//NOSONAR
            useLegalNode = BooleanTool.toBoolean(prop.getProperty(USE_LEGAL_NODE, "false"));
            ipCheck = BooleanTool.toBoolean(prop.getProperty(IP_CHECK, "true"));
            CapabilityDebugHelper.setDebug(BooleanTool.toBoolean(prop.getProperty(DEBUG_CAPABILITY, "0")));
            CapabilityDebugHelper.setOutBean(BooleanTool.toBoolean(prop.getProperty(DEBUG_OUTBEAN, "0")));
            //
            systemName = prop.getProperty(SYSTEM_NAME);
            nodeName = prop.getProperty(NODE_NAME);
            fileRootPath = prop.getProperty(FILE_ROOT_PATH);
            fileBackupRootPath = prop.getProperty(FILE_BACKUP_ROOT_PATH);
            mountFilePath = prop.getProperty(MOUNT_FILE_PATH);

            hostIp = prop.getProperty(HOST_IP);
            hostName = prop.getProperty(HOST_NAME);
            managerIp = prop.getProperty(MANAGER_IP);

            cmdPort = StringTool.toInt(prop.getProperty(CMD_PORT), 6000);//NOSONAR
            servPort = StringTool.toInt(prop.getProperty(FTP_SERV_PORT), 6001);//NOSONAR
            managePort = StringTool.toInt(prop.getProperty(FTP_MANAGE_PORT), 6002);//NOSONAR
            distributeFileReceivePort = StringTool.toInt(prop.getProperty(DISTRIBUTE_FILE_RECEIVE_PORT), 6003);//NOSONAR

            poolSize = StringTool.toInt(prop.getProperty(FTP_POOL_SIZE), 50);//NOSONAR
            httpPoolSize = StringTool.toInt(prop.getProperty(HTTP_POOL_SIZE), 50);//NOSONAR
            httpServPort = StringTool.toInt(prop.getProperty(HTTP_SERV_PORT), 6005);//NOSONAR
            streamServPort = StringTool.toInt(prop.getProperty(STREAM_SERV_PORT), 6004);//NOSONAR
        } catch (Exception e) {
            log.error("加载配置文件出错", e);
            throw new FtpException(FtpErrCode.LOAD_CONFIG_FILE_ERROR, e);
        }

        if (pieceNum > MAX_PIECE_NUM) {
            log.warn("文件分片最大{}Byte", MAX_PIECE_NUM);
            pieceNum = MAX_PIECE_NUM;
        }
        SysCfg.setPieceNum(pieceNum);
        networkCtrlSleepTime = Math.max(networkCtrlSleepTime, 0);
    }

    private Properties loadFromXML() throws IOException, DocumentException {
        File cfgFile = new File(configFile);
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(cfgFile)) {
            SAXReader r = new SAXReader();
            Document doc = r.read(fis);
            List l = doc.selectNodes("/properties/entry");
            for (Object aL : l) {
                Element e = (Element) aL;
                p.put(e.attributeValue("key"), e.getText().trim());
            }
        } catch (DocumentException e) {
            log.error("载入节点参数文件出现错误", e);
            throw e;
        }
        return p;
    }

    /**
     * 由NodeType+节点名称后三位数字转换过来, eg:FSD003=>1*1000+003=1003
     * 最大值为2^13=8192
     *
     * @param nodeName
     * @return
     */
    private static int tranToNodeId(String nodeName) {
        if (nodeName == null) return 0;
        Matcher matcher = nodeIdPattern.matcher(nodeName);
        if (matcher.find()) {
            String num = matcher.group(1);
            String num3 = num.substring(Math.max(0, num.length() - 3));//NOSONAR
            return Cfg.getNodeType().num() * 1000 + Integer.parseInt(num3);//NOSONAR
        }
        //4000-8000
        return RandomUtils.nextInt(NodeType.UNDEFINED.num() * 1000, 8000);//NOSONAR
    }

    /**
     * 获取配置文件的路径
     *
     * @return 文件的路径
     */
    public static String getConfigFile() {
        return configFile;
    }

    /**
     * 设置配置文件的路径
     *
     * @param cfgFile 文件的路径
     */
    public static void setConfigFile(String cfgFile) {
        configFile = cfgFile;
    }

    /**
     * 获取配置文件对应的配置信息
     *
     * @return 配置信息
     */
    public Properties getProp() {
        return prop;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }


    //get

    public String getNodeName() {
        return nodeName;
    }

    public int getCommandPort() {
        return cmdPort;
    }

    /**
     * 获取本地服务的端口
     *
     * @return 服务的端口
     */
    public int getServPort() {
        return servPort;
    }

    /**
     * 获取本地服务的管理端口
     *
     * @return 管理的端口
     */
    public int getManagePort() {
        return managePort;
    }

    /**
     * 获取线程池的大小
     *
     * @return 线程池的大小
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * 获取文件分片的大小
     *
     * @return 分片的大小
     */
    public int getPieceNum() {
        return this.pieceNum;
    }

    /**
     * 获取文件大小的最大值
     *
     * @return 最大的文件大小
     */
    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    public int getRoutePoolSize() {
        return routePoolSize;
    }

    public int getDistributeFileReceivePort() {
        return distributeFileReceivePort;
    }

    public int[] getTokenPools() {
        return tokenPools;
    }

    public long getMaxNetworkSpeed() {
        return maxNetworkSpeed;
    }

    public long getNetworkCtrlThreshold() {
        return networkCtrlThreshold;
    }

    public long getNetworkCtrlSleepTime() {
        return networkCtrlSleepTime;
    }

    public long getCfgBakKeepTime() {
        return cfgBakKeepTime;
    }

    public boolean isUseLegalNode() {
        return useLegalNode;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public String getFileBackupRootPath() {
        return fileBackupRootPath;
    }

    public String getHostIp() {
        return hostIp;
    }

    public String getHostName() {
        return hostName;
    }

    public String getManagerIp() {
        return managerIp;
    }

    public String getSystemName() {
        return systemName;
    }

    public boolean isIpCheck() {
        return ipCheck;
    }

    public String getMountFilePath() {
        return mountFilePath;
    }

    /**
     * 获取服务器超时时间
     *
     * @return
     */
    public int getTimeOut() {
        return Integer.parseInt(prop.getProperty(TIME_OUT));
    }

    public long getFileMsgPushDelayTime() {
        return fileMsgPushDelayTime;
    }

    public void setFileMsgPushDelayTime(long fileMsgPushDelayTime) {
        this.fileMsgPushDelayTime = fileMsgPushDelayTime;
    }

    public int getDistributeNodeNum() {
        return distributeNodeNum;
    }

    public void setDistributeNodeNum(int distributeNodeNum) {
        this.distributeNodeNum = distributeNodeNum;
    }

    public int getHttpPoolSize() {
        return httpPoolSize;
    }

    public int getHttpServPort() {
        return httpServPort;
    }

    public int getStreamServPort() {
        return streamServPort;
    }
}
