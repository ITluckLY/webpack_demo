package com.dcfs.esc.ftp.comm.scrt.security.rsa;

public enum RSAKeyType {
	PRIVATE("RSAPriKey"), PUBLIC("RSAPubKey");
	private String type;

	public String getType() {
		return type;
	}

	private RSAKeyType(String type) {
		this.type = type;
	}
}
