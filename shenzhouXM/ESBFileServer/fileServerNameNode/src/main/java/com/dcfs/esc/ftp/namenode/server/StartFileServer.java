package com.dcfs.esc.ftp.namenode.server;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.impls.uuid.IdWorker;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.kafka.KfkProducer;
import com.dcfs.esb.ftp.namenode.handler.ShutdownHookHandler;
import com.dcfs.esb.ftp.namenode.kafka.KfkCommConsumer;
import com.dcfs.esb.ftp.namenode.kafka.KfkConsumer;
import com.dcfs.esb.ftp.namenode.service.ZkService;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.invoke.BusDealSer;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.utils.ProcessIdUtil;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import com.dcfs.esc.ftp.namenode.network.server.NameServer;
import com.dcfs.esc.ftp.namenode.spring.NameSpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 文件服务启动类
 */
public class StartFileServer {
    private static final Logger log = LoggerFactory.getLogger(StartFileServer.class);

    private static void startNameServer() throws FtpException {
        log.info("开始启动Socket服务...");
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String ip = Cfg.getHostAddress();
        int servPort = ftpConfig.getServPort();
        new Thread(new NameServer(ip, servPort)).start();
    }

    private static void startInvokeSocket() throws FtpException {
        BusDealSer busDealSer = new BusDealSer(Cfg.getHostAddress(), FtpConfig.getInstance().getCommandPort());
        new Thread(busDealSer).start();
        log.info("启动外部调用服务socket结束...");
    }

    private static void startNameServer127() throws FtpException {
        log.info("开始启动Socket服务127...");
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String ip = "127.0.0.1";//NOSONAR
        int servPort = ftpConfig.getServPort();
        new Thread(new NameServer(ip, servPort)).start();
        log.info("启动Socket服务结束127...");
    }

    private static void startInvokeSocket127() throws FtpException {
        BusDealSer busDealSer = new BusDealSer("127.0.0.1", FtpConfig.getInstance().getCommandPort());//NOSONAR
        new Thread(busDealSer).start();
        log.info("启动外部调用服务socket结束127...");
    }

    private static void runCheck() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {//NOSONAR
                    try {
                        log.info("服务运行状态:{}-{}", SysContent.getInstance().getRunState(), System.currentTimeMillis());
                        Thread.sleep(50000);//NOSONAR
                    } catch (Exception e) {
                        log.error("服务运行出现异常", e);
                    }
                }
            }
        });
        t.setName("runCheck-Daemon");
        t.setDaemon(true);
        t.start();

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        log.info("========nameNode start ...=========");
        Cfg.setNodeType(NodeType.NAMENODE);
        Runtime.getRuntime().addShutdownHook(new ShutdownHookHandler());
        //初始化spring
        SpringContext.getInstance().init();
        NameSpringContext.getInstance().init();
        try {
            log.info("====文件服务启动开始====");
            ProcessIdUtil.writeOutPID();
            initParams();
            log.info("====文件服务参数加载完成====");
            NanoHelper.setFlag(Cfg.getNodeId());

            String ip = Cfg.getHostAddress();
            if (!"0.0.0.0".equals(ip) && !"127.0.0.1".equals(ip)) {//NOSONAR
                //127 为了在sleep时本地monitor能正常访问
                startNameServer127();
                startInvokeSocket127();
            }
            registerService();
            startNameServer();
            //启动外部调用socket服务
            startInvokeSocket();
            //zk注册要放到最后
            registerZK();

            runCheck();
            log.info("====文件服务启动成功====");
        } catch (Throwable e) {//NOSONAR
            log.error("====文件服务启动失败====", e);
            System.exit(0);
        }
    }

    private static void initParams() throws FtpException {
        Cfg.initConfigPath();

        Cfg.loadFtpConfig();

        IdWorker idWorker = new IdWorker(Cfg.getNodeId(), 0);
        UUIDService.setIdWorker(idWorker);
    }

    private static void registerService() throws IOException {
        KfkCommConsumer.getInstance().init();
        KfkCommConsumer.getInstance().start();
        KfkConsumer kfkConsumer = KfkConsumer.getInstance();
        kfkConsumer.init();
        kfkConsumer.start();
        KfkProducer.getInstance().init();
    }

    //zk注册要放到最后
    private static void registerZK() {
        ZkService.getInstance().noticeStart();
        ZkService.getInstance().subscribe();
    }
}
