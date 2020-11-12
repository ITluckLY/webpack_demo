package com.dcfs.esb.ftp.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mocg on 2016/7/21.
 */
public class FileDownloadRecord implements Serializable {
    /* 客户端IP */
    private String clientIp = null;
    /* 文件服务器IP地址  */
    private String serverIp = null;

    /* 文件服务器的文件名称  */
    private String fileName = null;
    /* 客户端本地的文件名称  */
    private String clientFileName = null;

    /* 文件服务器的名称  */
    private String serverName = null;

    /* 系统名称 */
    private String sysname = null;

    /* 用户ID */
    private String uid = null;
    /* 认证标志  */
    private boolean authFlag = false;

    /* 操作标志  */
    private String fileMsgFlag = null;
    /* 提示信息  */
    private String fileRetMsg = null;

    /* 文件大小  */
    private long fileSize = -1;
    /* 文件分片的序号  */
    private int fileIndex = 0;
    /* 分片的大小 */
    private int pieceNum = 0;

    /* 当前传输内容的大小  */
    private int contLen = 0;
    /* 是否是最后一个分片 */
    private boolean lastPiece = false;

    /* 加密的标志 */
    private boolean scrtFlag = false;

    /* 编码标志 */
    private boolean ebcdicFlag = false;

    /*目标系统名称*/
    private String tarSysName = null;

    /*目标系统文件路径*/
    private String tarFileName = null;

    /*交易码 */
    private String tranCode = null;

    /* 偏移量 */
    private long offset = 0;
    /* 压缩标识 */
    private String compressFlag = null;
    /* 服务端文件重命名控制 */
    private String fileRenameCtrl;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;
    /* 文件是否存在*/
    private boolean fileExists;

    private Date startTime;
    private Date endTime;
    private boolean suss;
    private String nodeName;
    /*文件版本号*/
    private long fileVersion;
    private String errCode;
    private Long nano;
    /*流水号*/
    private String flowNo;
    /*标识一次文件下载（可能包含对几个节点请求）,由生产方客户端生成*/
    private String downloadId;

    //getset

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(boolean authFlag) {
        this.authFlag = authFlag;
    }

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

    public boolean isLastPiece() {
        return lastPiece;
    }

    public void setLastPiece(boolean lastPiece) {
        this.lastPiece = lastPiece;
    }

    public boolean isScrtFlag() {
        return scrtFlag;
    }

    public void setScrtFlag(boolean scrtFlag) {
        this.scrtFlag = scrtFlag;
    }

    public boolean isEbcdicFlag() {
        return ebcdicFlag;
    }

    public void setEbcdicFlag(boolean ebcdicFlag) {
        this.ebcdicFlag = ebcdicFlag;
    }

    public String getTarSysName() {
        return tarSysName;
    }

    public void setTarSysName(String tarSysName) {
        this.tarSysName = tarSysName;
    }

    public String getTarFileName() {
        return tarFileName;
    }

    public void setTarFileName(String tarFileName) {
        this.tarFileName = tarFileName;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
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

    public String getFileRenameCtrl() {
        return fileRenameCtrl;
    }

    public void setFileRenameCtrl(String fileRenameCtrl) {
        this.fileRenameCtrl = fileRenameCtrl;
    }

    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isSuss() {
        return suss;
    }

    public void setSuss(boolean suss) {
        this.suss = suss;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public Long getNano() {
        return nano;
    }

    public void setNano(Long nano) {
        this.nano = nano;
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

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
}
