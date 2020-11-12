package com.dcfs.esb.ftp.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * Created by mocg on 2016/7/14.
 */
public class GsonUtil {

    private GsonUtil() {
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(str, type);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(str, type);
    }

    /**
     * 支持内部泛型
     * TypeToken<ResultDto<Date>> typeToken=new TypeToken<ResultDto<Date>>(){};
     *
     * @param str
     * @param typeToken
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String str, TypeToken<T> typeToken) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(str, typeToken.getType());
    }

    /**
     * 将指定子串转换成JSONObject
     *
     * @param str
     * @return
     */
    public static JsonObject toJsonObj(String str) {
        if (StringUtils.isEmpty(str)) return new JsonObject();
        JsonParser parser = new JsonParser();
        return parser.parse(str).getAsJsonObject();
    }

    /**
     * 将指定子串转换成JsonArray
     *
     * @param str
     * @return
     */
    public static JsonArray toJsonArray(String str) {
        if (StringUtils.isEmpty(str)) return new JsonArray();
        JsonParser parser = new JsonParser();
        return parser.parse(str).getAsJsonArray();
    }

    /**
     * 将指定子串转换成JsonElement
     *
     * @param str
     * @return
     */
    public static JsonElement toJsonElement(String str) {
        if (StringUtils.isEmpty(str)) return JsonNull.INSTANCE;
        JsonParser parser = new JsonParser();
        return parser.parse(str);
    }

    public static String getAsStringByNullSafe(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element == null || element.isJsonNull()) return null;
        return element.getAsString();
    }
}
