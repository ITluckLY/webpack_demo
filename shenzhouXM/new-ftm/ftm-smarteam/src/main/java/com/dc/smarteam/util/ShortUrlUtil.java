package com.dc.smarteam.util;

/**
 * Created by mocg on 2016/12/19.
 */
public class ShortUrlUtil {

    private static final char[] chars36 = new char[]{
            'z', 'x', 'c', 'v', 'b', 'n', 'm',
            'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };
    private static final int chars36Len = 36;//chars36.length;

    public static String short36(final long l) {
        long l2 = l;
        int[] arr = new int[13];
        int len = 0;
        while (true) {
            arr[len++] = (int) (l2 % chars36Len);
            l2 = l2 / chars36Len;
            if (l2 == 0) break;
        }
        StringBuilder builder = new StringBuilder();
        if (l < 0) builder.append("-");
        for (int i = 0; i < len; i++) {
            builder.append(chars36[Math.abs(arr[i])]);
        }
        return builder.toString();
    }
}
