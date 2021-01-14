package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import java.util.ArrayList;
import java.util.List;


@XStreamAlias("baseConfig")
public class treeNettyModel  extends BaseModel{

  private List<TwoNettyModel.Prarm> prarmList;
  private List<TwoNettyModel.ChannelSpeed> channelSpeedList;

  public void init() {
    if (prarmList == null) {
      prarmList = new ArrayList<>();
    }
    if (channelSpeedList == null) {
      channelSpeedList = new ArrayList<>();
    }
  }

  public List<TwoNettyModel.Prarm> getPrarmList() {
    return prarmList;
  }

  public void setPrarmList(List<TwoNettyModel.Prarm> prarmList) {
    this.prarmList = prarmList;
  }

  public List<TwoNettyModel.ChannelSpeed> getChannelSpeedList() {
    return channelSpeedList;
  }

  public void setChannelSpeedList(List<TwoNettyModel.ChannelSpeed> channelSpeedList) {
    this.channelSpeedList = channelSpeedList;
  }



  @XStreamAlias("nettyConfig")
  public static class NettyConfig {

    private TwoNettyModel.Prarm prarm;
    private TwoNettyModel.ChannelSpeed channelSpeed;

    public TwoNettyModel.Prarm getPrarm() {
      return prarm;
    }
    public void setPrarm(TwoNettyModel.Prarm prarm) {
      this.prarm = prarm;
    }
    public TwoNettyModel.ChannelSpeed getChannelSpeed() {
      return channelSpeed;
    }
    public void setChannelSpeed(TwoNettyModel.ChannelSpeed channelSpeed) {
      this.channelSpeed = channelSpeed;
    }
  }
  @XStreamAlias("Prarm")
  public static class Prarm {
    @XStreamImplicit
    private List<TwoNettyModel.Prarms> prarms;
    public List<TwoNettyModel.Prarms> getPrarms() {
      return prarms;
    }
    public void setPrarms(List<TwoNettyModel.Prarms> prarms) {
      this.prarms = prarms;
    }
  }


  @XStreamAlias("prarm")
  @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"prarm"})
  public static class Prarms {
    @XStreamAlias("maxsPeed")
    private Integer maxsPeed;   // 最大传输速度单位为MB
    @XStreamAlias("sleepTime")
    private Integer sleepTime;   // 最大睡眠时间
    @XStreamAlias("scanTime")
    private Integer scanTime;  //扫描时间

    public Integer getMaxsPeed() {
      return maxsPeed;
    }

    public void setMaxsPeed(Integer maxsPeed) {
      this.maxsPeed = maxsPeed;
    }

    public Integer getSleepTime() {
      return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
      this.sleepTime = sleepTime;
    }

    public Integer getScanTime() {
      return scanTime;
    }

    public void setScanTime(Integer scanTime) {
      this.scanTime = scanTime;
    }
  }

  @XStreamAlias("ChannelSpeed")
  public static class ChannelSpeed {
    @XStreamImplicit
    private List<TwoNettyModel.NettyChannel> channelList;
    public List<TwoNettyModel.NettyChannel> getChannelList() {
      return channelList;
    }
    public void setChannelList(List<TwoNettyModel.NettyChannel> channelList) {
      this.channelList = channelList;
    }
  }

  @XStreamAlias("channel")
  @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"channel"})
  public static class NettyChannel {

    @XStreamAsAttribute //   将 userName 作为 channel 属性输出在父节点
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


