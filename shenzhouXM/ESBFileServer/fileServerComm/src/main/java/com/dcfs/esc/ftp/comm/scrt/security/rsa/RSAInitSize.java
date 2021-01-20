package com.dcfs.esc.ftp.comm.scrt.security.rsa;


public enum RSAInitSize {
	S1024(1024), S2048(2048), S3092(3092), S4096(4096);
	private int size;

	public int getSize() {
		return size;
	}


	private RSAInitSize(int size) {
		this.size = size;
	}
}