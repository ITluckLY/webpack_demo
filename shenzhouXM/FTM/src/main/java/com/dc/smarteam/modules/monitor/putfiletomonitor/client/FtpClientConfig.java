package com.dc.smarteam.modules.monitor.putfiletomonitor.client;

import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.ScrtUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * 文件服务器客户端的连接工厂，用于生成文件服务器的连接
 */
public class FtpClientConfig {
    public static final String ClientConfigPath = "FtpClientConfig.properties";
    private static final Object lock = new Object();
    private static Logger log = LoggerFactory.getLogger(FtpClientConfig.class);
    private static FtpClientConfig instance;
    private String serverIp;
    private int port;
    private int apiCmdPort;
    private String uid;
    private String passwd;
    private String key;
    private boolean usedTargetNode;

    private FtpClientConfig() {
    }

    public FtpClientConfig(String serverIp, int port, String uid, String passwd, String key) {
        this.serverIp = serverIp;
        this.port = port;
        this.uid = uid;
        this.passwd = passwd;
        this.key = key;
    }

    public static FtpClientConfig getInstance() {
        if (instance != null)
            return instance;
        else {
            synchronized (lock) {
                if (instance == null)
                    instance = new FtpClientConfig();
            }
            return instance;
        }
    }

    public static void saveConf(Properties prop, String file) {
        FileOutputStream fos = null;
        if (log.isDebugEnabled()) {
            log.debug("保存配置到文件:" + file);
        }
        try {
            fos = new FileOutputStream(file);
            prop.store(fos, "ESBFileClientConfig");
        } catch (Exception ex) {
            log.error("配置文件保存失败！", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    log.error("关闭流异常！", ex);
                }
            }
        }
    }

    /**
     * 获取文件服务器的IP地址
     *
     * @return IP地址
     */
    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * 获取文件服务器的服务端口
     *
     * @return 服务端口
     */
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取文件服务器的用户名
     *
     * @return 用户名
     */
    public String getUid() {
        return uid;
    }

    /**
     * 获取文件服务器的用户密码
     *
     * @return 用户密码
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * 获取密钥
     *
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * 获取与文件服务的连接
     *
     * @return 服务器连接
     * @throws FtpException
     */
    public FtpConnector getConnector() throws FtpException {
        return new FtpConnector(serverIp, port);
    }

    /**
     * 加载配置文件
     *
     * @param file 配置文件
     */
    public void loadConf(String file) {
        Properties tmpsysProp = new Properties();
        InputStream input = null;
        try {
            if (null == file || !new File(file).exists()) {
                if (log.isDebugEnabled()) {
                    log.debug("文件[" + file + "]不存在,尝试通过当前类加载器加载配置信息...");
                }
                //获取文件路径,保存时使用
                file = FtpClientConfig.class.getResource("/FtpClientConfig.properties").getFile();
                //用空格替换URL的%20
                file = file.replace("%20", " ");
                if (null != file) {
                    input = FtpClientConfig.class.getResourceAsStream("/FtpClientConfig.properties");
                }
            } else {
                input = new FileInputStream(file);
            }
            tmpsysProp.load(input);
            this.serverIp = tmpsysProp.getProperty("serverIp");
            this.port = Integer.parseInt(tmpsysProp.getProperty("port"));
            String ur = tmpsysProp.getProperty("uid");
            this.key = tmpsysProp.getProperty("key");
            this.uid = ur;
            String pw = tmpsysProp.getProperty("passwd");
            //如果密码是没有加密的，加密然后存储
            if (pw.startsWith("${3DES}")) {
                pw = ScrtUtil.decryptEsb(pw.substring(7));
                this.passwd = pw;
            } else {
                this.passwd = pw;
                //加密保存
                tmpsysProp.setProperty("passwd", "${3DES}" + ScrtUtil.encryptEsb(pw));
                saveConf(tmpsysProp, file);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("文件[" + file + "]加载失败：", e);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }
        if (log.isDebugEnabled()) {
            log.debug("配置文件[" + file + "]加载成功!");
        }
    }

    public int getApiCmdPort() {
        return apiCmdPort;
    }

    public void setApiCmdPort(int apiCmdPort) {
        this.apiCmdPort = apiCmdPort;
    }

    public boolean isUsedTargetNode() {
        return usedTargetNode;
    }

    public void setUsedTargetNode(boolean usedTargetNode) {
        this.usedTargetNode = usedTargetNode;
    }
}
