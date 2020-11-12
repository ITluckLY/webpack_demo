package com.dcfs.esb.ftp.interfases.context;

import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;

/**
 * Created by mocg on 2016/12/3.
 */
public class ContextBean {
    private String errorCode;
    private String errorMsg;
    private FtpConnector connector;
    private String msgFlag;
    private String nodeName;
    private String sysname;
    private String user;//uid
    private String clientAddr;
    private String tranCode;
    private String flowName;//
    private long maxFileSize;//bit
    private long nano = NanoHelper.nanos();
    private boolean fileRename = true;
    private EsbFile esbFile;
    /*当前请求是否为挂载请求*/
    private boolean mount = false;
    private String fileRootPath;
    private String backupFileRootPath;
    private int taskPriority;
    private int taskSize;
    private int currTaskPriority;
    private int currTaskCount;
    private long currTaskNetworkSpeed;
    private Long fileVersion;
    private String originalFileMsgFlag;
    /*dto版本*/
    private long dtoVersion = 0;
    /*流水号*/
    private String flowNo;

    private long timestamp1;
    private long timestamp2;

    private boolean preProcessReturn = true;
    private boolean processReturn = true;
    private String preflowName;

    private String sessionId;
    private CachedContext context;


    //methods

    public void addTaskNetworkSpeed(long speed) {
        currTaskNetworkSpeed += speed;
    }

    public void minusTaskNetworkSpeed(long speed) {
        currTaskNetworkSpeed -= speed;
    }


    //set get


    public long getDtoVersion() {
        return dtoVersion;
    }

    public void setDtoVersion(long dtoVersion) {
        this.dtoVersion = dtoVersion;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public FtpConnector getConnector() {
        return connector;
    }

    public void setConnector(FtpConnector connector) {
        this.connector = connector;
    }

    public String getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(String msgFlag) {
        this.msgFlag = msgFlag;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClientAddr() {
        return clientAddr;
    }

    public void setClientAddr(String clientAddr) {
        this.clientAddr = clientAddr;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public boolean isFileRename() {
        return fileRename;
    }

    public void setFileRename(boolean fileRename) {
        this.fileRename = fileRename;
    }

    public EsbFile getEsbFile() {
        return esbFile;
    }

    public void setEsbFile(EsbFile esbFile) {
        this.esbFile = esbFile;
    }

    public boolean isMount() {
        return mount;
    }

    public void setMount(boolean mount) {
        this.mount = mount;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public void setFileRootPath(String fileRootPath) {
        this.fileRootPath = fileRootPath;
    }

    public String getBackupFileRootPath() {
        return backupFileRootPath;
    }

    public void setBackupFileRootPath(String backupFileRootPath) {
        this.backupFileRootPath = backupFileRootPath;
    }

    public int getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
    }

    public int getCurrTaskPriority() {
        return currTaskPriority;
    }

    public void setCurrTaskPriority(int currTaskPriority) {
        this.currTaskPriority = currTaskPriority;
    }

    public int getCurrTaskCount() {
        return currTaskCount;
    }

    public void setCurrTaskCount(int currTaskCount) {
        this.currTaskCount = currTaskCount;
    }

    public long getCurrTaskNetworkSpeed() {
        return currTaskNetworkSpeed;
    }

    public void setCurrTaskNetworkSpeed(long currTaskNetworkSpeed) {
        this.currTaskNetworkSpeed = currTaskNetworkSpeed;
    }

    public Long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(Long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getOriginalFileMsgFlag() {
        return originalFileMsgFlag;
    }

    public void setOriginalFileMsgFlag(String originalFileMsgFlag) {
        this.originalFileMsgFlag = originalFileMsgFlag;
    }

    public long getTimestamp1() {
        return timestamp1;
    }

    public void setTimestamp1(long timestamp1) {
        this.timestamp1 = timestamp1;
    }

    public long getTimestamp2() {
        return timestamp2;
    }

    public void setTimestamp2(long timestamp2) {
        this.timestamp2 = timestamp2;
    }

    public boolean isPreProcessReturn() {
        return preProcessReturn;
    }

    public void setPreProcessReturn(boolean preProcessReturn) {
        this.preProcessReturn = preProcessReturn;
    }

    public boolean isProcessReturn() {
        return processReturn;
    }

    public void setProcessReturn(boolean processReturn) {
        this.processReturn = processReturn;
    }

    public String getPreflowName() {
        return preflowName;
    }

    public void setPreflowName(String preflowName) {
        this.preflowName = preflowName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public CachedContext getContext() {
        return context;
    }

    public void setContext(CachedContext context) {
        this.context = context;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }
}
