package com.dc.smarteam.modules.client.file;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;

public class FileSearchReqDto extends BaseDto implements ReqDto {

    public static final ChunkType chunkType = ChunkType.FileSearchReq;
    private   String operateType;
    /* 系统名称 */
    private String sysname;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;
    /* 查询文件路径--相对路径*/
    private String filePath;

    /**分页查询开始索引*/
    private String startIndex;
    /**分页查询结束索引*/
    private String endIndex;

    public  FileSearchReqDto() {

    }
    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(sysname);
        buf.writeShortString(flowNo);
        buf.writeShortString(filePath);
        buf.writeShortString(startIndex);
        buf.writeShortString(endIndex);
        buf.writeShortString(operateType);

    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        sysname = buf.readShortString();
        flowNo = buf.readShortString();
        filePath = buf.readShortString();
        startIndex = buf.readShortString();
        endIndex = buf.readShortString();
        operateType = buf.readShortString();

    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 100;
        return super.objBytesLen() + bytesLen;
    }
    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public String getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(String endIndex) {
        this.endIndex = endIndex;
    }
}
