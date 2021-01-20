package com.dcfs.esc.ftp.comm.scrt.config;

import com.dcfs.esc.ftp.comm.scrt.security.aes.AESInitSize;
import com.dcfs.esc.ftp.comm.scrt.security.aes.AESPaddingType;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAInitSize;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSASignType;


public class Security {
	private volatile static Security security = null;
	private AESInitSize aesInitSize = AESInitSize.S128;
	private AESPaddingType aesPaddingType = AESPaddingType.AES_ECB_PKCS5PADDING;
	private RSAInitSize rsaInitSize = RSAInitSize.S2048;
	private RSASignType rsaSignType = RSASignType.SHA256withRSA;


	private Security() {
		init();
	}
	/**
	 * 获取实例
	 * @return 实例
	 */
	public static Security getInstance() {
		if (security == null) {
			synchronized (Security.class) {
				if (security == null) {
					security = new Security();
				}
			}
		}
		return security;
	}
	private void init() {
	}
	/**
	 * AES密钥长度
	 * @return 密钥长度
	 */
	public int getAesInitSize() {
		return aesInitSize.getSize();
	}
	/**
	 * AES填充模式
	 * @return 填充模式
	 */
	public String getAesPaddingType() {
		return aesPaddingType.getPadding();
	}
	/**
	 * RSA密钥长度
	 * @return 密钥长度
	 */
	public int getRsaInitSize() {
		return rsaInitSize.getSize();
	}
	/**
	 * RSA密钥模式
	 * @return 签名算法
	 */
	public String getRsaSignType() {
		return rsaSignType.name();
	}
}
