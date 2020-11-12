package com.dcfs.esb.ftp.common.msg;

import java.util.Arrays;

/**
 * 文件系统消息类，服务器和客户端都是用该类进行头的交互
 */
public class FileMsgBean {
    /* 系统名称 */
    private String sysname;
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

    /* 用户ID */
    private String uid;
    /* 用户 密码 */
    private String passwd;
    /* 认证标志  */
    private Boolean authFlag;

    /* 操作标志  */
    private String fileMsgFlag;
    /* 提示信息  */
    private String fileRetMsg;
    private String errCode;
    private Long nano;

    private String userDescribe;//用户描述
    /* 文件大小  */
    private long fileSize = -1;
    /* 文件分片的序号  */
    private int fileIndex = 0;
    /* 分片的大小 */
    private int pieceNum = 0;
    /* 偏移量 */
    private long offset = 0;

    /* 当前传输内容的大小  */
    private int contLen = 0;
    /* 文件的内容 */
    private byte[] fileCont;
    /* 是否是最后一个分片 */
    private Boolean lastPiece;

    /* MD5的校验码 */
    private String md5;
    private String sessionMD5;
    /* 加密的标志 */
    private Boolean scrtFlag;
    /* 加密的密钥 */
    private byte[] desKey;
    private String desKeyStr;//供传输用

    /* 编码标志 */
    private Boolean ebcdicFlag;

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
    /* 文件目标节点 ip:port  返回-1:表示没有可用节点，null、0:表示按遍历方式 1:表示继续使用本连接*/
    private String targetNodeAddr;
    /* 数据节点列表，格式：nodeName#ip:port,... eg:FS01#127.0.0.1:5001,FS02#127.0.0.2:5001 */
    private String nodeList;
    /* 文件是否存在*/
    private Boolean fileExists;
    /* 上次传输的服务器上的文件名,用于断点传输*/
    private String lastRemoteFileName;
    /* 服务器上的文件的大小*/
    private Long remoteFileSize;
    private String clientApiVersion;
    private String serverApiVersion;
    private String clientNodelistVersion;
    private String serverNodelistVersion;
    /*挂载的节点名称*/
    private String mountNodeName;
    /*系统映射kv 虚拟系统-真实系统*/
    private String vsysmap;
    /*流水号*/
    private String flowNo;
    /*客户端版本号*/
    private String clientVersion;

    //特殊处理
    public int getContLen() {
        return contLen;
    }

    public void setContLen(int contLen) {
        this.contLen = contLen;
        if (contLen != pieceNum) {
            for (int start = contLen; start < this.pieceNum; start++) {
                this.fileCont[start] = 0x00;
            }
        }
    }

    public void setContLen2(int contLen) {
        this.contLen = contLen;
    }

    public byte[] getFileCont() {
        if (fileCont == null) fileCont = new byte[pieceNum];
        return fileCont;
    }

    public void setFileCont(byte[] fileCont1) {
        if (this.fileCont == null) fileCont = new byte[pieceNum];
        System.arraycopy(fileCont1, 0, fileCont, 0, fileCont1.length);
    }

    public byte[] getFileCont2() {
        return fileCont;
    }

    public void setFileCont2(byte[] fileCont) {
        this.fileCont = fileCont;
    }

    public int addFileIndex() {
        this.fileIndex++;
        return this.fileIndex;
    }

    public void clean() {
        fileCont = null;
        desKey = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileName=").append(fileName).append(";");
        sb.append("Uid=").append(uid).append(";");
        sb.append("Passwd=").append(passwd).append(";");
        sb.append("MsgType=").append(fileMsgFlag).append(";");
        sb.append("AuthFlag=").append(authFlag).append(";");
        sb.append("LashPiece=").append(lastPiece).append(";");
        sb.append("AuthFlag=").append(authFlag).append(";");
        sb.append("PieceNum=").append(pieceNum).append(";");
        sb.append("DesKey=").append(Arrays.toString(desKey)).append(";");
        sb.append("tarSysName=").append(tarSysName).append(";");
        sb.append("tarFileName=").append(tarFileName).append(";");
        sb.append("tranCode=").append(tranCode).append(";");
        sb.append("offset=").append(offset).append(";");
        sb.append("flowNo=").append(flowNo).append(";");
        sb.append("clientVersion=").append(clientVersion).append(";");
        sb.append("compressFlag=").append(compressFlag);
        return sb.toString();
    }

    //getter setter


    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
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

    public Boolean isAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(Boolean authFlag) {
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

    public Boolean isLastPiece() {
        return lastPiece;
    }

    public void setLastPiece(Boolean lastPiece) {
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

    public Boolean isScrtFlag() {
        return scrtFlag;
    }

    public void setScrtFlag(Boolean scrtFlag) {
        this.scrtFlag = scrtFlag;
    }

    public byte[] getDesKey() {
        return desKey;
    }

    public void setDesKey(byte[] desKey) {
        this.desKey = desKey;
    }

    public String getDesKeyStr() {
        return desKeyStr;
    }

    public void setDesKeyStr(String desKeyStr) {
        this.desKeyStr = desKeyStr;
    }

    public Boolean isEbcdicFlag() {
        return ebcdicFlag;
    }

    public void setEbcdicFlag(Boolean ebcdicFlag) {
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

    public String getTargetNodeAddr() {
        return targetNodeAddr;
    }

    public void setTargetNodeAddr(String targetNodeAddr) {
        this.targetNodeAddr = targetNodeAddr;
    }

    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public Boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(Boolean fileExists) {
        this.fileExists = fileExists;
    }

    public String getLastRemoteFileName() {
        return lastRemoteFileName;
    }

    public void setLastRemoteFileName(String lastRemoteFileName) {
        this.lastRemoteFileName = lastRemoteFileName;
    }

    public Long getRemoteFileSize() {
        return remoteFileSize;
    }

    public void setRemoteFileSize(Long remoteFileSize) {
        this.remoteFileSize = remoteFileSize;
    }

    public String getClientApiVersion() {
        return clientApiVersion;
    }

    public void setClientApiVersion(String clientApiVersion) {
        this.clientApiVersion = clientApiVersion;
    }

    public String getServerApiVersion() {
        return serverApiVersion;
    }

    public void setServerApiVersion(String serverApiVersion) {
        this.serverApiVersion = serverApiVersion;
    }

    public String getClientNodelistVersion() {
        return clientNodelistVersion;
    }

    public void setClientNodelistVersion(String clientNodelistVersion) {
        this.clientNodelistVersion = clientNodelistVersion;
    }

    public String getServerNodelistVersion() {
        return serverNodelistVersion;
    }

    public void setServerNodelistVersion(String serverNodelistVersion) {
        this.serverNodelistVersion = serverNodelistVersion;
    }

    public String getMountNodeName() {
        return mountNodeName;
    }

    public void setMountNodeName(String mountNodeName) {
        this.mountNodeName = mountNodeName;
    }

    public String getVsysmap() {
        return vsysmap;
    }

    public void setVsysmap(String vsysmap) {
        this.vsysmap = vsysmap;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getUserDescribe() {
        return userDescribe;
    }

    public void setUserDescribe(String userDescribe) {
        this.userDescribe = userDescribe;
    }
}
