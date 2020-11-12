package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/3.
 */
public class EndDto extends BaseDto {
    public EndDto() {
        setEnd(true);
    }

    @Override
    public ChunkType getChunkType() {
        return ChunkType.End;
    }

}
