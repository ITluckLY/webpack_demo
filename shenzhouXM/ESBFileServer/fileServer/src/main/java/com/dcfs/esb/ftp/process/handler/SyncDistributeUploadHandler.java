package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esc.ftp.comm.dto.FileUploadDataReqDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataRspDto;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1、文件分发同步
 * Created by mocg on 2017/6/6.
 */
public class SyncDistributeUploadHandler extends UploadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SyncDistributeUploadHandler.class);

    //最长同步等待时间，防止占用线程，大文件最好不用同步方式
    //使用心跳机制 可以支持长时间
    private static final int DEF_WAITTIME = 1000 * 60 * 15;//15分钟
    private static int waitTime = DEF_WAITTIME;
    private boolean isBreak = false;

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileUploadDataReqDto reqDto = ctx.requestObj(FileUploadDataReqDto.class);
        ctx.setDistSync(reqDto.isDistSync());
        return msg;
    }

    @Override
    public void finish2(final ProcessHandlerContext ctx) throws Exception {//NOSONAR
        if (!ctx.isFileloadSucc()) return;
        if (ctx.isDistSync()) {
            final long startTime = System.currentTimeMillis();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ctx.getDistributeResult() == 1 && !Thread.currentThread().isInterrupted()) {//1-开启分发并正在分发 -1-完成分发并失败 2-完成分发并成功 0-初始化未开启
                        if (isBreak || System.currentTimeMillis() - startTime > waitTime) break;
                        long nano = ctx.getChannelContext().getNano();
                        String flowNo = ctx.getChannelContext().getFlowNo();
                        log.trace("nano:{}#flowNo:{}#同步上传#文件分发尚未完成，继续等待", nano, flowNo);
                        try {
                            final int sleeptime = 200;
                            Thread.sleep(sleeptime);
                        } catch (InterruptedException e) {
                            log.warn("nano:{}#flowNo:{}#文件同步分发超时中断同步", nano, flowNo, e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
            thread.join();

            //返回同步结果
            FileUploadDataRspDto rspDto = ctx.responseObj(FileUploadDataRspDto.class);
            rspDto.setDistributeResult(ctx.getDistributeResult());
        }
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        isBreak = true;
    }

    public static int getWaitTime() {
        return waitTime;
    }

    public static void setWaitTime(int waitTime) {
        SyncDistributeUploadHandler.waitTime = waitTime;
    }
}
