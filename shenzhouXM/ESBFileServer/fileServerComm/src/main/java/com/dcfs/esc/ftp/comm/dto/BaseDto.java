package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.BytesEntry;
import com.dcfs.esc.ftp.comm.chunk.ChunkCfgCons;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.constant.SysCfg;
import com.dcfs.esc.ftp.comm.exception.CorruptedChunkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mocg on 2017/6/2.
 */
public abstract class BaseDto {
    private Logger log;
    //ftom Chunk info [lastChunk end] 不用写入bytes中
    private boolean lastChunk;
    private boolean end;
    private long nano;
    /*dto版本,实现向下兼容 1707151334-基础版本*/
    private static final int BASE_DTO_VERSION = 1707151334;
    private long dtoVersion = BASE_DTO_VERSION;

    public BaseDto() {
    }

    private ByteArrayBuf toByteArrayBuf() {
        int objBytesLen = objBytesLen();
        //默认分片大小最大5M，此处检测的objBytesLen只是部分，总的来说还是可以防止创建大byte数组导致内存溢出
        if (objBytesLen > ChunkCfgCons.MAX_CHUNK_FRAME_LENGTH) {
            throw new CorruptedChunkException("Adjusted frame length (" + objBytesLen + ") is less than maxFrameLength: " + ChunkCfgCons.MAX_CHUNK_FRAME_LENGTH);
        }
        byte[] bytes = new byte[objBytesLen];
        ByteArrayBuf buf = ByteArrayBuf.wrap2Write(bytes);
        selfToBytes(buf);
        return buf;
    }

    public final BytesEntry toBytesEntry() {
        return toByteArrayBuf().getBytesEntry();
    }

    public final byte[] toBytes() {
        return toByteArrayBuf().getReadableBytes();
    }

    public final void fromBytes(byte[] bytes) {
        if (bytes != null) {
            ByteArrayBuf buf = ByteArrayBuf.wrap2Read(bytes);
            selfFromBytes(buf);
        }
        if (SysCfg.isPrintDtoJson()) {
            if (log == null) log = LoggerFactory.getLogger(getClass());//耗时操作
            if (log.isTraceEnabled()) {
                log.trace("dtoPrint#nano:{}#className:{},json:{}", nano, this.getClass().getName(), GsonUtil.toJson(this));
            }
        }
    }

    protected void selfToBytes(ByteArrayBuf buf) {
        if (SysCfg.isPrintDtoJson()) {
            if (log == null) log = LoggerFactory.getLogger(getClass());
            if (log.isTraceEnabled()) {
                log.trace("dtoPrint#nano:{}#className:{},json:{}", nano, this.getClass().getName(), GsonUtil.toJson(this));
            }
        }
        buf.writeLong(nano);
        buf.writeLong(dtoVersion);
    }

    protected void selfFromBytes(ByteArrayBuf buf) {
        nano = buf.readLong();
        dtoVersion = buf.readLong();
    }

    protected int objBytesLen() {
        return 16;//NOSONAR
    }

    public abstract ChunkType getChunkType();

    public final boolean isLastChunk() {
        return lastChunk;
    }

    public final void setLastChunk(boolean lastChunk) {
        this.lastChunk = lastChunk;
    }

    public final boolean isEnd() {
        return end;
    }

    public final void setEnd(boolean end) {
        this.end = end;
    }

    public final long getNano() {
        return nano;
    }

    public final void setNano(long nano) {
        this.nano = nano;
    }

    public final long getDtoVersion() {
        return dtoVersion;
    }

    public final void setDtoVersion(long dtoVersion) {
        this.dtoVersion = dtoVersion;
    }
}
