package com.dcfs.esc.ftp.comm.scrt.security;

import com.dcfs.esc.ftp.comm.scrt.config.Security;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

public class Decrypter {
	/**
	 * rsa
	 * @param decBase64 密文base64数据
	 * @param priKey 公钥
	 * @return 明文数据
	 * @throws NoSuchAlgorithmException 异常
	 * @throws NoSuchPaddingException 异常
	 * @throws InvalidKeyException 异常
	 * @throws IllegalBlockSizeException 异常
	 * @throws BadPaddingException 异常
	 */
	public static byte[] rsa(String decBase64, PrivateKey priKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		return cipher.doFinal(base64(decBase64));
	}
	/**
	 * aes
	 * @param decBase64 密文base64数据
	 * @param password 密码
	 * @return 明文数据
	 * @throws NoSuchAlgorithmException 异常
	 * @throws NoSuchPaddingException 异常
	 * @throws InvalidKeyException 异常
	 * @throws IllegalBlockSizeException 异常
	 * @throws BadPaddingException 异常
	 * @throws UnsupportedEncodingException 异常
	 */
	public static byte[] aes(String decBase64, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		Security security = Security.getInstance();
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(password.getBytes("UTF-8"));
		generator.init(security.getAesInitSize(), random);
		Key key = generator.generateKey();
		Cipher cipher = Cipher.getInstance(security.getAesPaddingType());
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(base64(decBase64));
	}
	/**
	 * base64
	 * @param data 待恢复base64转码的byte数组
	 * @return 转码后byte型数组的原值
	 */
	public static byte[] base64(String data) {
		return Base64.getDecoder().decode(data);
	}


}
