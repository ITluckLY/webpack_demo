package com.dcfs.esb.ftp.server.client;

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
 * 客户端状态信息管理工厂
 */
public class ClientManage {
    private static final Logger log = LoggerFactory.getLogger(ClientManage.class);
    public static final String CLIENT_ID = "id";
    public static final String CLIENT_STATUS = "status";
    public static final String CLIENT_TYPE = "type";
    public static final String CLIENT_MODE = "mode";
    public static final String CLIENT_NAME = "name";
    private static final Object lock = new Object();
    private static ClientManage instance = null;
    private HashMap<String, ClientStatusInfo> clientStatusMap = new HashMap<>();

    private ClientManage() {
    }

    /**
     * @return
     */
    public static ClientManage getInstance() {
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
            CachedCfgDoc.getInstance().reloadClientStatus();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        ClientManage inst = new ClientManage();
        inst.load();
        instance = inst;
    }

    /**
     *
     */
    public void load() {
        log.debug("加载客户端状态信息...");
        try {
            Document doc = CachedCfgDoc.getInstance().loadClientStatus();
            Element root = doc.getRootElement();
            clientStatusMap.clear();
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                Element client = (Element) i.next();
                String id = client.attributeValue(CLIENT_ID).trim();
                String status = client.attributeValue(CLIENT_STATUS).trim();
                String type = client.attributeValue(CLIENT_TYPE).trim();
                String mode = client.attributeValue(CLIENT_MODE).trim();
                String name = client.attributeValue(CLIENT_NAME).trim();

                ClientStatusInfo clientStatusInfo = new ClientStatusInfo();
                clientStatusInfo.setId(id);
                clientStatusInfo.setStatus(status);
                clientStatusInfo.setType(type);
                clientStatusInfo.setMode(mode);
                clientStatusInfo.setName(name);

                clientStatusMap.put(id, clientStatusInfo);
            }
        } catch (Exception e) {
            log.error("加载客户端状态信息失败", e);
        }
        log.debug("加载客户端状态信息成功");
    }

    public ClientStatusInfo getClientStatus(String clientId) {
        return clientStatusMap.get(clientId);
    }
}
