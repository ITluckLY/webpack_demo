package com.dc.smarteam.util;

import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2016/3/2.
 */
public class SocketClient {
    private static final Logger log = LoggerFactory.getLogger(SocketClient.class);

    private Socket socket = null;
    private DataOutputStream out = null;

    /**
     * 构造函数，客户端向服务器建立连接使用
     *
     * @param ip   - 服务器IP地址
     * @param port - 服务器IP端口
     */
    public SocketClient(String ip, int port) throws Throwable {
        try {
            socket = new Socket(ip, port);
            if (log.isInfoEnabled()) {
                log.info("建立文件服务器连接 --> " + ip + ":" + port);
            }
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {

            if (log.isInfoEnabled()) {
                log.info("建立文件服务器连接异常 --> " + ip + ":" + port);
            }
            throw e;
        }
    }

    public static void sendToNode(FtServiceNode ftServiceNode, String message) throws Throwable {
        String port = ftServiceNode.getCmdPort();
        SocketClient socketClient = new SocketClient(ftServiceNode.getIpAddress(), Integer.valueOf(port));
        socketClient.send(message);
    }

    public void send(String msg) throws Throwable {
        try {
            out.writeUTF(msg);
            out.flush();
            out.close();
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.info("发送信息失败！");
            }
            throw e;
        }

    }
}
