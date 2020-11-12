package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.util.ByteUtil;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileUploadDataRspDto extends BaseBusiDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.UploadDataRsp;

    private long position;
    private boolean succ;
    //最后一片成功后才有值返回
    private String filePath;
    /*文件分发结果 1-开启分发并正在分发 -1-完成分发并失败 2-完成分发并成功 0-初始未开启*/
    private int distributeResult;
    /*文件路由结果 0-不确定 1-成功 -1-失败 -2-出错有异常*/
    private int fileRouteResult;
    /*文件灾备结果 0-不确定 1-成功 -1-失败 -2-出错有异常*/
    private int fileBackupResult;

    public FileUploadDataRspDto() {
    }

    public FileUploadDataRspDto(long position) {
        this.position = position;
    }

    public FileUploadDataRspDto(String errCode, String errMsg, long position) {
        super(errCode, errMsg);
        this.position = position;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeLong(position);
        byte flag = 0;
        flag = ByteUtil.setFlag(flag, 0, succ);
        buf.writeByte(flag);
        buf.writeByte(distributeResult);
        buf.writeByte(fileRouteResult);
        buf.writeByte(fileBackupResult);
        buf.writeShortString(filePath);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        position = buf.readLong();
        byte flag = buf.readByte();
        succ = ByteUtil.getFlag(flag, 0);
        distributeResult = buf.readByte();
        fileRouteResult = buf.readByte();
        fileBackupResult = buf.readByte();
        filePath = buf.readShortString();
    }

    @Override
    protected int objBytesLen() {
        final int bytesLen = 120;
        return super.objBytesLen() + bytesLen;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDistributeResult() {
        return distributeResult;
    }

    public void setDistributeResult(int distributeResult) {
        this.distributeResult = distributeResult;
    }

    public int getFileRouteResult() {
        return fileRouteResult;
    }

    public void setFileRouteResult(int fileRouteResult) {
        this.fileRouteResult = fileRouteResult;
    }

    public int getFileBackupResult() {
        return fileBackupResult;
    }

    public void setFileBackupResult(int fileBackupResult) {
        this.fileBackupResult = fileBackupResult;
    }
}
