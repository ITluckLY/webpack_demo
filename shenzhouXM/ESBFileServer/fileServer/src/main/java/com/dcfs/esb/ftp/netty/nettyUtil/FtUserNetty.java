package com.dcfs.esb.ftp.netty.nettyUtil;

public class FtUserNetty {
  private String maxSpeed;   // 最大传输速度单位为MB
  private String sleepTime;   // 最大睡眠时间
  private String scanTime ;  //扫描时间

  private String userName; //用户名称
  private String readLimit; //读取宽带
  private String writeLimit; //写入宽带

  public String getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(String maxSpeed) {
    this.maxSpeed = maxSpeed;
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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
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
        "maxSpeed='" + maxSpeed + '\'' +
        ", sleepTime='" + sleepTime + '\'' +
        ", scanTime='" + scanTime + '\'' +
        ", userName='" + userName + '\'' +
        ", readLimit='" + readLimit + '\'' +
        ", writeLimit='" + writeLimit + '\'' +
        '}';
  }
}
