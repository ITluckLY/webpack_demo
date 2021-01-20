package com.dcfs.esc.ftp.comm.scrt.bean;
/**
 * 由SDK自动填充
 * 用于http header
 * @author zhudp
 *
 */
//TODO 根据使用场景看，有些set方法可以去掉
public class SDKResponse {
	/**
	 * key
	 */
	private String key;
	/**
	 * sign
	 */
	private String sign;
	/**
	 *文件总大小
	 */
	private long fileSize;
	/**
	 * 偏移量
	 */
	private long offset;
	/**
	 * 是否最后一个分片
	 */
	private boolean lastPiece;
	/**
	 * 是否最后一个分片
	 */
	private String msg;

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public boolean isLastPiece() {
		return lastPiece;
	}
	public void setLastPiece(boolean lastPiece) {
		this.lastPiece = lastPiece;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
