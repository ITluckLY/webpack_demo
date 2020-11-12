package com.dcfs.esb.ftp.server.route;

import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteManager {
    private static final Logger log = LoggerFactory.getLogger(RouteManager.class);
    private static final Object lock = new Object();
    public static final String SYSTEM = "s";
    public static final String SP = "&";
    public static final String ALL = "all";
    private static RouteManager instance;
    private Map<String, Route> routes;

    private RouteManager() {
        init();
    }

    public static RouteManager getInstance() {
        if (instance != null) return instance;
        synchronized (lock) {
            if (instance == null) instance = new RouteManager();
        }
        return instance;
    }

    public static void reload() {
        synchronized (lock) {
            instance = null;
            instance = new RouteManager();
        }
    }

    /**
     * 查找路由规则,优先查找user+tran_code相匹配的路由规则,
     * 如未查找到,则查找于all+tran_code相匹配的路由规则,
     * 如还未查找到,则查找于user+all相匹配的路由规则,
     *
     * @param user
     * @param tranCode
     * @return
     */
    public Route serachRoute(final String user, final String tranCode) {
        String user2 = user;
        String tranCode2 = tranCode;
        if (user2 == null || user2.isEmpty()) user2 = ALL;
        if (tranCode2 == null || tranCode2.isEmpty()) tranCode2 = ALL;
        String key = user2 + SP + tranCode2;
        if (routes.containsKey(key)) return routes.get(key);
        key = ALL + SP + tranCode2;
        if (routes.containsKey(key)) return routes.get(key);
        key = user2 + SP + ALL;
        if (routes.containsKey(key)) return routes.get(key);
        return null;
    }


    @SuppressWarnings("unchecked")
    private void init() { //NOSONAR
        Map<String, Route> tmpRoutes = new HashMap<>();
        try {
            Document doc = CachedCfgDoc.getInstance().reloadRoute();
            List l = doc.selectNodes("/rules/rule");
            for (Object aL : l) {
                Element e = (Element) aL;
                String user = e.attributeValue("user");
                if (user == null || user.trim().isEmpty())
                    user = ALL;
                String tranCode = e.attributeValue("tran_code");
                if (tranCode == null || tranCode.trim().isEmpty())
                    tranCode = ALL;
                String type = e.attributeValue("type");
                if (type == null || type.trim().isEmpty())
                    type = ALL;
                else {
                    if (!type.equalsIgnoreCase(SYSTEM)) {
                        log.error("未知的路由规则类型:{}", type);
                        continue;
                    }
                }
                String mode = e.attributeValue("mode");
                if (mode == null || mode.trim().isEmpty())
                    mode = "asyn";
                String dest = e.attributeValue("destination");
                String[] destination = null;
                if (dest != null && !dest.trim().isEmpty()) {
                    destination = dest.split(",");
                }
                Route r = new Route(user, tranCode.toLowerCase(), type, mode, destination);
                String key = user + SP + tranCode;
                if (tmpRoutes.containsKey(key))
                    log.warn("存在重复路由:{}", key);
                tmpRoutes.put(key, r);
                if (log.isInfoEnabled()) log.info("添加路由配置{}", r.toString());
            }
            this.routes = tmpRoutes;
        } catch (Exception e) {
            log.error("加载路由位置文件错误", e);
        }
    }
}
