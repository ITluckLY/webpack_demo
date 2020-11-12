package com.dcfs.esb.ftp.innertransfer;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mocg on 2016/7/25.
 */
public class InnerTcpServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(InnerTcpServer.class);
    private ServerSocket server;
    private ExecutorService es;
    private int port;
    private boolean running = true;

    /**
     * 构造函数，完成服务器Socket端口的创建
     *
     * @param port - 服务的端口
     * @throws IOException
     */
    public InnerTcpServer(int port) throws IOException {
        try {
            final int backlog = 100;
            server = new ServerSocket(port, backlog);
            checkIsExecutorPool();
        } catch (IOException e) {
            IOUtil.closeQuietly(server);
            log.error("启动Socket[" + port + "]失败", e);
            throw e;
        }
        this.port = port;
        // 打印到控制台，确认服务端口起来
        log.info("InnerTcpServer#[{}] is Listening...", port);
    }

    /**
     * socket的监听处理
     */
    public void run() {
        while (running) {
            Socket socket = null;
            try {
                // 接收客户端发送上来的一个Socket的请求
                socket = server.accept();
                // 创建文件服务连接对象
                FileTransferConnector conn = new FileTransferConnector(socket);
                InnerTcpServerDispatcher fts = new InnerTcpServerDispatcher(conn);

                // 创建线程或通过线程池处理客户端的请求
                if (es == null) new Thread(fts).start();
                else es.execute(fts);
            } catch (Exception e1) {
                log.error("Socket分配处理异常", e1);
                IOUtil.closeQuietly(socket);
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
                if (es == null) {
                    es = Executors.newFixedThreadPool(poolNum);
                }
                log.info("启动线程池，线程池的大小是：{}", poolNum);
            } else {
                log.error("线程池大小没有赋值，请修改配置文件");
            }
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("判断启动线程池出错，现场数配置是：" + poolNum, e);
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
