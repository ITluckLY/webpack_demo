package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;


public class BizFileUploadLog extends DataEntity<BizFileUploadLog> {

    /* 客户端IP */
    private String clientIp;
    /* 文件服务器IP地址  */
    private String serverIp;

    /* 文件服务器的文件名称  */
    private String fileName;
    /* 客户端本地的文件名称  */
    private String clientFileName;

    /* 文件服务器的名称  */
    private String serverName;

    /* 系统名称 */
    private String sysname;
    /*用户名称*/
    private String uname;
    /* 认证标志  */
    private boolean authFlag;

    /* 操作标志  */
    private String fileMsgFlag;
    /* 提示信息  */
    private String fileRetMsg;

    /* 文件大小  */
    private long fileSize;
    /* 文件分片的序号  */
    private int fileIndex;
    /* 分片的大小 */
    private int pieceNum;

    /* 当前传输内容的大小  */
    private int contLen;
    /* 是否是最后一个分片 */
    private boolean lastPiece;

    /* 加密的标志 */
    private boolean scrtFlag;

    /* 编码标志 */
    private boolean ebcdicFlag;

    /*目标系统名称*/
    private String tarSysName;

    /*目标系统文件路径*/
    private String tarFileName;

    /*交易码 */
    private String tranCode;

    /* 偏移量 */
    private long offset;
    /* 压缩标识 */
    private String compressFlag;
    /* 服务端文件重命名控制 */
    private String fileRenameCtrl;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;
    /* 文件是否存在*/
    private boolean fileExists;

    private String nodeNameTemp;
    private Date startTime;
    private Date endTime;
    /* 本流程是否执行成功 */
    private boolean suss;

    private Date createdTime;

    private Date modifiedTime;

    private long nano;

    private String errCode;

    private long uploadFailCount;

    private String flowNo;

    private String oriFileName;
    /* 文件上传成功标识，由lastPiece和suss组成 */
    private String uploadSuss;

    private String uploadErrMsg;
    private Date beginDate;
    private Date endDate;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOriFileName() {
        return oriFileName;
    }

    public void setOriFileName(String oriFileName) {
        this.oriFileName = oriFileName;
    }

    public String getFlowNo() {return flowNo;}

    public void setFlowNo(String flowNo) {this.flowNo = flowNo;}

    public long getUploadFailCount() {
        return uploadFailCount;
    }

    public void setUploadFailCount(long uploadFailCount) {
        this.uploadFailCount = uploadFailCount;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

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

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
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

    public String getNodeNameTemp() {
        return nodeNameTemp;
    }

    public void setNodeNameTemp(String nodeNameTemp) {
        this.nodeNameTemp = nodeNameTemp;
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getUploadSuss() {
        return uploadSuss;
    }

    public void setUploadSuss(String uploadSuss) {
        this.uploadSuss = uploadSuss;
    }

    public String getUploadErrMsg() {
        return uploadErrMsg;
    }

    public void setUploadErrMsg(String uploadErrMsg) {
        this.uploadErrMsg = uploadErrMsg;
    }



}
