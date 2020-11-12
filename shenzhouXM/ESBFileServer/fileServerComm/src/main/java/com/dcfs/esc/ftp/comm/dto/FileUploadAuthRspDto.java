package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileUploadAuthRspDto extends BaseBusiDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.UploadAuthRsp;
    private boolean auth;
    private String authSeq;
    private String serverNodelistVersion;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;
    private String vsysmap;
    /* 服务器上临时文件名，用于断点续传*/
    private String tmpFileName;
    /* 服务器上的文件的大小*/
    private long remoteFileSize;
    private String targetNodeAddr;

    public FileUploadAuthRspDto() {
    }

    public FileUploadAuthRspDto(boolean auth, String authSeq) {
        this.auth = auth;
        this.authSeq = authSeq;
    }

    public FileUploadAuthRspDto(boolean auth, String authSeq, String errCode, String errMsg) {
        super(errCode, errMsg);
        this.auth = auth;
        this.authSeq = authSeq;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeLong(remoteFileSize);
        buf.writeShortString(authSeq);
        buf.writeShortString(serverNodelistVersion);
        buf.writeShortString(nodeList);
        buf.writeShortString(vsysmap);
        buf.writeShortString(tmpFileName);
        buf.writeShortString(targetNodeAddr);
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 200;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        remoteFileSize = buf.readLong();
        authSeq = buf.readShortString();
        serverNodelistVersion = buf.readShortString();
        nodeList = buf.readShortString();
        vsysmap = buf.readShortString();
        tmpFileName = buf.readShortString();
        targetNodeAddr = buf.readShortString();
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

    public String getTmpFileName() {
        return tmpFileName;
    }

    public void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }

    public long getRemoteFileSize() {
        return remoteFileSize;
    }

    public void setRemoteFileSize(long remoteFileSize) {
        this.remoteFileSize = remoteFileSize;
    }

    public String getTargetNodeAddr() {
        return targetNodeAddr;
    }

    public void setTargetNodeAddr(String targetNodeAddr) {
        this.targetNodeAddr = targetNodeAddr;
    }
}
