package com.dcfs.esc.ftp.datanode.process.handler.interfac;

import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;

/**
 * Created by mocg on 2017/6/5.
 */
public interface ProcessHandler {
    /**
     * ProcessExecutor开始时调用，只调用一次，不能在此方法内获取requestObj、responseObj
     *
     * @param ctx
     */
    void start(ProcessHandlerContext ctx) throws Exception;//NOSONAR

    /**
     * 每分片
     *
     * @param ctx
     * @throws Exception
     */
    void preProcess(ProcessHandlerContext ctx) throws Exception;//NOSONAR

    Object process(ProcessHandlerContext ctx, Object msg) throws Exception;//NOSONAR

    void afterProcess(ProcessHandlerContext ctx) throws Exception;//NOSONAR

    /**
     * 文件上传完成后调用，无论是否成功，ctx中有属性可以判断成败
     *
     * @param ctx
     * @throws Exception
     */
    void finish(ProcessHandlerContext ctx) throws Exception;//NOSONAR

    /**
     * 分两个finish层，先执行完第一层，再执行第二层
     *
     * @param ctx
     * @throws Exception
     */
    void finish2(ProcessHandlerContext ctx) throws Exception;//NOSONAR

    void exceptionCaught(ProcessHandlerContext ctx, Throwable cause) throws Exception;//NOSONAR

    void clean(ProcessHandlerContext ctx);
}
