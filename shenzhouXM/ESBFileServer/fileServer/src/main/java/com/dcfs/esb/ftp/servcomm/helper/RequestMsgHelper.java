package com.dcfs.esb.ftp.servcomm.helper;

import com.dcfs.esb.ftp.utils.GsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mocg on 2016/10/11.
 */
public class RequestMsgHelper {
    private RequestMsgHelper() {
    }

    public static String generate(String target, String operateType, Map<String, ?> data) {
        Map<String, Object> map = new HashMap<>();
        map.put("target", target);
        map.put("operateType", operateType);
        map.put("data", data);
        return GsonUtil.toJson(map);
    }
}
