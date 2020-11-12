package com.dcfs.esc.ftp.svr.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBizDto;

/**
 * Created by mocg on 2017/6/3.
 */
public class PutFilePathRspDto extends BaseBizDto {
    public static final ChunkType chunkType = ChunkType.PutFilePathRsp;
    private boolean auth;
    private boolean succ;

    public PutFilePathRspDto() {
        //needed
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeBoolean(succ);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        succ = buf.readBoolean();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 2;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
