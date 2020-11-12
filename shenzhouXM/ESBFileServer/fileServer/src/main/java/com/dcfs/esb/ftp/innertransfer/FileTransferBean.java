package com.dcfs.esb.ftp.innertransfer;

import java.io.Serializable;

/**
 * Created by mocg on 2016/7/25.
 */
public class FileTransferBean implements Serializable {
    /*任务类型*/
    private String taskType;
    /* 文件服务器IP地址  */
    private String serverIp;
    /* 文件服务器的名称  */
    private String serverName;

    /* 文件服务器的文件名称  */
    private String fileName;
    /* 客户端本地的文件名称  */
    private String clientFileName;

    /* 用户ID */
    private String uid;
    /* 用户 密码 */
    private String passwd;
    /* 认证标志  */
    private boolean authFlag = false;

    /* 操作标志  */
    private String fileMsgFlag;
    /* 提示信息  */
    private String fileRetMsg;

    /* 文件大小  */
    private long fileSize = 0;
    /* 分片的大小 */
    private static final int DEF_PIECE_NUM = 8192;
    private int pieceNum = DEF_PIECE_NUM;

    /* MD5的校验码 */
    private String md5;
    private String sessionMD5;
    /* 加密的标志 */
    private boolean scrtFlag = false;
    /* 加密的密钥 */
    private byte[] desKey;
    /* 编码标志 */
    private boolean ebcdicFlag = false;
    /*目标系统名称*/
    private String tarSysName;
    /*目标系统文件路径*/
    private String tarFileName;
    /*交易码 */
    private String tranCode;
    /* 压缩标识 */
    private String compressFlag;
    /* 服务端文件重命名控制 */
    private String fileRenameCtrl;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
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

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
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
}
