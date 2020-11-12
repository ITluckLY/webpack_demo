package com.dcfs.esb.ftp.common.model;

import java.io.Serializable;

/**
 * Created by huangzbb on 2016/10/20.
 */
public class BizFileInfoMsg implements Serializable {
    private String nodeName;
    private String systemName;
    private Integer state;
    private Long fileVersion;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(Long fileVersion) {
        this.fileVersion = fileVersion;
    }
}
