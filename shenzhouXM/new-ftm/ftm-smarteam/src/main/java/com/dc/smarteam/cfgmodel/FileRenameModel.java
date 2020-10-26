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
public class FileRenameModel extends BaseModel {
    @XStreamImplicit
    private List<FileRename> fileRenames;

    public List<FileRename> getFileRenames() {
        return fileRenames;
    }

    public void setFileRenames(List<FileRename> fileRenames) {
        this.fileRenames = fileRenames;
    }

    public void init() {
        if (fileRenames == null) fileRenames = new ArrayList<>();
    }

    @XStreamAlias("rule")
    public static class FileRename {
        @XStreamAsAttribute
        private String id;
        @XStreamAsAttribute
        private String type;
        @XStreamAsAttribute
        private String path;
        @XStreamAsAttribute
        private String sysname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSysname() {
            return sysname;
        }

        public void setSysname(String sysname) {
            this.sysname = sysname;
        }
    }
}
