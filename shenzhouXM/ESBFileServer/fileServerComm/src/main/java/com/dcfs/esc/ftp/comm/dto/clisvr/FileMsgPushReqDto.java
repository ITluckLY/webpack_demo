package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.ReqDto;

/**
 * Created by mocg on 2017/6/19.
 */
public class FileMsgPushReqDto extends BaseBusiDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.FileMsgPushReq;
    /*接收消息的用户UID,用于校验*/
    private String toUid;
    private String tranCode;
    private String sysname;
    //服务器上的文件路径(平台内绝对)
    private String serverFileName;
    //客户端上的文件路径（以配置路径为根路径）
    private String clientFileName;
    private boolean sync;
    private long fileVersion;
    private long prenano;

    private static final int DTO_VERSION_1708181533 = 1708181533;
    /*流水号,由生产方客户端生成 dtoversion is 1708181533*/
    private String flowNo;
    private static final int DTO_VERSION_1708241537 = 1708241537;
    /*消息标识*/
    private long msgId;

    public FileMsgPushReqDto() {
        setDtoVersion(DTO_VERSION_1708241537);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(sync);
        buf.writeLong(fileVersion);
        buf.writeLong(prenano);
        buf.writeShortString(toUid);
        buf.writeShortString(tranCode);
        buf.writeShortString(sysname);
        buf.writeShortString(serverFileName);
        buf.writeShortString(clientFileName);

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            buf.writeShortString(flowNo);
        }
        if (dtoVersion >= DTO_VERSION_1708241537) {
            buf.writeLong(msgId);
        }
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        sync = buf.readBoolean();
        fileVersion = buf.readLong();
        prenano = buf.readLong();
        toUid = buf.readShortString();
        tranCode = buf.readShortString();
        sysname = buf.readShortString();
        serverFileName = buf.readShortString();
        clientFileName = buf.readShortString();

        long dtoVersion = getDtoVersion();
        if (dtoVersion >= DTO_VERSION_1708181533) {
            flowNo = buf.readShortString();
        }
        if (dtoVersion >= DTO_VERSION_1708241537) {
            msgId = buf.readLong();
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

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public long getPrenano() {
        return prenano;
    }

    public void setPrenano(long prenano) {
        this.prenano = prenano;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
}
