package com.dcfs.esc.ftp.comm.dto;

import com.dcfs.esc.ftp.comm.buffer.ByteArrayBuf;
import com.dcfs.esc.ftp.comm.chunk.ChunkType;
import com.dcfs.esc.ftp.comm.constant.ChunkInitSizeConst;

/**
 * Created by mocg on 2017/6/2.
 */
public class FileUploadDataReqDto extends BaseBusiDto implements ReqDto {
    public static final ChunkType chunkType = ChunkType.UploadDataReq;

    private String authSeq;
    private long position;
    /* 当前读取内容的大小  */
    private int readLen;
    private String md5;
    /* 当前传输内容的大小  */
    private int contLen;
    /* 文件的内容 */
    private byte[] fileCont;
    /*平台内的绝对文件路径*/
    private String serverAbsFileName;
    /*文件上传同步分发*/
    private boolean distSync;

    public FileUploadDataReqDto() {
    }

    public FileUploadDataReqDto(long position, String authSeq, byte[] fileCont) {
        this.position = position;
        this.authSeq = authSeq;
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
        buf.writeBoolean(distSync);
        buf.writeShortString(md5);
        buf.writeShortString(authSeq);
        buf.writeShortString(serverAbsFileName);
        buf.writeInt(contLen);
        buf.writeByteArr(fileCont);
    }

    @Override
    protected void selfFromBytes(ByteArrayBuf buf) {
        super.selfFromBytes(buf);
        position = buf.readLong();
        readLen = buf.readInt();
        distSync = buf.readBoolean();
        md5 = buf.readShortString();
        authSeq = buf.readShortString();
        serverAbsFileName = buf.readShortString();
        contLen = buf.readInt();
        fileCont = buf.readByteArr();
    }

    @Override
    protected int objBytesLen() {
        return super.objBytesLen() + ChunkInitSizeConst.UPLOAD_DATA_CHUNK_SIZE;
    }

    @Override
    public ChunkType getChunkType() {
        return chunkType;
    }

    public String getAuthSeq() {
        return authSeq;
    }

    public void setAuthSeq(String authSeq) {
        this.authSeq = authSeq;
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

    public byte[] getFileCont() {
        return fileCont;
    }

    public void setFileCont(byte[] fileCont) {
        this.fileCont = fileCont;
    }

    public int getContLen() {
        return contLen;
    }

    public void setContLen(int contLen) {
        this.contLen = contLen;
    }

    public String getServerAbsFileName() {
        return serverAbsFileName;
    }

    public void setServerAbsFileName(String serverAbsFileName) {
        this.serverAbsFileName = serverAbsFileName;
    }

    public boolean isDistSync() {
        return distSync;
    }

    public void setDistSync(boolean distSync) {
        this.distSync = distSync;
    }
}
