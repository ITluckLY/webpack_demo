package com.dcfs.esc.ftp.comm.chunk;

/**
 * Chunk静态配置
 * Created by mocg on 2017/7/29.
 */
public class ChunkCfgCons {
    /*分片大小最大5M*/
    public static final int MAX_CHUNK_FRAME_LENGTH = 1024 * 1024 * 5;
    /*固定为4 chunk前面4个字节表示chunk总长度,不包括最开头的4个字节*/
    public static final int LENGTH_FIELD_LENGTH = StreamChunk.LENGTH_FIELD_LENGTH;

    private ChunkCfgCons() {
    }
}
