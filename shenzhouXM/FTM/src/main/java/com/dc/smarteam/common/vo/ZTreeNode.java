package com.dc.smarteam.common.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/27.
 */
public class ZTreeNode implements Serializable {
    private String id;
    private String pid;
    private String name;
    private String pids;
    private String path;//文件路径
    private String isParent;
    private String parentTId;
    private String open;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPids() {
        return pids;
    }

    public void setPids(String pids) {
        this.pids = pids;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentTId() {
        return parentTId;
    }

    public void setParentTId(String parentTId) {
        this.parentTId = parentTId;
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }
}
