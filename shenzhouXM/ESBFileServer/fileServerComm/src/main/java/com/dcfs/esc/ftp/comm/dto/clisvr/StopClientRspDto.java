package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;

public class StopClientRspDto extends BaseDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.StopClientRsp;

    public StopClientRspDto() throws FtpException {

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
        final int bytesLen = 1;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

}
