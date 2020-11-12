package com.dcfs.esb.ftp.impls.flow;

import com.dcfs.esb.ftp.common.cons.EmptyCons;
import com.dcfs.esb.ftp.common.error.FtpException;
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

public class ServiceFlowManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceFlowManager.class);
    private static final Object lock = new Object();
    private static ServiceFlowManager instance;
    private Map<String, ServiceFlowBean> flows = new HashMap<>();
    private ServiceFlowBean defaultServiceFlowBean = null;

    private ServiceFlowManager() {
        load();
    }

    /**
     * 实例化流程工厂
     *      flow信息
     * @return
     */
    public static ServiceFlowManager getInstance() {
        if (instance != null) return instance;
        else {
            synchronized (lock) {
                if (instance == null) instance = new ServiceFlowManager();
                return instance;
            }
        }
    }

    public void reload() throws FtpException {
        synchronized (lock) {
            CachedCfgDoc.getInstance().reloadFlow();
            instance = new ServiceFlowManager();
        }
    }

    /**
     * 初始化流程管理
     */
    public void load() {
        Map<String, ServiceFlowBean> flowMap = new HashMap<>();
        try {
            Document doc = CachedCfgDoc.getInstance().loadFlow();
            List l = doc.selectNodes("/flows/flow");
            for (Object aL : l) {
                Element e = (Element) aL; // 此处获取flow。xml 文件类的所有数据
                String name = e.attributeValue("name");// 获取每个流程的 流程名称
                if (StringUtils.isEmpty(name)) continue;
                try { //NOSONAR
                    addFlow(flowMap, name, createServiceFlowBean(e));
                    if (log.isInfoEnabled()) log.info("加载处理流程" + name + ":" + e.asXML());
                } catch (Exception e2) {
                    log.error("加载处理流程失败" + name + ":" + e.asXML(), e2);
                }
            }
            Map<String, ServiceFlowBean> temp = this.flows;
            this.flows = flowMap;
            temp.clear();
        } catch (Exception e) {
            log.error("加载处理流程失败", e);
        }
    }

    /**
     * 创建流程对象，并初始化
     *
     * @param e
     * @return
     */
    public ServiceFlowBean createServiceFlowBean(Element e) {
        ServiceFlowBean sfb = new ServiceFlowBean();
        sfb.setFlowName(e.attributeValue("name"));
        sfb.setDescribe(e.attributeValue("describe"));
        sfb.setFlowType(e.attributeValue("type"));

        Properties properties = new Properties();
        String propertiesStr = e.attributeValue("properties");
        if (StringUtils.isNotEmpty(propertiesStr)) {
            String[] pp = propertiesStr.trim().split(",");
            for (String proper : pp) {
                String[] propers = proper.split("=");
                if (propers.length == 2) {//NOSONAR
                    properties.put(propers[0], propers[1]);
                }
            }
            sfb.setFlowServiceProperties(properties);
        }

        String components = e.attributeValue("components");
        if (StringUtils.isNotEmpty(components)) {
            sfb.setBaseService(components.trim().split(","));
        } else sfb.setBaseService(EmptyCons.EMPTY_STRING_ARRAY);

        return sfb;
    }

    /**
     * 添加流程
     *
     * @param flowName
     * @param flowBean
     * @return
     */
    public void addFlow(Map<String, ServiceFlowBean> flows, String flowName, ServiceFlowBean flowBean) {
        if (flowBean == null) return;
        flows.put(flowName, flowBean);
    }

    /**
     *     获取流程
     *
     * @param name
     * @return
     */
    public ServiceFlowBean getFlow(String name) { // ****************
        ServiceFlowBean flowBean = flows.get(name); // 前端页面指定的流程名称。
        if (flowBean != null) return flowBean;
        log.error("未获取到名称为{}的处理流程，返回默认处理流程", name);
        return getDefaultServiceFlowBean();
    }

    /**
     *   ///////  返回流程节点。
     * @return
     */
    private ServiceFlowBean getDefaultServiceFlowBean() {
        if (defaultServiceFlowBean != null) return defaultServiceFlowBean;
        ServiceFlowBean flowBean = new ServiceFlowBean();

        flowBean.setBaseService(new String[]{"IsolStateCheckService", "IPCheckService", "PwdAuthWithSeqService", "PushDataNodeListService"
                , "InitService", "FileRenameService", "ResourceCtrlService", "FileQueryService"});
        defaultServiceFlowBean = flowBean;
        return defaultServiceFlowBean;
    }
}
