package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("baseConfig")
public class NettyModel extends BaseModel {


  @XStreamImplicit
  private List<NettyConfig> nettyConfig;

  public void init() {
    if (nettyConfig == null) nettyConfig = new ArrayList<>();
  }


  public List<NettyConfig> getNettyConfig() {
    return nettyConfig;
  }

  public void setNettyConfig(List<NettyConfig> nettyConfig) {
    this.nettyConfig = nettyConfig;
  }




  @XStreamAlias("nettyConfig")
  public static class NettyConfig {
    @XStreamImplicit
    private List<Prarm> prarm;

    @XStreamAlias("channelSpeed")
    private ChannelSpeed channelSpeed;


    public List<Prarm> getPrarm() {
      return prarm;
    }

    public void setPrarm(List<Prarm> prarm) {
      this.prarm = prarm;
    }

    public ChannelSpeed getChannelSpeed() {
      return channelSpeed;
    }

    public void setChannelSpeed(ChannelSpeed channelSpeed) {
      this.channelSpeed = channelSpeed;
    }
  }



  @XStreamAlias("prarm")
  public static class Prarm {
    @XStreamAsAttribute
    private String maxSpeed;   // 最大传输速度单位为MB
    @XStreamAsAttribute
    private String sleepTime;   // 最大睡眠时间
    @XStreamAsAttribute
    private String scanTime;  //扫描时间

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
  }


  @XStreamAlias("channelSpeed")
  public static class ChannelSpeed {

    @XStreamImplicit
    private List<NettyChannel> channelList;

    public List<NettyChannel> getChannelList() {
      return channelList;
    }

    public void setChannelList(List<NettyChannel> channelList) {
      this.channelList = channelList;
    }
  }


  /*@XStreamConverter(value = ToAttributedValueConverter.class, strings = {"nettyChannel"})*/
  @XStreamAlias("channel")
  public static class NettyChannel {

    @XStreamAsAttribute
    private String userName; //读取宽带
    @XStreamAsAttribute
    private String readLimit; //读取宽带
    @XStreamAsAttribute
    private String writeLimit; //写入宽带

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
  }
}
