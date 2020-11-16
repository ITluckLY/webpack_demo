package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.helper.BizFileHelper;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.impls.filetail.FileTailerManager;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esb.ftp.server.model.FileSaveRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataReqDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataRspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by mocg on 2017/6/5.
 */
public class SaveUploadFileByEsbFileHandler extends UploadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SaveUploadFileByEsbFileHandler.class);

    private long nano;
    private ChannelContext channelContext;
    private EsbFile esbFile;
    private String fileMd5;
    private String flowNo;

    @Override
    public void start(ProcessHandlerContext ctx) {
        nano = ctx.getChannelContext().getNano();
        flowNo = ctx.getChannelContext().getFlowNo();
        channelContext = ctx.getChannelContext();
        //String fn = channelContext.cxtBean().getFileName()
        UploadContextBean cxtBean = channelContext.cxtBean();
        ctx.getMap().put(CommGlobalCons.TAIL_FILE_PATH_KEY, cxtBean.getEsbFile().getTmpFile().getAbsolutePath());
        esbFile = cxtBean.getEsbFile();
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileUploadDataReqDto reqDto = ctx.requestObj(FileUploadDataReqDto.class);
        FileUploadDataRspDto rspDto = ctx.responseObj(FileUploadDataRspDto.class);

        UploadContextBean cxtBean = channelContext.cxtBean();
        long position = reqDto.getPosition();
        byte[] fileCont = reqDto.getFileCont();
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#文件上传请求#position:{}", cxtBean.getNano(), cxtBean.getFlowNo(), position);
        }

        if (fileCont != null) {
            if (position + fileCont.length <= cxtBean.getFileSize()) {
                esbFile.write(fileCont);
            } else {
                String errMsg = "上传数据超过文件总大小:" + cxtBean.getFileSize();
                rspDto.setErrCode(FtpErrCode.DATA_LEN_GREATER_FILE_SIZE);
                rspDto.setErrMsg(errMsg);
                channelContext.setAccepted(false);
                channelContext.setForceClose(true);
                channelContext.chunkConfig().setEnd(true);
                throw new FtpException(FtpErrCode.FILE_ERROR, channelContext.getNano(), errMsg);
            }
        }

        boolean lastChunk = reqDto.isLastChunk();
        ctx.setFinish(lastChunk || reqDto.isEnd());
        ctx.setFileloadSucc(lastChunk);
        if (lastChunk) fileMd5 = reqDto.getMd5();

        return msg;
    }

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        Date uploadEndTime = new Date();
        UploadContextBean cxtBean = channelContext.cxtBean();
        cxtBean.setEndTime(uploadEndTime);
        if (esbFile != null) {
            esbFile.setFilePropertie(ContextConstants.UPLOAD_START_TIME, DateFormatUtils.format(cxtBean.getStartTime(), ContextConstants.DATE_FORMAT_PATT));
            esbFile.setFilePropertie(ContextConstants.UPLOAD_END_TIME, DateFormatUtils.format(uploadEndTime, ContextConstants.DATE_FORMAT_PATT));
            esbFile.checkMd5(fileMd5);
            FileTailerManager tailerManager = (FileTailerManager) ctx.getMap().get(SvrGlobalCons.TAILER_MANAGER_KEY);
            //文件记录
            FileSaveRecord saveRecord = new FileSaveRecord();
            saveRecord.setNodeName(FtpConfig.getInstance().getNodeName());
            saveRecord.setSystemName(cxtBean.getSysname());
            saveRecord.setClientUserName(cxtBean.getUid());
            saveRecord.setFilePath(cxtBean.getSvrFilePath());
            saveRecord.setRequestFilePath(cxtBean.getSvrFilePath());
            saveRecord.setClientFilePath(cxtBean.getClientFileName());
            saveRecord.setOriginalFilePath(cxtBean.getFileName());
            saveRecord.setClientIp(cxtBean.getUserIp());
            //saveRecord.setFileSize(esbFile.fileLength())
            saveRecord.setFileSize(cxtBean.getFileSize());
            saveRecord.setUploadStartTime(cxtBean.getStartTime());
            saveRecord.setUploadEndTime(cxtBean.getEndTime());
            saveRecord.setState(0);
            saveRecord.setFileMd5(fileMd5);
            saveRecord.setFileVersion(cxtBean.getFileVersion());
            BizFileHelper.setFileNameExt(saveRecord);
            saveRecord.setNano(cxtBean.getNano());
            saveRecord.setFlowNo(cxtBean.getFlowNo());

            if (tailerManager == null) {
                esbFile.finish();
                EsbFileService.getInstance().save(saveRecord);
            } else {
                try {
                    tailerManager.stopTailing4ReplaceFile();
                    esbFile.finish();
                    EsbFileService.getInstance().save(saveRecord);
                    tailerManager.reopenFile(esbFile.getFile());
                    tailerManager.setWaitingFinish(false);
                } catch (IOException e) {
                    throw new FtpException(FtpErrCode.FILE_LAST_PIECE_ERROR, flowNo, nano, e);
                }
            }
        }
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        if (esbFile != null) {
            try {
                esbFile.close();
                esbFile = null;
            } catch (Exception e) {
                log.error("nano:{}#flowNo:{}#资源回收失败", nano, flowNo, e);
            }
        }
    }
}
