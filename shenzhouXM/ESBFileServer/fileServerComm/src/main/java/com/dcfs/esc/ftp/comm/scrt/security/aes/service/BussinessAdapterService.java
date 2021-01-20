package com.dcfs.esc.ftp.comm.scrt.security.aes.service;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.comm.scrt.bean.SDKResponse;
import com.dcfs.esc.ftp.comm.scrt.config.ConfigFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BussinessAdapterService {

	private static Log log = LogFactory.getLog(BussinessAdapterService.class);

	public static SDKResponse post(String urlStr,byte[] reqJson,SDKRequestHead head) throws Exception {
		if(ConfigFile.MPUBLICURL.startsWith("https")){
			return BussinessHTTPSAdapterService.post(urlStr, reqJson, head);
		} else if(ConfigFile.MPUBLICURL.startsWith("http")){
			return BussinessHTTPAdapterService.post(urlStr, reqJson, head);
		} else {
			if(log.isErrorEnabled()){
				log.error("不能识别的通讯方式，url=["+urlStr+"]");
				log.error("不能识别的通讯方式，url=["+urlStr+"]");

			}
			return null;
		}
	}
}
