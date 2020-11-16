package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataRspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.pool.ThreadExecutorFactory;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Created by huangzbb on 2017/7/6.
 */
public class BakOnFinishHandler extends UploadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(BakOnFinishHandler.class);
    private static Executor exe = ThreadExecutorFactory.getExecutorForFileBackup();

    @Override
    public void finish2(ProcessHandlerContext ctx) throws Exception {
        log.debug("BakOnFinishHandler...");
        if (!ctx.isFileloadSucc()) return;
        FileUploadDataRspDto rspDto = ctx.responseObj(FileUploadDataRspDto.class);
        ChannelContext channelContext = ctx.getChannelContext();
        UploadContextBean cxtBean = channelContext.cxtBean();
        doFileRoute(cxtBean);
        rspDto.setFileBackupResult(cxtBean.getFileBackupResult());
    }

    private void doFileRoute(UploadContextBean cxtBean) {
        String svrFilePath = cxtBean.getSvrFilePath();
        String localFileName = EsbFileManager.getInstance().getFileAbsolutePath(svrFilePath);
        log.debug("传输完成，将文件[{}]备份至运维部", localFileName);
        // TODO 备份时包含文件路径信息\1001\aaa\7z9aoh19109b.txt
    }
}
