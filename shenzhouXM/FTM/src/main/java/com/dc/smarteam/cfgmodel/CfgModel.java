package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("properties")
public class CfgModel extends BaseModel {
    @XStreamImplicit
    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @XStreamAlias("entry")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"value"})
    public static class Entry {
        @XStreamAsAttribute
        private String key;
        private String value;
        @XStreamAsAttribute
        private String describe;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }
    }

}
