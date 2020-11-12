package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/3.
 */
public class ExceptionDto extends BaseDto {
    private String seq;
    private String errCode;
    private String errMsg;

    public ExceptionDto() {
    }

    public ExceptionDto(String errCode) {
        this.errCode = errCode;
        this.errMsg = FtpErrCode.getCodeMsg(errCode);
    }

    public ExceptionDto(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ExceptionDto(String seq, String errCode, String errMsg) {
        this.seq = seq;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(seq);
        buf.writeShortString(errCode);
        buf.writeString(errMsg);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        seq = buf.readShortString();
        errCode = buf.readShortString();
        errMsg = buf.readString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 100;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return ChunkType.Exception;
    }


    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
