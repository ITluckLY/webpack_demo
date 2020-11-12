package com.dcfs.esb.ftp.spring;

import com.dcfs.esb.ftp.helper.ContextHelper;
import com.dcfs.esb.ftp.interfases.NodeCfgSyncFace;
import com.dcfs.esb.ftp.server.config.CfgDocServiceFace;
import com.dcfs.esb.ftp.server.config.CommCfgCxtKey;
import com.dcfs.esb.ftp.server.invoke.bizfile.BizFileServiceFactoryFace;
import com.dcfs.esb.ftp.server.invoke.filemsg2client.FileMsg2ClientServiceFactoryFace;
import com.dcfs.esb.ftp.server.invoke.node.NodesServiceFactoryFace;
import com.dcfs.esb.ftp.server.invoke.nodesync.NodeSyncServiceFactoryFace;
import com.dcfs.esb.ftp.spring.outservice.IFileRecord;
import com.dcfs.esb.ftp.spring.outservice.IFileTargetNodeService;
import com.dcfs.esb.ftp.spring.service.FileRecord2FileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by mocg on 2016/7/13.
 */
public final class SpringContext {
    private static final Logger log = LoggerFactory.getLogger(SpringContext.class);

    private static SpringContext ourInstance = new SpringContext();
    private AbstractApplicationContext context;
    private FileRecord2FileService fileRecord2FileService;
    private IFileRecord iFileRecord;
    private NodeCfgSyncFace nodeCfgSyncService;
    private CfgDocServiceFace nodeCfgDocService;
    private EfsProperties efsProperties;
    //    private BizFileServiceFace bizFileService;//NOSONAR
    private BizFileServiceFactoryFace bizFileServiceFactory;
    private FileMsg2ClientServiceFactoryFace fileMsg2ClientServiceFactory;
    private NodeSyncServiceFactoryFace nodeSyncServiceFactory;
    private NodesServiceFactoryFace nodesServiceFactory;
    private IFileTargetNodeService fileTargetNodeService;

    private SpringContext() {
        init0();
    }

    public static SpringContext getInstance() {
        return ourInstance;
    }

    private synchronized void init0() {
        if (context != null) return;
        log.info("字符编码#file.encoding:{},defaultCharset:{}", System.getProperty("file.encoding"), Charset.defaultCharset());
        log.info("开始初始化SpringContext...");
        context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        fileRecord2FileService = getBeanIgnoreExist("fileRecord2FileService");
        nodeCfgSyncService = getBeanIgnoreExist("nodeCfgSyncService");
        nodeCfgDocService = getBeanIgnoreExist("nodeCfgDocService");
        bizFileServiceFactory = getBeanIgnoreExist("bizFileServiceFactory");
        fileMsg2ClientServiceFactory = getBeanIgnoreExist("fileMsg2ClientServiceFactory");
        nodeSyncServiceFactory = getBeanIgnoreExist("nodeSyncServiceFactory");
        nodesServiceFactory = getBeanIgnoreExist("nodesServiceFactory");
        fileTargetNodeService = getBeanIgnoreExist("fileTargetNodeService");

        context.registerShutdownHook();
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream("beans.properties");
            if (in != null) {
                properties.load(in);
                in.close();
                in = null;
            }
        } catch (IOException e) {
            log.error("初始化bean err", e);
        } finally {
            if (in != null) IOUtils.closeQuietly(in);
        }
        String fileRecordBeanName = (String) properties.get("IFileRecordBeanName");
        iFileRecord = getBeanIgnoreExist(fileRecordBeanName);
        //初始化CommContext一些参数
        efsProperties = (EfsProperties) context.getBean("efsProperties");
        boolean ifKfkGroupIdAppendHost = efsProperties.getIfKfkGroupIdAppendHost();
        ContextHelper.putByCommCfgCxt(CommCfgCxtKey.IF_KFK_GROUP_ID_APPEND_HOST, ifKfkGroupIdAppendHost);

        log.info("初始化SpringContext成功");
        log.info("inited字符编码#file.encoding:{},defaultCharset:{}", System.getProperty("file.encoding"), Charset.defaultCharset());
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanIgnoreExist(String beanName) {
        if (StringUtils.isEmpty(beanName)) return null;
        if (!context.containsBean(beanName)) return null;
        return (T) context.getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        return (T) context.getBean(beanName);
    }

    public void init() {
        if (context == null) init0();
    }

    public AbstractApplicationContext getContext() {
        init();
        return context;
    }

    public FileRecord2FileService getFileRecord2FileService() {
        return fileRecord2FileService;
    }

    public IFileRecord getiFileRecord() {
        return iFileRecord;
    }

    public NodeCfgSyncFace getNodeCfgSyncService() {
        return nodeCfgSyncService;
    }

    public CfgDocServiceFace getCfgDocServiceFace() {
        return nodeCfgDocService;
    }

    public EfsProperties getEfsProperties() {
        return efsProperties;
    }

    public BizFileServiceFactoryFace getBizFileServiceFactory() {
        return bizFileServiceFactory;
    }

    public NodeSyncServiceFactoryFace getNodeSyncServiceFactory() {
        return nodeSyncServiceFactory;
    }

    public NodesServiceFactoryFace getNodesServiceFactory() {
        return nodesServiceFactory;
    }

    public IFileTargetNodeService getFileTargetNodeService() {
        return fileTargetNodeService;
    }

    public FileMsg2ClientServiceFactoryFace getFileMsg2ClientServiceFactory() {
        return fileMsg2ClientServiceFactory;
    }
}
