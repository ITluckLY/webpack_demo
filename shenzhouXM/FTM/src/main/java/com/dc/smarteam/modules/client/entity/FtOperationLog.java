package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.persistence.LongDataEntity;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

/**
 *
 * 客户端概况
 * liuyfal  20180327
 *
 */
public class FtOperationLog extends LongDataEntity<FtOperationLog> {

    private String system;
    private String paramUpdateType;
    private String beforeModifyValue;
    private String afterModifyValue;
    private String paramName;
    private String cfgFileName;
    private String nodeType;
    private Date modifiedDate;
    private String opeId;
    private String opeName;
    private String remarks;
    private String remark1;
    private String remark2;
    private Date beginDate;
    private Date endDate;
    private String cleanFlag;

    public FtOperationLog() {

    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getParamUpdateType() {
        return paramUpdateType;
    }

    public void setParamUpdateType(String paramUpdateType) {
        this.paramUpdateType = paramUpdateType;
    }

    public String getBeforeModifyValue() {
        return beforeModifyValue;
    }

    public void setBeforeModifyValue(String beforeModifyValue) {
        this.beforeModifyValue = beforeModifyValue;
    }

    public String getAfterModifyValue() {
        return afterModifyValue;
    }

    public void setAfterModifyValue(String afterModifyValue) {
        this.afterModifyValue = afterModifyValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getCfgFileName() {
        return cfgFileName;
    }

    public void setCfgFileName(String cfgFileName) {
        this.cfgFileName = cfgFileName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getOpeId() {
        return opeId;
    }

    public void setOpeId(String opeId) {
        this.opeId = opeId;
    }

    public String getOpeName() {
        return opeName;
    }

    public void setOpeName(String opeName) {
        this.opeName = opeName;
    }

    public String getRemark1() {
//        return StringEscapeUtils.unescapeHtml(remark1);
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

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

    public String getCleanFlag() {
        return cleanFlag;
    }

    public void setCleanFlag(String cleanFlag) {
        this.cleanFlag = cleanFlag;
    }

    @Override
    public String toString() {
        return "FtOperationLog{" +
                "system='" + system + '\'' +
                ", paramUpdateType='" + paramUpdateType + '\'' +
                ", beforeModifyValue='" + beforeModifyValue + '\'' +
                ", afterModifyValue='" + afterModifyValue + '\'' +
                ", paramName='" + paramName + '\'' +
                ", cfgFileName='" + cfgFileName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", modifiedDate=" + modifiedDate +
                ", opeId='" + opeId + '\'' +
                ", opeName='" + opeName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", remark1='" + remark1 + '\'' +
                ", remark2='" + remark2 + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                '}';
    }
}
