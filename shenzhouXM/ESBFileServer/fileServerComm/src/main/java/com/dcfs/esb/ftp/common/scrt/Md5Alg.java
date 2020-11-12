package com.dcfs.esb.ftp.common.scrt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5算法的处理类
 */
public class Md5Alg {
    private static final Logger log = LoggerFactory.getLogger(Md5Alg.class);
    private MessageDigest md = null;

    /**
     *
     */
    public Md5Alg() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ne) {
            if (log.isErrorEnabled())
                log.error("NoSuchAlgorithmException: md5", ne);
        }
    }

    /**
     * @param bytes
     */
    public void update(byte[] bytes) {
        md.update(bytes);
    }

    /**
     * @param bytes
     * @param offset
     * @param len
     */
    public void update(byte[] bytes, int offset, int len) {
        md.update(bytes, offset, len);
    }

    /**
     * 功能：计算MD5的校验码
     *
     * @return 进行Md5的生成
     */
    public byte[] digest() {
        return md.digest();
    }

    public String digestAndString() {
        byte[] bytes = digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString((0x000000ff & b) | 0xffffff00).substring(6));//NOSONAR
        }
        return sb.toString();
    }

    public void reset() {
        md.reset();
    }
}
