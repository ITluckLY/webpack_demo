package com.dcfs.esb.ftp.helper;

import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.Cfg;

/**
 * Created by mocg on 2016/7/28.
 */
public class ContextHelper {

    @SuppressWarnings("unchecked")
    public static <T> T getDefault(CachedContext context, String key, T t) {
        Object o = context.get(key);
        if (o == null) {
            context.put(key, t);
            return t;
        }
        return (T) o;
    }

    public static <T> T getDefaultByCommCfgCxt(String key, T t) {
        return getDefault(Cfg.getCommCfgContext(), key, t);
    }

    public static Object getByCommCfgCxt(String key) {
        return Cfg.getCommCfgContext().get(key);
    }

    public static Object putByCommCfgCxt(String key, Object value) {
        return Cfg.getCommCfgContext().put(key, value);
    }

    private ContextHelper() {
    }
}
