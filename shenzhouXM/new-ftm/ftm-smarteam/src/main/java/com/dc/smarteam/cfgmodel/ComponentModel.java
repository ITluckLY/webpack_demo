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
public class ComponentModel extends BaseModel {
    @XStreamImplicit
    private List<Service> services;

    public void init() {
        if (services == null) services = new ArrayList<>();
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @XStreamAlias("service")
    public static class Service {
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String describe;
        private String implement;
        private List<Param> params;
        private String comment;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getImplement() {
            return implement;
        }

        public void setImplement(String implement) {
            this.implement = implement;
        }

        public List<Param> getParams() {
            return params;
        }

        public void setParams(List<Param> params) {
            this.params = params;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    @XStreamAlias("param")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"value"})
    public static class Param {
        @XStreamAsAttribute
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
