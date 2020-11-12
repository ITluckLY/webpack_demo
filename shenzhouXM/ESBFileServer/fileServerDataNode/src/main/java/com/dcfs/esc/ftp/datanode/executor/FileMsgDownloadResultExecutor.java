package com.dcfs.esc.ftp.datanode.executor;

import com.dcfs.esb.ftp.server.model.FileMsgDownloadResultRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgDownloadResultReqDto;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgDownloadResultRspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;

/**
 * Created by mocg on 2017/6/3.
 */
public class FileMsgDownloadResultExecutor extends BaseBusinessExecutor {

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke0(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception {
        final long nano = channelContext.getNano();
        FileMsgDownloadResultReqDto reqDto = (FileMsgDownloadResultReqDto) dto;
        if (log.isDebugEnabled()) log.debug("nano:{}#flowno{}#fileMsgDownloadResult:{}", nano, reqDto.getFlowNo(), GsonUtil.toJson(reqDto));
        FileMsgDownloadResultRecord fileMsgDownloadResultRecord = new FileMsgDownloadResultRecord();
        fileMsgDownloadResultRecord.setTranCode(reqDto.getTranCode());
        fileMsgDownloadResultRecord.setToUid(reqDto.getToUid());
        fileMsgDownloadResultRecord.setClientIp(channelContext.getUserIp());
        fileMsgDownloadResultRecord.setServerFileName(reqDto.getServerFileName());
        fileMsgDownloadResultRecord.setErrCode(reqDto.getErrCode());
        fileMsgDownloadResultRecord.setErrMsg(reqDto.getErrMsg());
        fileMsgDownloadResultRecord.setNano(nano);
        fileMsgDownloadResultRecord.setResultsucc(false);
        fileMsgDownloadResultRecord.setFlowNo(reqDto.getFlowNo());
        EsbFileService.getInstance().logFileMsgDownloadResult(fileMsgDownloadResultRecord);

        FileMsgDownloadResultRspDto rspDto = new FileMsgDownloadResultRspDto();
        return (T) rspDto;
    }
}
