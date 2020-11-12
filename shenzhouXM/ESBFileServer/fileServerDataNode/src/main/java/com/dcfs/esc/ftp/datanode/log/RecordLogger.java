package com.dcfs.esc.ftp.datanode.log;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.model.FileDownloadRecord;
import com.dcfs.esb.ftp.server.model.FileUploadRecord;
import com.dcfs.esb.ftp.server.model.NodeListGetRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BeanConverter;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.context.NodeListGetContextBean;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;

import java.util.Date;

/**
 * Created by mocg on 2017/6/7.
 */
public class RecordLogger {

    public void logRecord(ChunkType chunkType, ChannelContext channelContext, Date startTime, Date endTime) {
        if (chunkType == null) return;
        switch (chunkType) {
            case DownloadAuthReq:
                logDownload(channelContext, startTime, endTime);
                break;
            case DownloadDataReq:
                logDownload(channelContext, startTime, endTime);
                break;
            case UploadAuthReq:
                logUpload(channelContext, startTime, endTime);
                break;
            case UploadDataReq:
                logUpload(channelContext, startTime, endTime);
                break;
            case NodeListReq:
                logNodeListGet(channelContext, startTime, endTime);
                break;
            case End:
                break;
            default:
                break;
        }

    }

    private void logUpload(ChannelContext channelContext, Date startTime, Date endTime) {
        UploadContextBean cxtBean = channelContext.cxtBean();
        CachedContext esbContext = cxtBean.getEsbContext();
        FileMsgBean fileMsgBean = cxtBean.getFileMsgBean();

        FileUploadRecord record = BeanConverter.strictConvertTo(fileMsgBean, FileUploadRecord.class);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setSuss(!FLowHelper.hasError(esbContext) && FtpErrCode.isSucc(cxtBean.getErrCode()));
        record.setSysname(cxtBean.getSysname());
        record.setNodeName(FtpConfig.getInstance().getNodeName());
        record.setNodeList(null);
        //record.setFileVersion(FileVersionHelper.getFileVersion(esbContext));//NOSONAR
        record.setFileVersion(cxtBean.getFileVersion());
        record.setLastPiece(channelContext.chunkConfig().isLastChunk());
        record.setNano(channelContext.getNano());
        record.setErrCode(cxtBean.getErrCode());
        record.setFileRetMsg(cxtBean.getErrMsg());
        record.setServerIp(channelContext.getServerIp());
        record.setClientIp(channelContext.getUserIp());
        record.setOriFilename(cxtBean.getOriFilename());
        record.setFlowNo(cxtBean.getFlowNo());
        record.setUploadId(cxtBean.getUploadId());
        EsbFileService.getInstance().logFileUpload(record);
    }

    private void logDownload(ChannelContext channelContext, Date startTime, Date endTime) {
        DownloadContextBean cxtBean = channelContext.cxtBean();
        CachedContext esbContext = cxtBean.getEsbContext();
        FileMsgBean fileMsgBean = cxtBean.getFileMsgBean();

        FileDownloadRecord record = BeanConverter.strictConvertTo(fileMsgBean, FileDownloadRecord.class);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setSuss(!FLowHelper.hasError(esbContext) && FtpErrCode.isSucc(cxtBean.getErrCode()));
        record.setSysname(cxtBean.getSysname());
        record.setNodeName(FtpConfig.getInstance().getNodeName());
        record.setNodeList(null);
        //record.setFileVersion(FileVersionHelper.getFileVersion(esbContext));//NOSONAR
        record.setFileVersion(cxtBean.getFileVersion());
        record.setLastPiece(channelContext.chunkConfig().isLastChunk());
        record.setNano(channelContext.getNano());
        record.setErrCode(cxtBean.getErrCode());
        record.setFileRetMsg(cxtBean.getErrMsg());
        record.setServerIp(channelContext.getServerIp());
        record.setClientIp(channelContext.getUserIp());
        record.setFlowNo(cxtBean.getFlowNo());
        record.setDownloadId(cxtBean.getDownloadId());
        EsbFileService.getInstance().logFileDownload(record);
    }

    private void logNodeListGet(ChannelContext channelContext, Date startTime, Date endTime) {
        NodeListGetContextBean cxtBean = channelContext.cxtBean();
        NodeListGetRecord record = BeanConverter.strictConvertTo(cxtBean, NodeListGetRecord.class);
        record.setClientIp(channelContext.getUserIp());
        record.setServerIp(channelContext.getServerIp());
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setSuss(cxtBean.isAuth() && FtpErrCode.isSucc(cxtBean.getErrCode()));
        EsbFileService.getInstance().logNodeListGet(record);
    }
}
