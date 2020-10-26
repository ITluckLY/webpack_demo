package com.dc.smarteam.tool;

import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;


public class MessDealTool {
    private static final Logger log = LoggerFactory.getLogger(MessDealTool.class);

    /**
     * 将返回报文发送给调用端
     *
     * @param resultDto
     * @param socket
     */
    public static <T> void sendBackMes(ResultDto<T> resultDto, Socket socket) {
        String str = ResultDtoTool.toJson(resultDto);
        sendBackMes(str, socket);
    }

    /**
     * 将返回报文发送给调用端
     *
     * @param str
     * @param socket
     */
    public static void sendBackMes(String str, Socket socket) {
        if (str == null) return;

        str = str.replaceAll("\"@|\"#", "\"");
        String length = null;
        try {
            length = Integer.toString(str.getBytes("utf-8").length);
        } catch (UnsupportedEncodingException e1) {
            length = "000000";
            log.error("", e1);
        }
        String mesHeader = "000000" + length;
        mesHeader = mesHeader.substring(length.length());
        String mess = mesHeader + str;

        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            os.write(mess.getBytes("utf-8"));
            os.flush();
        } catch (IOException e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    //根据key获取json中字符串
    public static String getString(JSONObject json, String key) {
        try {
            if (json.containsKey(key)) {
                return json.getString(key);
            }
        } catch (Exception e) {
            log.error("数据中不包含查询参数[" + key + "]", e);
        }
        return null;
    }
}
