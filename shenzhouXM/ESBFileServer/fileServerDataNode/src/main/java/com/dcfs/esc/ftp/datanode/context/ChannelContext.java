package com.dcfs.esc.ftp.datanode.context;

import com.dcfs.esc.ftp.comm.chunk.ChunkConfig;
import com.dcfs.esc.ftp.comm.helper.ScrtKeyHelper;
import com.dcfs.esc.ftp.datanode.nework.handler.ChunkDispatchHandler;

/**
 * Created by mocg on 2017/6/3.
 *  服务器信息 ： 包含服务器信息、客户端信息和设置信息
 *
 */
public class ChannelContext {
    private long nano;
    /*服务器IP*/
    private String serverIp;
    private String userIp;
    private ContextBean cxtBean;
    private ChunkConfig chunkConfig;
    /*true-表示channel访问正常 false-立刻关闭channel,中止后续访问*/
    private boolean accepted = false;
    private boolean forceClose = false;
    /*业务结束标识*/
    private boolean bizEnd;
    /*当大于0时，要求当前时间必须大于此时间（毫秒）*/
    private long nextRequestAfterTime = 0;
    private String seq;

    private String currUserPwdMd5;
    /*流水号*/
    private String flowNo;

    /**
     *   初始化上下文
     * @param type
     */
    public final void initContextBean(ContextBeanType type) {
        if (cxtBean == null && type != null) {
            // type  枚举类型
            switch (type) {
                case UPLOAD:
                    cxtBean = new UploadContextBean();
                    break;
                case DOWNLOAD:
                    cxtBean = new DownloadContextBean();
                    break;
                case NODE_LIST:
                    cxtBean = new NodeListGetContextBean();
                    break;
                // add 20180330
                case HERAT_BEAT:
                    cxtBean = new HeartBeatContextBean();
                    break;
                default:
                    break;
            }
        }
        if (cxtBean == null) cxtBean = new SimpleContextBean();
        cxtBean.setNano(nano);
        chunkConfig = chunkConfig();
    }

    // * 客户端信息
    @SuppressWarnings("unchecked")
    public <T extends ContextBean> T
    cxtBean() {
        return (T) cxtBean;
    }

    public ChunkConfig chunkConfig() {
        if (chunkConfig == null) chunkConfig = new ChunkConfig();
        return chunkConfig;
    }

    public byte[] desKey() {
        return ScrtKeyHelper.convert(currUserPwdMd5, seq);
    }

    /**
     * 增强此方法只能由ChunkDispatchHandler调用
     *
     * @param handler
     */
    public void clean(ChunkDispatchHandler handler) { //NOSONAR
        if (cxtBean != null) {
            cxtBean.clean();
            cxtBean = null;
        }
    }

    public void bizEndTrue(boolean... bizEnds) {
        if (this.bizEnd || bizEnds == null) return;
        for (boolean end : bizEnds) {
            this.bizEnd = end;
            if (this.bizEnd) break;
        }
    }

    //gettet setter
    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public ContextBean getCxtBean() {
        return cxtBean;
    }

    public ChunkConfig getChunkConfig() {
        return chunkConfig;
    }

    public void setChunkConfig(ChunkConfig chunkConfig) {
        this.chunkConfig = chunkConfig;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isForceClose() {
        return forceClose;
    }

    public void setForceClose(boolean forceClose) {
        this.forceClose = forceClose;
    }

    public boolean isBizEnd() {
        return bizEnd;
    }

    public void setBizEnd(boolean bizEnd) {
        this.bizEnd = bizEnd;
    }

    public long getNextRequestAfterTime() {
        return nextRequestAfterTime;
    }

    public void setNextRequestAfterTime(long nextRequestAfterTime) {
        this.nextRequestAfterTime = nextRequestAfterTime;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getCurrUserPwdMd5() {
        return currUserPwdMd5;
    }

    public void setCurrUserPwdMd5(String currUserPwdMd5) {
        this.currUserPwdMd5 = currUserPwdMd5;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
