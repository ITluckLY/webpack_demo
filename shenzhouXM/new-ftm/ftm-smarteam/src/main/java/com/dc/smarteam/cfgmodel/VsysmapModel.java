package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mocg on 2017/4/13.
 */
@XStreamAlias("root")
public class VsysmapModel extends BaseModel {
    @XStreamImplicit
    private List<Map> maps;

    public List<Map> getMaps() {
        return maps;
    }

    public void setMaps(List<Map> maps) {
        this.maps = maps;
    }

    public void init() {
        if (maps == null) maps = new ArrayList<>();
    }

    @XStreamAlias("map")
    public static class Map {
        @XStreamAsAttribute
        @XStreamAlias("key")
        private String source;
        @XStreamAsAttribute
        @XStreamAlias("val")
        private String dest;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getDest() {
            return dest;
        }

        public void setDest(String dest) {
            this.dest = dest;
        }
    }
}
