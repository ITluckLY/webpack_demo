package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchuang on 2018/6/13.
 */

@XStreamAlias("clients")
public class ClientStatusModel extends BaseModel {

    @XStreamImplicit
    private List<Client> clients;

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Client> getClients() {
        return clients;
    }
    @Override
    public void init(){
        if(clients==null){
            clients = new ArrayList<>();
        }
    }
    @XStreamAlias("client")
    public static class Client{

        @XStreamAsAttribute
        private String id;
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String mode;
        @XStreamAsAttribute
        private String type;
        @XStreamAsAttribute
        private String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }



}
