package com.dc.smarteam.modules.client.version;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;

public class ClientVersionReqDto extends BaseDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.ClientVersionReq;
    /* 用户ID */
    private String uid;
    /* 用户 密码 */
    private String passwd;
    /* 系统名称 */
    private String sysname;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;

    public  ClientVersionReqDto() {

    }
    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeShortString(sysname);

    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        sysname = buf.readShortString();


        long dtoVersion = getDtoVersion();

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

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
