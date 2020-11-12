package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/3.
 */
public class NoAuthDto extends ExceptionDto {

    public NoAuthDto() {
        setEnd(true);
    }

    public NoAuthDto(String errCode) {
        super(errCode);
        setEnd(true);
    }

    public NoAuthDto(String errCode, String errMsg) {
        super(errCode, errMsg);
        setEnd(true);
    }

    public NoAuthDto(String seq, String errCode, String errMsg) {
        super(seq, errCode, errMsg);
        setEnd(true);
    }

    @Override
    public ChunkType getChunkType() {
        return ChunkType.NoAuth;
    }

}
