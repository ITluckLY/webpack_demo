package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileDownloadDataReqDto extends BaseBusiDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.DownloadDataReq;
    private String authSeq;
    private long position;
    /*平台内的绝对文件路径*/
    private String serverAbsFileName;

    public FileDownloadDataReqDto() {
    }

    public FileDownloadDataReqDto(long position) {
        this.position = position;
    }

    public FileDownloadDataReqDto(String authSeq, long position) {
        this.authSeq = authSeq;
        this.position = position;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeLong(position);
        buf.writeShortString(authSeq);
        buf.writeShortString(serverAbsFileName);
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 50;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        position = buf.readLong();
        authSeq = buf.readShortString();
        serverAbsFileName = buf.readShortString();
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public String getAuthSeq() {
        return authSeq;
    }

    public void setAuthSeq(String authSeq) {
        this.authSeq = authSeq;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getServerAbsFileName() {
        return serverAbsFileName;
    }

    public void setServerAbsFileName(String serverAbsFileName) {
        this.serverAbsFileName = serverAbsFileName;
    }
}
