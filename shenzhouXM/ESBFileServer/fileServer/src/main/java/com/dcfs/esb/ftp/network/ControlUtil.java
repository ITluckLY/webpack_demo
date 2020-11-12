package com.dcfs.esb.ftp.network;

import com.dcfs.esb.ftp.common.cons.EncodingCons;
import com.dcfs.esb.ftp.utils.MClassLoaderUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class ControlUtil {
    public static final Object SLEEP_LOCK = new Object();
    public static final Object CHANNEL_LOCK = new Object();
    private static final Logger log = LoggerFactory.getLogger(ControlUtil.class);
    private static ControlUtil instance;
    private Timer timer;
    private Map<String, Integer> channelSpeed;
    private Map<String, Channel> channelCollMap;
    private int netWorkSpeed;
    private int sleepTime;
    private static final String NET_CFG_FILE_NAME = "network.xml";
    private int scanTime;
    private String configPath = "./cfg/";

    public static ControlUtil getInstance() {
        if (instance != null) return instance;
        instance = syncCreateInstance();
        return instance;
    }

    private static synchronized ControlUtil syncCreateInstance() {
        if (instance == null) {
            instance = new ControlUtil();
            instance.initConfigPath();
            instance.init();
        }
        return instance;
    }

    public void initConfigPath() {
        log.debug("初始化配置文件夹路径CONFIG_PATH...");
        String cfgPath = System.getProperty("ESB_FS_CFG_PATH");
        if (cfgPath != null && cfgPath.length() > 0) {
            //需要转码
            cfgPath = new String(cfgPath.trim().getBytes(EncodingCons.SPRING_READ_VALUE_CHARSET), EncodingCons.PROPERTIES_FILE_DEF_CHARSET);
        }
        if (cfgPath == null) {
            String clsPath = MClassLoaderUtil.getBaseResourcePath();
            cfgPath = clsPath + "cfg" + File.separator;
        }
        if (!cfgPath.endsWith(File.separator))
            cfgPath = cfgPath + File.separator;
        configPath = cfgPath;
        log.debug("初始化配置文件夹路径CONFIG_PATH成功#{}", configPath);
    }

    /**
     * @deprecated 使用ResouceCtrlBySetCliHandler进行流控
     */
    @Deprecated
    private void init() {
        String pathname = configPath + NET_CFG_FILE_NAME;
        log.debug("pathnamej:{} ", pathname);
        channelCollMap = new HashMap<>();
        channelSpeed = new HashMap<>();
        if (true) return;
        SAXReader xmlReader = new SAXReader();
        try {
            Document document = xmlReader.read(new File(pathname));
            Element rootElement = document.getRootElement();
            Element paramElement = rootElement.element("prarm");
            Element speedElement = rootElement.element("channelspeed");

            this.netWorkSpeed = Integer.parseInt(paramElement.element("maxspeed").getText());
            this.sleepTime = Integer.parseInt(paramElement.element("sleeptime").getText());
            this.scanTime = Integer.parseInt(paramElement.element("scantime").getText());
            if (log.isDebugEnabled()) {
                log.debug(netWorkSpeed + "MB/S  " + sleepTime + "ms " + scanTime + "ms");
            }

            List<Element> list = speedElement.elements();
            for (Element element : list) {
                String name = element.getName();
                int speed = Integer.parseInt(element.getText().trim());
                channelSpeed.put(name, speed);
                if (log.isDebugEnabled()) log.debug(name + ":" + speed + "MB/S");
            }
        } catch (DocumentException e) {
            log.error("网络流控配置文件不存在,不能启动网络流控");
        }

    }

    /**
     * 开始带宽占用扫描
     */
    public void startScan() {
        if (log.isDebugEnabled()) {
            log.debug("流控流量[" + netWorkSpeed + "]M,睡眠时间[" + sleepTime + "]毫秒,扫描时间[" + scanTime + "]毫秒");
        }
        if (null == timer) {
            timer = new Timer();
            timer.schedule(new ContorlTask(), 0, scanTime);
        } else {
            stopScan();
            timer = new Timer();
            timer.schedule(new ContorlTask(), 0, scanTime);
        }
    }

    /**
     * 停止带宽占用扫描
     */
    public void stopScan() {
        timer.cancel();
        timer = null;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Map<String, Integer> getChannelSpeed() {
        return channelSpeed;
    }

    public void setChannelSpeed(Map<String, Integer> channelSpeed) {
        this.channelSpeed = channelSpeed;
    }

    public Map<String, Channel> getChannelCollMap() {
        return channelCollMap;
    }

    public void setChannelCollMap(Map<String, Channel> channelCollMap) {
        this.channelCollMap = channelCollMap;
    }

    public int getNetWorkSpeed() {
        return netWorkSpeed;
    }

    public void setNetWorkSpeed(int netWorkSpeed) {
        this.netWorkSpeed = netWorkSpeed;
    }

    public int getScanTime() {
        return this.scanTime;
    }

}
