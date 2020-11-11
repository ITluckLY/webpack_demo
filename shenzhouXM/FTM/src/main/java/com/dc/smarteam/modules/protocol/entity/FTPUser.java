package com.dc.smarteam.modules.protocol.entity;

/**
 * Created by huangzbb on 2017/6/7.
 */
public class FTPUser {
    private String name;
    private String password;
    private int maxIdleTimeSec;
    private String homeDir;
    private boolean isEnabled;
    private boolean isWriteable;
    private int maxDownloadRate;
    private int maxUploadRate;
    private int maxConcurrentLogins;
    private int maxConcurrentLoginsPerIP;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxIdleTimeSec() {
        return maxIdleTimeSec;
    }

    public void setMaxIdleTimeSec(int maxIdleTimeSec) {
        this.maxIdleTimeSec = maxIdleTimeSec;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isWriteable() {
        return isWriteable;
    }

    public void setWriteable(boolean writeable) {
        isWriteable = writeable;
    }

    public int getMaxDownloadRate() {
        return maxDownloadRate;
    }

    public void setMaxDownloadRate(int maxDownloadRate) {
        this.maxDownloadRate = maxDownloadRate;
    }

    public int getMaxUploadRate() {
        return maxUploadRate;
    }

    public void setMaxUploadRate(int maxUploadRate) {
        this.maxUploadRate = maxUploadRate;
    }

    public int getMaxConcurrentLogins() {
        return maxConcurrentLogins;
    }

    public void setMaxConcurrentLogins(int maxConcurrentLogins) {
        this.maxConcurrentLogins = maxConcurrentLogins;
    }

    public int getMaxConcurrentLoginsPerIP() {
        return maxConcurrentLoginsPerIP;
    }

    public void setMaxConcurrentLoginsPerIP(int maxConcurrentLoginsPerIP) {
        this.maxConcurrentLoginsPerIP = maxConcurrentLoginsPerIP;
    }
}
