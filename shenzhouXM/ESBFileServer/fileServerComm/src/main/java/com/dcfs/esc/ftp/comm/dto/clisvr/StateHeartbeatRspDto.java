package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;

/**
 * 应用存活状态心跳探测
 * Created by mocg on 2017/7/9.
 */
public class StateHeartbeatRspDto extends BaseDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.StateHeartbeatRsp;

    private boolean succ;
    /**
     * 存活标志
     */
    private boolean isAlive;

    public StateHeartbeatRspDto() {
//        setEnd(true);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);

        buf.writeBoolean(succ);
        buf.writeBoolean(isAlive);



    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        succ = buf.readBoolean();
        isAlive = buf.readBoolean();


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



    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
