package com.dc.smarteam.helper;

import com.dc.smarteam.common.config.Cfg;
import com.dc.smarteam.common.utils.SpringContextHolder;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mocg on 2016/11/3.
 */
public class CurrNameNodeHelper {

    private static Cfg cfg;
    private static final String FT_SERVICE_NODE = "ftServiceNode";

    private CurrNameNodeHelper() {
    }

    public static FtServiceNode getCurrNameNode(HttpServletRequest request) {
        return (FtServiceNode) request.getSession().getAttribute(FT_SERVICE_NODE);
    }

    public static void setCurrNameNode(HttpServletRequest request, FtServiceNode ftServiceNode) {
        if (ftServiceNode == null) return;
        if (cfg == null) {
            ApplicationContext context = SpringContextHolder.getApplicationContext();
            cfg = (Cfg) context.getBean("cfg");
        }
        ftServiceNode.setIpAddress(cfg.getNameNodeIP());
        ftServiceNode.setCmdPort(cfg.getNameNodePort());
        ftServiceNode.setName(cfg.getNameNodeName());
        ftServiceNode.setType(cfg.getNameNodeType());
        request.getSession().setAttribute(FT_SERVICE_NODE, ftServiceNode);
    }

    public static void setCurrSysname(HttpServletRequest request, String sysname) {
        FtServiceNode ftServiceNode = (FtServiceNode) request.getSession().getAttribute(FT_SERVICE_NODE);
        if (ftServiceNode == null) ftServiceNode = new FtServiceNode();
        ftServiceNode.setSystemName(sysname);
        request.getSession().setAttribute(FT_SERVICE_NODE, ftServiceNode);
    }
}
