package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.DownloadProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/6.
 */
public class LogOnFinishHandler extends UploadProcessHandlerAdapter implements DownloadProcessHandler {
    private static final Logger log = LoggerFactory.getLogger(LogOnFinishHandler.class);

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        log.debug("fileload finish");
    }

}
