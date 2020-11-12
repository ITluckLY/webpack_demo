package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileUploadAuthReqDto extends BaseBusiDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.UploadAuthReq;
    /* 用户ID */
    private String uid;
    /* 用户 密码 */
    private String passwd;
    /* 系统名称 */
    private String sysname;
    /* 文件服务器的文件名称  */
    private String fileName;
    /* 客户端本地的文件名称  */
    private String clientFileName;
    private long fileSize;
    /* 服务端文件重命名 */
    private boolean fileRename;
    /*交易码 */
    private String tranCode;
    private String clientNodelistVersion;
    /*系统映射kv 虚拟系统-真实系统*/
    private String vsysmap;
    /*目标系统名称*/
    private String targetSysname;
    /*目标系统文件路径*/
    private String targetFileName;
    /* 上次传输的服务器上的文件名,用于断点传输*/
    private String lastRemoteFileName;

    private static final int DTO_VERSION_1708181533 = 1708181533;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;
    private static final int DTO_VERSION_1709181037 = 1709181037;
    /*标识一次文件上传（可能包含对几个节点请求）,由生产方客户端生成 dtoversion is 1709181037*/
    private String uploadId;

    public FileUploadAuthReqDto() {
        setDtoVersion(DTO_VERSION_1709181037);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeLong(fileSize);
        buf.writeBoolean(fileRename);
        buf.writeShortString(uid);
        buf.writeShortString(passwd);
        buf.writeShortString(sysname);
        buf.writeShortString(fileName);
        buf.writeShortString(clientFileName);
        buf.writeShortString(tranCode);
        buf.writeShortString(vsysmap);
        buf.writeShortString(clientNodelistVersion);
        buf.writeShortString(lastRemoteFileName);
        buf.writeShortString(targetSysname);
        buf.writeShortString(targetFileName);

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            buf.writeShortString(flowNo);
        }
        if (dtoVersion >= DTO_VERSION_1709181037) {
            buf.writeShortString(uploadId);
        }
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 450;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        fileSize = buf.readLong();
        fileRename = buf.readBoolean();
        uid = buf.readShortString();
        passwd = buf.readShortString();
        sysname = buf.readShortString();
        fileName = buf.readShortString();
        clientFileName = buf.readShortString();
        tranCode = buf.readShortString();
        vsysmap = buf.readShortString();
        clientNodelistVersion = buf.readShortString();
        lastRemoteFileName = buf.readShortString();
        targetSysname = buf.readShortString();
        targetFileName = buf.readShortString();

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            flowNo = buf.readShortString();
        }
        if (dtoVersion >= DTO_VERSION_1709181037) {
            uploadId = buf.readShortString();
        }
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isFileRename() {
        return fileRename;
    }

    public void setFileRename(boolean fileRename) {
        this.fileRename = fileRename;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getClientNodelistVersion() {
        return clientNodelistVersion;
    }

    public void setClientNodelistVersion(String clientNodelistVersion) {
        this.clientNodelistVersion = clientNodelistVersion;
    }

    public String getLastRemoteFileName() {
        return lastRemoteFileName;
    }

    public void setLastRemoteFileName(String lastRemoteFileName) {
        this.lastRemoteFileName = lastRemoteFileName;
    }

    public String getVsysmap() {
        return vsysmap;
    }

    public void setVsysmap(String vsysmap) {
        this.vsysmap = vsysmap;
    }

    public String getTargetSysname() {
        return targetSysname;
    }

    public void setTargetSysname(String targetSysname) {
        this.targetSysname = targetSysname;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
