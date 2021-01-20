package com.dcfs.esc.ftp.comm.scrt.security.aes.service;

import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.comm.scrt.bean.SDKResponse;
import com.dcfs.esc.ftp.comm.scrt.config.ConfigFile;
import com.dcfs.esc.ftp.comm.scrt.config.Constants;
import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKException;
import com.dcfs.esc.ftp.comm.scrt.security.aes.exception.SDKExceptionEnums;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class BussinessHTTPAdapterService {

	private static Log log = LogFactory.getLog(BussinessHTTPAdapterService.class);

    public static SDKResponse post(String urlStr,byte[] reqJson,SDKRequestHead head) throws Exception {
        if(log.isDebugEnabled()){
        	log.debug("url=["+urlStr+"]");
        	log.debug("请求报文=["+new String(reqJson)+"]");
        }
        urlStr = ConfigFile.MPUBLICURL + "/" + urlStr;
        HttpURLConnection urlConnection = null;
        byte[] resultData = null;
        OutputStream OutputStream = null;
        InputStream inputStream = null;
        //创建返回实体
        SDKResponse response = new SDKResponse();
        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            addRequestHead(urlStr,head, urlConnection);
            urlConnection.connect();

            OutputStream = urlConnection.getOutputStream();
            OutputStream.write(reqJson);
            OutputStream.flush();

            //获取http链接码
            int code = urlConnection.getResponseCode();

            if (200 != code) {
            	if(log.isErrorEnabled()){
            		log.error("HTTP 响应异常 ,ResponseCode=["+code+"]");
            	}
                throw new SDKException(SDKExceptionEnums.HTTPCONN_ERROR);
            }

            //获取header 中文件大小、偏移量
            String filesize = urlConnection.getHeaderField("filesize");
            String offset = urlConnection.getHeaderField("offset");
            String lastPiece = urlConnection.getHeaderField("lastPiece");
            if(filesize != null && !"".equals(filesize) && offset != null && !"".equals(offset) && offset != null && !"".equals(offset)){
            	response.setFileSize(Integer.parseInt(filesize));
            	response.setOffset(Integer.parseInt(offset));
            	response.setLastPiece(Boolean.valueOf(lastPiece));
            }
            response.setKey(urlConnection.getHeaderField("key"));
            response.setSign(urlConnection.getHeaderField("sign"));
            inputStream = urlConnection.getInputStream();
            resultData = getResponse(inputStream);
            if(log.isDebugEnabled()){
            	log.debug("响应报文=["+new String(resultData)+"]");
            }
            response.setMsg(new String(resultData));
        } catch (Exception e) {
        	if(e instanceof SDKException){
        		throw e;
        	}
        	if(log.isErrorEnabled()){
        		log.error("通讯模块异常,通讯地址=["+urlStr+"]",e);
        	}
        	throw new SDKException(SDKExceptionEnums.POST_ERROR);
        } finally {
        	if (OutputStream != null) {
        		try {
					OutputStream.close();
				} catch (IOException e) {
					if(log.isErrorEnabled()){
						log.error("关闭输出流异常",e);
					}
				}
        	}
        	if (inputStream != null) {
        		try {
        			inputStream.close();
				} catch (IOException e) {
					if(log.isErrorEnabled()){
						log.error("关闭输入流异常",e);
					}
				}
        	}
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        //设置返回内容体
        return response;
    }

    /***
     * 配置连接设置
     *
     * @param mConnection 当前连接
     * @return 返回取出的数据
     * @throws ProtocolException
     */
    private static void addRequestHead(String urlStr,SDKRequestHead head, HttpURLConnection mConnection) throws
            Exception {
        mConnection.setDoOutput(true);
        mConnection.setDoInput(true);
        mConnection.setConnectTimeout(ConfigFile.CONNECT_TIMEOUT);
        mConnection.setReadTimeout(ConfigFile.READ_TIMEOUT);
        mConnection.setRequestMethod(Constants.POST);
        mConnection.setUseCaches(false);
        mConnection.setRequestProperty(Constants.KEY, head.getKey());
        mConnection.setRequestProperty(Constants.SIGN, head.getSign());
        mConnection.setRequestProperty(Constants.FILE_SIZE, String.valueOf(head.getFileSize()));
        mConnection.setRequestProperty(Constants.OFFSET, String.valueOf(head.getOffset()));
        mConnection.setRequestProperty(Constants.FILE_NAME, head.getFileName());
        mConnection.setRequestProperty(Constants.LAST_PIECE, String.valueOf(head.isLastPiece()));
        mConnection.setRequestProperty(Constants.TRAN_CODE, head.getTranCode());
        mConnection.setRequestProperty(Constants.UID, head.getUid());
        mConnection.setRequestProperty(Constants.PASSWD_ID, head.getPasswdId());
        //增加Content-type
        mConnection.addRequestProperty(Constants.CONTENT_TYPE,Constants.CONTENT_TXT);
        mConnection.addRequestProperty(Constants.URI,urlStr);
        //增加请求流水号
        mConnection.addRequestProperty(Constants.RqsSrlNo, head.getRqsSrlNo());
    }

    private static byte[] getResponse(InputStream in) throws IOException {
    	byte[] rspBytes = null;
    	ByteArrayOutputStream outt = new ByteArrayOutputStream();
		int n = 0;
		byte[] val = new byte[1024];
		while((n = in.read(val)) > 0){
			outt.write(val,0,n);
		}
		rspBytes = outt.toByteArray();
		return rspBytes;
    }
}
