package com.dcfs.esb.ftp.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Created by mocg on 2016/6/29.
 */
public class JSONUtil {
    private JSONUtil() {
    }

    public static String fromObject(Object object) {
        if (object == null) return null;
        if (object instanceof List || object instanceof Array) {
            return JSONArray.fromObject(object).toString();
        } else if (object instanceof Map) {
            return JSONObject.fromObject(object).toString();
        }
        return JSONObject.fromObject(object).toString();
    }
}
