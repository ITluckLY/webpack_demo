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
@XStreamAlias("nodes")
public class NodesModel extends BaseModel {
    @XStreamImplicit
    private List<Node> nodes;

    public void init() {
        if (nodes == null) nodes = new ArrayList<>();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    @XStreamAlias("node")
    public static class Node {
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String type;
        @XStreamAsAttribute
        private String ip;
        @XStreamAsAttribute
        @XStreamAlias("cmd_port")
        private String cmdPort;
        @XStreamAsAttribute
        @XStreamAlias("ftp_serv_port")
        private String servPort;
        @XStreamAsAttribute
        @XStreamAlias("receive_port")
        private String receivePort;
        @XStreamAsAttribute
        @XStreamAlias("ftp_manage_port")
        private String managePort;
        @XStreamAsAttribute
        private String state;
        @XStreamAsAttribute
        private String isolState;
        @XStreamAsAttribute
        private String model;
        @XStreamAsAttribute
        private System system;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCmdPort() {
            return cmdPort;
        }

        public void setCmdPort(String cmdPort) {
            this.cmdPort = cmdPort;
        }

        public String getServPort() {
            return servPort;
        }

        public void setServPort(String servPort) {
            this.servPort = servPort;
        }

        public String getReceivePort() {
            return receivePort;
        }

        public void setReceivePort(String receivePort) {
            this.receivePort = receivePort;
        }

        public String getManagePort() {
            return managePort;
        }

        public void setManagePort(String managePort) {
            this.managePort = managePort;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getIsolState() {
            return isolState;
        }

        public void setIsolState(String isolState) {
            this.isolState = isolState;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public System getSystem() {
            return system;
        }

        public void setSystem(System system) {
            this.system = system;
        }
    }

    @XStreamAlias("system")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"name"})
    public static class System {
        private String name;
        @XStreamAsAttribute
        private String storeModel;
        @XStreamAsAttribute
        private String switchModel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStoreModel() {
            return storeModel;
        }

        public void setStoreModel(String storeModel) {
            this.storeModel = storeModel;
        }

        public String getSwitchModel() {
            return switchModel;
        }

        public void setSwitchModel(String switchModel) {
            this.switchModel = switchModel;
        }
    }

}
