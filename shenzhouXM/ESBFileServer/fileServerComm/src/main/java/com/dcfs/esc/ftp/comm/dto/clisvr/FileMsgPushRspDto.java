package com.dcfs.esc.ftp.comm.dto.clisvr;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.dto.BaseBusiDto;
import com.dcfs.esc.ftp.comm.dto.RspDto;

/**
 * Created by mocg on 2017/6/19.
 */
public class FileMsgPushRspDto extends BaseBusiDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.FileMsgPushRsp;
    private boolean auth;
    private boolean succ;
    private boolean sync;

    public FileMsgPushRspDto() {
        setEnd(true);
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeBoolean(auth);
        buf.writeBoolean(succ);
        buf.writeBoolean(sync);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        auth = buf.readBoolean();
        succ = buf.readBoolean();
        sync = buf.readBoolean();
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

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }
}
