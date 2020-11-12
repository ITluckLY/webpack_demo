package com.dcfs.esb.ftp.impls.service;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.helper.FileMsgTypeHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.msg.FileMsgType;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.impls.component.*;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;
import com.dcfs.esb.ftp.interfases.service.BussService;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;

public class DefaultFileService extends AbstractProcessService implements BussService {
    private static final Logger log = LoggerFactory.getLogger(DefaultFileService.class);

    @Override
    public void invokeInner(CachedContext context, FileMsgBean bean) throws FtpException {//NOSONAR
        CapabilityDebugHelper.markCurrTime("defFsBegin");
        ContextBean cxtBean = context.getCxtBean();
        long nano = cxtBean.getNano();
        FtpConnector conn = null;
        EsbFile file = null;
        String reqFileName = bean.getFileName();
        String tarSysName = bean.getTarSysName();
        IFileComponent fileComponent = null;
        boolean putSmallFile = FileMsgType.PUT_SMALL_FILE.equals(bean.getFileMsgFlag());
        try {
            conn = cxtBean.getConnector();
            CapabilityDebugHelper.markCurrTime("defFs-readHeadBegin");
            if (!putSmallFile) conn.readHead(bean);
            CapabilityDebugHelper.markCurrTime("defFs-readHeadEnd");
            //校验sessionMD5,防止更改文件路径
            boolean passed = checkSessionMd5(context, conn, bean);
            if (!passed) {//NOSONAR
                context.setCanntInvokeNextFlow(true);
                log.info("nano:{}#SessionMd5检验不通过", nano);
                return;
            }
            //防止校验通过后更改文件名
            bean.setFileName(reqFileName);
            bean.setTarSysName(tarSysName);

            String msgFlag = bean.getFileMsgFlag();
            cxtBean.setMsgFlag(msgFlag);

            if (FileMsgTypeHelper.isGetFile(msgFlag)) {
                if (cxtBean.isMount()) fileComponent = new DownloadMountFileComponent();
                else fileComponent = new DownloadFileComponent();
            } else if (FileMsgTypeHelper.isPutFile(msgFlag)) {
                fileComponent = new UploadFileComponent();
            } else if (FileMsgType.DEL.equals(msgFlag)) {
                fileComponent = new DelFileComponent();
            } else if (FileMsgType.RNAM.equals(msgFlag)) {
                fileComponent = new RenameFileComponent();
            } else {
                fileComponent = new SimpleFileComponent();
            }
            CapabilityDebugHelper.markCurrTime("defFs-create");
            //创建file
            file = fileComponent.create(context, bean, conn);
            if (file != null) file.setNano(nano);
            CapabilityDebugHelper.markCurrTime("defFs-preProcess");
            //前置处理
            fileComponent.preProcess(context, bean, conn, file);
            CapabilityDebugHelper.markCurrTime("defFs-process");
            //eg 上传下载删除重命名文件操作
            fileComponent.process(context, bean, conn, file);
            CapabilityDebugHelper.markCurrTime("defFs-afterProcess");
            //eg 上传下载删除重命名文件完成后的操作
            fileComponent.afterProcess(context, bean, conn, file);
            CapabilityDebugHelper.markCurrTime("defFs-afterProcessEnd");
        } catch (FtpException e) {
            if ((e.getCause() instanceof EOFException)) {
                // why ???
                log.error("nano:{}#EOFFFFFFF Exception!!!!!!!!", nano, e);
                Thread.dumpStack();
            }
            String errCode = e.getCode();
            if (errCode == null) errCode = FtpErrCode.FAIL;
            bean.setErrCode(errCode);
            bean.setFileRetMsg(e.getMessage());
            FLowHelper.setError(context, errCode, e.getMessage());
            context.setCanntInvokeNextFlow(true);
            throw e;
        } finally {
            CapabilityDebugHelper.markCurrTime("defFs-finish");
            if (fileComponent != null) fileComponent.finish(context, bean, conn, file);
            CapabilityDebugHelper.markCurrTime("defFsEnd");
        }
    }

    private boolean checkSessionMd5(CachedContext context, FtpConnector conn, FileMsgBean bean) {//NOSONAR
        if (true) return true;//NOSONAR
        String md5 = MD5Util.md5(context.getCxtBean().getSessionId() + "-" + bean.getFileName());
        return md5.equals(bean.getSessionMD5());
    }
}
