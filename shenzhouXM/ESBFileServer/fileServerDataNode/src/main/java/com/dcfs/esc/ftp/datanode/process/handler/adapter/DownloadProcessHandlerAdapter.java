package com.dcfs.esc.ftp.datanode.process.handler.adapter;

import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.DownloadProcessHandler;

/**
 * Created by mocg on 2017/6/7.
 */
public class DownloadProcessHandlerAdapter implements DownloadProcessHandler {
    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        //empty
    }

    @Override
    public void preProcess(ProcessHandlerContext ctx) throws Exception {
        //empty
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public void afterProcess(ProcessHandlerContext ctx) throws Exception {
        //empty
    }

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        //empty
    }

    @Override
    public void finish2(ProcessHandlerContext ctx) throws Exception {
        //empty
    }

    @Override
    public void exceptionCaught(ProcessHandlerContext ctx, Throwable cause) throws Exception {
        //empty
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        //empty
    }
}
