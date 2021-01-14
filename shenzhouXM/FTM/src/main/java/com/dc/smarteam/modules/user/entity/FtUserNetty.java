package com.dc.smarteam.modules.user.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;

public class FtUserNetty   extends DataEntity<FtUserNetty> implements CfgData {
  private static final long serialVersionUID = 1L;


  private String maxSpeed;   // 最大传输速度单位为MB
  private String sleepTime;   // 最大睡眠时间
  private String scanTime ;  //扫描时间

  private String userName; //用户名称
  private String readLimit; //读取宽带
  private String writeLimit; //写入宽带

  private String nettyId;

  public String getNettyId() {
    return nettyId;
  }

  public void setNettyId(String nettyId) {
    this.nettyId = nettyId;
  }

  public FtUserNetty() {
    super();
  }

  public String getUserName() {
    return userName;
  }

  public String getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(String maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }



  public String getSleepTime() {
    return sleepTime;
  }

  public void setSleepTime(String sleepTime) {
    this.sleepTime = sleepTime;
  }

  public String getScanTime() {
    return scanTime;
  }

  public void setScanTime(String scanTime) {
    this.scanTime = scanTime;
  }

  public String getReadLimit() {
    return readLimit;
  }

  public void setReadLimit(String readLimit) {
    this.readLimit = readLimit;
  }

  public String getWriteLimit() {
    return writeLimit;
  }

  public void setWriteLimit(String writeLimit) {
    this.writeLimit = writeLimit;
  }

  @Override
  public String toString() {
    return "FtUserNetty{" +
        "userName='" + userName + '\'' +
        ", maxSpeed=" + maxSpeed + '\'' +
        ", sleepTime=" + sleepTime + '\'' +
        ", scanTime=" + scanTime + '\'' +
        ", readLimit='" + readLimit + '\'' +
        ", writeLimit='" + writeLimit + '\'' +
        '}';
  }

  @Override
  public String getParamName() {
    return null;
  }
}
