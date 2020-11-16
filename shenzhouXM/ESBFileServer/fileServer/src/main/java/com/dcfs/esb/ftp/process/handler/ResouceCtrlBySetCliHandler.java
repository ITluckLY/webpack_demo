package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.config.SysContent;
import com.dcfs.esb.ftp.server.helper.NetworkSpeedCtrlHelper;
import com.dcfs.esb.ftp.utils.ThreadSleepUtil;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataRspDto;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataReqDto;
import com.dcfs.esc.ftp.comm.util.NumberConvertUtil;
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
public class ResouceCtrlBySetCliHandler extends UploadProcessHandlerAdapter implements DownloadProcessHandler {
    private static final Logger log = LoggerFactory.getLogger(ResouceCtrlBySetCliHandler.class);
    public static boolean forTest = false;//NOSONAR
    public static int sleepSeconds = 5;//NOSONAR

    private LoadCommContextBean commContextBean;

    private SysContent sysContent = SysContent.getInstance();
    private long index = 0;
    private long index2 = 0;
    private static final int DISTANCE = 5;
    private long startTime;
    private long contLenSum = 0;//间隔内数据总大小
    private CachedContext esbContext;
    private long nano;
    private String flowNo;
    /*当流控睡眠时间为0，不作流控计算*/
    private long networkCtrlSleepTime;

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        ChannelContext channelContext = ctx.getChannelContext();
        nano = channelContext.getNano();
        flowNo = channelContext.getFlowNo();
        ContextBean cxtBean = channelContext.getCxtBean();
        if (cxtBean instanceof LoadCommContextBean) {
            commContextBean = (LoadCommContextBean) cxtBean;
            esbContext = commContextBean.getEsbContext();
        }
        startTime = System.currentTimeMillis();
        networkCtrlSleepTime = FtpConfig.getInstance().getNetworkCtrlSleepTime();
    }

    @Override
    public void preProcess(ProcessHandlerContext ctx) throws Exception {
        //当流控睡眠时间为0，不作流控计算
        if (networkCtrlSleepTime == 0) return;
        Object requestObj = ctx.getRequestObj();
        //上传
        if (requestObj instanceof FileUploadDataReqDto) {
            FileUploadDataReqDto uploadDataReqDto = (FileUploadDataReqDto) requestObj;
            if (uploadDataReqDto.getFileCont() != null) {
                contLenSum += uploadDataReqDto.getFileCont().length;
                commContextBean.sumContLen(uploadDataReqDto.getFileCont().length);
            }
        }
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        log.trace("ResouceCtrl...");
        //for test
        if (forTest) ThreadSleepUtil.sleepSecondIngoreEx(sleepSeconds);

        return msg;
    }

    @Override
    public void afterProcess(ProcessHandlerContext ctx) throws Exception {
        //当流控睡眠时间为0，不作流控计算
        if (networkCtrlSleepTime == 0) return;
        //下载
        Object responseObj = ctx.getResponseObj();
        if (responseObj instanceof FileDownloadDataRspDto) {
            FileDownloadDataRspDto downloadDataRspDto = (FileDownloadDataRspDto) responseObj;
            if (downloadDataRspDto.getFileCont() != null) {
                contLenSum += downloadDataRspDto.getFileCont().length;
                commContextBean.sumContLen(downloadDataRspDto.getFileCont().length);
            }
        }
        //
        CapabilityDebugHelper.markCurrTime("computeNetworkSpeed+setTime-before");
        computeNetworkSpeed();
        setNextRequestAfterTime(ctx);
        CapabilityDebugHelper.markCurrTime("computeNetworkSpeed+setTime-after");
        index++;
    }

    private void computeNetworkSpeed() {
        if (index - index2 == DISTANCE) sysContent.minusNetworkSpeed(esbContext);
        //第一次和每隔distance次做一次网速计算
        if (index == 0 || index - index2 == DISTANCE) {
            long now = System.currentTimeMillis();
            long usedTime = Math.max(now - startTime, 1);
            long speed = contLenSum * 1000 / usedTime;//单位:byte/s //NOSONAR
            //当前请求结束后会自动减去speed
            sysContent.addNetworkSpeed(esbContext, speed);
            index2 = index;
            startTime = now;
            contLenSum = 0;
        }
    }

    private void setNextRequestAfterTime(ProcessHandlerContext ctx) {
        //实时保护，设置客户端的睡眠时间、分片大小
        Object responseObj = ctx.getResponseObj();
        if (responseObj instanceof BaseBusiDto) {
            BaseBusiDto busiDto = (BaseBusiDto) responseObj;
            busiDto.setPieceNum(NetworkSpeedCtrlHelper.getPieceNum(esbContext));
            int sleepTime = NumberConvertUtil.longToInt(NetworkSpeedCtrlHelper.getSleepTime(esbContext));
            busiDto.setSleepTime(sleepTime);
            if (sleepTime > 0) {
                ctx.getChannelContext().setNextRequestAfterTime(System.currentTimeMillis() + sleepTime);
            }
            if (log.isTraceEnabled()) log.trace("nano:{}#flowNo:{}#set client sleepTime:{}", nano, flowNo, sleepTime);
        } else {
            NetworkSpeedCtrlHelper.sleep(esbContext);
        }
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        if (esbContext != null) SysContent.getInstance().minusNetworkSpeed(esbContext);
    }

}
