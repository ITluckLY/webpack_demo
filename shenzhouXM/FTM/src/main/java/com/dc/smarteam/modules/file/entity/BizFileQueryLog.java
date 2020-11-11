package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by mocg on 2016/7/13.
 */

public class BizFileQueryLog extends DataEntity<BizFileQueryLog> {

    /* 客户端IP */
    private String downclientIp = null;
    /* 文件服务器IP地址  */
    private String downserverIp = null;

    /*文件消费方用户名称*/
    private String downuname = null;

    /* 是否是最后一个分片 */
    private boolean downlastPiece;
    private Date downcreatedTime;
    private boolean downsuss;
    private String downerrCode;
    private String downid;

    /*交易码 */
    private String tranCode = null;
    private String sysname;



    private String nodeNameTemp;
    private String transuss;
    private String oriFilename;
    private String flowNo;
    private String fileName;


    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getUpclientIp() {
        return upclientIp;
    }

    public void setUpclientIp(String upclientIp) {
        this.upclientIp = upclientIp;
    }

    public String getUpserverIp() {
        return upserverIp;
    }

    public void setUpserverIp(String upserverIp) {
        this.upserverIp = upserverIp;
    }

    public String getUpuname() {
        return upuname;
    }

    public void setUpuname(String upuname) {
        this.upuname = upuname;
    }

    public boolean isUplastPiece() {
        return uplastPiece;
    }

    public void setUplastPiece(boolean uplastPiece) {
        this.uplastPiece = uplastPiece;
    }


    public boolean isUpsuss() {
        return upsuss;
    }

    public void setUpsuss(boolean upsuss) {
        this.upsuss = upsuss;
    }

    public String getUperrCode() {
        return uperrCode;
    }

    public void setUperrCode(String uperrCode) {
        this.uperrCode = uperrCode;
    }

    /* 客户端IP */
    private String upclientIp = null;
    /* 文件服务器IP地址  */
    private String upserverIp = null;
    /*文件生产方用户名*/
    private String upuname;
    /* 是否是最后一个分片 */
    private boolean uplastPiece = false;

    private Date upcreatedTime;
    private boolean upsuss;
    private String uperrCode;
    private String upid;
    private String upflowNo;

    private String uperrMsg;
    private String downerrMsg;
    private Date beginDate;
    private Date endDate;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUperrMsg() {
        return uperrMsg;
    }

    public void setUperrMsg(String uperrMsg) {
        this.uperrMsg = uperrMsg;
    }

    public String getDownerrMsg() {
        return downerrMsg;
    }

    public void setDownerrMsg(String downerrMsg) {
        this.downerrMsg = downerrMsg;
    }

    public String getDownclientIp() {
        return downclientIp;
    }

    public void setDownclientIp(String downclientIp) {
        this.downclientIp = downclientIp;
    }

    public String getDownserverIp() {
        return downserverIp;
    }

    public void setDownserverIp(String downserverIp) {
        this.downserverIp = downserverIp;
    }

    public String getDownuname() {
        return downuname;
    }

    public void setDownuname(String downuname) {
        this.downuname = downuname;
    }

    public boolean isDownlastPiece() {
        return downlastPiece;
    }

    public void setDownlastPiece(boolean downlastPiece) {
        this.downlastPiece = downlastPiece;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public Date getDowncreatedTime() {
        return downcreatedTime;
    }

    public void setDowncreatedTime(Date downcreatedTime) {
        this.downcreatedTime = downcreatedTime;
    }

    public boolean isDownsuss() {
        return downsuss;
    }

    public void setDownsuss(boolean downsuss) {
        this.downsuss = downsuss;
    }

    public String getDownerrCode() {
        return downerrCode;
    }

    public void setDownerrCode(String downerrCode) {
        this.downerrCode = downerrCode;
    }


    public String getNodeNameTemp() {
        return nodeNameTemp;
    }

    public void setNodeNameTemp(String nodeNameTemp) {
        this.nodeNameTemp = nodeNameTemp;
    }
    public Date getUpcreatedTime() {
        return upcreatedTime;
    }

    public void setUpcreatedTime(Date upcreatedTime) {
        this.upcreatedTime = upcreatedTime;
    }

    public String getOriFilename() {
        return oriFilename;
    }

    public void setOriFilename(String oriFilename) {
        this.oriFilename = oriFilename;
    }

    public String getTransuss() {
        return transuss;
    }

    public void setTransuss(String transuss) {
        this.transuss = transuss;
    }

    public String getUpid() {
        return upid;
    }

    public void setUpid(String upid) {
        this.upid = upid;
    }

    public String getDownid() {
        return downid;
    }

    public void setDownid(String downid) {
        this.downid = downid;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    /*原流水号 */
    private String origFlowNo;

    /*节点名称 */
    private String NODENAME;
    /*同步成功状态 */
    private String STATE ;

    public String getOrigFlowNo() {
        return origFlowNo;
    }

    public void setOrigFlowNo(String origFlowNo) {
        this.origFlowNo = origFlowNo;
    }

    public String getNODENAME() {
        return NODENAME;
    }

    public void setNODENAME(String NODENAME) {
        this.NODENAME = NODENAME;
    }

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;

    }
}
