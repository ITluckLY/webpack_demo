package com.dcfs.esb.ftp.key;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyManager2 {

  private static final Logger log = LoggerFactory.getLogger(KeyManager2.class);
  private static KeyManager2 instance;//在静态常量区定义引用变量（指针）
  private static final Object lock = new Object();
  public static final String ALL = "all";
  public static final String SYSTEM_P= "p";
  public static final String SYSTEM_S= "s";
  public static final String SP = "&";
  private Map<String, Key> keyMap;
  private Map<String, Key> flows = new HashMap<>();
  private KeyManager2(){
    init();
  }

  public static KeyManager2 getInstance(){
    if (instance !=null) return instance;//所指对象不为空，返回
    synchronized (lock){
      if (instance == null) instance = new KeyManager2();//未创建单例模式，在堆内存中创建对象
      return instance;
    }
  }

  public static void reload() throws FtpException {//清空之前的堆内存，重新创建单例模式
    synchronized (lock) {
      CachedCfgDoc.getInstance().reloadKey();
      instance = new KeyManager2();
    }
  }

  /*加载秘钥配置文件，并解析文件*/
  private void init(){ //NOSONAR
    Map<String, Key> tmpKeys = new HashMap<>();
    try {
      Document doc = CachedCfgDoc.getInstance().reloadKey();
      List list = doc.selectNodes("/keys/key");
      for (Object aList : list) {
        Element element = (Element) aList;
        String user = element.attributeValue("user");
        if (StringUtils.isEmpty(user)) continue;
        addFlow(tmpKeys, user, createUserBean(element));

        Map<String, Key> temp = this.flows;
        this.flows = tmpKeys;
        temp.clear();
      }
    } catch (Exception e) {
      log.error("加载秘钥文件错误", e);
    }
  }
  public void addFlow(Map<String, Key> keys, String keyName, Key key) {
    if (key == null) return;
    keys.put(keyName, key);
  }
  public Key createUserBean(Element e) {
    Key sfb = new Key();
    sfb.setUser(e.attributeValue("user"));
    sfb.setType(e.attributeValue("type"));
    sfb.setContent(e.attributeValue("content"));
    return sfb;
  }


  /**
   * 查找路由规则,优先查找user+tran_code相匹配的路由规则,
   * 如未查找到,则查找于all+tran_code相匹配的路由规则,
   * 如还未查找到,则查找于user+all相匹配的路由规则,
   *
   * @param user
   * @param type
   * @return
   */
  public Key serachKey(final String user, final String type) {
    String user2 = user;
    String type2 = type;
    if (user2 == null || user2.isEmpty())
      return null;
    if (type2 == null || type2.isEmpty())
      return null;;
    String key = user2 + SP + type2;
    if (keyMap.containsKey(key))
      return keyMap.get(key);
    return null;
  }
}

