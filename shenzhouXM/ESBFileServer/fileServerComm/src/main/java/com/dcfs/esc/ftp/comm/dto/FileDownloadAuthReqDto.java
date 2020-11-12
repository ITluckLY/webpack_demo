package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileDownloadAuthReqDto extends BaseBusiDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.DownloadAuthReq;
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
    /*交易码 */
    private String tranCode;
    private String clientNodelistVersion;
    /*挂载的节点名称*/
    private String mountNodeName;
    /*系统映射kv 虚拟系统-真实系统*/
    private String vsysmap;
    /*目标系统名称*/
    private String targetSysname;
    /*目标系统文件路径*/
    private String targetFileName;

    private static final int DTO_VERSION_1708181533 = 1708181533;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;
    private static final int DTO_VERSION_1709181037 = 1709181037;
    /*标识一次文件下载（可能包含对几个节点请求）,由生产方客户端生成 dtoversion is 1709181037*/
    private String downloadId;

    public FileDownloadAuthReqDto() {
        setDtoVersion(DTO_VERSION_1709181037);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(uid);
        buf.writeShortString(passwd);
        buf.writeShortString(sysname);
        buf.writeShortString(fileName);
        buf.writeShortString(clientFileName);
        buf.writeShortString(tranCode);
        buf.writeShortString(clientNodelistVersion);
        buf.writeShortString(mountNodeName);
        buf.writeShortString(vsysmap);
        buf.writeShortString(targetSysname);
        buf.writeShortString(targetFileName);

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            buf.writeShortString(flowNo);
        }
        if (dtoVersion >= DTO_VERSION_1709181037) {
            buf.writeShortString(downloadId);
        }
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        uid = buf.readShortString();
        passwd = buf.readShortString();
        sysname = buf.readShortString();
        fileName = buf.readShortString();
        clientFileName = buf.readShortString();
        tranCode = buf.readShortString();
        clientNodelistVersion = buf.readShortString();
        mountNodeName = buf.readShortString();
        vsysmap = buf.readShortString();
        targetSysname = buf.readShortString();
        targetFileName = buf.readShortString();

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            flowNo = buf.readShortString();
        }
        if (dtoVersion >= DTO_VERSION_1709181037) {
            downloadId = buf.readShortString();
        }
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 400;
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

    public String getMountNodeName() {
        return mountNodeName;
    }

    public void setMountNodeName(String mountNodeName) {
        this.mountNodeName = mountNodeName;
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

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
}
