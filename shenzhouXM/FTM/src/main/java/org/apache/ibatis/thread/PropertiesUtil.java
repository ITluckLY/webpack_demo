package org.apache.ibatis.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {//NOSONAR

    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
    private static final String MYBATIS_REFRESH_PROPERTIES = "/mybatis-refresh.properties";
    private static Properties pro = new Properties();
    private static InputStream inputStream;
    static {
        try {
            inputStream = PropertiesUtil.class.getResourceAsStream(MYBATIS_REFRESH_PROPERTIES);
            pro.load(inputStream);
        } catch (Exception e) {
            log.error("Load mybatis-refresh [{}] error.", MYBATIS_REFRESH_PROPERTIES, e);
        }finally {
            try {
                if (inputStream != null)
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("关闭PropertiesUtil服务输入流inputStream失败");
            }
        }
    }

    public static int getInt(String key) {
        int i = 0;
        try {
            i = Integer.parseInt(getString(key));
        } catch (Exception e) {
            log.error("", e);
        }
        return i;
    }

    public static String getString(String key) {
        return pro == null ? null : pro.getProperty(key);
    }

}
