package com.dcfs.esb.ftp.server.system;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 系统信息管理工厂
 */
public class SystemManage {
    private static final Logger log = LoggerFactory.getLogger(SystemManage.class);
    private static final Object lock = new Object();
    private static SystemManage instance = null;
    private HashMap<String, SystemInfo> systemInfoMap = new HashMap<>();

    private SystemManage() {
    }

    /**
     * @return
     */
    public static SystemManage getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    reload();
                }
            }
        }
        return instance;
    }

    /**
     *
     */
    public static void reload() {
        try {
            CachedCfgDoc.getInstance().reloadSystem();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        SystemManage inst = new SystemManage();
        inst.load();
        instance = inst;
    }

    /**
     *
     */
    public void load() {
        log.debug("加载系统信息...");
        try {
            Document doc = CachedCfgDoc.getInstance().loadSystem();
            Element root = doc.getRootElement();
            systemInfoMap.clear();
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                Element system = (Element) i.next();
                String name = system.attributeValue("name");
                String protocol = system.attributeValue("protocol");
                SystemInfo systemInfo = new SystemInfo();
                systemInfo.setName(name);
                systemInfo.setProtocol(protocol);
                Element elementIp = system.element("ip");
                if (elementIp != null) {
                    String ip = elementIp.getText().trim();
                    systemInfo.setIp(ip);
                }
                Element elementPort = system.element("port");
                if (elementPort != null) {
                    String port = elementPort.getText().trim();
                    systemInfo.setPort(port);
                }
                Element elementUsername = system.element("username");
                if (elementUsername != null) {
                    String username = elementUsername.getText().trim();
                    systemInfo.setUsername(username);
                }
                Element elementPassword = system.element("password");
                if (elementPassword != null) {
                    String password = elementPassword.getText().trim();
                    systemInfo.setPassword(password);
                }
                systemInfoMap.put(name, systemInfo);
            }
        } catch (Exception e) {
            log.error("加载系统信息失败", e);
        }
        log.debug("加载系统信息成功");
    }

    public SystemInfo getSystemInfo(String systemName) {
        return systemInfoMap.get(systemName);
    }
}
