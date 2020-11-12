package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;

public class StopClientReqDto extends BaseDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.StopClientReq;

    public StopClientReqDto() {

    }
    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 100;
        return super.objBytesLen() + bytesLen;
    }
    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }
}
