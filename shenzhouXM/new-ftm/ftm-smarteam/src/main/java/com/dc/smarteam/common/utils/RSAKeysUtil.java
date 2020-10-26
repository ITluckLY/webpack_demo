package com.dc.smarteam.common.utils;

import com.alibaba.druid.filter.config.ConfigTools;
import org.apache.commons.codec.binary.Base64;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangzbb on 2017/9/13.
 */
public class RSAKeysUtil {
    private RSAKeysUtil() {
    }

    private static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return encodeBase64(key.getEncoded());
    }

    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encodeBase64(key.getEncoded());
    }

    public static Map<String, Object> initKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    public static byte[] decodeBase64(String content) {
        return Base64.decodeBase64(content);
    }

    public static String encodeBase64(byte[] content) {
        return Base64.encodeBase64String(content);
    }

    /**
     * 生成密文
     *
     * @param privateKey 私钥
     * @param plainText  明文
     */
    public static String encrypt(String privateKey, String plainText) throws Exception {
        return ConfigTools.encrypt(privateKey, plainText);
    }

    /**
     * 获取公钥私钥
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Map<String, Object> keyMap;
        keyMap = initKey();
        getPublicKey(keyMap);
        getPrivateKey(keyMap);
    }
}
