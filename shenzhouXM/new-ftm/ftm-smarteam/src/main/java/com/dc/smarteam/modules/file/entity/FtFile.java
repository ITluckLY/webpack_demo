/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import com.dc.smarteam.modules.file.service.FtFileService;
import com.dc.smarteam.modules.file.service.impl.FtFileServiceImpl;
import org.hibernate.validator.constraints.Length;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 文件管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtFile extends DataEntity<FtFile> {

    private static final long serialVersionUID = 1L;
    private String fileName;        // 文件名称
    private String systemName;        // 上传节点组
    private String fileSize;        // 文件大小
    private String parentPath;        // 文件路径
    private String lastModifiedDate;

    public FtFile() {
        super();
    }

    public FtFile(String id) {
        super(id);
    }

    public FtFile(File file) {
        this.id = FtFileServiceImpl.generateFileId(file);
        this.fileName = file.getName();
        this.systemName = file.getParentFile().getName();
        this.fileSize = String.valueOf(file.length());
        this.parentPath = file.getParentFile().getPath();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        String lastModifiedDate = sdf.format(cal.getTime());
        this.lastModifiedDate = lastModifiedDate;
    }

    @Length(min = 0, max = 256, message = "文件名称长度必须介于 0 和 256 之间")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Length(min = 0, max = 256, message = "上传节点组长度必须介于 0 和 256 之间")
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Length(min = 0, max = 256, message = "文件大小长度必须介于 0 和 256 之间")
    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Length(min = 0, max = 256, message = "文件路径长度必须介于 0 和 256 之间")
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}