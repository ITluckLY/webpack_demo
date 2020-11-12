package com.dcfs.esb.ftp.distribute;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.InetAddressUtil;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mocg on 2016/7/14.
 */
public class DistributeFileReceiveService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DistributeFileReceiveService.class);
    private static final int BACKLOG = 100;
    private ServerSocket server;
    private ExecutorService es;
    private boolean running = true;

    public DistributeFileReceiveService(int port) throws FtpException {
        try {
            server = new ServerSocket(port, BACKLOG);
            checkIsExecutorPool();
        } catch (IOException e) {
            IOUtil.closeQuietly(server);
            throw new FtpException("分布式文件接收端启动失败", e);
        }
        // 打印到控制台，确认服务端口起来
        log.info("分布式文件接收端启动成功,端口是{}", port);
    }

    public DistributeFileReceiveService(String ip, int port) throws FtpException {
        try {
            server = new ServerSocket();
            //InetAddress addr = InetAddress.getByName(ip);//NOSONAR
            InetAddress addr = InetAddress.getByAddress(InetAddressUtil.ip4ToBytes(ip));
            InetSocketAddress address = new InetSocketAddress(addr, port);
            server.bind(address, BACKLOG);
            checkIsExecutorPool();
        } catch (IOException e) {
            IOUtil.closeQuietly(server);
            throw new FtpException("分布式文件接收端启动失败", e);
        }
        // 打印到控制台，确认服务端口起来
        log.info("分布式文件接收端启动成功#{}:{}", ip, port);
    }

    @Override
    public void run() {
        while (running) {
            Socket socket = null;
            try {
                socket = server.accept();
                DistributeFileReceiver receiver = new DistributeFileReceiver(socket);
                if (es == null) new Thread(receiver).start();
                else es.execute(receiver);
            } catch (Exception e) {
                IOUtil.closeQuietly(socket);
                log.error("分布式文件接收端接收出错", e);
            }
        }
    }

    /**
     * 功能 : 判断连接是否启用线程池
     */
    private void checkIsExecutorPool() {
        int poolNum = FtpConfig.getInstance().getPoolSize();
        try {
            if (poolNum > 0) {
                if (es == null) es = Executors.newFixedThreadPool(poolNum);
                log.info("启动线程池，线程池的大小是{}", poolNum);
            } else log.error("线程池大小没有赋值，请修改配置文件");
        } catch (Exception e) {
            log.error("判断启动线程池出错，现场数配置是：" + poolNum, e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
