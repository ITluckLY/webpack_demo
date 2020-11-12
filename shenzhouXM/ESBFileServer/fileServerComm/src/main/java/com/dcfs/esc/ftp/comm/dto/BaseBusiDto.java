package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.constant.SysCfg;
import com.dcfs.esc.ftp.comm.util.ByteUtil;

/**
 * Created by mocg on 2017/6/2.
 */
public abstract class BaseBusiDto extends BaseDto {
    /*分片大小,此值可能在传输过程有变化*/
    private int pieceNum = SysCfg.getPieceNum();
    /*传给客户端让其睡眠*/
    private int sleepTime;
    private String apiVersion;
    private String serverApiVersion;
    private String compressMode;
    /* 是否是打包 */
    private boolean pack;
    /* 加密的标志 */
    private boolean scrt;
    /* 上传后不路由 */
    private boolean dontRoute;
    /* 以客户端方式  */
    private boolean byClient;
    /*节点重定向 req的表示已是重定向,不需再次查询; rsp的表示发生重定向,同时返回目标节点地址列表*/
    private boolean redirect;
    private String tags;
    private String errCode;
    private String errMsg;
    /*捕获FtpException后是否尝试遍历下一个节点*/
    private boolean tryNextNode;

    public BaseBusiDto() {
    }

    public BaseBusiDto(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public BaseBusiDto(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public BaseBusiDto(String apiVersion, String errCode, String errMsg) {
        this.apiVersion = apiVersion;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeInt(pieceNum);
        buf.writeInt(sleepTime);
        byte flag = 0;
        flag = ByteUtil.setFlag(flag, 0, scrt);
        flag = ByteUtil.setFlag(flag, 1, pack);
        flag = ByteUtil.setFlag(flag, 2, dontRoute);//NOSONAR
        flag = ByteUtil.setFlag(flag, 3, byClient);//NOSONAR
        flag = ByteUtil.setFlag(flag, 4, redirect);//NOSONAR
        flag = ByteUtil.setFlag(flag, 5, tryNextNode);//NOSONAR
        buf.writeByte(flag);
        buf.writeShortString(apiVersion);
        buf.writeShortString(serverApiVersion);
        buf.writeShortString(compressMode);
        buf.writeShortString(tags);
        buf.writeShortString(errCode);
        buf.writeShortString(errMsg);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        pieceNum = buf.readInt();
        sleepTime = buf.readInt();
        byte flag = buf.readByte();
        scrt = ByteUtil.getFlag(flag, 0);
        pack = ByteUtil.getFlag(flag, 1);
        dontRoute = ByteUtil.getFlag(flag, 2);//NOSONAR
        byClient = ByteUtil.getFlag(flag, 3);//NOSONAR
        redirect = ByteUtil.getFlag(flag, 4);//NOSONAR
        tryNextNode = ByteUtil.getFlag(flag, 5);//NOSONAR
        apiVersion = buf.readShortString();
        serverApiVersion = buf.readShortString();
        compressMode = buf.readShortString();
        tags = buf.readShortString();
        errCode = buf.readShortString();
        errMsg = buf.readShortString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 100;
        return super.objBytesLen() + bytesLen;
    }

    public final void errCode(String errCode) {
        this.errCode = errCode;
        this.errMsg = FtpErrCode.getCodeMsg(errCode);
    }

    public abstract ChunkType getChunkType();

    public final String getApiVersion() {
        return apiVersion;
    }

    public final void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public final int getPieceNum() {
        return pieceNum;
    }

    public final void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
    }

    public final int getSleepTime() {
        return sleepTime;
    }

    public final void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public final String getServerApiVersion() {
        return serverApiVersion;
    }

    public final void setServerApiVersion(String serverApiVersion) {
        this.serverApiVersion = serverApiVersion;
    }

    public final String getCompressMode() {
        return compressMode;
    }

    public final void setCompressMode(String compressMode) {
        this.compressMode = compressMode;
    }

    public final boolean isPack() {
        return pack;
    }

    public final void setPack(boolean pack) {
        this.pack = pack;
    }

    public final boolean isScrt() {
        return scrt;
    }

    public final void setScrt(boolean scrt) {
        this.scrt = scrt;
    }

    public final boolean isDontRoute() {
        return dontRoute;
    }

    public final void setDontRoute(boolean dontRoute) {
        this.dontRoute = dontRoute;
    }

    public final boolean isByClient() {
        return byClient;
    }

    public final void setByClient(boolean byClient) {
        this.byClient = byClient;
    }

    public final String getTags() {
        return tags;
    }

    public final void setTags(String tags) {
        this.tags = tags;
    }

    public final String getErrCode() {
        return errCode;
    }

    public final void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public final String getErrMsg() {
        return errMsg;
    }

    public final void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public final boolean isRedirect() {
        return redirect;
    }

    public final void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public boolean isTryNextNode() {
        return tryNextNode;
    }

    public void setTryNextNode(boolean tryNextNode) {
        this.tryNextNode = tryNextNode;
    }
}
