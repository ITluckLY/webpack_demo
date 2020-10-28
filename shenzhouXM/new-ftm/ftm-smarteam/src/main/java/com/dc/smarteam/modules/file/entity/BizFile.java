package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by huangzbb on 2016/8/3.
 */
public class BizFile extends DataEntity<BizFile> {

    /*节点名称*/
    private String nodeNameBiz;
    /*系统名称*/
    private String systemName;
    /*客户端用户名称*/
    private String clientUserName;
    /*文件平台内绝对路径*/
    private String filePath;
    /*请求文件路径 外部根据这路径找到相应的文件 一般与filePath相同，也可能不同(分发时文件存在)*/
    private String requestFilePath;
    /*文件名称*/
    //@Column(length = 255)
    private String fileName;
    /*客户端的文件路径*/
    private String clientFilePath;
    /*客户端的文件名称*/
    private String clientFileName;
    /*客户端所给的服务器文件路径 原始的fileName*/
    private String originalFilePath;
    /*客户端的IP*/
    private String clientIp;
    /*文件大小（单位：字节）*/
    private Long fileSize;
    /*文件后缀*/
    private String fileExt;
    /*上传开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date uploadStartTime;
    /*上传结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date uploadEndTime;
    /*0 原始的，1 从其他分发过来的 -1 分发失败*/
    private int state;
    private String fileMd5;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;
    private String flowNo;

    public String getFlowNo() {return flowNo;}

    public void setFlowNo(String flowNo) {this.flowNo = flowNo;}

    public BizFile() {
        super();
    }

    public BizFile(String id) {
        super(id);
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {this.id = id;}

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRequestFilePath() {
        return requestFilePath;
    }

    public void setRequestFilePath(String requestFilePath) {
        this.requestFilePath = requestFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClientFilePath() {
        return clientFilePath;
    }

    public void setClientFilePath(String clientFilePath) {
        this.clientFilePath = clientFilePath;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public void setClientFileName(String clientFileName) {
        this.clientFileName = clientFileName;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Date getUploadStartTime() {
        return uploadStartTime;
    }

    public void setUploadStartTime(Date uploadStartTime) {
        this.uploadStartTime = uploadStartTime;
    }

    public Date getUploadEndTime() {
        return uploadEndTime;
    }

    public void setUploadEndTime(Date uploadEndTime) {
        this.uploadEndTime = uploadEndTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
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

    public String getNodeNameBiz() {
        return nodeNameBiz;
    }

    public void setNodeNameBiz(String nodeNameBiz) {
        this.nodeNameBiz = nodeNameBiz;
    }
}
