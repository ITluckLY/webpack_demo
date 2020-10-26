package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("rules")
public class RouteModel extends BaseModel {
    @XStreamImplicit
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void init() {
        if (routes == null) routes = new ArrayList<>();
    }

    @XStreamAlias("rule")
    public static class Route {
        @XStreamAsAttribute
        private String user;
        @XStreamAsAttribute
        @XStreamAlias("tran_code")
        private String tranCode;
        @XStreamAsAttribute
        private String type;
        @XStreamAsAttribute
        private String mode;
        @XStreamAsAttribute
        private String destination;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getTranCode() {
            return tranCode;
        }

        public void setTranCode(String tranCode) {
            this.tranCode = tranCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }
    }
}
