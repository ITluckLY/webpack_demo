package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("systems")
public class SystemModel extends BaseModel {
    @XStreamImplicit
    private List<System> systems;

    public void init() {
        if (systems == null) systems = new ArrayList<>();
    }

    public List<System> getSystems() {
        return systems;
    }

    public void setSystems(List<System> systems) {
        this.systems = systems;
    }

    @XStreamAlias("system")
    public static class System {
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String protocol;
        private String ip;
        private String port;
        private String username;
        private String password;
        private String uploadPath;
        private String downloadPath;

        public String getUploadPath() {
            return uploadPath;
        }

        public void setUploadPath(String uploadPath) {
            this.uploadPath = uploadPath;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public void setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

}
