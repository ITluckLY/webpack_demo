package com.dcfs.esc.ftp.namenode.spring;

import com.dcfs.esb.ftp.namenode.spring.core.repository.NodeMonitorRepository;
import com.dcfs.esb.ftp.spring.SpringContext;

/**
 * Created by mocg on 2017/8/23.
 */
public final class ServiceBeans {

    private static SpringContext springContext = SpringContext.getInstance();

    private ServiceBeans() {
    }

    public static NodeMonitorRepository getNodeMonitorRepository() {
        return springContext.getBean("nodeMonitorRepository");
    }
}
