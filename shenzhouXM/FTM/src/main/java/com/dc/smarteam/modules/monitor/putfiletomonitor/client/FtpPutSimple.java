package com.dc.smarteam.modules.monitor.putfiletomonitor.client;

import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import com.dc.smarteam.modules.monitor.putfiletomonitor.file.EsbFile;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgBean;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgType;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.MD5Util;
import com.dc.smarteam.util.ObjectsTool;
import com.dc.smarteam.util.PropertiesTool;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by huangzbb on 2016/11/10.
 */
public class FtpPutSimple {
    private static final Logger log = LoggerFactory.getLogger(FtpPutSimple.class);

    private String remoteFileName = null;
    private String localFileName = null;
    private FtpConnector conn = null;
    private EsbFile file = null;
    private FileMsgBean bean = null;
    private FtpClientConfig config = null;
    private String remoteFile = null;
    private int pieceNum = 1024 * 8;
    private int idx = 0;
    private String msgType = null;
    private Long remoteFileSize;

    private String ServerAddr = null;
    private Properties prop = null;
    private String ip;
    private int port;
    private String md5;

    public FtpPutSimple(String localFile, String remoteFile, boolean scrtFlag, FtpClientConfig config) throws FtpException {
        this.config = config;
        this.localFileName = localFile;
        this.remoteFileName = remoteFile;
        // 创建上传的文件
        file = new EsbFile(localFileName, EsbFile.CLIENT);
        // 创建处理的信息头，并设置相关初始化参数信息
        bean = new FileMsgBean();
        bean.setFileName(remoteFileName);
        bean.setClientFileName(localFileName);
        bean.setFileSize(file.getSize());
        bean.setScrtFlag(scrtFlag);
    }

    public FtpPutSimple(String localFile, String remoteFile, String msgType, FtpClientConfig config) throws FtpException {
        this.config = config;
        this.localFileName = localFile;
        this.remoteFileName = remoteFile;
        this.msgType = msgType;
        // 创建上传的文件
        file = new EsbFile(localFileName, EsbFile.CLIENT);
        // 创建处理的信息头，并设置相关初始化参数信息
        bean = new FileMsgBean();
        bean.setFileName(remoteFileName);
        bean.setClientFileName(localFileName);
        bean.setFileSize(file.getSize());
    }

    public FileMsgBean doFupFile() throws FtpException {
        try {
            md5 = MD5Util.md5(new File(bean.getClientFileName()));
        } catch (IOException e) {
            throw new FtpException("md5 err", e);
        }
        bean.setMd5(md5);

        // 建立与服务器的连接
        conn = config.getConnector();
        ip = conn.getSocket().getInetAddress().getHostAddress();
        port = conn.getSocket().getPort();

        bean.setClientApiVersion(null);
        //获取pieceNum
        doAuth();
        String remoteFileFromWjcs = getRemoteFileFromWjcs(localFileName);
        bean.setLastRemoteFileName(remoteFileFromWjcs);
        remoteFileSize = bean.getRemoteFileSize();

        log.info("开始上传文件{}", bean.getFileName());
        pieceNum = bean.getPieceNum();
        loadCfgFile(localFileName);
        if (idx == 0) {
            this.remoteFile = bean.getFileName();
        } else {
            //断点续传修改远程文件名为上次传输的文件名
            bean.setFileName(remoteFile);
        }
        bean.setOffset((long)(pieceNum * idx));
        bean.setFileIndex(idx);
        file.openForRead(bean.getOffset());
        // 设置文件上传的数据
        bean.setFileIndex(idx);
        while (true) {
            // 读取本地文件一个分片
            bean.setFileMsgFlag(FileMsgType.PUT);
            bean.addFileIndex();
            file.read(bean);
            // 写入消息头和文件的内容
            conn.writeHead(bean);
            conn.writeFileContent(bean);
            // 读取响应的头信息
            conn.readHead(bean);
            if (FileMsgType.SUCC.equals(bean.getFileMsgFlag())) {
                log.debug("上传文件[{}]" + "第[{}]分片成功", bean.getClientFileName(), bean.getFileIndex());
            } else {
                throw new FtpException(bean.getFileMsgFlag(), bean.getFileRetMsg());
            }
            idx++;
            if (idx > 1) {//当有多个分片时才记录
                saveCfgFile(localFileName);
            }
            if (bean.isLastPiece()) {
                log.info("最后一个分片处理结束");
                file.close();
                deleteCfgFile(localFileName + GlobalCons.CFG_FILE_EXT);
                break;
            }
        }
        // 读取finish响应的头信息
        conn.readHead(bean);
        String resultFlag = bean.getFileMsgFlag().substring(FileMsgType.FINISH.length() + 1);
        if (!FileMsgType.SUCC.equals(resultFlag)) {
            throw new FtpException(resultFlag, bean.getFileRetMsg());
        }
        log.info("文件上传结束,服务端接收到文件[{}]", bean.getRealFileName());
        doUpdate();
        String updateFlag = bean.getFileMsgFlag().substring(FileMsgType.FINISH.length() + 1);
        if (!FileMsgType.SUCC.equals(updateFlag)) {
            log.error("文件发布失败");
            throw new FtpException(updateFlag, bean.getFileRetMsg());
        }
        log.info("文件发布结束");
        return bean;
    }

    private void loadProp(File propfile, Properties prop) throws IOException {
        PropertiesTool.load(prop, propfile);
    }

    private String getRemoteFileFromWjcs(String file) {
        File cfg = new File(file + GlobalCons.CFG_FILE_EXT);
        if (!cfg.exists()) {
            return null;
        }
        File f = new File(file);
        //对比文件的时间戳，如果比wjcs新则说明文件被改动过，放弃续传
        if (f.lastModified() > cfg.lastModified()) {
            log.warn("待传输文件有更改，放弃续传。");
            return null;
        }
        String remoteFile = null;
        Properties prop = new Properties();
        try {
            loadProp(cfg, prop);
            remoteFile = prop.getProperty("file");
        } catch (FileNotFoundException e) {
            log.error("找不到文件[{}]!", cfg);
        } catch (IOException e) {
            log.error("", e);
        }
        return remoteFile;
    }

    private void loadCfgFile(String file) {
        File cfg = new File(file + GlobalCons.CFG_FILE_EXT);
        if (!cfg.exists()) {
            return;
        }
        File f = new File(file);
        //对比文件的时间戳，如果比wjcs新则说明文件被改动过，放弃续传
        if (f.lastModified() > cfg.lastModified()) {
            log.warn("待传输文件有更改，放弃续传。");
            FileUtils.deleteQuietly(cfg);
            return;
        }

        prop = new Properties();
        try {
            loadProp(cfg, prop);
            this.idx = Integer.parseInt(prop.getProperty("idx", "0"));
            int pieceNum = Integer.parseInt(prop.getProperty("pieceNum", "0"));
            this.ServerAddr = prop.getProperty("addr");
            this.remoteFile = prop.getProperty("file");
            String md5 = prop.getProperty("md5");
            boolean isRemoteFileRight = remoteFileSize != null && remoteFileSize > 0 && remoteFileSize == idx * pieceNum;
            if (isRemoteFileRight && ObjectsTool.equals(this.md5, md5) &&
                    ObjectsTool.equals(this.ServerAddr, getSocketAddress(conn.getSocket())) &&
                    this.pieceNum == pieceNum
                    ) {
                //md5校验通过，继续续传
            } else {
                log.warn("文件或传输代码发生改变，放弃续传。#ipPort:{}:{}", ip, port);
                this.idx = 0;
                this.ServerAddr = null;
                this.remoteFile = null;
                FileUtils.deleteQuietly(cfg);
            }
        } catch (FileNotFoundException e) {
            log.error("找不到文件[{}]!", cfg);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private void deleteCfgFile(String cfgFile) {
        File f = new File(cfgFile);
        if (f.exists()) {
            boolean delete = f.delete();
            if (!delete) {
                log.warn("删除上传控制配置文件失败,尝试在退出时删除#{}", cfgFile);
                f.deleteOnExit();
            }
        }
    }

    private FileMsgBean doAuth() throws FtpException {
        log.info("开始进行认证.");
        bean.setFileMsgFlag(FileMsgType.PUT_AUTH);
        conn.writeHead(bean);
        conn.readHead(bean);
        log.info("认证结束.");
        return bean;
    }

    private FileMsgBean doUpdate() throws FtpException {
        log.info("版本更新开始执行.");
        bean.setFileMsgFlag(msgType);
        conn.writeHead(bean);
        conn.readHead(bean);
        log.info("服务端原文件备份[{}]", bean.getBakFileName());
        if (FileMsgType.INCRE_UPDATE.equals(msgType)) {
            log.info("版本增量更新执行结束.");
        } else {
            log.info("版本全量更新执行结束.");
        }
        return bean;
    }

    private String getSocketAddress(Socket socket) {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    private void saveCfgFile(String file) {
        File cfg = new File(file + GlobalCons.CFG_FILE_EXT);
        try {
            if (null == prop) {
                prop = new Properties();
            }
            prop.put("pieceNum", String.valueOf(this.pieceNum));
            prop.put("idx", String.valueOf(this.idx));
            prop.put("addr", getSocketAddress(conn.getSocket()));
            prop.put("file", this.remoteFile);
            FileOutputStream fos = new FileOutputStream(cfg);
            prop.store(fos, "breakpoint config");
            fos.flush();
            fos.close();
        } catch (IOException e) {
            log.error("保存文件[" + cfg + "]出错!", e);
        }
    }
}
