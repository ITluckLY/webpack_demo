package com.dcfs.esc.ftp.datanode.process.handler.adapter;

import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.UploadProcessHandler;

/**
 * Created by mocg on 2017/6/5.
 */
public class UploadProcessHandlerAdapter implements UploadProcessHandler {

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        //nothing
    }

    @Override
    public void preProcess(ProcessHandlerContext ctx) throws Exception {
        //nothing
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public void afterProcess(ProcessHandlerContext ctx) throws Exception {
        //nothing
    }

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        //nothing
    }

    @Override
    public void finish2(ProcessHandlerContext ctx) throws Exception {
        //nothing
    }

    @Override
    public void exceptionCaught(ProcessHandlerContext ctx, Throwable cause) throws Exception {
        //nothing
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        //nothing
    }
}
