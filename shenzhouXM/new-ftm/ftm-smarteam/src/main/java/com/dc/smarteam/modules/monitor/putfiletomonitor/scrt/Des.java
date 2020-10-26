package com.dc.smarteam.modules.monitor.putfiletomonitor.scrt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES和3DES算法的处理类
 */
public class Des {
    private static final Logger log = LoggerFactory.getLogger(Des.class);

    private static final byte[] des3Key = "1234567890qwertyuiopzdlw".getBytes();

    private static final String DES3 = "DESede";

    private static final String DES = "DES";

    /**
     * 获取通用的处理密钥
     *
     * @return 返回通用的密钥
     */
    public static byte[] getDesKey() {
        return des3Key;
    }

    /**
     * 3DES加密
     *
     * @param value 需要加密的数据
     * @param key   加密的密钥
     * @return 加密后的结果
     */
    public static byte[] encrypt3DES(byte[] value, byte[] key) {
        byte[] retValue = null;

        try {
            SecretKey deskey = new SecretKeySpec(key, DES3);
            Cipher c1 = Cipher.getInstance(DES3);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            retValue = c1.doFinal(value);
        } catch (Exception ex) {
            log.error("3DES加密错误", ex);
        }

        return retValue;
    }

    /**
     * 3DES解密
     *
     * @param value 需要解密的数据
     * @param key   解密的密钥
     * @return 解密后的结果
     */
    public static byte[] decrypt3DES(byte[] value, byte[] key) {
        byte[] retValue = null;

        try {
            SecretKey deskey = new SecretKeySpec(key, DES3);
            Cipher c1 = Cipher.getInstance(DES3);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            retValue = c1.doFinal(value);
        } catch (Exception ex) {
            log.error("3DES解密错误", ex);
        }

        return retValue;
    }

}
