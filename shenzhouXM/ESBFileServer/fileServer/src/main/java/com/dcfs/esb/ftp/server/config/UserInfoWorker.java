package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.PasswordHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.server.auth.AuthInfo;
import com.dcfs.esb.ftp.server.auth.UserInfo;
import com.dcfs.esb.ftp.utils.MD5Util;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserInfoWorker {
    private static final Logger log = LoggerFactory.getLogger(UserInfoWorker.class);
    private static final Object lock = new Object();
    private static UserInfoWorker instance;
    private Map<String, AuthInfo> authInfoPool = new HashMap<>();
    private Map<String, UserInfo> userInfos = new HashMap<>();

    private UserInfoWorker() throws FtpException {
        init();
    }

    public static UserInfoWorker getInstance() throws FtpException {
        if (instance != null) return instance;
        instance = syncCreateInstance();
        return instance;
    }

    private static synchronized UserInfoWorker syncCreateInstance() throws FtpException {
        if (instance == null) instance = new UserInfoWorker();
        return instance;
    }

    public static void reload() throws FtpException {
        CachedCfgDoc.getInstance().reloadUser();
        synchronized (lock) {
            instance = new UserInfoWorker();
        }
    }

    public UserInfo getUserInfo(String uid) {
        return userInfos.get(uid);
    }

    public boolean checkIP(String user, String ip) {
        UserInfo ui = userInfos.get(user);
        if (ui == null) return false;
        Map<String, Boolean> ips = ui.getList();
        Boolean b = ips.get(ip);
        if (b == null) {
            log.warn("{}不在{}的受信任地址列表中!", ip, user);
            return false;
        }
        if (!b) log.warn("{}处于{}禁止地址列表中!", ip, user);
        return b;
    }

    private void init() throws FtpException {
        log.debug("加载用户认证信息...");
        Document doc = CachedCfgDoc.getInstance().loadUser();
        try {
            // 获取根对象
            Element root = doc.getRootElement();
            Element userInfo = null;
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                // 获取一条用户记录
                userInfo = (Element) i.next();
                Element u = userInfo.element("Uid");

                // 获取用户名
                String uid = u.getText().trim();
                String home = u.attributeValue("home");
                String sysname = u.attributeValue("sysname");
                String chnName = u.attributeValue("describe");
                //String sysuser = sysname + "_" + uid;//NOSONAR
                String sysuser = uid;
                UserInfo ui = new UserInfo(uid, home, sysname, chnName);
                List ips = userInfo.elements("IP");
                for (Object ip1 : ips) {
                    Element e = (Element) ip1;
                    String ip = e.getText();
                    String st = e.attributeValue("status");
                    ui.addIP(ip, "1".equals(st) || "allowed".equals(st));
                }
                userInfos.put(sysuser, ui);
                // 获取用户密码，如果是${3DES}开头，则解密，否则设置加密的标志
                String passwd = userInfo.elementText("Passwd");
                String des3Flag = "${3DES}";
                if (passwd.startsWith(des3Flag)) passwd = ScrtUtil.decryptEsb(passwd.substring(des3Flag.length()));

                // 创建用户对象
                AuthInfo info = new AuthInfo();
                info.setUid(sysuser);
                info.setPassWord(passwd);
                info.setPwdMd5(MD5Util.md5(passwd));

                // 保存到用户池中
                authInfoPool.put(info.getUid(), info);
            }
        } catch (Exception e) {
            log.error("加载用户信息出错", e);
            throw new FtpException(FtpErrCode.LOAD_CONFIG_FILE_ERROR, e);
        }
        log.debug("加载用户认证信息成功");
    }

    /**
     * 进行用户的权限认证
     *
     * @param head 文件消息对象，包含用户名
     * @return 验证结果 true - 成功 false - 失败
     */
    public boolean doAuth(FileMsgBean head) {
        String uid = head.getUid();
        AuthInfo info = this.authInfoPool.get(uid);
        return info != null && info.doAuth(head.getPasswd());
    }

    public boolean doAuthByMd5(FileMsgBean head) {
        String uid = head.getUid();
        AuthInfo info = this.authInfoPool.get(uid);
        return info != null && info.doAuthByMd5(head.getPasswd());
    }

    /**
     * 用户认证，成功则返回pwdmd5，否则返回null
     *
     * @param head
     * @param seq
     * @return
     */
    public String doAuthByMd5AndSeq(FileMsgBean head, String seq) {
        String uid = head.getUid();
        AuthInfo info = this.authInfoPool.get(uid);
        if (info == null) return null;
        if (PasswordHelper.convert(info.getPwdMd5(), seq).equals(head.getPasswd())) {
            return info.getPwdMd5();
        }
        return null;
    }

    public String findPwdMd5(String uid) {
        AuthInfo info = this.authInfoPool.get(uid);
        if (info == null) return null;
        return info.getPwdMd5();
    }
}
