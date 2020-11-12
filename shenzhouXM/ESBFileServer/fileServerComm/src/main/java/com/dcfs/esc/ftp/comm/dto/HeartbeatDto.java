package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * 心跳
 * Created by mocg on 2017/8/17.
 */
public class HeartbeatDto extends BaseDto {
    public static final HeartbeatDto INSTANCE = new HeartbeatDto();

    @Override
    public ChunkType getChunkType() {
        return ChunkType.Heartbeat;
    }
}
