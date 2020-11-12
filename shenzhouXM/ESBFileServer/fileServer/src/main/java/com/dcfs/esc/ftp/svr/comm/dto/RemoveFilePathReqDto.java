package com.dcfs.esc.ftp.svr.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBizDto;

/**
 * Created by mocg on 2017/6/3.
 */
public class RemoveFilePathReqDto extends BaseBizDto {
    public static final ChunkType chunkType = ChunkType.RemoveFilePathReq;
    /*节点名称*/
    private String nodeName;
    /*系统名称*/
    private String systemName;
    /*文件平台内相对路径*/
    private String filePath;

    public RemoveFilePathReqDto() {
        //needed
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(nodeName);
        buf.writeShortString(systemName);
        buf.writeString(filePath);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        nodeName = buf.readShortString();
        systemName = buf.readShortString();
        filePath = buf.readString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 300;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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
