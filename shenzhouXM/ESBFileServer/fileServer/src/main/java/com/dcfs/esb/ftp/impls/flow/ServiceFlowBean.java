package com.dcfs.esb.ftp.impls.flow;

import java.util.Properties;

public class ServiceFlowBean {

    private String flowName;
    private String[] baseService;
    private Properties flowServiceProperties;
    private String flowType;
    private String describe;

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Properties getFlowServiceProperties() {
        return flowServiceProperties;
    }

    public void setFlowServiceProperties(Properties flowServiceProperties) {
        this.flowServiceProperties = flowServiceProperties;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String[] getBaseService() {
        return baseService;
    }

    public void setBaseService(String[] baseService) {
        this.baseService = baseService;
    }

}
