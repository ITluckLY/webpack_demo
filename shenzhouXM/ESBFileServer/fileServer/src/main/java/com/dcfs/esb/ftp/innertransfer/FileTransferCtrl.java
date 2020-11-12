package com.dcfs.esb.ftp.innertransfer;

import java.io.Serializable;

/**
 * Created by mocg on 2016/7/25.
 */
public class FileTransferCtrl implements Serializable {

    /* 操作标志  */
    private String fileMsgFlag = null;
    /* 提示信息  */
    private String fileRetMsg = null;

    /* 文件大小  */
    private long fileSize = 0;
    /* 文件分片的序号  */
    private int fileIndex = 0;
    /* 分片的大小 */
    private int pieceNum = 0;

    /* 当前传输内容的大小  */
    private int contLen = 0;
    /* 文件的内容 */
    private byte[] fileCont = null;
    /* 是否是最后一个分片 */
    private boolean lastPiece = false;

    /* MD5的校验码 */
    private String md5 = null;
    private String sessionMD5 = null;
    /* 加密的标志 */
    private boolean scrtFlag = false;
    /* 加密的密钥 */
    private byte[] desKey = null;

    /* 编码标志 */
    private boolean ebcdicFlag = false;

    /* 偏移量 */
    private long offset = 0;
    /* 压缩标识 */
    private String compressFlag = null;


    public String getFileMsgFlag() {
        return fileMsgFlag;
    }

    public void setFileMsgFlag(String fileMsgFlag) {
        this.fileMsgFlag = fileMsgFlag;
    }

    public String getFileRetMsg() {
        return fileRetMsg;
    }

    public void setFileRetMsg(String fileRetMsg) {
        this.fileRetMsg = fileRetMsg;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
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

    public boolean isLastPiece() {
        return lastPiece;
    }

    public void setLastPiece(boolean lastPiece) {
        this.lastPiece = lastPiece;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSessionMD5() {
        return sessionMD5;
    }

    public void setSessionMD5(String sessionMD5) {
        this.sessionMD5 = sessionMD5;
    }

    public boolean isScrtFlag() {
        return scrtFlag;
    }

    public void setScrtFlag(boolean scrtFlag) {
        this.scrtFlag = scrtFlag;
    }

    public byte[] getDesKey() {
        return desKey;
    }

    public void setDesKey(byte[] desKey) {
        this.desKey = desKey;
    }

    public boolean isEbcdicFlag() {
        return ebcdicFlag;
    }

    public void setEbcdicFlag(boolean ebcdicFlag) {
        this.ebcdicFlag = ebcdicFlag;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getCompressFlag() {
        return compressFlag;
    }

    public void setCompressFlag(String compressFlag) {
        this.compressFlag = compressFlag;
    }
}
