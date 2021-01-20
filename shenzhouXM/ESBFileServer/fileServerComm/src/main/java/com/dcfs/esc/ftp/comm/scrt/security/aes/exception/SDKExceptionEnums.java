package com.dcfs.esc.ftp.comm.scrt.security.aes.exception;
public enum SDKExceptionEnums implements ExceptionEnums{
	INITIALIZE_ERROR("SDK0100001","SDK初始化异常"),
	INITIALIZE_KEYSTORE_ERROR("SDK0100003","初始化证书文件异常"),
	CHECK_FAIL("SDK0100004","字段校验异常"),
	//合并到SDK初始化失败
	INITIALIZE_PROPERTIES_ERROR("SDK0100005","初始化proprties文件不存在"),
	//合并到SDK初始化失败
	INITIALIZE_PROPERTIES_READ_ERROR("SDK0100006","初始化proprties文件读取失败"),
	//合并到初始化证书文件异常
	FILI_NOT_FOUND_EXCEPTION("SDK0100007","文件不存在"),
	//合并到初始化证书文件异常
	FILI_READ_FAIL_EXCEPTION("SDK0100008","文件读取失败"),
	SECURITY_ERROR("SDK0100012","SDK加签失败"),
	POST_ERROR("SDK0100014","网络连接异常"),

	CHERSA_ERROR("SDK0200011","SDK验签失败"),
	HTTPCONN_ERROR("SDK0200012","HTTP响应异常");

    private String code;
    private String message;

    private SDKExceptionEnums(String code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

	@Override
	public void setMessage(String message) {
		this.message = message;
	}
}