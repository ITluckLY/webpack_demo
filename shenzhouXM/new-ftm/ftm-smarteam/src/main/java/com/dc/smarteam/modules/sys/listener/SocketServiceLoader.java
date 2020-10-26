package com.dc.smarteam.modules.sys.listener;

/**
 * Created by lvchuan on 2016/6/30.
 */

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 将socket service随tomcat启动
 *
 * @author zhangzhongwen
 */
public class SocketServiceLoader implements ServletContextListener {
    //线程
    private SocketThread socketThread;
    private NetworkSocket networkSocket;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        if (null != socketThread && !socketThread.isInterrupted()) {
//            socketThread.closeSocketServer();
            socketThread.interrupt();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        if (null == socketThread || null == networkSocket) {
            ServletContext context = arg0.getServletContext();
            //取得appliction上下文
            ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

            //接收监控数据
            socketThread = new SocketThread(null, ctx);
            socketThread.start();

            //检查节点状态
            networkSocket = new NetworkSocket(ctx);
            networkSocket.start();
        }
    }

}