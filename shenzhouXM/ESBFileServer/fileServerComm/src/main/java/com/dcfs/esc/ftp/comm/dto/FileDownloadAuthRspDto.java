package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.util.ByteUtil;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileDownloadAuthRspDto extends BaseBusiDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.DownloadAuthRsp;
    private boolean auth;
    private String authSeq;
    private long fileSize;
    /* 文件是否存在 当前节点内*/
    private boolean fileExists;
    /* 文件是否存在 系统内 默认存在*/
    private boolean fileExistsInSys = true;
    private long fileVersion;
    private String serverNodelistVersion;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;
    /* 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接*/
    private String vsysmap;
    private String targetNodeAddr;
    /*流水号 dtoversion is 1708181533*/
    private String flowNo;

    private static final int DTO_VERSION_1708181533 = 1708181533;

    public FileDownloadAuthRspDto() {
        setDtoVersion(DTO_VERSION_1708181533);
    }

    public FileDownloadAuthRspDto(boolean auth, String authSeq, long fileSize) {
        this.auth = auth;
        this.authSeq = authSeq;
        this.fileSize = fileSize;
    }

    public FileDownloadAuthRspDto(boolean auth, String authSeq, long fileSize, String errCode, String errMsg) {
        super(errCode, errMsg);
        this.auth = auth;
        this.authSeq = authSeq;
        this.fileSize = fileSize;
    }


    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        byte flag = 0;
        flag = ByteUtil.setFlag(flag, 0, auth);
        flag = ByteUtil.setFlag(flag, 1, fileExists);
        flag = ByteUtil.setFlag(flag, 2, fileExistsInSys);
        buf.writeByte(flag);
        buf.writeLong(fileSize);
        buf.writeLong(fileVersion);
        buf.writeShortString(authSeq);
        buf.writeShortString(serverNodelistVersion);
        buf.writeShortString(nodeList);
        buf.writeShortString(vsysmap);
        buf.writeShortString(targetNodeAddr);

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            buf.writeShortString(flowNo);
        }
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        byte flag = buf.readByte();
        auth = ByteUtil.getFlag(flag, 0);
        fileExists = ByteUtil.getFlag(flag, 1);
        fileExistsInSys = ByteUtil.getFlag(flag, 2);
        fileSize = buf.readLong();
        fileVersion = buf.readLong();
        authSeq = buf.readShortString();
        serverNodelistVersion = buf.readShortString();
        nodeList = buf.readShortString();
        vsysmap = buf.readShortString();
        targetNodeAddr = buf.readShortString();

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            flowNo = buf.readShortString();
        }
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 50;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getAuthSeq() {
        return authSeq;
    }

    public void setAuthSeq(String authSeq) {
        this.authSeq = authSeq;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public boolean isFileExistsInSys() {
        return fileExistsInSys;
    }

    public void setFileExistsInSys(boolean fileExistsInSys) {
        this.fileExistsInSys = fileExistsInSys;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getServerNodelistVersion() {
        return serverNodelistVersion;
    }

    public void setServerNodelistVersion(String serverNodelistVersion) {
        this.serverNodelistVersion = serverNodelistVersion;
    }

    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public String getVsysmap() {
        return vsysmap;
    }

    public void setVsysmap(String vsysmap) {
        this.vsysmap = vsysmap;
    }

    public String getTargetNodeAddr() {
        return targetNodeAddr;
    }

    public void setTargetNodeAddr(String targetNodeAddr) {
        this.targetNodeAddr = targetNodeAddr;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
