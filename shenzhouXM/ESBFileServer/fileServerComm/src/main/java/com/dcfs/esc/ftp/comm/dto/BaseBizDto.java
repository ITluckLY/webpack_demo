package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.constant.SysConst;

/**
 * 内部基础业务Dto
 * 区分com.dcfs.esc.ftp.comm.dto.BaseBusiDto
 */
public abstract class BaseBizDto extends BaseDto {
    private int pieceNum = SysConst.DEF_PIECE_NUM;//分片大小,此值可能在传输过程有变化
    private int sleepTime;//传给客户端让其睡眠
    private String authSeq;
    /* 加密的标志 */
    private boolean scrt;
    private String tags;
    private String errCode;
    private String errMsg;

    public BaseBizDto() {
    }

    public BaseBizDto(String authSeq) {
        this.authSeq = authSeq;
    }

    public BaseBizDto(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public BaseBizDto(String authSeq, String errCode, String errMsg) {
        this.authSeq = authSeq;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeInt(pieceNum);
        buf.writeInt(sleepTime);
        buf.writeBoolean(scrt);
        buf.writeShortString(authSeq);
        buf.writeShortString(tags);
        buf.writeShortString(errCode);
        buf.writeShortString(errMsg);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        pieceNum = buf.readInt();
        sleepTime = buf.readInt();
        scrt = buf.readBoolean();
        authSeq = buf.readShortString();
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

    public final String getAuthSeq() {
        return authSeq;
    }

    public final void setAuthSeq(String authSeq) {
        this.authSeq = authSeq;
    }

    public final int getPieceNum() {
        return pieceNum;
    }

    public final void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public final boolean isScrt() {
        return scrt;
    }

    public final void setScrt(boolean scrt) {
        this.scrt = scrt;
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
}
