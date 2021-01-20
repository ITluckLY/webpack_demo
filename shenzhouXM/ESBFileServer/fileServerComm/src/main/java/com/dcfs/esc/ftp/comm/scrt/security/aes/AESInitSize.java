package com.dcfs.esc.ftp.comm.scrt.security.aes;


public enum AESInitSize {
	S128(128), S192(192), S256(256);
	private int size;

	public int getSize() {
		return size;
	}


	private AESInitSize(int size) {
		this.size = size;
	}
}