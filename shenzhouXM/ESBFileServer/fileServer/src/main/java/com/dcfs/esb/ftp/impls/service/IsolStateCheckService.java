package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.invoke.node.NodesWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsolStateCheckService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(ApiVersionCheckService.class);


    /**
     *
     * @param context  缓存上下文
     * @param bean     文件系统消息类
     * @throws FtpException
     */
    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        int currNodeIsolState = NodesWorker.getInstance().getCurrNodeIsolState();
        if (currNodeIsolState == 1) {
            log.warn("隔离节点不接受传输请求#currNodeIsolState:{}", currNodeIsolState);
            bean.setAuthFlag(false);
            bean.setFileRetMsg("隔离节点不接受传输请求");
            context.setCanntInvokeNextFlow(true);
            FLowHelper.setError(context, FtpErrCode.NODE_ISOLSTATE);
        }
    }
}
