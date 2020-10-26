/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.monitor.ftnodemonitor.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.math.BigInteger;
import java.util.Date;

/**
 * Entity
 *
 * @author lvchuan
 * @version 2016-06-26
 */
public class FtNodeAlarmLine{

    private static final long serialVersionUID = 1L;

    private BigInteger id;
    private String cpuline;
    private String diskline;
    private String memoryline;
    private String filenumberline;
    private String cpuwarn;
    private String diskwarn;
    private String memorywarn;
    private String filenumberwarn;
    private String storageline;
    private String storagewarn;
    private Date time;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getCpuline() {
        return cpuline;
    }

    public void setCpuline(String cpuline) {
        this.cpuline = cpuline;
    }

    public String getDiskline() {
        return diskline;
    }

    public void setDiskline(String diskline) {
        this.diskline = diskline;
    }

    public String getMemoryline() {
        return memoryline;
    }

    public void setMemoryline(String memoryline) {
        this.memoryline = memoryline;
    }

    public String getFilenumberline() {
        return filenumberline;
    }

    public void setFilenumberline(String filenumberline) {
        this.filenumberline = filenumberline;
    }

    public String getCpuwarn() {
        return cpuwarn;
    }

    public void setCpuwarn(String cpuwarn) {
        this.cpuwarn = cpuwarn;
    }

    public String getDiskwarn() {
        return diskwarn;
    }

    public void setDiskwarn(String diskwarn) {
        this.diskwarn = diskwarn;
    }

    public String getMemorywarn() {
        return memorywarn;
    }

    public void setMemorywarn(String memorywarn) {
        this.memorywarn = memorywarn;
    }

    public String getFilenumberwarn() {
        return filenumberwarn;
    }

    public void setFilenumberwarn(String filenumberwarn) {
        this.filenumberwarn = filenumberwarn;
    }

    public String getStorageline() {
        return storageline;
    }

    public void setStorageline(String storageline) {
        this.storageline = storageline;
    }

    public String getStoragewarn() {
        return storagewarn;
    }

    public void setStoragewarn(String storagewarn) {
        this.storagewarn = storagewarn;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
