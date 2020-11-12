package com.dcfs.esb.ftp.server.socket;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.helper.FileMsgBeanHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.impls.context.ContextFactory;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.socket.dispatcher.AddressDispatcher;
import com.dcfs.esb.ftp.server.socket.dispatcher.FSDefaultDispatcher;
import com.dcfs.esb.ftp.server.socket.dispatcher.NodesInfoDispatcher;
import com.dcfs.esb.ftp.server.socket.dispatcher.PutSmallFileDispatcher;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 文件服务器线程处理类
 */
public class TcpServerDispatcher implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TcpServerDispatcher.class);
    private static final String FORMAT = "yyyyMMdd-HHmmssSSS";

    private FileMsgBean bean;
    private CachedContext context;
    private ContextBean cxtBean;
    private FtpConnector conn;
    private long nano = 0;

    public TcpServerDispatcher(FtpConnector conn) {
        this.conn = conn;
    }


    public TcpServerDispatcher(CachedContext context, FileMsgBean bean, FtpConnector conn) {
        this.context = context;
        this.bean = bean;
        this.conn = conn;
    }

    private void init() throws FtpException {
        conn.setKeepAlive();
        conn.init();
    }

    /**
     * 流程化调度
     */
    public void run() {
        CapabilityDebugHelper.init();
        CapabilityDebugHelper.markCurrTime("appStart");
        long st1 = System.currentTimeMillis();
        try {
            // 创建调度器处理
            if (context == null) context = ContextFactory.createContext();
            cxtBean = context.getCxtBean();
            nano = cxtBean.getNano();
            conn.setNano(nano);
            init();
            cxtBean.setConnector(conn);
            cxtBean.setTimestamp1(st1);
            if (log.isDebugEnabled()) {
                log.info("nano:{}#文件传输Dispatcher开始执行,开始时间:{}", nano, DateFormatUtils.format(new Date(st1), FORMAT));
            }
            if (bean == null) bean = new FileMsgBean();
            //init bean
            bean.setNano(nano);
            bean.setErrCode(null);
            CapabilityDebugHelper.markCurrTime("readFileMsgBeanBegin");
            readFileMsgBean();
            CapabilityDebugHelper.markCurrTime("readFileMsgBeanEnd");
            if (CapabilityDebugHelper.isOutBean()) {
                CapabilityDebugHelper.markCurrTime("readFileMsgBean-FileMsgBeanXML:" + FileMsgBeanHelper.toStringIgnoreEx(bean));
            }
            initContext();
            CapabilityDebugHelper.markCurrTime("initContextEnd");
            String fileMsgFlag = bean.getFileMsgFlag();
            /*if (fileMsgFlag == null) fileMsgFlag = "";//NOSONAR
            switch (fileMsgFlag) {
                case FileMsgType.PUT_AUTH:
                case FileMsgType.GET_AUTH:
                    new FSDefaultDispatcher(context, bean, conn).run();
                    break;
                case FileMsgType.PUT:
                case FileMsgType.GET:
                    runException();
                    break;
                case FileMsgType.PUT_ADDR:
                case FileMsgType.GET_ADDR:
                    new AddressDispatcher(context, bean, conn).run(fileMsgFlag);
                    break;
                case FileMsgType.GET_NODESINFO:
                    new NodesInfoDispatcher(context, bean, conn).run();
                    break;
                case FileMsgType.PUT_SMALL_FILE:
                    new PutSmallFileDispatcher(context, bean, conn).run();
                    break;
            }*/
            if (FileMsgType.PUT_AUTH.equals(fileMsgFlag) || FileMsgType.GET_AUTH.equals(fileMsgFlag)) {
                new FSDefaultDispatcher(context, bean, conn).run();
            } else if (FileMsgType.PUT.equals(fileMsgFlag) || FileMsgType.GET.equals(fileMsgFlag)) {
                runException();
            } else if (FileMsgType.PUT_ADDR.equals(fileMsgFlag) || FileMsgType.GET_ADDR.equals(fileMsgFlag)) {
                new AddressDispatcher(context, bean, conn).run(fileMsgFlag);
            } else if (FileMsgType.GET_NODESINFO.equals(fileMsgFlag)) {
                new NodesInfoDispatcher(context, bean, conn).run();
            } else if (FileMsgType.PUT_SMALL_FILE.equals(fileMsgFlag)) {
                new PutSmallFileDispatcher(context, bean, conn).run();
            }
        } catch (FtpException e) {
            log.error("nano:{}#", nano, e);
        } finally {
            CapabilityDebugHelper.markCurrTime("appFinally");
            if (CapabilityDebugHelper.isOutBean()) {
                CapabilityDebugHelper.markCurrTime("appFinally-FileMsgBeanXML:" + FileMsgBeanHelper.toStringIgnoreEx(bean));
            }
            recycleResource();
            long st3 = System.currentTimeMillis();
            long st2 = cxtBean.getTimestamp2();
            if (log.isDebugEnabled()) log.debug("nano:{}#Dispatcher总耗费时间:{}ms#{}ms", nano, st3 - st1, st2 - st1);
            //context.clear()
            CapabilityDebugHelper.markCurrTime("appEnd");
            CapabilityDebugHelper.printAndClean(nano);
        }
    }

    /**
     * 从流中读取FileMsgBean对象
     */
    private void readFileMsgBean() {
        try {
            conn.readHead(bean);
        } catch (FtpException e) {
            FLowHelper.setError(context, FtpErrCode.READ_FILE_MSG_BEAN_ERROR);
            context.setCanntInvokeNextFlow(true);
            log.error("nano:{}#读取FileMsgBean出错", nano, e);
            return;
        }
        String tranCode = bean.getTranCode();
        String uid = bean.getUid();
        if (tranCode == null || tranCode.isEmpty()) tranCode = "DEFAULT";
        context.put(ContextConstants.ORIGINAL_FILE_PATH, bean.getFileName());
        cxtBean.setTranCode(tranCode);
        cxtBean.setUser(uid);
    }

    /**
     * 设置一些公共参数
     */
    private void initContext() {
        cxtBean.setSessionId(String.valueOf(NanoHelper.nanos()));
        FtpConfig ftpConfig = FtpConfig.getInstance();
        cxtBean.setFileRootPath(ftpConfig.getFileRootPath());
        cxtBean.setBackupFileRootPath(ftpConfig.getFileBackupRootPath());
        cxtBean.setOriginalFileMsgFlag(bean.getFileMsgFlag());
        cxtBean.setClientAddr(conn.getSocket().getInetAddress().getHostAddress());
        String sysName = ftpConfig.getSystemName();
        //init cxtbean
        cxtBean.setNodeName(ftpConfig.getNodeName());
        cxtBean.setSysname(sysName);
        cxtBean.setMaxFileSize(ftpConfig.getMaxFileSize());
    }

    private void runException() {
        throw new RuntimeException("请求异常");//NOSONAR
    }

    private void closeConn() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (Exception e) {
                log.error("nano:{}#closeConn err", nano, e);
            }
        }
    }

    //回收资源
    private void recycleResource() {
        closeConn();
        try {
            EsbFile esbFile = cxtBean.getEsbFile();
            if (esbFile != null) {
                esbFile.close();
                cxtBean.setEsbFile(null);
            }
        } catch (FtpException e) {
            log.error("nano:{}#回收资源err", nano, e);
        }
    }
}
