/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.serviceinfo.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 用户管理Entity
 *
 * @author liwang
 * @version 2016-01-12
 */
public class FtServiceInfo extends DataEntity<FtServiceInfo> implements CfgData {

  private static final long serialVersionUID = 1L;
  private String trancode;            //交易码
  private String flow;     // %流 %
  private String psflow;     // %校验流程 %
  private String describe;    //描述
  private String systemName;      //系统
  private String rename;          //是否重命名
  private String filePeriod;      //文件保留期限
  private String putAuth;
  private String getAuth;
  private String priority;        //优先级
  private String size;            //数量
  private String cross;            //跨地域

  public FtServiceInfo() {
    super();
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getFilePeriod() {
    return filePeriod;
  }

  public void setFilePeriod(String filePeriod) {
    this.filePeriod = filePeriod;
  }

  public String getRename() {
    return rename;
  }

  public void setRename(String rename) {
    this.rename = rename;
  }

  public String getPutAuth() {
    return putAuth;
  }

  public void setPutAuth(String putAuth) {
    this.putAuth = putAuth;
  }

  public String getGetAuth() {
    return getAuth;
  }

  public void setGetAuth(String getAuth) {
    this.getAuth = getAuth;
  }

  @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

//	public FtServiceInfo(String id){
//		super(id);
//	}

  @Length(min = 0, max = 256, message = "名称长度必须介于 0 和 256 之间")
  public String getTrancode() {
    return trancode;
  }

  public void setTrancode(String trancode) {
    this.trancode = trancode;
  }


  @Length(min = 0, max = 256, message = "系统名称长度必须介于 0 和 256 之间")
  public String getFlow() {
    return flow;
  }

  public void setFlow(String flow) {
    this.flow = flow;
  }

  @Length(min = 0, max = 256, message = "中文描述长度必须介于 0 和 256 之间")
  public String getDescribe() {
    return describe;
  }

  public void setDescribe(String describe) {
    this.describe = describe;
  }

  public String getCross() {
    return cross;
  }

  public void setCross(String cross) {
    this.cross = cross;
  }

  @Override
  public String getParamName() {
    return this.getTrancode();
  }

  public String getPsflow() {
    return psflow;
  }

  public void setPsflow(String psflow) {
    this.psflow = psflow;
  }

  @Override
  public String toString() {
    return "FtServiceInfo{" +
        "trancode='" + trancode + '\'' +
        ", flow='" + flow + '\'' +
        ", psflow='" + psflow + '\'' +
        ", describe='" + describe + '\'' +
        ", systemName='" + systemName + '\'' +
        ", rename='" + rename + '\'' +
        ", filePeriod='" + filePeriod + '\'' +
        ", putAuth='" + putAuth + '\'' +
        ", getAuth='" + getAuth + '\'' +
        ", priority='" + priority + '\'' +
        ", size='" + size + '\'' +
        ", cross='" + cross + '\'' +
        '}';
  }
}