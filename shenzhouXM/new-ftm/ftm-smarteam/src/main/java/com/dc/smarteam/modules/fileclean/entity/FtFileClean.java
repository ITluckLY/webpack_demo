/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.fileclean.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 文件清理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtFileClean extends DataEntity<FtFileClean> implements CfgData{


    public static final String WAITING = "0";            //等待状态
    public static final String RUNNINIG = "1";      //正运行状态

    public static final String TRUE = "true";       //isBackup
    public static final String FALSE = "false";

    private static final long serialVersionUID = 1L;
    private String targetDir;            // 目标目录
    private String userName;            // 用户
    private String keepTime;            // 保留时间
    private String state;                  // 状态
    private String isBackup;            // 文件归档
    private String backupType;            // 归档类型
    private String backupPath;            // 归档路径
    private String system;              // 系统名称


    public FtFileClean() {
        super();
    }

    public FtFileClean(String id) {
        super(id);
    }

    @Length(min = 0, max = 256, message = "目标目录长度必须介于 0 和 256 之间")
    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    @Length(min = 0, max = 256, message = "用户长度必须介于 0 和 256 之间")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Length(min = 0, max = 256, message = "保留时间长度必须介于 0 和 256 之间")
    public String getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(String keepTime) {
        this.keepTime = keepTime;
    }

    @Length(min = 0, max = 256, message = "状态长度必须介于 0 和 256 之间")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Length(min = 0, max = 256, message = "文件归档长度必须介于 0 和 256 之间")
    public String getIsBackup() {
        return isBackup;
    }

    public void setIsBackup(String isBackup) {
        this.isBackup = isBackup;
    }

    @Length(min = 0, max = 256, message = "归档类型长度必须介于 0 和 256 之间")
    public String getBackupType() {
        return backupType;
    }

    public void setBackupType(String backupType) {
        this.backupType = backupType;
    }

    @Length(min = 0, max = 256, message = "归档路径长度必须介于 0 和 256 之间")
    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    @Length(min = 0, max = 256, message = "系统名称长度必须介于 0 和 256 之间")
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Override
    public String getParamName() {
        return targetDir;
    }
}