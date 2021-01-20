package com.dcfs.esc.ftp.comm.scrt.bean;

/**
 * SDK 文件传输信息类
 * @author zhudp
 *
 */
public class FtsFile {
	/* 本地文件名称 */
	private String localFile;
	/* 远程文件名称 */
	private String remoteFile;
	/* 远程文件名称 */
	private String tranCode;
	/* 当前指针位置 */
	private long pointe;
	/* 流水号 */
	private String RqsSrlNo;
	/* 文件大小 */
	private long fileSize;
	/**
	 * 是否最后一个分片
	 */
	private boolean lastPiece;

	public FtsFile(String localFile, String remoteFile, String tranCode) {
		this.localFile = localFile;
		this.remoteFile = remoteFile;
		this.tranCode = tranCode;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public String getRemoteFile() {
		return remoteFile;
	}

	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}

	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	public long getPointe() {
		return pointe;
	}

	public void setPointe(long pointe) {
		this.pointe = pointe;
	}

	public String getRqsSrlNo() {
		return RqsSrlNo;
	}

	public void setRqsSrlNo(String rqsSrlNo) {
		RqsSrlNo = rqsSrlNo;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isLastPiece() {
		return lastPiece;
	}

	public void setLastPiece(boolean lastPiece) {
		this.lastPiece = lastPiece;
	}
}
