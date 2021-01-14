package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 @XStreamAlias("baseConfig") xml 最外层的标签

 //此注解标明该属性是集合,对应的XML的节点是newslist
 @XStreamImplicit(itemFieldName="newslist")

 @XStreamAsAttribute //   将 userName 作为 channel 属性输出在父节点
 private String userName; //读取宽带


 设置转换器
 @XStreamConverter(),转换器的意思就是在输出前做个处理。
 */

@XStreamAlias("baseConfig")
public class TwoNettyModel extends BaseModel{

  private NettyConfig  nettyConfig;

  public void init() {
    if (nettyConfig == null) nettyConfig = new NettyConfig();
  }

  public NettyConfig getNettyConfig() {
    return nettyConfig;
  }

  public void setNettyConfig(NettyConfig nettyConfig) {
    this.nettyConfig = nettyConfig;
  }




  @XStreamAlias("nettyConfig")
  public static class NettyConfig {

    private Prarm prarm;
    private ChannelSpeed channelSpeed;

    public Prarm getPrarm() {
      return prarm;
    }
    public void setPrarm(Prarm prarm) {
      this.prarm = prarm;
    }
    public ChannelSpeed getChannelSpeed() {
      return channelSpeed;
    }
    public void setChannelSpeed(ChannelSpeed channelSpeed) {
      this.channelSpeed = channelSpeed;
    }
  }
  @XStreamAlias("Prarm")
  public static class Prarm {
    @XStreamImplicit
    private  List<Prarms> prarms;
    public List<Prarms> getPrarms() {
      return prarms;
    }
    public void setPrarms(List<Prarms> prarms) {
      this.prarms = prarms;
    }
  }


  @XStreamAlias("prarm")
  @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"prarm"})
  public static class Prarms {
    @XStreamAlias("maxsPeed")
    private String maxsPeed;   // 最大传输速度单位为MB
    @XStreamAlias("sleepTime")
    private String sleepTime;   // 最大睡眠时间
    @XStreamAlias("scanTime")
    private String scanTime;  //扫描时间

    public String getMaxsPeed() {
      return maxsPeed;
    }

    public void setMaxsPeed(String maxsPeed) {
      this.maxsPeed = maxsPeed;
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

  @XStreamAlias("ChannelSpeed")
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

