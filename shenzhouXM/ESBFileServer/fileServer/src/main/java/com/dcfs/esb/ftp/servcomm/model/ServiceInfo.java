package com.dcfs.esb.ftp.servcomm.model;

/**
 * ref services_info.xml
 * Created by mocg on 2016/9/19.
 */
public class ServiceInfo {

    private String sysName;
    private String trancode;
    private String flow;
    private String psFlow;
    private String discribe;
    private boolean rename;
    private long filePeriod;
    private int priority;
    private int size;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
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

    public String getDiscribe() {
        return discribe;
    }

    public void setDiscribe(String discribe) {
        this.discribe = discribe;
    }

    public boolean isRename() {
        return rename;
    }

    public void setRename(boolean rename) {
        this.rename = rename;
    }

    public long getFilePeriod() {
        return filePeriod;
    }

    public void setFilePeriod(long filePeriod) {
        this.filePeriod = filePeriod;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getSize() {
        return size;
    }
    public String getPsFlow() {
        return psFlow;
    }

    public void setPsFlow(String psFlow) {
        this.psFlow = psFlow;
    }
    public void setSize(int size) {
        this.size = size;
    }
}
