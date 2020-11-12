package com.dcfs.esc.ftp.comm.chunk.util;

import com.dcfs.esc.ftp.comm.chunk.ChunkCfgCons;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.chunk.StreamChunk;
import com.dcfs.esc.ftp.comm.exception.CorruptedChunkException;
import com.dcfs.esc.ftp.comm.util.BytesUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cgmo on 2017/6/1.
 */
public class ChunkStreamUtil {
    /*默认分片大小最大5M*/
    private static final int MAX_CHUNK_FRAME_LENGTH = ChunkCfgCons.MAX_CHUNK_FRAME_LENGTH;

    private ChunkStreamUtil() {
    }

    public static byte[] readChunk(InputStream in) throws IOException {
        final int len = ChunkCfgCons.LENGTH_FIELD_LENGTH;
        byte[] lenBytes = new byte[len];
        IOUtils.readFully(in, lenBytes);
        int total = BytesUtil.bytes2Int(lenBytes);
        //检测 可以防止创建大byte数组导致内存溢出
        if (total > MAX_CHUNK_FRAME_LENGTH) {
            throw new CorruptedChunkException("Adjusted frame length (" + total + ") is less than maxFrameLength: " + MAX_CHUNK_FRAME_LENGTH);
        }
        byte[] bytes = new byte[total + len];
        IOUtils.readFully(in, bytes, len, total);
        bytes[0] = lenBytes[0];
        bytes[1] = lenBytes[1];
        bytes[2] = lenBytes[2];//NOSONAR
        bytes[3] = lenBytes[3];//NOSONAR
        return bytes;
    }

    public static ChunkType findChunkType(byte[] chunkBytes) {
        byte type = StreamChunk.findType(chunkBytes);
        return ChunkType.find(type);
    }

}
