package com.dcfs.esb.ftp.msggenerator;

/**
 * Created by mocg on 2016/8/29.
 */
public class FileDataParam {
    private String key;
    private String rename;
    private String oldCharset;
    private String newCharset;
    private String cryptogramType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRename() {
        return rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public String getOldCharset() {
        return oldCharset;
    }

    public void setOldCharset(String oldCharset) {
        this.oldCharset = oldCharset;
    }

    public String getNewCharset() {
        return newCharset;
    }

    public void setNewCharset(String newCharset) {
        this.newCharset = newCharset;
    }

    public String getCryptogramType() {
        return cryptogramType;
    }

    public void setCryptogramType(String cryptogramType) {
        this.cryptogramType = cryptogramType;
    }
}
