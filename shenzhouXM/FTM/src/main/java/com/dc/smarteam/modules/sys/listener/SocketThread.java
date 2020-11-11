package com.dc.smarteam.modules.sys.listener;

/**
 * Created by lvchuan on 2016/6/30.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.net.ServerSocket;
import java.net.Socket;


public class SocketThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(SocketThread.class);
    private ServerSocket serverSocket = null;
    private ApplicationContext ctx;

    public SocketThread(ServerSocket serverSocket, ApplicationContext ctx) {
        try {
            if (null == serverSocket) {
                this.serverSocket = new ServerSocket(8877);
                this.ctx = ctx;
                log.info("socket start");
            }
        } catch (Exception e) {
            log.error("SocketThread创建socket服务出错", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                log.info("run");
                if (null != socket && !socket.isClosed()) {
                    //处理接收的数据
                    new SocketOperate(socket, ctx).start();
                    socket.setSoTimeout(60000);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
}