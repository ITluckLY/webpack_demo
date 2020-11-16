package com.dcfs.esb.ftp.process.handler.adapter;


import com.dcfs.esb.ftp.process.ProcessHandlerContext;
import com.dcfs.esb.ftp.process.handler.interfac.UploadProcessHandler;

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
