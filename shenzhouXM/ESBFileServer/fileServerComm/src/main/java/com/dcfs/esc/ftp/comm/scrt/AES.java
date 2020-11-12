package com.dcfs.esc.ftp.comm.scrt;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by mocg on 2017/8/14.
 */
public class AES {
    private AES() {
    }

    private static final int KEY_SIZE = 128;
    private static final String ALGORITHM = "SHA1PRNG";

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param key     加密密码
     * @return
     */
    public static byte[] encrypt(byte[] content, byte[] key) throws FtpException {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance(ALGORITHM);
            secureRandom.setSeed(key);
            kgen.init(KEY_SIZE, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            // 加密
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            throw new FtpException(FtpErrCode.SCRT_ENCRYPT_ERROR, e);
        } catch (NoSuchPaddingException e) {
            throw new FtpException(FtpErrCode.SCRT_ENCRYPT_ERROR, e);
        } catch (InvalidKeyException e) {
            throw new FtpException(FtpErrCode.SCRT_ENCRYPT_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new FtpException(FtpErrCode.SCRT_ENCRYPT_ERROR, e);
        } catch (BadPaddingException e) {
            throw new FtpException(FtpErrCode.SCRT_ENCRYPT_ERROR, e);
        }
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param key     解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, byte[] key) throws FtpException {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance(ALGORITHM);
            secureRandom.setSeed(key);
            kgen.init(KEY_SIZE, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            // 解密
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            throw new FtpException(FtpErrCode.SCRT_DECRYPT_ERROR, e);
        } catch (NoSuchPaddingException e) {
            throw new FtpException(FtpErrCode.SCRT_DECRYPT_ERROR, e);
        } catch (InvalidKeyException e) {
            throw new FtpException(FtpErrCode.SCRT_DECRYPT_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new FtpException(FtpErrCode.SCRT_DECRYPT_ERROR, e);
        } catch (BadPaddingException e) {
            throw new FtpException(FtpErrCode.SCRT_DECRYPT_ERROR, e);
        }
    }

}
