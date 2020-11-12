package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.UserInfoWorker;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户名密码权限校验
 * Created by mocg on 2016/8/22.
 */
public class PwdAuthWithSeqService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(PwdAuthWithSeqService.class);

    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        CapabilityDebugHelper.markCurrTime("PwdAuthServiceBegin");
        String seq = (String) context.get(CommGlobalCons.SEQ_KEY);
        // 检查用户名和密码，确认用户是否合法
        //用户认证，成功则返回pwdmd5，否则返回null
        UserInfoWorker userInfoWorker = UserInfoWorker.getInstance();
        String pwdmd5 = userInfoWorker.doAuthByMd5AndSeq(bean, seq);
        boolean userFlag = pwdmd5 != null;
        bean.setAuthFlag(userFlag);
        log.debug("nano:{}#flowNo:{}#用户名和密码检验:{}", bean.getNano(), bean.getFlowNo(), userFlag);
        if (!userFlag) {
            context.setCanntInvokeNextFlow(true);
            FLowHelper.setError(context, FtpErrCode.AUTH_PWD_FAILED);
        }
        bean.setPasswd(null);
        context.put(CommGlobalCons.CURR_USER_PWDMD5_KEY, pwdmd5);
        CapabilityDebugHelper.markCurrTime("PwdAuthServiceEnd");
    }
}
