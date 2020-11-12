package com.dcfs.esc.ftp.svr.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBizDto;

/**
 * Created by mocg on 2017/6/3.
 */
public class GetFilePathReqDto extends BaseBizDto {
    public static final ChunkType chunkType = ChunkType.GetFilePathReq;
    /*系统名称*/
    private String systemName;
    /*文件平台内相对路径*/
    private String filePath;

    public GetFilePathReqDto() {
        //needed
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(systemName);
        buf.writeString(filePath);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        systemName = buf.readShortString();
        filePath = buf.readString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 150;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
