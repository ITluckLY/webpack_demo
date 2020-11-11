package com.dc.smarteam.modules.ftinterface.enity;



/**
 * Created by Administrator on 2019/12/10.
 */
public class Data {
    //user
    private String name;          // 名称
    private String systemName;    // 系统名称
    private String des;           // 中文描述
    private String userDir;       // 用户目录
    private String permession;    // 目录权限
    private String pwd;           //密码
    private String clientAddress; //客户端地址
    //key
    private String user;
    private String type; //s,p
    private String content;
//上传
    private String trancode;              // 交易码
    private String directoy;
    private String userName;


    private String putorget;

    private String mode;

    //通知上传下载路径
    private String protocol;
    private String ip;
    private String port;
//    private String username1;
    private String password;
    private String uploadPath;
    private String downloadPath;

    private String userid;

    private String serviceid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    public String getServiceurl() {
        return serviceurl;
    }

    public void setServiceurl(String serviceurl) {
        this.serviceurl = serviceurl;
    }

    private String serviceurl;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPutorget() {
        return putorget;
    }

    public void setPutorget(String putorget) {
        this.putorget = putorget;
    }
    public String getTrancode() {
        return trancode;
    }

    public void setTrancode(String trancode) {
        this.trancode = trancode;
    }

    public String getDirectoy() {
        return directoy;
    }

    public void setDirectoy(String directoy) {
        this.directoy = directoy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    public String getPermession() {
        return permession;
    }

    public void setPermession(String permession) {
        this.permession = permession;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }
}
