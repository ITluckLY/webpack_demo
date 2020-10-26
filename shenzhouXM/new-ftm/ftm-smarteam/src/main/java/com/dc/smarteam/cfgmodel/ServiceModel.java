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
@XStreamAlias("services")
public class ServiceModel extends BaseModel {
    @XStreamImplicit
    private List<Service> services;

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void init() {
        if (services == null) services = new ArrayList<>();
    }

    @XStreamAlias("service")
    public static class Service {
        @XStreamAsAttribute
        private String sysname;
        @XStreamAsAttribute
        private String trancode;
        @XStreamAsAttribute
        private String flow;
        @XStreamAsAttribute
        private String describe;
        @XStreamAsAttribute
        private String rename;
        @XStreamAsAttribute
        private String filePeriod;
        @XStreamAsAttribute
        private String priority;
        @XStreamAsAttribute
        private String size;
        @XStreamAsAttribute
        private String cross;
        private PutAuth putAuth;
        private GetAuth getAuth;

        public String getSysname() {
            return sysname;
        }

        public void setSysname(String sysname) {
            this.sysname = sysname;
        }

        public String getTrancode() {
            return trancode;
        }

        public void setTrancode(String trancode) {
            this.trancode = trancode;
        }

        public String getFlow() {
            return flow;
        }

        public void setFlow(String flow) {
            this.flow = flow;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getRename() {
            return rename;
        }

        public void setRename(String rename) {
            this.rename = rename;
        }

        public String getFilePeriod() {
            return filePeriod;
        }

        public void setFilePeriod(String filePeriod) {
            this.filePeriod = filePeriod;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public PutAuth getPutAuth() {
            return putAuth;
        }

        public void setPutAuth(PutAuth putAuth) {
            this.putAuth = putAuth;
        }

        public GetAuth getGetAuth() {
            return getAuth;
        }

        public void setGetAuth(GetAuth getAuth) {
            this.getAuth = getAuth;
        }

        public String getCross() {
            return cross;
        }

        public void setCross(String cross) {
            this.cross = cross;
        }
    }

    @XStreamAlias("putAuth")
    public static class PutAuth {
        @XStreamImplicit
        private List<AuthUser> users;

        public List<AuthUser> getUsers() {
            return users;
        }

        public void setUsers(List<AuthUser> users) {
            this.users = users;
        }
    }

    @XStreamAlias("getAuth")
    public static class GetAuth {
        @XStreamImplicit
        private List<AuthUser> users;

        public List<AuthUser> getUsers() {
            return users;
        }

        public void setUsers(List<AuthUser> users) {
            this.users = users;
        }
    }

    @XStreamAlias("user")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"user"})
    public static class AuthUser {
        private String user;
        @XStreamAsAttribute
        private String directoy;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getDirectoy() {
            return directoy;
        }

        public void setDirectoy(String directoy) {
            this.directoy = directoy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AuthUser authUser = (AuthUser) o;

            if (user != null ? !user.equals(authUser.user) : authUser.user != null) return false;
            return directoy != null ? directoy.equals(authUser.directoy) : authUser.directoy == null;
        }

        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + (directoy != null ? directoy.hashCode() : 0);
            return result;
        }
    }
}
