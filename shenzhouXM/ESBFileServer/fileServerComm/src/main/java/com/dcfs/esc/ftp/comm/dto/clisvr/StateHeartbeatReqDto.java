package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;

/**
 * 应用存活状态心跳探测
 * Created by mocg on 2017/7/9.
 */
public class StateHeartbeatReqDto extends BaseDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.StateHeartbeatReq;
    /**     * 心跳自动注册    dtoversion is 1808151800 */
    private static final int DTO_VERSION_1808151800 = 1808151800;

    /* 用户ID */
    private String uid;
    /* 用户密码*/
    private String password;
    /* 系统名称 */
    private String sysname;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;
    private String clientVersion;

    /*配置文件FtpClisvrConfig中客户端名称*/
    private String confName;
    /*配置文件FtpClisvrConfig中客户端IP*/
    private String confIp;
    /*配置文件FtpClisvrConfig中客户端Port*/
    private int confPort;
    /*配置文件FtpClisvrConfig中返回标志*/
    private boolean confFlag;
    /*存活标志*/
    private boolean isAlive;

    private String status;//客户端状态：注册，启动 ，停止
    private int cmdPort; //客户端监听端口
    private String userDomainName;//用户域名


    public  StateHeartbeatReqDto() {
        setDtoVersion(DTO_VERSION_1808151800);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(sysname);
        buf.writeShortString(clientVersion);
        buf.writeShortString(confName);
        buf.writeShortString(confIp);
        buf.writeInt(confPort);
        buf.writeBoolean(confFlag);
        buf.writeBoolean(isAlive);
        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1808151800) {
            buf.writeShortString(uid);
            buf.writeShortString(password);
            buf.writeInt(cmdPort);
            buf.writeShortString(userDomainName);



        }
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        sysname = buf.readShortString();
        clientVersion = buf.readShortString();
        confName = buf.readShortString();
        confIp = buf.readShortString();
        confPort = buf.readInt();
        confFlag = buf.readBoolean();
        isAlive= buf.readBoolean();
        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1808151800) {
            uid = buf.readShortString();
            password = buf.readShortString();
            cmdPort = buf.readInt();
            userDomainName = buf.readShortString();

        }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public String getConfIp() {
        return confIp;
    }

    public void setConfIp(String confIp) {
        this.confIp = confIp;
    }

    public int getConfPort() {
        return confPort;
    }

    public void setConfPort(int confPort) {
        this.confPort = confPort;
    }

    public boolean isConfFlag() {
        return confFlag;
    }

    public void setConfFlag(boolean confFlag) {
        this.confFlag = confFlag;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCmdPort() {
        return cmdPort;
    }

    public void setCmdPort(int cmdPort) {
        this.cmdPort = cmdPort;
    }

    public String getUserDomainName() {
        return userDomainName;
    }

    public void setUserDomainName(String userDomainName) {
        this.userDomainName = userDomainName;
    }
}
