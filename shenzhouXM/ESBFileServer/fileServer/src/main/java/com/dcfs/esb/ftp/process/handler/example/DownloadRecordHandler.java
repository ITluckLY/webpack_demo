package com.dcfs.esb.ftp.process.handler.example;

import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.helper.FileVersionHelper;
import com.dcfs.esb.ftp.impls.flow.FLowHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.model.FileDownloadRecord;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BeanConverter;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.DownloadProcessHandlerAdapter;

import java.util.Date;

/**
 * Created by mocg on 2017/6/7.
 */
public class DownloadRecordHandler extends DownloadProcessHandlerAdapter {

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        ChannelContext channelContext = ctx.getChannelContext();
        DownloadContextBean cxtBean = channelContext.cxtBean();
        CachedContext esbContext = cxtBean.getEsbContext();
        FileMsgBean fileMsgBean = cxtBean.getFileMsgBean();

        FileDownloadRecord downloadRecord = BeanConverter.strictConvertTo(fileMsgBean, FileDownloadRecord.class);
        downloadRecord.setStartTime(cxtBean.getStartTime());
        downloadRecord.setEndTime(new Date());
        downloadRecord.setSuss(!FLowHelper.hasError(esbContext));
        downloadRecord.setSysname(cxtBean.getSysname());
        downloadRecord.setNodeName(FtpConfig.getInstance().getNodeName());
        downloadRecord.setNodeList(null);
        downloadRecord.setFileVersion(FileVersionHelper.getFileVersion(esbContext));
        EsbFileService.getInstance().logFileDownload(downloadRecord);
    }
}
