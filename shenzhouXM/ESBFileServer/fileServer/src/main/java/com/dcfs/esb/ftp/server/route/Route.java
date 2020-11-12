package com.dcfs.esb.ftp.server.route;

/**
 * 路由配置表
 *
 * @author zhuliang
 */
public class Route {
    private String uid;
    private String tranCode;
    private String type;
    private String mode;
    private String[] destination;

    public Route(String uid, String tranCode, String type, String mode, String[] destination) {
        this.uid = uid;
        this.tranCode = tranCode;
        this.type = type;
        this.mode = mode;
        this.destination = destination;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String[] getDestination() {
        return destination;
    }

    public void setDestination(String[] destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("uid:");
        sb.append(uid);
        sb.append(",tran_code:");
        sb.append(tranCode);
        sb.append(",type:");
        sb.append(type);
        sb.append(",mode:");
        sb.append(mode);
        sb.append(",destination:");
        sb.append(getArrayString(destination));
        sb.append(")");
        return sb.toString();
    }

    public String getArrayString(Object[] objs) {
        StringBuilder sb = new StringBuilder();
        if (objs == null) {
            return sb.toString();
        }
        sb.append("[");
        for (Object o : objs) {
            sb.append(o.toString()).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}
