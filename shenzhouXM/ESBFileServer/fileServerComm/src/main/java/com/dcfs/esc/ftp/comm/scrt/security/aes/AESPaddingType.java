package com.dcfs.esc.ftp.comm.scrt.security.aes;


public enum AESPaddingType {
	AES_ECB_PKCS5PADDING("AES/ECB/PKCS5Padding"), AES_ECB_ISO10126PADDING("AES/ECB/ISO10126Padding");
	private String padding;

	public String getPadding() {
		return padding;
	}

	private AESPaddingType(String padding) {
		this.padding = padding;
	}
}