package com.dcfs.esc.ftp.datanode.process.handler.example;

import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.helper.FileVersionHelper;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.model.FileUploadRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BeanConverter;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;

import java.util.Date;

/**
 * Created by mocg on 2017/6/7.
 */
public class UploadRecordHandler extends UploadProcessHandlerAdapter {

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        ChannelContext channelContext = ctx.getChannelContext();
        UploadContextBean cxtBean = channelContext.cxtBean();
        CachedContext esbContext = cxtBean.getEsbContext();
        FileMsgBean fileMsgBean = cxtBean.getFileMsgBean();

        FileUploadRecord fileUploadRecord = BeanConverter.strictConvertTo(fileMsgBean, FileUploadRecord.class);
        fileUploadRecord.setStartTime(cxtBean.getStartTime());
        fileUploadRecord.setEndTime(new Date());
        fileUploadRecord.setSuss(!FLowHelper.hasError(esbContext));
        fileUploadRecord.setSysname(cxtBean.getSysname());
        fileUploadRecord.setNodeName(FtpConfig.getInstance().getNodeName());
        fileUploadRecord.setNodeList(null);
        fileUploadRecord.setFileVersion(FileVersionHelper.getFileVersion(esbContext));
        EsbFileService.getInstance().logFileUpload(fileUploadRecord);
    }
}
