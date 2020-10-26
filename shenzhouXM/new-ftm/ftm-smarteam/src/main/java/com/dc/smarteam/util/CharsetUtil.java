package com.dc.smarteam.util;

import java.nio.charset.Charset;

/**
 * Created by mocg on 2017/4/27.
 */
public class CharsetUtil {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String convertBy(String str, String fromCharset, String toCharset) {
        return new String(str.getBytes(Charset.forName(fromCharset)), Charset.forName(toCharset));
    }

    public static String convertToUTF8(String str) {
        return new String(str.getBytes(), UTF8);
    }

    public static String convertFromUTF8(String str) {
        return new String(str.getBytes(UTF8));
    }
}
