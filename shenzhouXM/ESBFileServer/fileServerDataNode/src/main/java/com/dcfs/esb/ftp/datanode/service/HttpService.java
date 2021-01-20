
package com.dcfs.esb.ftp.datanode.service;

import com.dcfs.esb.ftp.server.system.SystemInfo;
import com.dcfs.esb.ftp.server.system.SystemManage;
import com.dcfs.esc.ftp.comm.dto.clisvr.FileMsgPushReqDto;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpService {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private String url;
    private HttpPost httpPost = null;
    private HttpClient httpClient = null;
    private FileMsgPushReqDto reqDto;


    public String getUrl(String systemName) {
        SystemInfo systemInfo = SystemManage.getInstance().getSystemInfo(systemName);
        if (systemInfo == null || systemInfo.equals("")){
            logger.debug("没有目标名称:{}对应的信息",systemName);
        }
        String uploadPath = systemInfo.getUploadPath();
        if (uploadPath == null || uploadPath.equals("")){
            logger.debug("目标名称:{}对应的上传url为空",systemName);
        }
        this.url = uploadPath;
        return url;
    }
    public String post(String paramaters){
        httpPost = new HttpPost(this.url);
        logger.info("请求url[{}]报文[{}]", this.url, paramaters);
        String body = null;
        if (null != paramaters && null != httpPost && !"".equals(paramaters.trim())) {
            httpPost.addHeader("Content-type", "application/json");
//            httpPost.addHeader("Authorization", getHeader());
            httpPost.setEntity(new StringEntity(paramaters, Charset.forName("UTF-8")));
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(5000).build();
            httpPost.setConfig(requestConfig);
            try {
            	httpClient = HttpClientBuilder.create().build();
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.error("Method failed:{} " + httpResponse.getStatusLine().getStatusCode());
                    return body;
                }
                body = EntityUtils.toString(httpResponse.getEntity());
                return body;
            } catch (IOException e) {
                logger.error("", e);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
        }
        return body;
    }
}
