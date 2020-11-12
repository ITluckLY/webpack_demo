package com.dcfs.esb.ftp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mocg on 2016/9/22.
 */
public class PropertiesTool {
    protected PropertiesTool() {
    }

    public static String print(Properties properties) {
        return print(properties, "\r\n");
    }

    public static String print(Properties properties, String separator) {
        if (separator == null) separator = "";//NOSONAR
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String name : properties.stringPropertyNames()) {
            if (index++ > 0) builder.append(separator);
            builder.append(name).append("=").append(properties.getProperty(name));
        }
        return builder.toString();
    }

    public static Properties load(Properties properties, File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
        } finally {
            if (fis != null) fis.close();
        }
        return properties;
    }

    public static Properties load(Properties properties, String fileName) throws IOException {
        return load(properties, new File(fileName));
    }

    public static Properties safeLoad(Properties properties, String fileName) throws IOException {
        if (properties == null || fileName == null) return properties;
        File file = new File(fileName);
        if (file.exists() && file.isFile()) return load(properties, file);
        return properties;
    }

    public static Properties loadByNullSafe(Properties properties, File file) throws IOException {
        if (file == null) return properties;
        return load(properties, file);
    }

    public static Properties loadByNullSafe(Properties properties, String fileName) throws IOException {
        if (fileName == null) return properties;
        return load(properties, fileName);
    }

    public static Properties loadByClasspath(Properties properties, String clsPathFileName) throws IOException {
        String fileName = MClassLoaderUtil.getResourceFile(clsPathFileName);
        return load(properties, fileName);
    }

    public static void setByNullSafe(Properties properties, String key, String value) {
        if (properties == null || key == null) return;
        if (value == null) properties.setProperty(key, "");
        else properties.setProperty(key, value);
    }

    public static void setByNullSafe2(Properties properties, String key, String value) {
        if (properties == null || key == null || value == null) return;
        properties.setProperty(key, value);
    }
}
