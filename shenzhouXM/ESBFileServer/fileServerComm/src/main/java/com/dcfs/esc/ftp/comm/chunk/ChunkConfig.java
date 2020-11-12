package com.dcfs.esc.ftp.comm.chunk;

/**
 * Created by mocg on 2017/6/3.
 */
public class ChunkConfig {
    private boolean lastChunk;//最后一片，不一定结束
    private boolean end; //结束 不一定成功
    /* 压缩标识 */
    private boolean compress;
    /* 加密的标志 */
    private boolean scrt;
    private ChunkContentFormat chunkContentFormat;  //Chunk中data的内容格式
    /*是否有authSeq*/
    private boolean hasAuthSeq;

    public ChunkConfig() {
    }

    public ChunkConfig(boolean lastChunk, boolean end) {
        this.lastChunk = lastChunk;
        this.end = end;
    }

    public ChunkConfig(boolean lastChunk, boolean end, boolean compress, boolean scrt) {
        this.lastChunk = lastChunk;
        this.end = end;
        this.compress = compress;
        this.scrt = scrt;
    }

    public boolean isLastChunk() {
        return lastChunk;
    }

    public void setLastChunk(boolean lastChunk) {
        this.lastChunk = lastChunk;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isScrt() {
        return scrt;
    }

    public void setScrt(boolean scrt) {
        this.scrt = scrt;
    }

    public ChunkContentFormat getChunkContentFormat() {
        return chunkContentFormat;
    }

    public void setChunkContentFormat(ChunkContentFormat chunkContentFormat) {
        this.chunkContentFormat = chunkContentFormat;
    }

    public boolean isHasAuthSeq() {
        return hasAuthSeq;
    }

    public void setHasAuthSeq(boolean hasAuthSeq) {
        this.hasAuthSeq = hasAuthSeq;
    }
}
