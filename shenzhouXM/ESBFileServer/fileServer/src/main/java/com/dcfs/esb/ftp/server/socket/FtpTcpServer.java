package com.dcfs.esb.ftp.server.socket;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.InetAddressUtil;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件服务器的Socket服务
 */
public class FtpTcpServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(FtpTcpServer.class);
    private static int backlog = 100; //NOSONAR
    private ServerSocket server;
    private ExecutorService es;
    private boolean running = true;

    /**
     * 构造函数，完成服务器Socket端口的创建
     *
     * @param port - 服务的端口
     * @throws FtpException
     */
    public FtpTcpServer(int port) throws FtpException {
        try {
            server = new ServerSocket(port, backlog);
            checkIsExecutorPool();
        } catch (Exception e) {
            IOUtil.closeQuietly(server);
            log.error("FtpTcpServer启动Socket[:{}]", port, e);
            throw new FtpException(FtpErrCode.SOCKET_ERROR);
        }
        // 打印到控制台，确认服务端口起来
        log.info("FtpTcpServer#[{}] is Listening...", port);
    }

    public FtpTcpServer(String ip, int port) throws FtpException {
        try {
            server = new ServerSocket();
            //InetAddress addr = InetAddress.getByName(ip);//NOSONAR
            InetAddress addr = InetAddress.getByAddress(InetAddressUtil.ip4ToBytes(ip));
            InetSocketAddress address = new InetSocketAddress(addr, port);
            server.bind(address, backlog);
            checkIsExecutorPool();
        } catch (Exception e) {
            IOUtil.closeQuietly(server);
            log.error("FtpTcpServer启动Socket[{}:{}]", ip, port, e);
            throw new FtpException(FtpErrCode.SOCKET_ERROR);
        }
        // 打印到控制台，确认服务端口起来
        log.info("FtpTcpServer#[{}:{}] is Listening...", ip, port);
    }

    public static int getBacklog() {
        return backlog;
    }

    public static void setBacklog(int backlog) {
        FtpTcpServer.backlog = backlog;
    }

    /**
     * socket的监听处理
     */
    @Override
    public void run() {
        CapabilityDebugHelper.init();
        while (running) {
            // 接收客户端发送上来的一个Socket的请求
            Socket socket = null;
            CapabilityDebugHelper.markCurrTime("socketStart");
            try {
                socket = server.accept();
                // 创建文件服务器连接对象
                TcpServerDispatcher fts = new TcpServerDispatcher(new FtpConnector(socket));
                CapabilityDebugHelper.markCurrTime("socketFtpConn");
                // 创建线程或通过线程池处理客户端的请求
                if (es == null) new Thread(fts).start();
                else es.execute(fts);
            } catch (Exception e1) {
                IOUtil.closeQuietly(socket);
                log.error("Socket分配处理异常", e1);
            }
            CapabilityDebugHelper.markCurrTime("socketEnd");
            CapabilityDebugHelper.printAndClean(0);
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
            log.error("判断启动线程池出错，现场数配置是：{}", poolNum, e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
