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
@XStreamAlias("flows")
public class FlowModel extends BaseModel {
    @XStreamImplicit
    private List<Flow> flows;

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }

    public void init() {
        if (flows == null) flows = new ArrayList<>();
    }

    @XStreamAlias("flow")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"sysname"})
    public static class Flow {
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String components;
        @XStreamAsAttribute
        private String describe;
        private String sysname;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getComponents() {
            return components;
        }

        public void setComponents(String components) {
            this.components = components;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getSysname() {
            return sysname;
        }

        public void setSysname(String sysname) {
            this.sysname = sysname;
        }
    }
}
