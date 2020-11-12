package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.constant.ChunkInitSizeConst;
import com.dcfs.esc.ftp.comm.util.ByteUtil;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileDownloadDataRspDto extends BaseBusiDto implements RspDto {
    public static final ChunkType chunkType = ChunkType.DownloadDataRsp;

    private long position;
    /* 当前读取内容的大小  */
    private int readLen;
    private String md5;
    private boolean succ;
    /* 当前传输内容的大小  */
    private int contLen;
    /* 文件的内容 */
    private byte[] fileCont;

    public FileDownloadDataRspDto() {
    }

    public FileDownloadDataRspDto(long position, String md5, byte[] fileCont) {
        this.position = position;
        this.md5 = md5;
        this.fileCont = fileCont;
        if (fileCont != null) contLen = fileCont.length;
    }

    public void fileCont(byte[] fileCont) {
        this.fileCont = fileCont;
        contLen = fileCont == null ? 0 : fileCont.length;
    }

    @Override
    protected void selfToBytes(ByteArrayBuf buf) {
        super.selfToBytes(buf);
        buf.writeLong(position);
        buf.writeInt(readLen);
        byte flag = 0;
        flag = ByteUtil.setFlag(flag, 0, succ);
        buf.writeByte(flag);
        buf.writeShortString(md5);
        buf.writeInt(contLen);
        buf.writeByteArr(fileCont);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        position = buf.readLong();
        readLen = buf.readInt();
        byte flag = buf.readByte();
        succ = ByteUtil.getFlag(flag, 0);
        md5 = buf.readShortString();
        contLen = buf.readInt();
        fileCont = buf.readByteArr();
    }

    @Override
    protected int objBytesLen() {
        return super.objBytesLen() + ChunkInitSizeConst.DOWNLOAD_DATA_CHUNK_SIZE;
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

    public int getReadLen() {
        return readLen;
    }

    public void setReadLen(int readLen) {
        this.readLen = readLen;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public int getContLen() {
        return contLen;
    }

    public void setContLen(int contLen) {
        this.contLen = contLen;
    }

    public byte[] getFileCont() {
        return fileCont;
    }

    public void setFileCont(byte[] fileCont) {
        this.fileCont = fileCont;
    }
}
