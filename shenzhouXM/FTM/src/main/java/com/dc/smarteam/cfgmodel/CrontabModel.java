package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("schedules")
public class CrontabModel extends BaseModel {
    @XStreamImplicit
    private List<Task> tasks;

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void init() {
        if (tasks == null) tasks = new ArrayList<>();
    }

    @XStreamAlias("task")
    public static class Task {
        @XStreamAsAttribute
        @XStreamAlias("ID")
        private String id;
        @XStreamAsAttribute
        private String taskName;
        @XStreamAsAttribute
        private String description;
        @XStreamAsAttribute
        private String status;
        @XStreamAsAttribute
        private String count;
        @XStreamAsAttribute
        private String nodeName;
        private String timeExp;
        private String mission;
        private String params;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getTimeExp() {
            return timeExp;
        }

        public void setTimeExp(String timeExp) {
            this.timeExp = timeExp;
        }

        public String getMission() {
            return mission;
        }

        public void setMission(String mission) {
            this.mission = mission;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }
    }
}
