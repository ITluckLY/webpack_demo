package com.dcfs.esb.ftp.utils;

import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;

/**
 * Created by mocg on 2016/12/19.
 */
public class ShortUrlUtil {
    private ShortUrlUtil() {
    }

    private static final char[] chars36 = new char[]{
            'z', 'x', 'c', 'v', 'b', 'n', 'm',
            'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };
    //chars36.length
    private static final int CHARS_36_LEN = 36;

    public static String short36(final long along) {
        long long2 = along;
        //第一位是个位,36进制最多13位
        final int byteLen = 13;
        byte[] arr = new byte[byteLen];
        int len = 0;
        final int divisor = CHARS_36_LEN;
        while (true) {
            arr[len++] = (byte) Math.abs((long2 % divisor));
            long2 = long2 / divisor;
            if (long2 == 0) break;
        }
        StringBuilder builder = new StringBuilder();
        if (along < 0) builder.append("-");
        for (int i = 0; i < len; i++) {
            builder.append(chars36[arr[i]]);
        }
        return builder.toString();
    }

    public static long parse36(String str) {
        //第一位是个位
        long val = 0;
        char[] chars = str.toCharArray();
        boolean negative = chars[0] == '-';
        long multiplier = 1;
        for (int i = (negative ? 1 : 0); i < chars.length; i++) {
            int index = search(chars36, chars[i]);
            val += index * multiplier;
            multiplier *= CHARS_36_LEN;
        }
        return negative ? -val : val;
    }

    private static int search(char[] chars, char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) return i;
        }
        throw new NestedRuntimeException("没查找charKey");
    }
}
