package com.dcfs.esc.ftp.comm.scrt.config;

public class Constants {
	public static final String URI = "URI";
	public static final String POST = "POST";
	public static final String UID = "uId";
	public static final String PASSWD_ID = "passwdId";
	public static final String KEY = "key";
	public static final String MSG = "msg";
	public static final String SIGN = "sign";
	public static final String FILE_SIZE = "fileSize";
	public static final String OFFSET = "offset";
	//请求流水
	public static final String RqsSrlNo = "RqsSrlNo";
	public static final String ENCODING_UTF8 = "UTF-8";
	//增加Content-type
	public static final String CONTENT_TYPE = "Content-Type";
	//json类型
	public static final String CONTENT_JSON = "application/json";
	//text/plain类型
	public static final String CONTENT_TXT = "text/plain";
	//octet-stream类型
	public static final String CONTENT_OCTET_STREAM = "application/octet-stream";
	//文件传输信息后缀
    public static final String DCFS_CFG_FILE_EXT = ".dcfscfg";
    //临时文件后缀
    public static final String DCFS_TMP_FILE_EXT = ".dcfstmp";
    //传输文件名
    public static final String FILE_NAME = "fileName";
    //是否最后一分片
    public static final String LAST_PIECE = "lastPiece";
    //交易码
    public static final String TRAN_CODE = "tranCode";


	public static final String HTTPMANAGER_SSL = "SSL";
	//public static final String HTTPMANAGER_TLS = "TLSv1";
	public static final String HTTPMANAGER_TLS = "TLS";
	public static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";
	public static final String KEYSTORE_TYPE_JKS = "JKS";
	public static final String KEYSTORE_ALGORITHM_SUN = "SunX509";
	public static final String AES_MODE = "AES/CBC/PKCS5Padding";
	public static final String SHA256WITHRSA = "SHA256withRSA";
	public static final String SHA256 = "SHA-256";
	public static final String AES = "AES";
	public static final String RSA = "RSA";
}
