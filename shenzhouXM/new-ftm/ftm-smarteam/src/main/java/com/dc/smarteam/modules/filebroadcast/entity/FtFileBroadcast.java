/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.filebroadcast.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 文件广播Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtFileBroadcast extends DataEntity<FtFileBroadcast> {

    private static final long serialVersionUID = 1L;
    private String name;        // 名称
    private String des;        // 中文描述
    private String retryNum;        // 重发次数
    private String retryTime;        // 重发间隔
    private String fileId;        // 文件

    public FtFileBroadcast() {
        super();
    }

    public FtFileBroadcast(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 256, message = "中文描述长度必须介于 0 和 256 之间")
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Length(min = 0, max = 256, message = "重发次数长度必须介于 0 和 256 之间")
    public String getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(String retryNum) {
        this.retryNum = retryNum;
    }

    @Length(min = 0, max = 256, message = "重发间隔长度必须介于 0 和 256 之间")
    public String getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(String retryTime) {
        this.retryTime = retryTime;
    }

    @Length(min = 0, max = 256, message = "文件长度必须介于 0 和 256 之间")
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

}