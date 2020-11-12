package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.utils.ObjectsTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2016/10/18.
 */
public class ApiVersionCheckService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(ApiVersionCheckService.class);

    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        //dtoVersion大于0不作api版本校验，由dtoVersion实现向下兼容
        /*if (context.getCxtBean().getDtoVersion() > 0) {//NOSONAR
            bean.setClientApiVersion(null);//通过则设置为null
            return;
        }*/
        String apiVersion = Cfg.getApiVersion();
        if (!ObjectsTool.equals(apiVersion, bean.getClientApiVersion())) {
            bean.setServerApiVersion(apiVersion);
            bean.setAuthFlag(false);
            String msg = FtpErrCode.getCodeMsg(FtpErrCode.API_VERSION_NOT_SAME);
            msg = msg + "#ClientApiVersion:" + bean.getClientApiVersion() + ",ServerApiVersion:" + apiVersion;
            bean.setFileRetMsg(msg);
            FLowHelper.setError(context, FtpErrCode.API_VERSION_NOT_SAME);
            context.setCanntInvokeNextFlow(true);
            log.debug("apiVersin检查#ClientApiVersion:{},ServerApiVersion:{}", bean.getClientApiVersion(), apiVersion);
        } else bean.setClientApiVersion(null);//通过则设置为null
    }
}
