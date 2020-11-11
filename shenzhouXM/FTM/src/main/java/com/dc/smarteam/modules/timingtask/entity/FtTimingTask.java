/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.timingtask.entity;

import com.dc.smarteam.aspectCfg.cfgOperate.CfgData;
import com.dc.smarteam.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 定时任务Entity
 *
 * @author liwang
 * @version 2016-01-11
 */
public class FtTimingTask extends DataEntity<FtTimingTask> implements CfgData{

    public static final String STOPPING = "0";
    public static final String RUNNING = "1";

    private static final long serialVersionUID = 1L;
    private String seq;            // seq
    private String nodeNameTemp;      //nodeNameTemp
    private String timeExp;            // time_exp
    private String flowId;            // flow_id
    private String state;            // state
    private String params;            // params
    private String description;            //description
    private String count;
    private String taskName;

    //------------------------------------------------------
    private String ftDay;
    private String ftTime;
    private String ftMinu;
    //------------------------------------------------------


    public FtTimingTask() {
        super();
    }

    public FtTimingTask(String id) {
        super(id);
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getFtDay() {
        return ftDay;
    }

    public void setFtDay(String ftDay) {
        this.ftDay = ftDay;
    }

    public String getFtTime() {
        return ftTime;
    }

    public void setFtTime(String ftTime) {
        this.ftTime = ftTime;
    }

    public String getFtMinu() {
        return ftMinu;
    }

    public void setFtMinu(String ftMinu) {
        this.ftMinu = ftMinu;
    }

    public String getNodeNameTemp() {
        return nodeNameTemp;
    }

    public void setNodeNameTemp(String nodeNameTemp) {
        this.nodeNameTemp = nodeNameTemp;
    }

    @Length(min = 0, max = 256, message = "seq长度必须介于 0 和 256 之间")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Length(min = 0, max = 256, message = "seq长度必须介于 0 和 256 之间")
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @Length(min = 0, max = 256, message = "time_exp长度必须介于 0 和 256 之间")
    public String getTimeExp() {
        return timeExp;
    }

    public void setTimeExp(String timeExp) {
        this.timeExp = timeExp;
    }

    @Length(min = 0, max = 256, message = "flow_id长度必须介于 0 和 256 之间")
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    @Length(min = 0, max = 256, message = "state长度必须介于 0 和 256 之间")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String getParamName() {
        return this.getSeq();
    }
}