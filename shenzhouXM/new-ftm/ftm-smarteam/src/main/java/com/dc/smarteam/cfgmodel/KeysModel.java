package com.dc.smarteam.cfgmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaona
 * 2019/12/05
 * 公私钥model
 */
@XStreamAlias("keys")
public class KeysModel extends BaseModel {
    @XStreamImplicit
    private List<Key> keys;

    public void init() {
        if (keys == null) keys = new ArrayList<>();
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    @XStreamAlias("key")
    public static class Key{
        /*user:用户 type:p-公钥，s-私钥 content:公私钥内容*/
        @XStreamAsAttribute
        private String user;
        @XStreamAsAttribute
        private String type;
        @XStreamAsAttribute
        private String content;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
