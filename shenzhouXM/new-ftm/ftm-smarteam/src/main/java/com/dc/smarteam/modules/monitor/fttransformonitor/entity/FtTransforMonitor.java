package com.dc.smarteam.modules.monitor.fttransformonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * Created by lvchuan on 2016/8/1.
 */
public class FtTransforMonitor extends DataEntity<FtTransforMonitor> {
    private Long id;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;
    private Byte authFlag;
    private String clientFileName;
    private String clientIp;
    private String compressFlag;
    private int contLen;
    private Byte ebcdicFlag;
    private Date endTime;
    private Byte fileExists;
    private int fileIndex;
    private String fileMsgFlag;
    private String fileName;
    private String fileRenameCtrl;
    private String fileRetMsg;
    private Long fileSize;
    private Byte lastPiece;
    private String nodeList;
    private String nodeName;
    private Long offset;
    private int pieceNum;
    private byte scrtFlag;
    private String serverIp;
    private String serverName;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    private Byte suss;
    private String sysname;
    private String tarFileName;
    private String tarSysName;
    private String tranCode;
    private String uname;
   /* private int count;
    private int successcount;
    private int failcount;
    private Date hour;
    private Date day;*/

    public FtTransforMonitor() {
        super();
    }

    public void setId(Long id) {
        this.id = id;
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

    public Byte getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(Byte authFlag) {
        this.authFlag = authFlag;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getCompressFlag() {
        return compressFlag;
    }

    public void setCompressFlag(String compressFlag) {
        this.compressFlag = compressFlag;
    }

    public int getContLen() {
        return contLen;
    }

    public void setContLen(int contLen) {
        this.contLen = contLen;
    }

    public Byte getEbcdicFlag() {
        return ebcdicFlag;
    }

    public void setEbcdicFlag(Byte ebcdicFlag) {
        this.ebcdicFlag = ebcdicFlag;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Byte getFileExists() {
        return fileExists;
    }

    public void setFileExists(Byte fileExists) {
        this.fileExists = fileExists;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getFileMsgFlag() {
        return fileMsgFlag;
    }

    public void setFileMsgFlag(String fileMsgFlag) {
        this.fileMsgFlag = fileMsgFlag;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getFileRenameCtrl() {
        return fileRenameCtrl;
    }

    public void setFileRenameCtrl(String fileRenameCtrl) {
        this.fileRenameCtrl = fileRenameCtrl;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getFileRetMsg() {
        return fileRetMsg;
    }

    public void setFileRetMsg(String fileRetMsg) {
        this.fileRetMsg = fileRetMsg;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Byte getLastPiece() {
        return lastPiece;
    }

    public void setLastPiece(Byte lastPiece) {
        this.lastPiece = lastPiece;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
    }

    public Byte getScrtFlag() {
        return scrtFlag;
    }

    public void setScrtFlag(Byte scrtFlag) {
        this.scrtFlag = scrtFlag;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Byte getSuss() {
        return suss;
    }

    public void setSuss(Byte suss) {
        this.suss = suss;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getTarFileName() {
        return tarFileName;
    }

    public void setTarFileName(String tarFileName) {
        this.tarFileName = tarFileName;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getTarSysName() {
        return tarSysName;
    }

    public void setTarSysName(String tarSysName) {
        this.tarSysName = tarSysName;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    /*public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSuccesscount() {
        return successcount;
    }

    public void setSuccesscount(int successcount) {
        this.successcount = successcount;
    }

    public int getFailcount() {
        return failcount;
    }

    public void setFailcount(int failcount) {
        this.failcount = failcount;
    }


    public Date getHour() {
        return hour;
    }

    public void setHour(Date hour) {
        this.hour = hour;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }*/
}
