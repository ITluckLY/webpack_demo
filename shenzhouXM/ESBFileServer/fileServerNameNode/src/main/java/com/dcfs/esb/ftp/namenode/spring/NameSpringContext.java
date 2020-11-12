package com.dcfs.esb.ftp.namenode.spring;

import com.dcfs.esb.ftp.namenode.spring.core.repository.BizFileRepository;
import com.dcfs.esb.ftp.namenode.spring.core.repository.CfgFileRepository;
import com.dcfs.esb.ftp.namenode.spring.core.repository.NodeMonitorRepository;
import com.dcfs.esb.ftp.spring.SpringContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by mocg on 2016/7/26.
 */
public class NameSpringContext {
    private static NameSpringContext ourInstance = new NameSpringContext();
    private AbstractApplicationContext context = SpringContext.getInstance().getContext();
    private CfgFileRepository cfgFileRepository;
    private BizFileRepository bizFileRepository;
    private NodeMonitorRepository nodeMonitorRepository;
    private boolean inited = false;

    private NameSpringContext() {
    }

    public static NameSpringContext getInstance() {
        return ourInstance;
    }

    private synchronized void init0() {
        if (inited) return;
        cfgFileRepository = (CfgFileRepository) context.getBean("cfgFileRepository");
        bizFileRepository = (BizFileRepository) context.getBean("bizFileRepository");
        nodeMonitorRepository = (NodeMonitorRepository) context.getBean("nodeMonitorRepository");
        inited = true;
    }

    public void init() {
        if (!inited) init0();
    }

    public CfgFileRepository getCfgFileRepository() {
        init();
        return cfgFileRepository;
    }

    public BizFileRepository getBizFileRepository() {
        init();
        return bizFileRepository;
    }

    public NodeMonitorRepository getNodeMonitorRepository() {
        init();
        return nodeMonitorRepository;
    }
}
