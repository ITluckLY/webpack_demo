package com.dcfs.esb.ftp.server.socket.dispatcher;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.helper.FileVersionHelper;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.impls.service.AbstractPreprocessService;
import com.dcfs.esb.ftp.impls.service.AbstractProcessService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.interfases.service.GeneralService;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.helper.NetworkSpeedCtrlHelper;
import com.dcfs.esb.ftp.server.model.FileDownloadRecord;
import com.dcfs.esb.ftp.server.model.FileUploadRecord;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BeanConverter;
import com.dcfs.esb.ftp.utils.BooleanTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by mocg on 2016/12/26.
 */
public class MountFileDispatcher {
    private static final Logger log = LoggerFactory.getLogger(MountFileDispatcher.class);
    private CachedContext context;
    private FileMsgBean bean;
    private FtpConnector conn;
    private long nano = 0;

    public MountFileDispatcher(CachedContext context, FileMsgBean bean, FtpConnector conn) {
        this.context = context;
        this.bean = bean;
        this.conn = conn;
    }

    public void run() {
        ContextBean cxtBean = context.getCxtBean();
        nano = cxtBean.getNano();

        try {
            preprocess();
            //下载文件时，认证通过并且文件存在，才能进行文件下载
            if (FileMsgTypeHelper.isGetFileAuth(bean.getFileMsgFlag())) {
                if (BooleanTool.anyFalseByNull(bean.isAuthFlag(), bean.isFileExists())) {//NOSONAR
                    context.setCanntInvokeNextFlow(true);
                }
            }
            //去掉一些后面处理不需要的信息:nodeList 版本号
            bean.setNodeList(null);
            bean.setClientApiVersion(null);
            bean.setServerApiVersion(null);
            bean.setClientNodelistVersion(null);
            bean.setServerNodelistVersion(null);

            process();
        } finally {
            postprocess();
            //回收资源
            closeConn();
            //回收令牌资源
            SysContent.getInstance().decrementTaskCount(context);
            SysContent.getInstance().decrementTaskPriorityTokenCount(context);
            SysContent.getInstance().minusNetworkSpeed(context);

            long st2 = System.currentTimeMillis();
            long st1 = context.getCxtBean().getTimestamp1();
            context.getCxtBean().setTimestamp2(st2);
            log.debug("nano:{}#Dispatcher耗费时间:{}ms", nano, st2 - st1);

            //保存文件上传下载记录
            String originalFileMsgFlag = context.getCxtBean().getOriginalFileMsgFlag();
            if (FileMsgTypeHelper.isGetFileAuth(originalFileMsgFlag)) {
                FileDownloadRecord downloadRecord = BeanConverter.strictConvertTo(bean, FileDownloadRecord.class);
                downloadRecord.setStartTime(new Date(context.getCxtBean().getTimestamp1()));
                downloadRecord.setEndTime(new Date(st2));
                downloadRecord.setSuss(!FLowHelper.hasError(context));
                downloadRecord.setSysname(cxtBean.getSysname());
                downloadRecord.setNodeName(cxtBean.getNodeName());
                downloadRecord.setNodeList(null);
                downloadRecord.setFileVersion(FileVersionHelper.getFileVersion(context));
                EsbFileService.getInstance().logFileDownload(downloadRecord);
            } else if (FileMsgTypeHelper.isPutFileAuth(originalFileMsgFlag)) {
                FileUploadRecord fileUploadRecord = BeanConverter.strictConvertTo(bean, FileUploadRecord.class);
                fileUploadRecord.setStartTime(new Date(context.getCxtBean().getTimestamp1()));
                fileUploadRecord.setEndTime(new Date(st2));
                fileUploadRecord.setSuss(!FLowHelper.hasError(context));
                fileUploadRecord.setSysname(cxtBean.getSysname());
                fileUploadRecord.setNodeName(cxtBean.getNodeName());
                fileUploadRecord.setNodeList(null);
                fileUploadRecord.setFileVersion(FileVersionHelper.getFileVersion(context));
                EsbFileService.getInstance().logFileUpload(fileUploadRecord);
            }
        }
    }


    /**
     * 前置处理，权限校验(密码、IP)，日志，获取流程码
     * 最终会在context设置FLOW_NAME,传给process
     */
    protected void preprocess() {
        //IP校验、权限校验、文件路由
        //InitService 里有路径权限校验、使用原文件名权限校验、交易码校验
        String[] preServices = new String[]{"ApiVersionCheckService", "IPCheckService", "PwdAuthService", "PushDataNodeListService",
                "MountCheckService", "InitService", "ResourceCtrlService"};

        for (String service : preServices) {
            if (context.isCanntInvokeNextFlow()) break;

            log.debug("nano:{}#Dispatcher正在执行前置基础服务{}", nano, service);
            try {
                GeneralService impl = ServiceContainer.getInstance().getService(service);
                if (!impl.isStarted()) {
                    log.error("nano:{}#前置基础服务{}未正确启动", nano, service);
                    FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");//NOSONAR
                } else {
                    if (impl instanceof AbstractPreprocessService) impl.invoke(context, bean);
                    else log.warn("nano:{}#类[{}]不属于AbstractPreprocessService", nano, impl.getClass().getName());
                }
            } catch (FtpException e) {
                log.error("nano:{}#前置基础服务{} error", nano, service, e);
                FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");
            }
        }

        if (FLowHelper.hasError(context)) {
            bean.setErrCode(FLowHelper.getErrorCode(context));
            bean.setFileRetMsg(FLowHelper.getErrorMsg(context));
        }
        //设置加密
        if (BooleanTool.allTrueByNull(bean.isAuthFlag(), bean.isScrtFlag())) bean.setDesKey(ScrtUtil.getDesKey());
        //设置分片大小
        bean.setPieceNum(NetworkSpeedCtrlHelper.getPieceNum(context));
        try {
            conn.writeHead(bean);
        } catch (FtpException e) {
            FLowHelper.setError(context, FtpErrCode.SOCKET_ERROR, "前置基础服务输出Err");
            log.error("nano:{}#前置基础服务输出Err", nano, e);
        }
    }

    /**
     * 根据流程名称获取一个或多个服务进行业务处理
     * 处理流程，文件上传下载，日志
     */
    protected void process() {
        if (FLowHelper.hasError(context)) return;
        //根据交易码查找对应的处理流程，并执行器对应的处理流程
        String[] services = new String[]{"FileService"};
        for (String service : services) {
            if (context.isCanntInvokeNextFlow()) break;
            log.debug("nano:{}#Dispatcher正在执行基础服务{}", nano, service);

            try {
                GeneralService impl = ServiceContainer.getInstance().getService(service);
                if (!impl.isStarted()) {
                    log.error("nano:{}#基础服务{}未正确启动", nano, service);
                    FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度基础服务" + service + "出现异常,");
                } else {
                    if (impl instanceof AbstractProcessService) impl.invoke(context, bean);
                    else log.warn("nano:{}#类[{}]不属于AbstractProcessService", nano, impl.getClass().getName());
                }
            } catch (FtpException e) {
                log.error("nano:{}#基础服务{} error", nano, service, e);
                FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度基础服务" + service + "出现异常,");
            }
        }
    }

    /**
     * 后置处理，日志
     */
    protected void postprocess() {
        //nothing
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
}
