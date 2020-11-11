package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("UserRoot")
public class UserModel extends BaseModel {
    @XStreamImplicit
    private List<UserInfo> userInfos;

    public void init() {
        if (userInfos == null) userInfos = new ArrayList<>();
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    @XStreamAlias("UserInfo")
    public static class UserInfo {
        @XStreamAlias("Uid")
        private Uid uid;
        @XStreamAlias("Grant")
        private Grant grant;
        @XStreamAlias("Passwd")
        private String passwd;
        @XStreamImplicit
        private List<IP> ips;
        @XStreamAlias("ClientAddress")
        private String clientAddress;

        public Uid getUid() {
            return uid;
        }

        public void setUid(Uid uid) {
            this.uid = uid;
        }

        public Grant getGrant() {
            return grant;
        }

        public void setGrant(Grant grant) {
            this.grant = grant;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public List<IP> getIps() {
            return ips;
        }

        public void setIps(List<IP> ips) {
            this.ips = ips;
        }

        public String getClientAddress() {
            return clientAddress;
        }

        public void setClientAddress(String clientAddress) {
            this.clientAddress = clientAddress;
        }
    }

    @XStreamAlias("Uid")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"uid"})
    public static class Uid {
        private String uid;
        @XStreamAsAttribute
        private String home;
        @XStreamAsAttribute
        private String describe;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }
    }

    @XStreamAlias("Grant")
    public static class Grant {
        @XStreamAsAttribute
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @XStreamAlias("IP")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"ip"})
    public static class IP {
        private String ip;
        @XStreamAsAttribute
        private String status;
        @XStreamAsAttribute
        @XStreamAlias("IPDescribe")
        private String describe;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }
    }
}
