package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esc.ftp.svr.comm.cons.ServerCfgCons;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 只支持有交易码的请求
 * Created by mocg on 2016/11/17.
 */
public class ResourceCtrlService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(ResourceCtrlService.class);
    private static final int MIN_PRIORITY = ServerCfgCons.TRAN_CODE_MIN_PRIORITY;

    @Override
    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        String tranCode = bean.getTranCode();
        //只支持有交易码的
        if (StringUtils.isEmpty(tranCode)) return;

        int taskSize = context.getCxtBean().getTaskSize();
        int taskCount = SysContent.getInstance().incrementTaskCount(context, tranCode);
        log.debug("taskSize:{},taskCount:{},tranCode:{}", taskSize, taskCount, tranCode);
        if (taskCount > taskSize) {
            bean.setAuthFlag(false);
            log.error("TaskSize资源不足:{}", tranCode);
            FLowHelper.setError(context, FtpErrCode.TASK_RESOURCE_NOT_ENOUGH);
            return;
        }
        //
        selTaskPriorityAndIncrementToken(context, bean);
    }

    //--synchronized
    private void selTaskPriorityAndIncrementToken(CachedContext context, FileMsgBean bean) {
        int priority = context.getCxtBean().getTaskPriority();//从1开始
        if (priority < MIN_PRIORITY) {
            bean.setAuthFlag(false);
            return;
        }
        SysContent sysContent = SysContent.getInstance();
        int[] tokenPools = FtpConfig.getInstance().getTokenPools();
        int priorityMaxIndex = Math.min(tokenPools.length, priority);
        int currTaskPriority = -1;
        for (int i = priorityMaxIndex; i >= MIN_PRIORITY; i--) {
            int taskPriorityCount = sysContent.getTaskPriorityTokenCount(i);
            if (taskPriorityCount < tokenPools[i - 1]) {
                currTaskPriority = i;
                break;
            }
        }
        log.debug("priority:{}, currTaskPriority:{},tranCode:{}", priority, currTaskPriority, bean.getTranCode());
        if (currTaskPriority > 0) {
            sysContent.incrementTaskPriorityTokenCount(context, currTaskPriority);
        } else {
            bean.setAuthFlag(false);
            log.error("prioritySize资源不足:{}", bean.getTranCode());
            FLowHelper.setError(context, FtpErrCode.TOKEN_RESOURCE_NOT_ENOUGH);
        }
    }
}
