package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户名密码权限校验
 * Created by mocg on 2016/8/22.
 */
public class PwdAuthService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(PwdAuthService.class);

    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        CapabilityDebugHelper.markCurrTime("PwdAuthServiceBegin");
        // 检查用户名和密码，确认用户是否合法
        boolean userFlag = UserInfoWorker.getInstance().doAuth(bean);
        bean.setAuthFlag(userFlag);
        log.debug("用户名和密码检验:{}", userFlag);
        if (!userFlag) {
            context.setCanntInvokeNextFlow(true);
            FLowHelper.setError(context, FtpErrCode.AUTH_PWD_FAILED);
        }
        bean.setPasswd(null);
        CapabilityDebugHelper.markCurrTime("PwdAuthServiceEnd");
    }
}
