package com.dcfs.esc.ftp.comm.scrt.config;

import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKException;
import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKExceptionEnums;
import com.dcfs.esc.ftp.comm.scrt.security.rsa.RSAKeyPair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class KeyStoreFactory {

	private static Log log = LogFactory.getLog(KeyStoreFactory.class);

	private static KeyStoreFactory instance = null;

	private KeyStore keyStore = null;

	private SSLContext sslcontext;


	public static KeyStoreFactory getInstance() throws SDKException {
		if (null == instance) {
			instance = new KeyStoreFactory();
		}
		return instance;
	}

	private KeyStoreFactory() throws SDKException {
		createCCBSSLContext();
	}

	private void createSSLContext() throws SDKException {
		KeyManagerFactory manager = null;
		try {
			sslcontext = SSLContext.getInstance(Constants.HTTPMANAGER_SSL);
			keyStore = KeyStore.getInstance(Constants.KEYSTORE_TYPE_PKCS12);
			FileInputStream fis = new FileInputStream(ConfigFile.KEYPATH);
			keyStore.load(fis, ConfigFile.KEYPWD.toCharArray());
			fis.close();
			fis = null;
			manager = KeyManagerFactory.getInstance(Constants.KEYSTORE_ALGORITHM_SUN);
			manager.init(keyStore, ConfigFile.KEYPWD.toCharArray());
			KeyManager[] keyManagers = manager.getKeyManagers();
			sslcontext.init(keyManagers, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("加载证书异常", e);
			}
			throw new SDKException(SDKExceptionEnums.INITIALIZE_KEYSTORE_ERROR);
		}
	}

	/**
	 * 修改https方法，
	 *
	 * @throws SDKException
	 */
	private void createCCBSSLContext() throws SDKException {
		KeyManagerFactory manager = null;
		FileInputStream fis = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			sslcontext = SSLContext.getInstance(Constants.HTTPMANAGER_TLS);
			keyStore = KeyStore.getInstance(Constants.KEYSTORE_TYPE_JKS);
			// 取个人证书
			fis = new FileInputStream(ConfigFile.KEYPATH);
			Certificate cer = cf.generateCertificate(fis);
			keyStore.load(null, null);
			keyStore.setKeyEntry("mykey", RSAKeyPair.getPrivateKey(ConfigFile.PRIVATEKEY), "".toCharArray(), new Certificate[] { cer });

			manager = KeyManagerFactory.getInstance(Constants.KEYSTORE_ALGORITHM_SUN);
			manager.init(keyStore, "".toCharArray());
			KeyManager[] keyManagers = manager.getKeyManagers();

			// 信任证书库
			InputStream caInput = new FileInputStream(ConfigFile.CAPATH);
			Certificate ca = cf.generateCertificate(caInput);
			caInput.close();
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore cakeyStore = KeyStore.getInstance(keyStoreType);
			cakeyStore.load(null, null);
			cakeyStore.setCertificateEntry("CA", ca);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(cakeyStore);
			sslcontext.init(keyManagers, tmf.getTrustManagers(), new SecureRandom());
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("加载证书异常", e);
			}
			throw new SDKException(SDKExceptionEnums.INITIALIZE_KEYSTORE_ERROR);
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (Exception e) {
			}
		}
	}


	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	public SSLContext getSslcontext() {
		return sslcontext;
	}
}
