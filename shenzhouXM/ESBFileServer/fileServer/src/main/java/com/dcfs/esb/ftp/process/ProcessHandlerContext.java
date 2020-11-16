package com.dcfs.esb.ftp.process;

import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mocg on 2017/6/5.
 */
public class ProcessHandlerContext {

    private ChannelContext channelContext;
    private Object requestObj;//最先的请求对象，每次请求是一个新对象，在流程初始化时指定，一般过程中不允许改变
    private Object responseObj;//最终流程输出对象，每次请求是一个新对象，在流程初始化时指定，一般过程中不允许改变引用，可以改变属性值

    private boolean finish;
    private boolean fileloadSucc;//文件上传下载是否成功
    private boolean continueNextStart = true;
    private boolean continueNextPreProcess = true;
    private boolean continueNextProcess = true;
    private boolean continueNextAfterProcess = true;
    private boolean continueNextFinish = true;
    private boolean continueNextFinish2 = true;
    private boolean continueNextExceptionCaught = true;
    private ProcessStatus processStatus = ProcessStatus.NONE;

    private Map<String, Object> map = new HashMap<>();
    /*文件上传同步分发*/
    private boolean distSync = false;
    private boolean distributeByTail;//是否以tailer方式文件分发
    /*文件分发结果 1-开启分发并正在分发 -1-完成分发并失败 2-完成分发并成功 0-初始化未开启*/
    private int distributeResult;

    public ProcessHandlerContext(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    public void clean() {
        if (map != null) {
            map.clear();
            map = null;
        }

        //此处不能channelContext.clean(),会导致循环清理,留给ChunkDispatchHandler.clean()
        channelContext = null;

        requestObj = null;
        responseObj = null;
    }

    public final <T extends BaseDto & ReqDto> void setRequestObj(T reqDto) {
        requestObj = reqDto;
    }

    public final <T extends BaseDto & RspDto> void setResponseObj(T rspDto) {
        responseObj = rspDto;
    }

    public final <T extends BaseDto & ReqDto> T requestObj(Class<T> tClass) {//NOSONAR
        return (T) requestObj;
    }

    public final <T extends BaseDto & RspDto> T responseObj(Class<T> tClass) {//NOSONAR
        return (T) responseObj;
    }

    //

    public Object getRequestObj() {
        return requestObj;
    }

    public Object getResponseObj() {
        return responseObj;
    }

    protected final void setRequestObj(Object requestObj) {
        this.requestObj = requestObj;
    }

    protected final void setResponseObj(Object responseObj) {
        this.responseObj = responseObj;
    }

    //getter setter

    public ChannelContext getChannelContext() {
        return channelContext;
    }

    public void setChannelContext(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isFileloadSucc() {
        return fileloadSucc;
    }

    public void setFileloadSucc(boolean fileloadSucc) {
        this.fileloadSucc = fileloadSucc;
    }

    public boolean isContinueNextStart() {
        return continueNextStart;
    }

    public void setContinueNextStart(boolean continueNextStart) {
        this.continueNextStart = continueNextStart;
    }

    public boolean isContinueNextPreProcess() {
        return continueNextPreProcess;
    }

    public void setContinueNextPreProcess(boolean continueNextPreProcess) {
        this.continueNextPreProcess = continueNextPreProcess;
    }

    public boolean isContinueNextProcess() {
        return continueNextProcess;
    }

    public void setContinueNextProcess(boolean continueNextProcess) {
        this.continueNextProcess = continueNextProcess;
    }

    public boolean isContinueNextAfterProcess() {
        return continueNextAfterProcess;
    }

    public void setContinueNextAfterProcess(boolean continueNextAfterProcess) {
        this.continueNextAfterProcess = continueNextAfterProcess;
    }

    public boolean isContinueNextFinish() {
        return continueNextFinish;
    }

    public void setContinueNextFinish(boolean continueNextFinish) {
        this.continueNextFinish = continueNextFinish;
    }

    public boolean isContinueNextFinish2() {
        return continueNextFinish2;
    }

    public void setContinueNextFinish2(boolean continueNextFinish2) {
        this.continueNextFinish2 = continueNextFinish2;
    }

    public boolean isContinueNextExceptionCaught() {
        return continueNextExceptionCaught;
    }

    public void setContinueNextExceptionCaught(boolean continueNextExceptionCaught) {
        this.continueNextExceptionCaught = continueNextExceptionCaught;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public boolean isDistSync() {
        return distSync;
    }

    public void setDistSync(boolean distSync) {
        this.distSync = distSync;
    }

    public boolean isDistributeByTail() {
        return distributeByTail;
    }

    public void setDistributeByTail(boolean distributeByTail) {
        this.distributeByTail = distributeByTail;
    }

    public int getDistributeResult() {
        return distributeResult;
    }

    public void setDistributeResult(int distributeResult) {
        this.distributeResult = distributeResult;
    }
}
