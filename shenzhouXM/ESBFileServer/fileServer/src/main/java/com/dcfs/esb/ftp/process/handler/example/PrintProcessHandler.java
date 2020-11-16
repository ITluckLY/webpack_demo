package com.dcfs.esb.ftp.process.handler.example;

import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.DownloadProcessHandler;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.UploadProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/5.
 */
public class PrintProcessHandler implements UploadProcessHandler, DownloadProcessHandler {
    private static final Logger log = LoggerFactory.getLogger(PrintProcessHandler.class);
    private String flag;

    public PrintProcessHandler(String flag) {
        this.flag = flag;
    }

    @Override
    public void start(ProcessHandlerContext ctx) {
        log.info("start#{}", flag);
    }

    @Override
    public void preProcess(ProcessHandlerContext ctx) throws Exception {
        log.info("preProcess#{}", flag);
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        log.info("process#{}", flag);
        return msg;
    }

    @Override
    public void afterProcess(ProcessHandlerContext ctx) throws Exception {
        log.info("afterProcess#{}", flag);
    }

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        log.info("finish#{}", flag);
    }

    @Override
    public void finish2(ProcessHandlerContext ctx) throws Exception {
        log.info("finish2#{}", flag);
    }

    @Override
    public void exceptionCaught(ProcessHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught#{}", flag);
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        //nothing
    }
}
