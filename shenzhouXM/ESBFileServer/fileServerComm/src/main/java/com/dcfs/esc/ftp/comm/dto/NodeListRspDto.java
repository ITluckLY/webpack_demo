package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/2.
 */
public class NodeListRspDto extends BaseBusiDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.NodeListRsp;
    private boolean auth;
    private String authSeq;
    private String serverNodelistVersion;
    private String nodesData;
    private String vsysmap;

    public NodeListRspDto() {
        //needed
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeShortString(authSeq);
        buf.writeShortString(serverNodelistVersion);
        buf.writeString(vsysmap);
        buf.writeString(nodesData);
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 1500;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        authSeq = buf.readShortString();
        serverNodelistVersion = buf.readShortString();
        vsysmap = buf.readString();
        nodesData = buf.readString();
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

    public String getNodesData() {
        return nodesData;
    }

    public void setNodesData(String nodesData) {
        this.nodesData = nodesData;
    }

    public String getServerNodelistVersion() {
        return serverNodelistVersion;
    }

    public void setServerNodelistVersion(String serverNodelistVersion) {
        this.serverNodelistVersion = serverNodelistVersion;
    }

    public String getVsysmap() {
        return vsysmap;
    }

    public void setVsysmap(String vsysmap) {
        this.vsysmap = vsysmap;
    }
}
