package com.dcfs.esc.ftp.datanode.executor;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esc.ftp.comm.dto.*;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessExecutor;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.*;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.DownloadProcessHandler;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.ProcessHandler;

import java.util.List;

/**
 * Created by mocg on 2017/6/3.
 */
public class DownloadExecutor extends BaseBusinessExecutor {

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends BaseDto & RspDto, D extends BaseDto & ReqDto> T invoke0(ChannelContext channelContext, D dto, Class<T> tClass) throws Exception {
        if (!(dto instanceof FileDownloadDataReqDto)) {
            throw new FtpException(FtpErrCode.FAIL, dto.getNano()
                    , "FileDownloadDataReqDto#请求对象类型错误#" + dto.getClass().getName());
        }
        FileDownloadDataReqDto reqDto = (FileDownloadDataReqDto) dto;
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#文件下载请求#position:{}", channelContext.getNano(), channelContext.getFlowNo(), reqDto.getPosition());
        }
        DownloadContextBean cxtBean = channelContext.cxtBean();
        //流程处理
        ProcessExecutor processExecutor = cxtBean.getProcessExecutor();
        if (processExecutor == null) {
            processExecutor = new ProcessExecutor();
            cxtBean.setProcessExecutor(processExecutor);
            addHandlers(processExecutor);
            ProcessHandlerContext processHandlerContext = new ProcessHandlerContext(channelContext);
            cxtBean.setProcessHandlerContext(processHandlerContext);
            processExecutor.start(processHandlerContext);
        }
        //设置ResponseObj
        ProcessHandlerContext processHandlerContext = cxtBean.getProcessHandlerContext();
        processHandlerContext.setRequestObj(reqDto);
        FileDownloadDataRspDto rspDto = new FileDownloadDataRspDto();
        processHandlerContext.setResponseObj(rspDto);
        //流程执行
        CapabilityDebugHelper.markCurrTime("downloadExecutor.processExecutor.invoke-before");
        processExecutor.invoke(processHandlerContext, reqDto);
        CapabilityDebugHelper.markCurrTime("downloadExecutor.processExecutor.invoke-end");
        //加工处理返回结果
        rspDto = processHandlerContext.responseObj(FileDownloadDataRspDto.class);
        return (T) rspDto;
    }

    /**
     *   ？？？
     * @param processExecutor
     */
    private void addHandlers(ProcessExecutor processExecutor) {
        List<ProcessHandler> handlers = processExecutor.getHandlers();
        filterAndAdd(handlers, new ResouceCtrlBySetCliHandler());
        filterAndAdd(handlers, new DownloadFileByRafHandler());
        filterAndAdd(handlers, new DownloadCompressHandler());
        filterAndAdd(handlers, new DownloadEncryptHandler());
        filterAndAdd(handlers, new LogOnFinishHandler());
        //filterAndAdd(handlers, new DownloadRecordHandler());//NOSONAR
    }

    private void filterAndAdd(List<ProcessHandler> handlers, ProcessHandler handler) {
        if (handler instanceof DownloadProcessHandler) handlers.add(handler);
    }
}
