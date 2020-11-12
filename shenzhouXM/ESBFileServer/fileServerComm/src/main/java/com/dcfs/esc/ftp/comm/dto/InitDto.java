package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/3.
 */
public class InitDto extends BaseDto {
    private String seq;

    public InitDto() {
    }

    public InitDto(String seq) {
        this.seq = seq;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(seq);
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 128;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        seq = buf.readShortString();
    }

    @Override
    public ChunkType getChunkType() {
        return ChunkType.Init;
    }


    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
