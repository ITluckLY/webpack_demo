package com.dcfs.esc.ftp.comm.scrt.util;

import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKException;
import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKExceptionEnums;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * 检测证书是否真实存在
	 *
	 * @param path
	 * @throws SDKException
	 */
	public static void isExist(String path) throws SDKException {
		File file = new File(path);
		if (!file.exists()) {
			log.error("找不到证书文件["+file.getAbsolutePath()+"]");
			throw new SDKException(SDKExceptionEnums.INITIALIZE_KEYSTORE_ERROR);
		} else {
			//ConfigFile.KEYPATH = path;
		}
	}

	public static String readTxtFile(String filePath) throws Exception {
		String AllTxt = "";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis, encoding);// 考虑到编码格式
				br = new BufferedReader(isr);
				String lineTxt = null;
				while ((lineTxt = br.readLine()) != null) {
					AllTxt += lineTxt;
				}
			} else {
				log.error("找不到指定的文件["+file.getAbsolutePath()+"]");
				throw new SDKException(SDKExceptionEnums.FILI_NOT_FOUND_EXCEPTION);
			}
		} catch (Exception e) {
			if(e instanceof SDKException){
				throw e;
			}
			log.error("读取文件内容出错",e);
			throw new SDKException(SDKExceptionEnums.FILI_READ_FAIL_EXCEPTION);
		} finally {
			try {
				if (null != br) {
					br.close();
				}
			} catch (Exception e) {
			}
			try {
				if (null != isr) {
					isr.close();
				}
			} catch (Exception e) {
			}
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (Exception e) {
			}
		}
		return AllTxt;
	}
}
