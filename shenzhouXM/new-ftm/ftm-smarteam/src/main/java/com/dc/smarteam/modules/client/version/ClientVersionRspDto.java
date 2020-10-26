package com.dc.smarteam.modules.client.version;


import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;


public class ClientVersionRspDto extends BaseDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.ClientVersionRsp;
    private boolean auth;
    private boolean succ;
    private String jsonMsg;


    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeBoolean(succ);
        buf.writeShortString(jsonMsg);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        succ = buf.readBoolean();
        jsonMsg = buf.readShortString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 1;
        return super.objBytesLen() + bytesLen;
    }
    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }


    //getter setter


    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public String getJsonMsg() {
        return jsonMsg;
    }
    public void setJsonMsg(String jsonMsg) {
        this.jsonMsg = jsonMsg;
    }

}
