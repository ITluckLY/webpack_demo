package com.dcfs.esc.ftp.svr.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBizDto;

/**
 * Created by mocg on 2017/6/3.
 */
public class PutFilePathReqDto extends BaseBizDto {
    public static final ChunkType chunkType = ChunkType.PutFilePathReq;
    /*节点名称*/
    private String nodeName;
    /*系统名称*/
    private String systemName;
    /*文件平台内绝对路径*/
    private String filePath;
    /*请求文件路径 外部根据这路径找到相应的文件 一般与filePath相同，也可能不同(分发时文件存在)*/
    /*20170719无论分发与否均与filePath相同，为相对路径*/
    private String requestFilePath;
    /*文件名称*/
    private String fileName;
    /*文件大小（单位：字节）*/
    private Long fileSize;
    /*文件后缀*/
    private String fileExt;
    /*上传开始时间*/
    private String uploadStartTime;
    /*上传结束时间*/
    private String uploadEndTime;
    /*文件MD5*/
    private String fileMd5;
    /*文件版本号*/
    private Long fileVersion;

    public PutFilePathReqDto() {
        //needed
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(nodeName);
        buf.writeShortString(systemName);
        buf.writeString(filePath);
        buf.writeString(requestFilePath);
        buf.writeShortString(fileName);
        buf.writeLong(fileSize);
        buf.writeShortString(fileExt);
        buf.writeShortString(uploadStartTime);
        buf.writeShortString(uploadEndTime);
        buf.writeShortString(fileMd5);
        buf.writeLongObj(fileVersion);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        nodeName = buf.readShortString();
        systemName = buf.readShortString();
        filePath = buf.readString();
        requestFilePath = buf.readString();
        fileName = buf.readShortString();
        fileSize = buf.readLong();
        fileExt = buf.readShortString();
        uploadStartTime = buf.readShortString();
        uploadEndTime = buf.readShortString();
        fileMd5 = buf.readShortString();
        fileVersion = buf.readLongObj();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 1024;
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

    public String getRequestFilePath() {
        return requestFilePath;
    }

    public void setRequestFilePath(String requestFilePath) {
        this.requestFilePath = requestFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getUploadStartTime() {
        return uploadStartTime;
    }

    public void setUploadStartTime(String uploadStartTime) {
        this.uploadStartTime = uploadStartTime;
    }

    public String getUploadEndTime() {
        return uploadEndTime;
    }

    public void setUploadEndTime(String uploadEndTime) {
        this.uploadEndTime = uploadEndTime;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(Long fileVersion) {
        this.fileVersion = fileVersion;
    }
}
