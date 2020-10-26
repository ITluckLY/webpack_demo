package com.dc.smarteam.modules.monitor.putfiletomonitor.scrt;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * 安全工具处理类
 */
public class ScrtUtil {

    /**
     * 随机产生3DES的密钥
     *
     * @return 3DES的密钥
     */
    public static byte[] get3DesKey() {
        byte[] retKey = new byte[24];
        Random rd = new Random();//NOSONAR
        long rl = rd.nextLong() * System.currentTimeMillis() + rd.nextLong();
        byte[] base = Long.toString(rl).getBytes();
        int blen = base.length;

        Random rd1 = new Random(rl);//NOSONAR

        byte[] key = new byte[blen * 4];
        rd.nextBytes(key);
        for (int i = 0; i < blen; i++) {
            int idx = 3 * i;
            key[idx] = (byte) (((base[i] + key[idx])) & 0xff);
            key[idx + 1] = (byte) (((key[i + 1] & key[idx])) & 0xff);
            key[idx + 2] = (byte) (((key[i + 2] ^ key[idx + 1])) & 0xff);
            key[idx + 3] = (byte) (((key[i + 3] | key[idx + 2])) & 0xff);
        }

        int start = rd1.nextInt() % key.length + key.length;
        for (int i = 0; i < 24; i++)
            retKey[i] = key[(start + i) % key.length];
        return retKey;
    }

    /**
     * 随机产生Des的密钥
     *
     * @return Des的密钥
     */
    public static byte[] getDesKey() {
        byte[] retKey = new byte[8];
        byte[] key = get3DesKey();
        System.arraycopy(key, 8, retKey, 0, 8);
        return retKey;
    }

    /**
     * base64的编码，从字符数组转换成字符串
     *
     * @param byteArray 原始数据
     * @return base64的编码数据
     */
    public static String encodeBase64(byte[] byteArray) {
        return new BASE64Encoder().encode(byteArray);
    }

    /**
     * base64的解码，从字符串转换成字符数组
     *
     * @param base64String base64的编码数据
     * @return 原始数据
     */
    public static byte[] decodeBase64(String base64String) {
        byte[] byteArray = null;
        try {
            byteArray = new BASE64Decoder().decodeBuffer(base64String);
        } catch (IOException e) {
            return "".getBytes();
        }
        return byteArray;
    }

    /**
     * ESB系统的加密处理
     *
     * @param str 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptEsb(String str) {
        if (str == null) return null;
        byte[] tmp = str.getBytes();
        int len = tmp.length * 2;
        Random rd = new Random();//NOSONAR
        byte[] tmp1 = new byte[len];
        rd.nextBytes(tmp1);
        for (int i = 0; i < tmp.length; i++) {
            tmp1[2 * i] = tmp[i];
        }

        byte[] scrt = Des.encrypt3DES(tmp1, Des.getDesKey());
        return encodeBase64(scrt);
    }

    /**
     * ESB系统的解密处理
     *
     * @param str 需要解密的字符串
     * @return 解密后的字符串
     */
    public static String decryptEsb(String str) {
        if (str == null) return null;
        byte[] scrt = decodeBase64(str);
        byte[] tmp1 = Des.decrypt3DES(scrt, Des.getDesKey());
        byte[] tmp = new byte[tmp1.length / 2];
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = tmp1[2 * i];

        return new String(tmp, Charset.forName("UTF-8"));
    }

    public static String encrypt3DES(String str) {
        return "${3DES}" + ScrtUtil.encryptEsb(str);
    }

    public static String decrypt3DES(String str) {
        return decryptEsb(str.substring(7));
    }
}
