package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.helper.NetworkSpeedCtrlHelper;
import com.dcfs.esb.ftp.utils.ThreadSleepUtil;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataReqDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataReqDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.ContextBean;
import com.dcfs.esc.ftp.datanode.context.LoadCommContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import com.dcfs.esc.ftp.datanode.process.handler.interfac.DownloadProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在这里循环读取文件分片大小，这可以在设置当前渠道睡眠以及获取当前时间速度
 * 实时保护,第一次和每隔distance次做一次网速计算
 * Created by mocg on 2017/6/6.
 */
public class ResouceCtrlBySleepHandler extends UploadProcessHandlerAdapter implements DownloadProcessHandler {
    private static final Logger log = LoggerFactory.getLogger(ResouceCtrlBySleepHandler.class);
    public static boolean forTest = false;//NOSONAR
    public static int sleepSeconds = 5;//NOSONAR

    private LoadCommContextBean commContextBean;

    private SysContent sysContent = SysContent.getInstance();
    private long index = 0;
    private long index2 = 0;
    private static final int DISTANCE = 5;
    private long startTime;
    private long contLenSum2 = 0;//间隔内数据总大小
    private CachedContext esbContext;
    private long preSumContLen = 0;

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        ChannelContext channelContext = ctx.getChannelContext();
        ContextBean cxtBean = channelContext.getCxtBean();
        if (cxtBean instanceof LoadCommContextBean) {
            commContextBean = (LoadCommContextBean) cxtBean;
            esbContext = commContextBean.getEsbContext();
        }
        startTime = System.currentTimeMillis();
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        log.debug("ResouceCtrl...");
        Object requestObj = ctx.getRequestObj();
        if (requestObj instanceof FileUploadDataReqDto || requestObj instanceof FileDownloadDataReqDto) {
            long len = commContextBean.getSumContLen() - preSumContLen;
            preSumContLen = commContextBean.getSumContLen();
            contLenSum2 += len;
        } else {
            return msg;
        }

        if (index - index2 == DISTANCE) sysContent.minusNetworkSpeed(esbContext);

        //第一次和每隔distance次做一次网速计算
        if (index == 0 || index - index2 == DISTANCE) {
            long now = System.currentTimeMillis();
            long usedTime = now - startTime + 1;
            long speed = contLenSum2 * 1000 / usedTime;//byte/s //NOSONAR
            //当前请求结束后会自动减去speed
            sysContent.addNetworkSpeed(esbContext, speed);
            index2 = index;
            startTime = now;
            contLenSum2 = 0;
        }

        //for test
        if (forTest) ThreadSleepUtil.sleepSecondIngoreEx(sleepSeconds);

        return msg;
    }

    @Override
    public void afterProcess(ProcessHandlerContext ctx) throws Exception {
        //实时保护
        NetworkSpeedCtrlHelper.sleep(esbContext);
        index++;
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        SysContent.getInstance().minusNetworkSpeed(esbContext);
    }

}
