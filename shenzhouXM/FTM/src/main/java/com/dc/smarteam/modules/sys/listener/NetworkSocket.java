package com.dc.smarteam.modules.sys.listener;

import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;


/**
 * Created by lvchuan on 2016/7/18.
 */
public class NetworkSocket extends Thread {

    protected String ip;
    protected int port;
    protected Socket socket;
    private long keepAliveDelay = 30000;
    private long checkDelay = 1000;
    private ApplicationContext ctx;
    private boolean check = true;
    private long lastSendTime;
    private static final String NODE_MONITOR_SERVICE = "ftNodeMonitorService";

    public NetworkSocket(ApplicationContext ctx) {
        this.ctx = ctx;
        this.lastSendTime = System.currentTimeMillis();
    }

    public void setStateSuccess(FtNodeMonitor ftNodeMonitor) {
        FtNodeMonitorService ftNodeMonitorService = (FtNodeMonitorService) ctx.getBean(NODE_MONITOR_SERVICE);
        ftNodeMonitor.setState("1");
        ftNodeMonitorService.setNetState(ftNodeMonitor);
    }

    public void setStateFail(FtNodeMonitor ftNodeMonitor) {
        FtNodeMonitorService ftNodeMonitorService = (FtNodeMonitorService) ctx.getBean(NODE_MONITOR_SERVICE);
        ftNodeMonitor.setState("0");
        ftNodeMonitorService.setNetState(ftNodeMonitor);
    }

    @Override
    public void run() {
        while (check) {
            if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
                check = false;
                FtNodeMonitorService ftNodeMonitorService = (FtNodeMonitorService) ctx.getBean(NODE_MONITOR_SERVICE);
                List<FtNodeMonitor> nodelist = ftNodeMonitorService.findList(new FtNodeMonitor());
                for (FtNodeMonitor node : nodelist) {
                    try {
                        ip = node.getIp();
                        port = Integer.parseInt(node.getPort());
                        socket = new Socket(ip, port);
                        //构建IO
                        InputStream is = socket.getInputStream();
                        OutputStream os = socket.getOutputStream();
                        //向服务器端发送一条消息
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        bw.write("connect" + node.getNode() + "\n");
                        bw.flush();

                        //读取服务器返回的消息
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String mess = br.readLine();
                        if (mess.equals("1")) {
                            setStateSuccess(node);
                        } else {
                            setStateFail(node);
                        }
                    } catch (NumberFormatException e) {
                        setStateFail(node);
                    } catch (UnknownHostException e) {
                        setStateFail(node);
                    } catch (IOException e) {
                        setStateFail(node);
                    }
                }
                check = true;
                lastSendTime = System.currentTimeMillis();
            } else {
                try {
                    Thread.sleep(checkDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }
}
