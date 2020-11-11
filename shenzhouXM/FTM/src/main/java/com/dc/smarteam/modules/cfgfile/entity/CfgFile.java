package com.dc.smarteam.modules.cfgfile.entity;

import com.dc.smarteam.common.persistence.LongDataEntity;



/**
 * Created by mocg on 2017/3/15.
 *  文件属性
 */
public class CfgFile extends LongDataEntity<CfgFile> {

    private String fileName;
    private String content;
    private String nodeType;
    private String system;
    private String fileType;
    private Long fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
