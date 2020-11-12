package com.dcfs.esb.ftp.server.config;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.server.auth.AuthInfo;
import com.dcfs.esb.ftp.server.auth.UserInfo;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserInfoFactory {
    private static final Logger log = LoggerFactory.getLogger(UserInfoFactory.class);
    private static final Object lock = new Object();
    private static UserInfoFactory instance;
    private Map<String, AuthInfo> authInfoPool = new HashMap<>();
    private Map<String, UserInfo> userInfos = new HashMap<>();

    private UserInfoFactory() throws FtpException {
        init();
    }

    public static UserInfoFactory getInstance() throws FtpException {
        if (instance != null) return instance;
        instance = syncCreateInstance();
        return instance;
    }

    private static synchronized UserInfoFactory syncCreateInstance() throws FtpException {
        if (instance == null) instance = new UserInfoFactory();
        return instance;
    }

    public static void reload() throws FtpException {
        CachedCfgDoc.getInstance().reloadUser();
        synchronized (lock) {
            instance = new UserInfoFactory();
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
        boolean saveFlag = false;
        Document doc = CachedCfgDoc.getInstance().loadUser();
        try {
            // 获取根对象
            Element root = doc.getRootElement();
            Element userInfo;
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                // 获取一条用户记录
                userInfo = (Element) i.next();
                Element u = userInfo.element("Uid");

                // 获取用户名
                String uid = u.getText().trim();
                String home = u.attributeValue("home");
                String sysname = u.attributeValue("sysname");
                String chnName = u.attributeValue("describe");
                //String sysuser = sysname + "_" + uid
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
                if (passwd.startsWith("${3DES}"))//NOSONAR
                    passwd = ScrtUtil.decryptEsb(passwd.substring(7));//NOSONAR
                else
                    saveFlag = true;

                // 创建用户对象
                AuthInfo info = new AuthInfo();
                info.setUid(sysuser);
                info.setPassWord(passwd);

                // 保存到用户池中
                authInfoPool.put(info.getUid(), info);
            }
        } catch (Exception e) {
            log.error("加载用户信息出错", e);
            throw new FtpException(FtpErrCode.LOAD_CONFIG_FILE_ERROR, e);
        }

        // 判断数据是否需要重新加密，如果需要则进行文件的加密处理
        if (saveFlag) {
            saveFlag = saveWithEncrypt(doc);
            if (saveFlag) log.info("保存用户信息成功");
        }
        log.info("加载用户认证信息成功");

    }

    /**
     * 将用户的密码加密后保存到文件中
     *
     * @return 保存的标志 true - 成功 false - 失败
     */
    private boolean saveWithEncrypt(Document doc) {
        Element root = doc.getRootElement();
        List<Element> passwdList = root.selectNodes("/UserRoot/UserInfo/Passwd");
        for (Element ele : passwdList) {
            String text = ele.getText().trim();
            if (text.startsWith("${3DES}")) continue;
            String passwd = "${3DES}" + ScrtUtil.encryptEsb(text);
            ele.setText(passwd);
        }
        XMLDealTool.xmlWriter(doc, Cfg.getUserCfg());
        return true;
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
}
