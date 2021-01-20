package com.dcfs.esc.ftp.comm.scrt.security;

import com.dcfs.esc.ftp.comm.scrt.config.Security;

import java.security.*;

public class Signer {
	/**
	 * sign
	 * @param data 数据
	 * @param priKey 私钥
	 * @return 签名base64数据
	 * @throws NoSuchAlgorithmException 异常
	 * @throws InvalidKeyException 异常
	 * @throws SignatureException 异常
	 */
	public static String sign(byte[] data, PrivateKey priKey)  throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Security security = Security.getInstance();
		Signature signature = Signature.getInstance(security.getRsaSignType());
		signature.initSign(priKey);
		signature.update(data);
		return Encrypter.base64(signature.sign());
	}
	/**
	 * verify
	 * @param data 数据
	 * @param pubKey 公钥
	 * @param rsaBase64 签名base64数据
	 * @return 验签结果
	 * @throws NoSuchAlgorithmException 异常
	 * @throws InvalidKeyException 异常
	 * @throws SignatureException 异常
	 */
	public static boolean verify(byte[] data, String rsaBase64, PublicKey pubKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Security security = Security.getInstance();
		Signature signature = Signature.getInstance(security.getRsaSignType());
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(Decrypter.base64(rsaBase64));
	}
}
