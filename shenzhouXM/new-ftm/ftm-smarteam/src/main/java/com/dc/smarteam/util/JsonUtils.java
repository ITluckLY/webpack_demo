package com.dc.smarteam.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonUtils {
    public static <T> T JsonStrToObj (String value,Class<T> type){
        JSONObject jout  = JSON.parseObject(value);
        T obj = (T) JSON.toJavaObject(jout, type);
        return obj;
    }

    public static String ObjToJsonStr(Object obj){
        JSONObject jout = (JSONObject) JSON.toJSON(obj);
        return jout.toJSONString();
    }

    public static Map EntityToMap(Object obj){
        if(null==obj){
            return new HashMap();
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
        Set<Map.Entry<String,Object>> entrySet = jsonObject.entrySet();
        Map<String,Object> map = new HashMap<>();
        for(Map.Entry<String,Object> entry : entrySet){
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }
}
