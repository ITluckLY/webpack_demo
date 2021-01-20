package com.dcfs.esc.ftp.comm.scrt.security.rsa;

import com.dcfs.esc.ftp.comm.scrt.config.Security;
import com.dcfs.esc.ftp.comm.scrt.security.Encrypter;
import sun.misc.BASE64Decoder;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAKeyPair {
	private Map<RSAKeyType, Key> keyMap = new HashMap<RSAKeyType, Key>();
	/**
	 * 生成
	 * @throws NoSuchAlgorithmException 异常
	 * @throws InvalidKeySpecException 异常
	 */
	public void generator() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(Security.getInstance().getRsaInitSize());
		KeyPair keyPair = generator.generateKeyPair();
		keyMap.put(RSAKeyType.PRIVATE, keyPair.getPrivate());
		keyMap.put(RSAKeyType.PUBLIC, keyPair.getPublic());
	}
	/**
	 * 加载
	 * @param priKey 私钥
	 * @param pubKey 公钥
	 * @throws NoSuchAlgorithmException 异常
	 * @throws InvalidKeySpecException 异常
	 */
	public void load(byte[] priKey, byte[] pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		keyMap.put(RSAKeyType.PRIVATE, factory.generatePrivate(new PKCS8EncodedKeySpec(priKey)));
		keyMap.put(RSAKeyType.PUBLIC, factory.generatePublic(new X509EncodedKeySpec(pubKey)));
	}
	/**
	 * 获取密钥
	 * @param keyType 密钥类型
	 * @return 密钥
	 */
	public Key getKey(RSAKeyType keyType) {
		return keyMap.get(keyType);
	}
	/**
	 * 获取密钥
	 * @param keyType 密钥类型
	 * @param oneLine 是否单行 true:单行 false:多行
	 * @return base64编码密钥
	 */
	public String getKey(RSAKeyType keyType, boolean oneLine) {
		Key key = getKey(keyType);
		String base64 = Encrypter.base64(key.getEncoded());
		if (oneLine) {
			base64 = base64.replace("\n", "");
		}
		return base64;
	}

	/**
     * String转公钥PublicKey
     * @param key
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * String转私钥PrivateKey
     * @param key
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

}