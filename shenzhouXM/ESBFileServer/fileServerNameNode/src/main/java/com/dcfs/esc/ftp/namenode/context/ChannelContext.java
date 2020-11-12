package com.dcfs.esc.ftp.namenode.context;

import com.dcfs.esc.ftp.comm.chunk.ChunkConfig;
import com.dcfs.esc.ftp.comm.helper.ScrtKeyHelper;

/**
 * Created by mocg on 2017/6/3.
 */
public class ChannelContext {
    private long nano;
    private String userIp;
    private ChunkConfig chunkConfig;
    //true-表示channel访问正常 false-立刻关闭channel,中止后续访问
    private boolean accepted = false;
    private boolean forceClose = false;
    //当大于0时，要求当前时间必须大于此时间（毫秒）
    private long nextRequestAfterTime;
    private String seq;
    private String currUserPwdMd5;

    public ChunkConfig chunkConfig() {
        if (chunkConfig == null) chunkConfig = new ChunkConfig();
        return chunkConfig;
    }

    public byte[] desKey() {
        return ScrtKeyHelper.convert(currUserPwdMd5, seq);
    }

    public void clean() {
        chunkConfig = null;
    }

    //gettet setter
    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
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

}
