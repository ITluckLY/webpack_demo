package com.dcfs.esb.ftp.impls.context;

import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.interfases.context.ContextBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseCachedContext implements CachedContext {
    private Map<String, Object> values;
    private ContextBean cxtBean;
    private boolean canntInvokeNextFlow = false;

    public BaseCachedContext() {
        values = new HashMap<>();
        cxtBean = new ContextBean();
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Object put(String key, Object value) {
        return values.put(key, value);
    }

    public CachedContext copy() {
        CachedContext copy = new BaseCachedContext();
        Set<Map.Entry<String, Object>> entrys = values.entrySet();
        for (Map.Entry<String, Object> e : entrys) {
            copy.put(e.getKey(), e.getValue());
        }
        return copy;
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public Object remove(String key) {
        return values.remove(key);
    }

    public void clear() {
        values.clear();
        //cxtBean = null;//NOSONAR
    }

    @Override
    public ContextBean getCxtBean() {
        return cxtBean;
    }

    @Override
    public boolean isCanntInvokeNextFlow() {
        return canntInvokeNextFlow;
    }

    @Override
    public void setCanntInvokeNextFlow(boolean canntInvokeNextFlow) {
        this.canntInvokeNextFlow = canntInvokeNextFlow;
    }

}
