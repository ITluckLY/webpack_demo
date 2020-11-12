package com.dcfs.esc.ftp.datanode.file;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.server.file.EsbFile;
import com.dcfs.esc.ftp.comm.helper.FileMd5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 文件服务器的文件处理类，处理的功能包括：
 * 1、实现服务端文件的上传和下载；
 * 2、实现客户端文件的上传和下载；
 * 3、实现服务端和客户端文件的MD5校验功能；
 * 4、实现文件服务器的负载均衡控制处理；
 * 5、文件上传的过程中实现文件的远程重命名控制；
 * 6、文件下载过程实现远程服务器文件读取；
 * 7、文件重命名时实现文件的远程重命名控制。
 */
public class EscFile extends EsbFile {
    private static final Logger log = LoggerFactory.getLogger(EscFile.class);

    public EscFile(String filePath, int server, FileMsgBean bean, String lastTmpFilePath) throws FtpException {
        super(filePath, server, bean, lastTmpFilePath);
    }

    @Override
    public void openForWrite(long offset) throws FtpException {
        if (offset > 0) {
            this.deleteFile();
            // 删除相关文件
            // 文件服务器的文件，删除远程文件
            if (type == SERVER)
                this.deleteRemoteFile();
            // 目录不存在，则创建相关目录
            if (!tmpFile.getParentFile().exists())
                tmpFile.getParentFile().mkdirs();
        } else {
            this.deleteFile();
            this.deleteCfgTmp();
            // 删除相关文件
            // 文件服务器的文件，删除远程文件
            if (type == SERVER)
                this.deleteRemoteFile();
            // 目录不存在，则创建相关目录
            if (!tmpFile.getParentFile().exists())
                tmpFile.getParentFile().mkdirs();
        }
        try {
            // 创建临时文件写入流
            writer = new RandomAccessFile(tmpFile, "rw");
            writer.seek(offset);
            cfgWriter = new RandomAccessFile(cfgFile, "rw");
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#打开文件出错", nano, flowNo, e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, flowNo, nano, e);
        }
    }

    public void reMdD5ForRead(final long position) throws IOException {
        RandomAccessFile raf = reader;
        FileMd5Helper.md5(raf, position, md5Alm);
        raf.seek(position);
        offset = position;
    }

    public void reMdD5ForWrite(final long position) throws IOException {
        RandomAccessFile raf = writer;
        FileMd5Helper.md5(raf, position, md5Alm);
        raf.seek(position);
        offset = position;
    }
}
