package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPCheckService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(IPCheckService.class);

    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        boolean flag = FtpConfig.getInstance().isIpCheck();
        if (!flag) {
            log.debug("nano:{}#flowNo:{}#IP校验开关未打开", bean.getNano(), bean.getFlowNo());
            return;
        }
        ContextBean cxtBean = context.getCxtBean();
        String user = cxtBean.getUser();
        String ip = cxtBean.getClientAddr();
        long nano = cxtBean.getNano();
        String flowNo = cxtBean.getFlowNo();
        boolean b = UserInfoWorker.getInstance().checkIP(user, ip);
        if (b) {
            log.debug("nano:{}#flowNo:{}#IP地址检查通过#User:{},ip:{}", nano, flowNo, user, ip);
        } else {
            log.error("nano:{}#flowNo:{}#IP地址检查不通过#User:{},ip:{}", nano, flowNo, user, ip);
            bean.setAuthFlag(false);
            bean.setFileRetMsg("IP地址检查不通过");
            FLowHelper.setError(context, FtpErrCode.IP_CHECK_FAILED);
            context.setCanntInvokeNextFlow(true);
        }
    }
}
