package com.dcfs.esc.ftp.svr.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBizDto;

/**
 * Created by mocg on 2017/6/3.
 */
public class GetFilePathRspDto extends BaseBizDto {
    public static final ChunkType chunkType = ChunkType.GetFilePathRsp;
    private boolean auth;
    private boolean succ;
    private String filePathValueList;

    public GetFilePathRspDto() {
        //needed
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeBoolean(succ);
        buf.writeString(filePathValueList);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        succ = buf.readBoolean();
        filePathValueList = buf.readString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 1024;
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

    public String getFilePathValueList() {
        return filePathValueList;
    }

    public void setFilePathValueList(String filePathValueList) {
        this.filePathValueList = filePathValueList;
    }
}
