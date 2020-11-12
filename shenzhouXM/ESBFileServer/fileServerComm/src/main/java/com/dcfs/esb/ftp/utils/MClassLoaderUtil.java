package com.dcfs.esb.ftp.utils;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by mocg on 2016/7/28.
 */
public class MClassLoaderUtil {
    private static String dfltEncName = "UTF-8";
    private static boolean isDecodePath = true;

    public static InputStream getResourceAsStream(String fileClassesPath) {
        return findClassLoader().getResourceAsStream(fileClassesPath);
    }

    public static String getResourceFile(String fileClassesPath) {
        URL url = findClassLoader().getResource(fileClassesPath);
        return getUrlFileAndDecode(url);
    }

    public static String getBaseResourcePath() {
        URL url = findClassLoader().getResource(".");
        if (url == null) url = findClassLoader().getResource(""); // 根据父级的name 获取url
        return getUrlFileAndDecode(url);
    }

    public static String getFilePath(String fileClassesPath) {
        URL url = findClassLoader().getResource(fileClassesPath);
        if (url != null) return getUrlFileAndDecode(url);
        String path = getBaseResourcePath();
        String filePath = new File(path == null ? "" : path, fileClassesPath).getPath();
        return decodePath(filePath);
    }

    public static String getUrlFileAndDecode(URL url) {
        if (url == null) return null;
        return decodePath(url.getFile());
    }

    public static ClassLoader findClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }

    public static String decodePath(String path) {
        if (!isDecodePath) return path;
        if (path == null) return null;
        try {
            return URLDecoder.decode(path, dfltEncName); // 设置路径 的utf8
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncoding:" + dfltEncName, e);//NOSONAR
        }
    }

    private MClassLoaderUtil() {
    }

    public static String getDfltEncName() {
        return dfltEncName;
    }

    public static void setDfltEncName(String dfltEncName) {
        MClassLoaderUtil.dfltEncName = dfltEncName;
    }

    public static boolean isIsDecodePath() {
        return isDecodePath;
    }

    public static void setIsDecodePath(boolean isDecodePath) {
        MClassLoaderUtil.isDecodePath = isDecodePath;
    }
}
