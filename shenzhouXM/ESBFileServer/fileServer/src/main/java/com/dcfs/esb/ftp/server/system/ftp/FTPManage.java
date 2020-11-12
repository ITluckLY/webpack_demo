package com.dcfs.esb.ftp.server.system.ftp;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.network.Channel;
import com.dcfs.esb.ftp.network.ControlUtil;
import com.dcfs.esb.ftp.server.system.BaseProtocol;
import com.dcfs.esb.ftp.server.system.SystemInfo;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class FTPManage extends BaseProtocol {
    private static final Logger log = LoggerFactory.getLogger(FTPManage.class);
    private SystemInfo systemInfo;
    private String localFileName;
    private String remoteFileName;
    private FtpClient client = new FtpClient();
    private String channelName;

    public FTPManage(SystemInfo systemInfo, String localFileName, String remoteFileName) {
        this.systemInfo = systemInfo;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
        client.setEncoding("GBK");
    }

    public FTPManage(SystemInfo systemInfo, String localFileName, String remoteFileName, String name) {
        this.systemInfo = systemInfo;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
        this.channelName = name;
        client.setEncoding("GBK");
    }

    /**
     * FTP登录
     */
    private void login() {
        String ip = systemInfo.getIp();
        int port = systemInfo.getPort();
        String userName = systemInfo.getUsername();
        String password = systemInfo.getPassword();
        try {
            if (port <= 0) {
                client.connect(ip);
            } else {
                client.connect(ip, port);
            }
            client.login(userName, password);
            if (log.isInfoEnabled()) {
                log.info("user[" + userName + "]passwd[" + password + "]@[" + ip + ":" + port + "]ftp登录结果[" + client.getReplyString() + "]");
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 关闭FTP
     */
    private void close() {
        if (client != null) {
            try {
                client.quit();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    /***
     * 文件上传改造代码，返回每秒的传输速度 2016-05-21 shiqla
     *
     * @throws FtpException
     */

    @Override
    public boolean uploadBySync() throws FtpException {
        login();
        OutputStream os = null;
        FileInputStream is = null;
        Map<String, Channel> map = ControlUtil.getInstance().getChannelCollMap();

        log.info("upload,remoteFileName:{}", remoteFileName);
        try {
            String[] list = remoteFileName.split("/");
            if (remoteFileName.indexOf('/') == 0) {
                client.changeWorkingDirectory("/");
            }
            for (int i = 0; i < list.length - 1; i++) {
                if (list[i] != null) {
//					if (!isDirExist(list[i])) {
//						mkdir(list[i]);
//						client.cd(list[i]);
//					}
                    client.makeDirectory(list[i]);
                    client.changeWorkingDirectory(list[i]);
                }
            }
//			client.binary()
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            String fileName = list[list.length - 1];
//			os = client.put(fileName)
            os = client.storeFileStream(fileName);

            File file = new File(localFileName);
            is = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len = 0;
//			// 在这里添加获取速度和 休眠来控制流量的目的
//			Channel channel = map.get(channelName)
//			channel.setStartTime(System.currentTimeMillis())
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
//				channel.setEndTime(System.currentTimeMillis())
//				channel.setDataSize(len)
//				channel.getCurrSpeed()
//				channel.sleep()
            }

        } catch (IOException e) {
            log.error("", e);
            throw new FtpException(FtpErrCode.FILE_UP_ERROR, e);
        } finally {
            synchronized (ControlUtil.CHANNEL_LOCK) {
                if (map != null) {
                    map.remove(this.channelName);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        close();
        return true;
    }

    /**
     * 文件上传
     */
    @Override
    public boolean uploadByAsync() throws FtpException {
        login();
        OutputStream os = null;
        FileInputStream is = null;
        try {
            log.info("upload,remoteFileName:{}", remoteFileName);
            String[] list = remoteFileName.split("/");
            if (remoteFileName.indexOf('/') == 0) {
                client.changeWorkingDirectory("/");
            }
            for (int i = 0; i < list.length - 1; i++) {
                if (list[i] != null) {
//					if(!isDirExist(list[i])) {
//						mkdir(list[i]);
//						client.cd(list[i]);
//					}
                    client.makeDirectory(list[i]);
                    client.changeWorkingDirectory(list[i]);
                }
            }
//			client.binary()
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            String fileName = list[list.length - 1];
            os = client.storeFileStream(fileName);

            File file = new File(localFileName);
            is = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
            log.error("", e);
            throw new FtpException(FtpErrCode.FILE_UP_ERROR, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        close();
        return true;
    }

    /**
     * 文件下载
     */
    @Override
    public boolean download() throws FtpException {
        login();
        InputStream is = null;
        FileOutputStream os = null;
        try {
            String[] list = remoteFileName.split("/");
            if (remoteFileName.indexOf('/') == 0) {
                client.changeWorkingDirectory("/");
            }
            for (int i = 0; i < list.length - 1; i++) {
                if (list[i] != null) {
                    client.changeWorkingDirectory(list[i]);
                }
            }
//			client.binary()
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            String fileName = list[list.length - 1];
//			is = client.get(fileName)
            is = client.retrieveFileStream(fileName);

            int index = localFileName.lastIndexOf('/');
            if (index > 0) {
                File file = new File(localFileName.substring(0, index));
                if (!file.exists())
                    file.mkdirs();
            }
            os = new FileOutputStream(localFileName);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
            log.error("", e);
            throw new FtpException(FtpErrCode.FILE_DOWN_ERROR, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        close();
        return true;
    }

}
