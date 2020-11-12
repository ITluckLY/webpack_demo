package com.dcfs.esb.ftp.server.auth;

/**
 * 用户信息类
 */
public class AuthInfo {
    private String uid;
    private String passWord;
    private String pwdMd5;
    private String synKey;

    /**
     * 获取用户的名称
     *
     * @return 用户名称
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置用户的名称
     *
     * @param uid 用户名称
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取用户的密码
     *
     * @return 用户密码
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * 设置用户的密码
     *
     * @param passWord 用户密码
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * 获取用户的同步密钥
     *
     * @return 同步的密钥
     */
    public String getSynKey() {
        return synKey;
    }

    /**
     * 设置用户的同步密钥
     *
     * @param synKey 同步的密钥
     */
    public void setSynKey(String synKey) {
        this.synKey = synKey;
    }

    /**
     * 验证用户名的密码是否正确
     *
     * @param passwd 用户的密码
     * @return 验证结果 true - 成功 false - 失败
     */
    public boolean doAuth(String passwd) {
        return passwd != null && passwd.equals(this.passWord);
    }

    public boolean doAuthByMd5(String passwd) {
        return passwd != null && passwd.equals(this.pwdMd5);
    }


    public String getPwdMd5() {
        return pwdMd5;
    }

    public void setPwdMd5(String pwdMd5) {
        this.pwdMd5 = pwdMd5;
    }
}
