package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("root")
public class FileCleanModel extends BaseModel {
    @XStreamImplicit
    private List<FileClean> fileCleans = new ArrayList<>();

    public List<FileClean> getFileCleans() {
        return fileCleans;
    }

    public void setFileCleans(List<FileClean> fileCleans) {
        this.fileCleans = fileCleans;
    }

    public void init() {
        if (fileCleans == null) fileCleans = new ArrayList<>();
    }

    @XStreamAlias("fileClean")
    public static class FileClean {
        @XStreamAsAttribute
        private String id;
        @XStreamAsAttribute
        private String srcPath;
        @XStreamAsAttribute
        private String keepTime;
        @XStreamAsAttribute
        private String isBackup;
        @XStreamAsAttribute
        private String backupPath;
        @XStreamAsAttribute
        private String desc;
        @XStreamAsAttribute
        private String state;
        @XStreamAsAttribute
        private String system;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSrcPath() {
            return srcPath;
        }

        public void setSrcPath(String srcPath) {
            this.srcPath = srcPath;
        }

        public String getKeepTime() {
            return keepTime;
        }

        public void setKeepTime(String keepTime) {
            this.keepTime = keepTime;
        }

        public String getIsBackup() {
            return isBackup;
        }

        public void setIsBackup(String isBackup) {
            this.isBackup = isBackup;
        }

        public String getBackupPath() {
            return backupPath;
        }

        public void setBackupPath(String backupPath) {
            this.backupPath = backupPath;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }
    }
}
