package com.dc.smarteam.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntityConveterUtils {
    private static final Logger log = LoggerFactory.getLogger(EntityConveterUtils.class);
    private static ObjectMapper mapper;

    /**
     * 获取ObjectMapper实例
     *
     * @param createNew 方式：true，新实例；false,存在的mapper实例
     * @return
     */
    public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
        if (createNew || mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    public static <T> T conveterJsonToEntity(String jsonString, Class<T> c) {
        T t;
        try {
            ObjectMapper objectMapper = getMapperInstance(false);
            t = objectMapper.readValue(jsonString, c);
            return t;
        } catch (IOException e) {
            log.error("", e);
            return null;
        }
    }


    public static String conveterEntityToJson(Object entity) {
        String json;
        try {
            ObjectMapper objectMapper = getMapperInstance(false);
            json = objectMapper.writeValueAsString(entity);
            return json;
        } catch (IOException e) {
            log.error("", e);
            return null;
        }
    }

    public class CustomDateSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            String formattedDate = formatter.format(value);
            jgen.writeString(formattedDate);
        }
    }
}
