package com.dcfs.esb.ftp.process.handler.example;


import com.dcfs.esb.ftp.process.ProcessHandlerContext;
import com.dcfs.esb.ftp.process.handler.adapter.UploadProcessHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/6.
 */
public class FileDistributeHandler extends UploadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(FileDistributeHandler.class);

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        if (!ctx.isFileloadSucc()) return;
        log.debug("FileDistribute...");
        ctx.setDistributeResult(0);
    }
}
