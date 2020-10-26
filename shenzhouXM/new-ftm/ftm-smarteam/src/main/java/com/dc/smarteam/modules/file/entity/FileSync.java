package com.dc.smarteam.modules.file.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by Administrator on 2019/9/4.
 */
public class FileSync extends DataEntity<FileSync> {

    /*开始时间 */
    private String syncStartTime = null;
    /*结束时间 */
    private String syncEndTime = null;
    /*文件名称 */
    private String fileName = null;
    /*真实文件名称 */
    private String realFileName = null;
    /*节点名称 */
    private String NODENAME = null;
    /*交易码 */
    private String TRAN_CODE = null;
    /*交易码 */
    private String tranCode = null;
    /*原流水号 */
    private String origFlowNo = null;
    /*新流水号 */
    private String newFlowNo = null;
    /*错误信息 */
    private String errCode = null;
    /*错误信息 */
    private String errMsg = null;
    /*同步成功状态 */
    private String STATE = null;

    private Date createdTime;

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getSyncStartTime() {
        return syncStartTime;
    }

    public void setSyncStartTime(String syncStartTime) {
        this.syncStartTime = syncStartTime;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE ;
    }
    public String getSyncEndTime() {
        return syncEndTime;
    }

    public void setSyncEndTime(String syncEndTime) {
        this.syncEndTime = syncEndTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public String getTRAN_CODE() {
        return TRAN_CODE;
    }

    public void setTRAN_CODE(String TRAN_CODE) {
        this.TRAN_CODE = TRAN_CODE;
    }

    public String getNODENAME() {
        return NODENAME;
    }

    public void setNODENAME(String NODENAME) {
        this.NODENAME = NODENAME;
    }
    public String getOrigFlowNo() {
        return origFlowNo;
    }

    public void setOrigFlowNo(String origFlowNo) {
        this.origFlowNo = origFlowNo;
    }

    public String getNewFlowNo() {
        return newFlowNo;
    }

    public void setNewFlowNo(String newFlowNo) {
        this.newFlowNo = newFlowNo;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

}
