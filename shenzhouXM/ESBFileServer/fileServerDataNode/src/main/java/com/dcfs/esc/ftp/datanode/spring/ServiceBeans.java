package com.dcfs.esc.ftp.datanode.spring;

import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esc.ftp.datanode.service.AuthService;

/**
 * Created by mocg on 2017/8/23.
 */
public class ServiceBeans {

    private static SpringContext springContext = SpringContext.getInstance();

    private ServiceBeans() {
    }

    public static AuthService getAuthService() {
        return springContext.getBean("authService");
    }

    public static DataEfsProperties getDataEfsProperties() {
        return springContext.getBean("dataEfsProperties");
    }
}
