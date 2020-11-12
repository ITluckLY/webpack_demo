package com.dcfs.esc.ftp.datanode.server;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.datanode.handler.ShutdownHookHandler;
import com.dcfs.esb.ftp.datanode.kafka.KfkCommConsumer;
import com.dcfs.esb.ftp.datanode.kafka.KfkConsumer;
import com.dcfs.esb.ftp.datanode.service.BakCleanService;
import com.dcfs.esb.ftp.datanode.service.ZkService;
import com.dcfs.esb.ftp.distribute.DistributeFileReceiveService;
import com.dcfs.esb.ftp.helper.NodeListHelper;
import com.dcfs.esb.ftp.impls.flow.ServiceFlowManager;
import com.dcfs.esb.ftp.impls.uuid.IdWorker;
import com.dcfs.esb.ftp.impls.uuid.UUIDService;
import com.dcfs.esb.ftp.kafka.KfkProducer;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.invoke.BusDealSer;
import com.dcfs.esb.ftp.server.invoke.client.ClientStatusManager;
import com.dcfs.esb.ftp.server.invoke.fileclean.FileCleanManager;
import com.dcfs.esb.ftp.server.route.RouteManager;
import com.dcfs.esb.ftp.server.schedule.ScheduleManager;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.server.socket.FtpTcpServer;
import com.dcfs.esb.ftp.spring.SpringContext;
import com.dcfs.esb.ftp.utils.ProcessIdUtil;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import com.dcfs.esc.ftp.datanode.nework.server.FTPServer;
import com.dcfs.esc.ftp.datanode.pool.ThreadExecutorFactory;
import com.dcfs.esc.ftp.datanode.spring.DataSpringContext;
import com.dcfs.esc.ftp.datanode.spring.ServiceBeans;
import com.dcfs.esc.ftp.datanode.svrimpl.SvrAbstractSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务启动类
 */
public class StartFileServer {
    private static final Logger log = LoggerFactory.getLogger(StartFileServer.class);

    private static String handlerLogLevelName = null;

    /**
     * @throws FtpException
     */
    private static void startSocket() throws FtpException {//NOSONAR
        log.info("开始启动Socket服务...");
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String ip = Cfg.getHostAddress();
        FtpTcpServer server = new FtpTcpServer(ip, ftpConfig.getServPort());
        new Thread(server).start();
        FtpTcpServer server1 = new FtpTcpServer(ip, ftpConfig.getManagePort());
        new Thread(server1).start();
        DistributeFileReceiveService receiveService = new DistributeFileReceiveService(ip, ftpConfig.getDistributeFileReceivePort());
        new Thread(receiveService).start();
        log.info("启动Socket服务结束...");
    }

    private static void startFtpServer() throws FtpException {
        log.info("开始启动FTS服务...");
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String ip = Cfg.getHostAddress();
        int servPort = ftpConfig.getServPort();
        new Thread(new FTPServer(ip, servPort, handlerLogLevelName)).start();

        DistributeFileReceiveService receiveService = new DistributeFileReceiveService(ip, ftpConfig.getDistributeFileReceivePort());
        new Thread(receiveService).start();
        log.info("启动FTS服务结束...");
    }

    private static void startInvokeSocket() throws FtpException {
        BusDealSer busDealSer = new BusDealSer(Cfg.getHostAddress(), FtpConfig.getInstance().getCommandPort());
        new Thread(busDealSer).start();
        log.info("启动外部调用服务socket结束...");
    }

    private static void startFtpServer127() throws FtpException {
        log.info("开始启动FTS服务127...");
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String ip = "127.0.0.1";//NOSONAR
        int servPort = ftpConfig.getServPort();
        new Thread(new FTPServer(ip, servPort, handlerLogLevelName)).start();

        DistributeFileReceiveService receiveService = new DistributeFileReceiveService(ip, ftpConfig.getDistributeFileReceivePort());
        new Thread(receiveService).start();
        log.info("启动FTS服务结束127...");
    }

    private static void startSocket127() throws FtpException {//NOSONAR
        log.info("开始启动Socket服务127...");
        FtpConfig ftpConfig = FtpConfig.getInstance();
        String ip = "127.0.0.1";//NOSONAR
        FtpTcpServer server = new FtpTcpServer(ip, ftpConfig.getServPort());
        new Thread(server).start();
        FtpTcpServer server1 = new FtpTcpServer(ip, ftpConfig.getManagePort());
        new Thread(server1).start();
        DistributeFileReceiveService receiveService = new DistributeFileReceiveService(ip, ftpConfig.getDistributeFileReceivePort());
        new Thread(receiveService).start();
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
        log.info("========dataNode start ...=========");
        Cfg.setNodeType(NodeType.DATANODE);  // 获取枚举类型
        Runtime.getRuntime().addShutdownHook(new ShutdownHookHandler()); // 关闭kakfa zk
        //初始化spring
        SpringContext.getInstance().init();
        DataSpringContext.getInstance().init();
        SvrAbstractSetter.getInstance().setIt();
        try {
            log.info("====文件服务启动开始====");
            ProcessIdUtil.writeOutPID();   // 创建文件 写文件 退出删除文件
            initParams();
            log.info("====文件服务参数加载完成====");
            NanoHelper.setFlag(Cfg.getNodeId());
            handlerLogLevelName = ServiceBeans.getDataEfsProperties().getHandlerLogLevelName();

            String ip = Cfg.getHostAddress();
            if (!"0.0.0.0".equals(ip) && !"127.0.0.1".equals(ip)) {//NOSONAR
                //127 为了在sleep时本地monitor能正常访问
                //startSocket127();//NOSONAR
                startFtpServer127();
                startInvokeSocket127();
            }
            registerService();
            //startSocket();//NOSONAR
            startFtpServer();
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

    /**
     *  初始化 尝试
     * @throws FtpException
     */
    private static void initParams() throws FtpException {
        Cfg.initConfigPath(); // 返回一个路径 并设置utf8 的格式

        Cfg.loadFtpConfig();  //读取对应的配置文件
        Cfg.loadUserConfig();  //读取用户的配置文件
        Cfg.loadFileConfig(); //  file.xml
        //Cfg.loadRuleConfig();//NOSONAR
        Cfg.loadSystemConfig(); // 读取系统配置文件 system.xml

        //初始化基本组件参数
        FtpConfig ftpConfig = FtpConfig.getInstance(); // new 文件服务器配置参数类
        IdWorker idWorker = new IdWorker(Cfg.getNodeId(), 0); //业务编码无业务则用5位随机数
        UUIDService.setIdWorker(idWorker);
        ThreadExecutorFactory.setChunkDispatchExecutorCount(ftpConfig.getPoolSize()); // 获取线程池的大小
        ThreadExecutorFactory.setFileRouteExecutorCount(ftpConfig.getRoutePoolSize()); //获取文件大小的最大值

        String defNodeListStrForTest = ServiceBeans.getDataEfsProperties().getDefNodeListStrForTest();
        NodeListHelper.setDefNodeListStrForTest(defNodeListStrForTest);
        //
        ServiceContainer.getInstance();
        RouteManager.getInstance();
        ServiceFlowManager.getInstance();
        FileCleanManager.getInstance();
        ClientStatusManager.getInstance();
    }

    private static void registerService() throws IOException, InterruptedException {
        ScheduleManager.getInstance().startAll();
        Thread bakCleanThread = new Thread(new BakCleanService());
        bakCleanThread.setDaemon(true);
        bakCleanThread.start();

        KfkCommConsumer.getInstance().init();
        KfkCommConsumer.getInstance().start();
        KfkConsumer kfkConsumer = KfkConsumer.getInstance();
        kfkConsumer.init();
        kfkConsumer.start();
        KfkProducer.getInstance().init();

        //先消费完之前的删除文件消息 如果连接kafka不成功，等待1分钟
        int count = 0;
        int oldNoRecordSum = 0;
        while (kfkConsumer.isToConsumeDelFileRecord()) {
            log.info("sleep for kafka consume...");
            TimeUnit.SECONDS.sleep(1);
            int noRecordSum = kfkConsumer.getNoRecordSum();
            if (oldNoRecordSum == noRecordSum) count++;
            else oldNoRecordSum = noRecordSum;

            if (count > 20) break;
        }
    }

    //zk注册要放到最后
    private static void registerZK() {
        ZkService.getInstance().noticeStart();
        ZkService.getInstance().subscribe();
    }
}
