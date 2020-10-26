package com.dc.smarteam.modules.serviceinfo.entity;

import com.dc.smarteam.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by liwjx on 2017/9/6.
 */
public class TradeScenario extends DataEntity<TradeScenario> {

    private String fromUid;
    private String toUid;
    private long  tradeScenarioTotal;
    /*交易码总的成功交易场景*/
    private long tradeScenarioPass;
    /*各个交易码和成功交易笔数信息,如：0001:3,0002:4*/
    private String tradeScenarioPassMess;
    private long tradeScenarioFailed;
    private String tradeScenarioFailedMess;
    private long tradeScenarioUnregulated;
    private String  groups;
    private String tranCode;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public long getTradeScenarioTotal() {
        return tradeScenarioTotal;
    }

    public void setTradeScenarioTotal(long tradeScenarioTotal) {
        this.tradeScenarioTotal = tradeScenarioTotal;
    }

    public long getTradeScenarioPass() {
        return tradeScenarioPass;
    }

    public void setTradeScenarioPass(long tradeScenarioPass) {
        this.tradeScenarioPass = tradeScenarioPass;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public long getTradeScenarioUnregulated() {
        return tradeScenarioUnregulated;
    }

    public void setTradeScenarioUnregulated(long tradeScenarioUnregulated) {
        this.tradeScenarioUnregulated = tradeScenarioUnregulated;
    }

    public String getTradeScenarioPassMess() {
        return tradeScenarioPassMess;
    }

    public void setTradeScenarioPassMess(String tradeScenarioPassMess) {
        this.tradeScenarioPassMess = tradeScenarioPassMess;
    }

    public long getTradeScenarioFailed() {
        return tradeScenarioFailed;
    }

    public void setTradeScenarioFailed(long tradeScenarioFailed) {
        this.tradeScenarioFailed = tradeScenarioFailed;
    }

    public String getTradeScenarioFailedMess() {
        return tradeScenarioFailedMess;
    }

    public void setTradeScenarioFailedMess(String tradeScenarioFailedMess) {
        this.tradeScenarioFailedMess = tradeScenarioFailedMess;
    }
}
