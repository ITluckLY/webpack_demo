package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MountCheckService extends AbstractPreprocessService {
    private static final Logger log = LoggerFactory.getLogger(MountCheckService.class);

    protected void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {
        ContextBean cxtBean = context.getCxtBean();
        cxtBean.setMount(false);
        String mountNodeName = bean.getMountNodeName();
        if (mountNodeName == null) return;

        long nano = cxtBean.getNano();
        String fileMsgFlag = bean.getFileMsgFlag();
        String fileName = bean.getFileName();
        boolean result = FileMsgTypeHelper.isGetFileAuth(fileMsgFlag);
        result = result && mountNodeName.equals(cxtBean.getNodeName());
        result = result && fileName != null;
        if (!result) {
            log.error("nano:{}#Mount检查不通过#mountNodeName:{}", nano, mountNodeName);
            bean.setAuthFlag(false);
            bean.setFileRetMsg("Mount检查不通过");
            FLowHelper.setError(context, FtpErrCode.MOUNT_CHECK_FAILED);
            context.setCanntInvokeNextFlow(true);
            return;
        }
        String mountFilePath = FtpConfig.getInstance().getMountFilePath();
        result = StringUtils.isNotEmpty(mountFilePath);
        if (!result) {
            log.error("nano:{}#节点不支持Mount#mountNodeName:{}", nano, mountNodeName);
            bean.setAuthFlag(false);
            bean.setFileRetMsg("节点不支持Mount");
            FLowHelper.setError(context, FtpErrCode.NOT_SUPPORTED_MOUNT);
            context.setCanntInvokeNextFlow(true);
            return;
        }
        cxtBean.setMount(true);

        //转换路径
        String preFileName = '/' + mountNodeName + '/';
        if (!fileName.startsWith(preFileName)) {
            if (fileName.startsWith("/")) bean.setFileName("/" + mountNodeName + fileName);
            else bean.setFileName(preFileName + fileName);
        }
        //
        String concatFileName = FileUtil.concatFilePath(mountFilePath, bean.getFileName().substring(preFileName.length()));
        String localFileName = FilenameUtils.separatorsToSystem(concatFileName);
        bean.setFileExists(new File(localFileName).exists());
    }
}
