package com.dcfs.esc.ftp.comm.scrt.security;

import com.dcfs.esc.ftp.comm.scrt.config.Security;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import java.util.Base64;


public class Encrypter {
	//private static Logger log = Logger.getLogger(Encrypter.class);
	/**
	 * md5
	 * @param data 明文数据
	 * @return 密文hex数据
	 */
	public static String md5(byte[] data) {
		return DigestUtils.md5Hex(data);
	}
	/**
	 * rsa
	 * @param data 明文数据
	 * @param pubKey 公钥
	 * @return 密文base64数据
	 * @throws NoSuchPaddingException 异常
	 * @throws NoSuchAlgorithmException 异常
	 * @throws InvalidKeyException 异常
	 * @throws BadPaddingException 异常
	 * @throws IllegalBlockSizeException 异常
	 */
	public static String rsa(byte[] data, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return base64(cipher.doFinal(data));
	}
	/**
	 * aes
	 * @param data 明文数据
	 * @param password 密码
	 * @return 密文base64数据
	 * @throws NoSuchPaddingException 异常
	 * @throws NoSuchAlgorithmException 异常
	 * @throws InvalidKeyException 异常
	 * @throws BadPaddingException 异常
	 * @throws IllegalBlockSizeException 异常
	 * @throws UnsupportedEncodingException 异常
	 */
	public static String aes(byte[] data, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		Security security = Security.getInstance();
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(password.getBytes("UTF-8"));
		generator.init(security.getAesInitSize(), random);
		Key key = generator.generateKey();
		Cipher cipher = Cipher.getInstance(security.getAesPaddingType());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return base64(cipher.doFinal(data));
	}
	/**
	 * base64
	 * @param data 明文数据
	 * @return 密文base64数据
	 */
	public static String base64(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	/**
	 * url
	 * @param data 转码前数据
	 * @param encoding 编码类型
	 * @return 转码后数据
	 * @throws UnsupportedEncodingException 异常
	 */
	public static String url(String data, String encoding) throws UnsupportedEncodingException {
		return URLEncoder.encode(data, encoding);
	}


}
