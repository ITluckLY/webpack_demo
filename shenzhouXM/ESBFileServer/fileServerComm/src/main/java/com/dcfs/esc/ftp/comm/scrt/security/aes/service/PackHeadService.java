package com.dcfs.esc.ftp.comm.scrt.security.aes.service;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.comm.scrt.config.ConfigFile;

/**
 * 公共头处理，SDK自动赋值到head
 * @author chenzyn
 *
 */
public class PackHeadService {

	public static SDKRequestHead packReqHead() throws Exception{
		SDKRequestHead head = new SDKRequestHead();
		head.setUid(ConfigFile.U_ID);
		head.setPasswdId(ConfigFile.PASSWD);
		head.setSystemGroup(ConfigFile.SYSTEM);
		return head;
	}
}
