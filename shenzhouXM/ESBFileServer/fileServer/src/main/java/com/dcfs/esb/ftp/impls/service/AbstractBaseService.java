package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractBaseService implements BaseService {
    private static final Logger log = LoggerFactory.getLogger(AbstractBaseService.class);
    protected boolean started = false;

    /**
     * @param params 基础服务启动的参数
     *               有需要时重写该方法
     */
    public boolean start(Map<String, String> params) {
        started = true;
        return true;
    }

    public boolean stop() {
        started = false;
        return false;
    }

    public boolean isStarted() {
        return started;
    }

    public void invoke(CachedContext context, FileMsgBean bean) throws FtpException {
        if (log.isDebugEnabled()) {
            log.debug("nano:{}#flowNo:{}#{} start invoke...", context.getCxtBean().getNano(), context.getCxtBean().getFlowNo(), getClass().getName());
        }
        if (FLowHelper.hasError(context)) return;
        if (context.isCanntInvokeNextFlow()) return;
        if (CapabilityDebugHelper.isDebug()) CapabilityDebugHelper.markCurrTime(getClass().getName() + "Begin");
        invokeInner(context, bean);
        if (CapabilityDebugHelper.isDebug()) CapabilityDebugHelper.markCurrTime(getClass().getName() + "End");
    }

    protected abstract void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException;

}
