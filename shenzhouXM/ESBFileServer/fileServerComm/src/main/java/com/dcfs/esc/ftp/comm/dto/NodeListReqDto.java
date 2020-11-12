package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;

/**
 * Created by mocg on 2017/6/2.
 */
public class NodeListReqDto extends BaseBusiDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.NodeListReq;
    /* 用户ID */
    private String uid;
    /* 用户 密码 */
    private String passwd;
    /* 系统名称 */
    private String sysname;
    private String clientNodelistVersion;
    /*true:表示客户端启动时的请求*/
    private boolean byClientStart;
    private String userDescribe;//用户描述

    private static final int DTO_VERSION_1709060944 = 1709060944;
    /*客户端版本号 dtoversion is 1709060944*/
    private String clientVersion;
    /**     * 心跳自动注册    dtoversion is 1808151800 */
    private static final int DTO_VERSION_1808151800 = 1808151800;

    public NodeListReqDto() {
//        setDtoVersion(DTO_VERSION_1709060944);
        setDtoVersion(DTO_VERSION_1808151800);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(byClientStart);
        buf.writeShortString(uid);
        buf.writeShortString(passwd);
        buf.writeShortString(sysname);
        buf.writeShortString(clientNodelistVersion);

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1709060944) {
            buf.writeShortString(clientVersion);
        }
        if (dtoVersion >= DTO_VERSION_1808151800) {
            buf.writeShortString(userDescribe);
        }
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 200;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        byClientStart = buf.readBoolean();
        uid = buf.readShortString();
        passwd = buf.readShortString();
        sysname = buf.readShortString();
        clientNodelistVersion = buf.readShortString();

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1709060944) {
            clientVersion = buf.readShortString();
        }
        if (dtoVersion >= DTO_VERSION_1808151800) {
            userDescribe = buf.readShortString();
        }
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

    public String getClientNodelistVersion() {
        return clientNodelistVersion;
    }

    public void setClientNodelistVersion(String clientNodelistVersion) {
        this.clientNodelistVersion = clientNodelistVersion;
    }

    public boolean isByClientStart() {
        return byClientStart;
    }

    public void setByClientStart(boolean byClientStart) {
        this.byClientStart = byClientStart;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getUserDescribe() {
        return userDescribe;
    }

    public void setUserDescribe(String userDescribe) {
        this.userDescribe = userDescribe;
    }
}
