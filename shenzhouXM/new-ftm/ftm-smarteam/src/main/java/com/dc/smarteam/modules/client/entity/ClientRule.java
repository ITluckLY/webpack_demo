package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.persistence.LongDataEntity;

public class ClientRule extends LongDataEntity<ClientSyn> {
    private String user;
    private String tranCode;
    private String type;
    private String mode;
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

    @Override
    public String toString() {
        return "ClientRule{" +
                "user='" + user + '\'' +
                ", tranCode='" + tranCode + '\'' +
                ", type='" + type + '\'' +
                ", mode='" + mode + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
