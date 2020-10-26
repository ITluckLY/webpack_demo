package com.dc.smarteam.modules.client.file;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;

public class FileSearchRspDto extends BaseDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.FileSearchRsp;
    private boolean auth;
    private boolean succ;
    private String jsonMsg;
    public FileSearchRspDto() {
        setEnd(true);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeBoolean(succ);
        buf.writeString(jsonMsg);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        succ = buf.readBoolean();
        jsonMsg = buf.readString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 1;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public String getJsonMsg() {
        return jsonMsg;
    }

    public void setJsonMsg(String jsonMsg) {
        this.jsonMsg = jsonMsg;
    }
}
