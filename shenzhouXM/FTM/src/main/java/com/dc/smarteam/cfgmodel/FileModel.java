package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("FileRoot")
public class FileModel extends BaseModel {
    @XStreamImplicit
    private List<BaseFile> baseFiles = new ArrayList<>();

    public List<BaseFile> getBaseFiles() {
        return baseFiles;
    }

    public void setBaseFiles(List<BaseFile> baseFiles) {
        this.baseFiles = baseFiles;
    }

    public void init() {
        if (baseFiles == null) baseFiles = new ArrayList<>();
    }

    @XStreamAlias("BaseFile")
    public static class BaseFile {
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String path;
        @XStreamImplicit
        private List<Grant> grants;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<Grant> getGrants() {
            return grants;
        }

        public void setGrants(List<Grant> grants) {
            this.grants = grants;
        }
    }

    @XStreamAlias("grant")
    public static class Grant {
        @XStreamAsAttribute
        private String user;
        @XStreamAsAttribute
        private String type;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
