package com.dc.smarteam.modules.sys.listener;

import com.dc.smarteam.common.zk.ZkService;
import com.dc.smarteam.modules.sys.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
    private static final Logger log = LoggerFactory.getLogger(WebContextListener.class);

    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        if (!SystemService.printKeyLoadMessage()) {
            return null;
        }
        WebApplicationContext context = super.initWebApplicationContext(servletContext);
        try {
            //maybe throws java.lang.ExceptionInInitializerError
            ZkService.getInstance().subscribe();
        } catch (Throwable e) {//NOSONAR
            log.error("启动初始化失败#ZK初始化出错#exit", e);
            System.exit(0);
        }
        return context;
    }
}
