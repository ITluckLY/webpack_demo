package com.dc.smarteam.modules.sys.listener;

/**
 * Created by lvchuan on 2016/6/30.
 */

import com.dc.smarteam.modules.monitor.ftnodemonitor.entity.FtNodeMonitor;
import com.dc.smarteam.modules.monitor.ftnodemonitor.service.FtNodeMonitorService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

public class SocketOperate extends Thread {
    private static final Logger log = LoggerFactory.getLogger(SocketOperate.class);

    private ApplicationContext ctx;

    private Socket socket;
    private OutputStream outputStream;

    public SocketOperate(Socket socket, ApplicationContext ctx) {
        this.ctx = ctx;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            log.debug("serversocket start");
            //读取客户端发送来的消息
            SocketAddress socketAddress = socket.getRemoteSocketAddress();
            String[] socketAddr = socketAddress.toString().split(":");
            String ip = socketAddr[0].substring(1, socketAddr[0].length());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String mess = br.readLine();
            JSONObject json = JSONObject.fromObject(mess);
            FtNodeMonitor ftNodeMonitor = new FtNodeMonitor();
            ftNodeMonitor.setIp(ip);
            ftNodeMonitor.setFlowrate(json.getString("flowrate"));
            ftNodeMonitor.setPeriods(json.getString("periods"));
            ftNodeMonitor.setNode(json.getString("node"));
            ftNodeMonitor.setSystem(json.getString("system"));
            ftNodeMonitor.setTime(new Date());
            ftNodeMonitor.setCpu(json.getString("CPU"));
            ftNodeMonitor.setMemory(json.getString("memory"));
            ftNodeMonitor.setDisk(json.getString("disk"));
            ftNodeMonitor.setFilenumber(json.getString("filenumber"));
            FtNodeMonitorService ftNodeMonitorService = (FtNodeMonitorService) ctx.getBean("ftNodeMonitorService");
            ftNodeMonitorService.setNodeInfo(ftNodeMonitor);
            ftNodeMonitorService.insertNodeInfo(ftNodeMonitor);
            log.debug("客户端:{}", mess);
            outputStream = socket.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bw.write(mess + "\n");
            bw.flush();
        } catch (IOException e) {
            log.error("", e);
        }finally {
            try {
                if (outputStream!=null)
                    outputStream.close();
            } catch (IOException e) {
                log.error("关闭SocketOperate服务输出流outputStream出错", e);
            }
        }

    }
}
