/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtFileUpload extends DataEntity<FtFileUpload> {

    private static final long serialVersionUID = 1L;
    private String fileName;        // 文件名称
    private String systemName;        // 上传系统
    private String nodeName;        //上传节点
    private String uploadUser;
    private String fileType;
    private String fileSize;        // 文件大小
    private String uploadPath;        // 文件路径
    private String renameFileName;    //重命名文件名称

    private String sendNodeName;        //发送节点
    private int monitorForDataNodePort;    //端口号
    private String monitorNodeIp;        //监控端地址
    private MultipartFile uploadFileName;   //上传文件名

    private String realFileName;    //服务端接收文件的绝对路径
    private String bakFileName;        //服务端原文件的备份路径
    private String retCode;            //响应码
    private String retMsg;            //响应信息
    private String updateType;        //更新方式


    public String getSendNodeName() {
        return sendNodeName;
    }

    public void setSendNodeName(String sendNodeName) {
        this.sendNodeName = sendNodeName;
    }

    public int getMonitorForDataNodePort() {
        return monitorForDataNodePort;
    }

    public void setMonitorForDataNodePort(int monitorForDataNodePort) {
        this.monitorForDataNodePort = monitorForDataNodePort;
    }

    public MultipartFile getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(MultipartFile uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getRenameFileName() {
        return renameFileName;
    }

    public void setRenameFileName(String renameFileName) {
        this.renameFileName = renameFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public String getBakFileName() {
        return bakFileName;
    }

    public void setBakFileName(String bakFileName) {
        this.bakFileName = bakFileName;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getMonitorNodeIp() {
        return monitorNodeIp;
    }

    public void setMonitorNodeIp(String monitorNodeIp) {
        this.monitorNodeIp = monitorNodeIp;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }
}