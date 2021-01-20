package com.dcfs.esc.ftp.comm.scrt.security.util;

import com.dcfs.esc.ftp.comm.scrt.security.Decrypter;
import com.dcfs.esc.ftp.comm.scrt.security.Encrypter;
import com.dcfs.esc.ftp.comm.scrt.security.Signer;
import com.dcfs.esc.ftp.comm.scrt.security.aes.AESKey;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAKeyPair;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAKeyType;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.HashMap;
import java.util.Map;


/**
 * 加密解密报文 AES
 * 加密解密密钥 RSA
 * 加签验签报文 MD5+RSA
 */
public class Securitier {
    //private static Logger log = Logger.getLogger(Securitier.class);
    private String password = new String();
    private PrivateKey priKey = null;
    private PublicKey pubKey = null;
    private String encoding = "UTF-8";

    /**
     * 设置AES密钥
     *
     * @param key AES密钥
     */
    public void setKeyPair(AESKey key) {
        key.generator();
        password = key.getPassword();
    }

    /**
     * 设置RSA密钥对
     *
     * @param keyPair RSA密钥对
     */
    public void setKeyPair(RSAKeyPair keyPair) {
        this.priKey = (PrivateKey) keyPair.getKey(RSAKeyType.PRIVATE);
        this.pubKey = (PublicKey) keyPair.getKey(RSAKeyType.PUBLIC);
    }

    /**
     * 加密+签名
     *
     * @param msg    源数据
     * @param hasKey 是否包含密钥
     * @return 结果数据
     */
    public Map<String, String> encSign(byte[] msg, boolean hasKey) {
    	Map<String, String> dataMap = new HashMap<String, String>();
        try {
            if (hasKey) {
            	// 将未加密的aes用第三方的Rsa公钥加密（key不能超过117字节）
            	dataMap.put("key", encKey());
            }
            //对报文内容使用 未加密前的aes密钥 加密
            dataMap.put("msg", encMsg(msg));
            //对报文内容 用自己的Rsa私钥加签
            dataMap.put("sign", sign(msg));
        } catch (Exception ike) {
        }
        return dataMap;
    }

    /**
     * 解密+验签
     *
     * @param json   结果数据
     * @param hasKey 是否包含密钥
     * @return 源数据
     */
    public byte[] decVerify(String key, String sign, String msg, boolean hasKey) {
        byte[] data = null;
        try {
            if (hasKey) {
            	//对已经被加密的aes密钥 使用自己私钥解密
                decKey(key);
            }
            //对报文内容 使用上面已经解密出来的aes密钥进行解密
            data = decMsg(msg);
            //对报文内容 用别人的Rsa公钥验签
            if (!verify(data, sign)) {
                throw new Exception("verify error.");
            }
        } catch (Exception ike) {
        	ike.printStackTrace();
        }
        return data;
    }

    private String encKey() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return Encrypter.rsa(password.getBytes(encoding), pubKey);
    }

    private void decKey(String password) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        this.password = new String(Decrypter.rsa(password, priKey), encoding);
    }

    private String encMsg(byte[] msg) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        return Encrypter.aes(msg, password);
    }

    private byte[] decMsg(String msg) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return Decrypter.aes(msg, password);
    }

    private String sign(byte[] msg) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException {
        return Signer.sign(Encrypter.md5(msg).getBytes(encoding), priKey);
    }

    private boolean verify(byte[] msg, String sign) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException {
        return Signer.verify(Encrypter.md5(msg).getBytes(encoding), sign, pubKey);
    }
}
