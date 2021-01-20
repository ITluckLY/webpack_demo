package com.dcfs.esc.ftp.comm.scrt.config;

import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKException;
import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKExceptionEnums;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class ConfigFile {

	public static Log log = LogFactory.getLog(ConfigFile.class);

	/**
	 * 请求前缀
	 */
	public static String MPUBLICURL = "";
	/**
	 * 公钥
	 */
	public static String PUBLICKEY = "";
	/**
	 * 客户端证书密码
	 */
	public static String KEYPWD = "";
	/**
	 * 客户端证书路径
	 */
	public static String KEYPATH = "";
	/**
	 * 客户端证书类型
	 */
	public static String TYPE = "";
	/**
	 * 建立连接的超时时间
	 */
	public static int CONNECT_TIMEOUT=10000;

	/**
	 * 读取响应的超时时间
	 */
	public static int READ_TIMEOUT=20000;

	/**
	 * ca证书路径
	 */
	public static String CAPATH = "";

	/**
	 * 私钥信息
	 */
	public static String PRIVATEKEY = "";
	/**
	 * 用户ID
	 */
	public static String U_ID = "";
	/**
	 * 用户密码
	 */
	public static String PASSWD = "";
	/**
	 * 分片大小
	 */
	public static String PICE_SIZE = "";
	/**
	 * 系统
	 */
	public static String SYSTEM = "";

	public static void readConfig(String path) throws SDKException {
		Properties prop = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			prop.load(in);
			CAPATH = prop.getProperty("CA_PATH");
			PUBLICKEY = prop.getProperty("PUBLIC_KEY");
			KEYPWD = prop.getProperty("CLIENT_PWD");
			TYPE = prop.getProperty("TYPE");
			KEYPATH = prop.getProperty("KEY_PATH");
			MPUBLICURL = prop.getProperty("PUBLIC_URL");
			CONNECT_TIMEOUT = Integer.parseInt(prop.getProperty("CONNECT_TIMEOUT"));
			READ_TIMEOUT = Integer.parseInt(prop.getProperty("READ_TIMEOUT"));
			U_ID = prop.getProperty("U_ID");
			PASSWD = prop.getProperty("PASSWD");
			SYSTEM = prop.getProperty("SYSTEM");
			PICE_SIZE = prop.getProperty("PICE_SIZE");
			in.close();
		} catch (FileNotFoundException e) {
			log.error("properties文件找不到",e);
			throw new SDKException(SDKExceptionEnums.INITIALIZE_PROPERTIES_ERROR);
		} catch (IOException e) {
			log.error("properties文件读取失败", e);
			throw new SDKException(SDKExceptionEnums.INITIALIZE_PROPERTIES_READ_ERROR);
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
