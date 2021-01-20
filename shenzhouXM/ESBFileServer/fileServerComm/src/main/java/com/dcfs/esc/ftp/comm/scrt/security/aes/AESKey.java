package com.dcfs.esc.ftp.comm.scrt.security.aes;

import java.security.SecureRandom;


public class AESKey {
	private SecureRandom random = new SecureRandom();

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String password = new String();
	/**
	 * 生成
	 */
	public void generator() {
		password = String.format("%016x", random.nextLong());
	}
}