package com.dcfs.esc.ftp.comm.util;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.scrt.Des;

/**
 * Created by mocg on 2017/8/14.
 */
public class ScrtUtil {
    private ScrtUtil() {
    }

    /**
     * 加密
     *
     * @param value 需要加密的数据
     * @param key   加密的密钥
     * @return 加密后的结果
     */
    public static byte[] encrypt(byte[] value, byte[] key) throws FtpException {//NOSONAR
        byte[] bytes = Des.encrypt3DES(value, key);
        //byte[] bytes = AES.encrypt(value, key)
        //对第一个字节取反
        //bitwiseComplementOnFisrtByte(bytes)
        return bytes;
    }

    /**
     * 解密
     *
     * @param value 需要解密的数据
     * @param key   解密的密钥
     * @return 解密后的结果
     */
    public static byte[] decrypt(byte[] value, byte[] key) throws FtpException {//NOSONAR
        //对第一个字节取反
        //bitwiseComplementOnFisrtByte(value)
        return Des.decrypt3DES(value, key);
        //return AES.decrypt(value, key)
    }

    /**
     * 对第一个字节取反
     *
     * @param bytes
     */
    private static void bitwiseComplementOnFisrtByte(byte[] bytes) {
        if (bytes != null && bytes.length > 0) bytes[0] = (byte) ~bytes[0];//NOSONAR
    }

}
