package com.dc.smarteam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mocg on 2016/9/22.
 */
public class PropertiesTool {

    public static String print(Properties properties) {
        return print(properties, "\r\n");
    }

    public static String print(Properties properties, String separator) {
        if (separator == null) separator = "";
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String name : properties.stringPropertyNames()) {
            if (index++ > 0) builder.append(separator);
            builder.append(name).append("=").append(properties.getProperty(name));
        }
        return builder.toString();
    }

    public static Properties load(Properties properties, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        }
        return properties;
    }

    public static Properties load(Properties properties, String fileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        }
        return properties;
    }
}
