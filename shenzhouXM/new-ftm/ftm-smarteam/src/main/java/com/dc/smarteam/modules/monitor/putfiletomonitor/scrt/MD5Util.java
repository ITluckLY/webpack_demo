package com.dc.smarteam.modules.monitor.putfiletomonitor.scrt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mocg on 2016/7/5.
 */
public class MD5Util {
    public static String md5(String str) {
        Md5Alg md5Alg = new Md5Alg();
        md5Alg.update(str.getBytes());
        byte[] digest = md5Alg.digest();
        return toHexString(digest);
    }

    public static String toHexString(byte bytes[]) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (byte aByte : bytes) {
            stmp = Integer.toHexString(aByte & 0xff);
            if (stmp.length() == 1) hs.append("0").append(stmp);
            else hs.append(stmp);
        }
        return hs.toString();
    }

    public static String md5(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return md5(fis);
        } finally {
            if (fis != null) fis.close();
        }
    }

    public static String md5(InputStream in) throws IOException {
        Md5Alg md5Alg = new Md5Alg();
        byte[] buff = new byte[1024];
        int readLen;
        while ((readLen = in.read(buff)) != -1) {
            md5Alg.update(buff, 0, readLen);
        }
        byte[] digest = md5Alg.digest();
        return toHexString(digest);
    }

    public String fastMd5(File file) {
        return null;
    }
}
