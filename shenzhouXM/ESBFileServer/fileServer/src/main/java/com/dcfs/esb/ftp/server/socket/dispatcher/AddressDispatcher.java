package com.dcfs.esb.ftp.server.socket.dispatcher;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.helper.FsFileHelper;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.impls.service.AbstractPreprocessService;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.interfases.service.GeneralService;
import com.dcfs.esb.ftp.server.service.ServiceContainer;
import com.dcfs.esb.ftp.spring.factory.FileTargetNodeFactory;
import com.dcfs.esb.ftp.spring.outservice.IFileTargetNodeService;
import com.dcfs.esb.ftp.utils.BooleanTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mocg on 2016/12/26.
 */
public class AddressDispatcher {
    private static final Logger log = LoggerFactory.getLogger(AddressDispatcher.class);
    private static final String[] PRE_SERVICES = new String[]{"ApiVersionCheckService", "IPCheckService", "PwdAuthService", "PushDataNodeListService"};
    private CachedContext context;
    private FileMsgBean bean;
    private FtpConnector conn;
    private long nano = 0;

    public AddressDispatcher(CachedContext context, FileMsgBean bean, FtpConnector conn) {
        this.context = context;
        this.bean = bean;
        this.conn = conn;
    }

    public void run(String fileMsgType) {//NOSONAR
        ContextBean cxtBean = context.getCxtBean();
        nano = cxtBean.getNano();
        try {
            //????????????????//
            preprocess();
            if (BooleanTool.toBoolean(bean.isAuthFlag())) {
                String targetNodeAddr = null;
                if (bean.getMountNodeName() != null) {
                    targetNodeAddr = null;
                } else {
                    if (FileMsgType.GET_ADDR.equals(fileMsgType)) {
                        if (FsFileHelper.existsFileAndSetFileSize(context, bean)) {
                            targetNodeAddr = "1";//1:表示继续使用本连接
                        } else {
                            IFileTargetNodeService fileTargetNodeService = FileTargetNodeFactory.getInstance().getFileTargetNodeService();
                            try {
                                targetNodeAddr = fileTargetNodeService.findTargetNodeAddrByFilePath(bean.getFileName());
                            } catch (IOException e) {
                                log.warn("nano:{}#获取目标节点err", nano, e);
                            }
                        }
                    } else if (FileMsgType.PUT_ADDR.equals(fileMsgType)) {
                        targetNodeAddr = "1";
                    }
                }
                log.debug("nano:{}#targetNodeAddr:{}", nano, targetNodeAddr);
                bean.setTargetNodeAddr(targetNodeAddr);
            } else bean.setTargetNodeAddr(null);

            try {
                conn.writeHead(bean);
            } catch (FtpException e) {
                FLowHelper.setError(context, FtpErrCode.SOCKET_ERROR, "前置基础服务输出Err");
                log.error("nano:{}#前置基础服务输出Err:", nano, e);
            }
        } finally {
            long st2 = System.currentTimeMillis();
            long st1 = context.getCxtBean().getTimestamp1();
            context.getCxtBean().setTimestamp2(st2);
            log.debug("nano:{}#Dispatcher耗费时间:{}ms", nano, st2 - st1);
        }
    }

    /**
     * 查询目标节点前进行校验和推送节点列表信息
     */
    protected void preprocess() {
        //IP校验、权限校验、文件路由
        //InitService 里有路径权限校验、使用原文件名权限校验、交易码校验
        String[] preServices = AddressDispatcher.PRE_SERVICES;
        for (String service : preServices) {
            if (context.isCanntInvokeNextFlow()) break;
            log.debug("nano:{}#Dispatcher正在执行前置基础服务{}", nano, service);
            try {
                GeneralService impl = ServiceContainer.getInstance().getService(service);
                if (!impl.isStarted()) {
                    log.error("nano:{}#前置基础服务{}未正确启动", nano, service);
                    FLowHelper.setError(context, FtpErrCode.FLOW_ERROR, "Dispatcher调度前置基础服务" + service + "出现异常,");
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
    }
}
