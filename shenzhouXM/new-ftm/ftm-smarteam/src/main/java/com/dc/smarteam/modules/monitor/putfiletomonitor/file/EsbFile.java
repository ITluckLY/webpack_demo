package com.dc.smarteam.modules.monitor.putfiletomonitor.file;

import com.dc.smarteam.cons.GlobalCons;
import com.dc.smarteam.modules.monitor.putfiletomonitor.client.FtpConnector;
import com.dc.smarteam.modules.monitor.putfiletomonitor.compress.CompressFactory;
import com.dc.smarteam.modules.monitor.putfiletomonitor.context.ContextConstants;
import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpErrCode;
import com.dc.smarteam.modules.monitor.putfiletomonitor.error.FtpException;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgBean;
import com.dc.smarteam.modules.monitor.putfiletomonitor.msg.FileMsgType;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.Md5Alg;
import com.dc.smarteam.modules.monitor.putfiletomonitor.scrt.ScrtUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.Properties;

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
public class EsbFile {
    /* EsbFile类型，服务器 */
    public static final int SERVER = 1;
    /* EsbFile类型，客户端 */
    public static final int CLIENT = 2;
    private static Logger log = LoggerFactory.getLogger(EsbFile.class);
    /* 文件的Md5计算对象 */
    protected Md5Alg md5Alm = new Md5Alg();
    /* tmpFile的写入流，文件下载是客户端有效，文件上传服务端有效 */
    protected RandomAccessFile writer = null;
    /* EsbFile类型标志 */
    private int type = -1;
    /* 文件的名称 */
    private String fileName = null;
    /* 文件的绝对路径 */
    private String realFileName = null;
    /* 文件的临时文件名 */
    private String tmpFileName = null;
    /* 文件的配置文件名 */
    private String cfgFileName = null;
    /* 文件的重命名文件名 */
    private String rnmFileName = null;
    /* 文件的配置重命名 */
    private String cfgRnmFileName = null;
    /* 文件的属性 */
    private Properties fileProperties = null;
    /* realFileName对应的File对象 */
    private File file = null;
    /* tmpFileName对应的File对象 */
    private File tmpFile = null;
    /* cfgFileName对应的File对象 */
    private File cfgFile = null;
    /* rnmFileName对应的File对象 */
    private File rnmFile = null;
    /* cfgRnmFileName对应的File对象 */
    private File cfgRnmFile = null;
    /* 文件的Md5值 */
    private String fileMd5 = null;
    /* 文件的远程属性 */
    private boolean isRemoteFile = false;
    /* 文件的远程连接 */
    private FtpConnector remoteConnect = null;
    /* realFileName的读取流 */
    private FileInputStream reader = null;
    /* cfgFile的写入流，服务端有效 */
    private RandomAccessFile cfgWriter = null;
    /* cfgFile的读取流，服务端有效 */
    private FileInputStream cfgReader = null;
    /* 文件的大小，读取文件内容时有效 */
    private long size = 0;
    /* 文件的便移量，读取文件内容时有效 */
    private long offset = 0;

    private boolean md5Valid;

    /**
     * 构造函数
     *
     * @param fileName 文件名称
     * @param type     1-服务器文件，使用相对路径 2-客户端文件，使用文件绝对路径
     * @throws FtpException
     */
    public EsbFile(String fileName, int type) {
        this.type = type;
        this.fileName = fileName;
        this.realFileName = fileName;

        // 创建真实文件信息
        file = new File(realFileName);
        this.size = file.length();

        long timestamp = System.currentTimeMillis();
        // 创建临时文件信息
        this.tmpFileName = realFileName + "." + timestamp + GlobalCons.TMP_FILE_EXT;
        tmpFile = new File(tmpFileName);

        // 创建配置文件信息
        this.cfgFileName = realFileName + GlobalCons.CFG_FILE_EXT;
        cfgFile = new File(cfgFileName);
    }

    /**
     * 构造函数(文件重命名用)
     *
     * @param fileName   文件名称
     * @param clientName 重命名文件名称
     * @param type       1-服务器文件，使用相对路径 2-客户端文件，使用文件绝对路径
     * @throws FtpException
     */
    public EsbFile(String fileName, String clientName, int type) {
        this.type = type;
        this.fileName = fileName;
        this.realFileName = fileName;

        // 创建真实文件信息
        file = new File(realFileName);
        this.size = file.length();

        // 创建配置文件信息
        this.cfgFileName = realFileName + GlobalCons.CFG_FILE_EXT;
        cfgFile = new File(cfgFileName);

        // 创建重命名文件信息
        this.rnmFileName = realFileName.substring(0,
                realFileName.lastIndexOf('/') + 1) + clientName;
        this.cfgRnmFileName = realFileName.substring(0,
                realFileName.lastIndexOf('/') + 1) + clientName + "_bak.cfg";
    }

    public static byte[] compress(byte[] src, String compressFlag) throws Exception {
        return CompressFactory.compress(src, compressFlag);
    }

    public static byte[] decompress(byte[] src, String compressFlag) throws Exception {
        return CompressFactory.decompress(src, compressFlag);
    }

    public boolean hasTempFile() {
        return tmpFile.exists();
    }

    /**
     * 设置文件的相关信息
     *
     * @param bean 传入参数
     */
    public void setFileProperties(FileMsgBean bean) {
        Date now = new Date();
        fileProperties = new Properties();
        setFilePropertie("ClientIp", bean.getClientIp());
        setFilePropertie("FileName", bean.getFileName());
        setFilePropertie("ClientFileName", bean.getClientFileName());
        setFilePropertie("User", bean.getUid());
        setFilePropertie("CreateTime", DateFormatUtils.format(now, ContextConstants.DateFormatPatt));
        setFilePropertie("FileSize", Long.toString(bean.getFileSize()));
        setFilePropertie("version", Long.toString(now.getTime()));
    }

    public void setFilePropertie(String key, String value) {
        fileProperties.setProperty(key, value == null ? "" : value);
    }

    public String getFilePropertie(String key) {
        return fileProperties.getProperty(key);
    }

    /**
     * 保存文件的配置信息
     */
    public void saveFileProperties() throws FtpException {
        try (FileOutputStream os = new FileOutputStream(cfgWriter.getFD());) {
            //保存完成后，去除前面的curr_idx=X的部分
            cfgWriter.seek(0);
            if (fileProperties != null) {
                // 保存到文件中
                fileProperties.setProperty("ClientFileMd5", ScrtUtil.encryptEsb(fileMd5));
                fileProperties.store(os, "file properties");
            }
        } catch (Exception e) {
            log.error("保存文件的配置信息出错", e);
            throw new FtpException(FtpErrCode.SAVE_CONFIG_FILE_ERROR, e);
        } finally {
            try {
                cfgWriter.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    /**
     * 加载文件的配置信息
     *
     * @throws FtpException
     */
    public void loadFileProperties() throws FtpException {
        try {
            if (cfgFile.exists()) {
                // 加载配置信息
                cfgReader = new FileInputStream(cfgFile);
                fileProperties = new Properties();
                fileProperties.load(cfgReader);

                // 对MD5进行解码处理
                fileMd5 = ScrtUtil.decryptEsb(fileProperties.getProperty("ClientFileMd5"));
                log.info("读取文件的Md5校验码：{}", fileMd5);
            }
        } catch (Exception e) {
            log.error("加载文件的配置信息出错", e);
            throw new FtpException(FtpErrCode.LOAD_CONFIG_FILE_ERROR, e);
        }

    }

    /**
     * 创建文件，在服务端用于处理文件的上传，在客户端用于处理文件的下载。
     * 如果文件对应的内容存在则会被删除掉，暂时不提供文件的断点续传功能
     *
     * @throws FtpException
     */
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
            log.error("打开文件出错", e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        }
    }

    /**
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForRead(long offset) throws FtpException {

        // 服务端的本地文件，加载配置信息
        if (type == SERVER)
            this.loadFileProperties();

        // 本地文件，打开文件的输入流
        try {
            size = file.length();
            this.offset = offset;
            reader = new FileInputStream(realFileName);
            cfgWriter = new RandomAccessFile(cfgFile, "rw");
            if (this.offset > 0) {
                long skip = reader.skip(this.offset);
                log.debug("", skip);
            }
        } catch (FileNotFoundException e) {
            throw new FtpException(FtpErrCode.FILE_NOT_FOUND_ERROR, e);
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        }
    }

    /**
     * 重命名时文件的备份
     */
    private void fileBakupRnm() {
        rnmFile = new File(rnmFileName);
        cfgRnmFile = new File(cfgRnmFileName);
        if (rnmFile.exists()) {
            // 本地文件如果存在，则重命名
            if (rnmFile.isFile()) {
                File newRnmFile = new File(rnmFileName + "_" + this.getDateTime() + ".bak");
                if (rnmFile.renameTo(newRnmFile)) {
                    log.debug("重命名本地文件[{}]成功，重命名后文件[{}]", rnmFile, newRnmFile);
                }
                // 原本的配置文件如存在则删除
                File oldCfgRnmFile = new File(rnmFileName + GlobalCons.CFG_FILE_EXT);
                if (oldCfgRnmFile.exists() && oldCfgRnmFile.isFile() && oldCfgRnmFile.delete()) {
                    log.info("删除文件[ {} ]成功", oldCfgRnmFile);
                }
                File newCfgRnmFile = new File(rnmFileName + "_bak.cfg_" + this.getDateTime() + ".bak");
                if (cfgRnmFile.renameTo(newCfgRnmFile)) {
                    log.debug("重命名本地文件[{}]成功，重命名后文件[{}]", cfgRnmFile, newCfgRnmFile);
                }
            } else {
                log.error("{}是目录!", rnmFile);
            }
        }
    }

    /**
     * 重命名文件
     *
     * @throws FtpException
     */
    public void renameFile() throws FtpException {
        this.fileBakupRnm();
        log.info("-----------------------开始重命名文件[{}]", file);

        if (file.isFile()) {
            if (file.renameTo(rnmFile)) {
                log.debug("重命名文件[{}]成功", file);
            } else {
                log.error("重命名文件[{}]出错", file);
                throw new FtpException(FtpErrCode.FILE_RENAME_ERROR);
            }
            if (cfgFile.renameTo(cfgRnmFile)) {
                log.debug("重命名文件[{}]成功", cfgFile);
            } else {
                log.error("重命名文件[{}]出错", cfgFile);
                throw new FtpException(FtpErrCode.FILE_RENAME_ERROR);
            }
        } else {
            log.error("{}是目录", cfgFile);
            throw new FtpException(FtpErrCode.NOT_FILE_ERROR);
        }
    }

    // 获取当前日期时间
    private String getDateTime() {
        return DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmssSSS");
    }

    /**
     * 删除文件，在openForWrite方法中调用，刪除原始文件、临时文件
     *
     * @throws FtpException
     */
    public void deleteFile() throws FtpException {
        // 数据文件如果存在，则备份
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    log.error("删除文件[{}]出错", file);
                    throw new FtpException(FtpErrCode.FILE_DELETE_ERROR);
                }

            } else {
                log.error("{}是目录", cfgFile);
                throw new FtpException(FtpErrCode.NOT_FILE_ERROR);
            }
        }
    }

    public void deleteCfgTmp() throws FtpException {
        // 配置文件如果存在，则备份
        if (cfgFile.exists()) {
            if (cfgFile.isFile()) {
                if (!cfgFile.delete()) {
                    log.error("删除配置文件[{}]出错", cfgFile);
                    throw new FtpException(FtpErrCode.FILE_DELETE_ERROR);
                }
            } else {
                log.error("{}是目录", cfgFile);
                throw new FtpException(FtpErrCode.NOT_FILE_ERROR);
            }
        }

        // 临时文件如果存在，则删除
        if (tmpFile.exists()) {
            if (tmpFile.isFile()) {
                if (!tmpFile.delete()) {
                    log.error("删除临时文件[{}]出错", tmpFile);
                    throw new FtpException(FtpErrCode.FILE_DELETE_ERROR);
                }
            } else {
                log.error("{}是目录", tmpFile);
                throw new FtpException(FtpErrCode.NOT_FILE_ERROR);
            }
        }
    }

    /**
     * 异步删除远程服务器的文件
     */
    public void deleteRemoteFile() {
        EsbFileDel delFile = new EsbFileDel(this.fileName);
        new Thread(delFile).start();
    }

    /**
     * 完成文件的写入之前，要先进行md5的校验。
     * md5校验成功，则将临时文件命名为真正的文件名称；
     * md5校验失败，则不进行临时文件的重命名，保留临时文件以备确认问题。
     *
     * @param md5 远程服务器的md校验码
     * @throws FtpException
     */
    public boolean checkMd5(String md5) throws FtpException {
        // 设置文件的Md5信息
        fileMd5 = (md5 == null ? "" : md5);
        // 完成文件的MD5校验
        String tmpMd5 = this.getFileMd5();
        if (!tmpMd5.equals(fileMd5)) {
            md5Valid = false;
            log.error("文件内容MD5校验失败,local={},remote={}", tmpMd5, md5);
            throw new FtpException(FtpErrCode.FILE_CHECK_ERROR);
        }
        md5Valid = true;
        return true;
    }

    /**
     * 完成文件的写入之前，要先进行md5的校验。
     *
     * @throws FtpException
     */
    public void finish() throws FtpException {
        if (!md5Valid) throw new FtpException("未校验文件MD5或校验失败");

        // 在服务器上，保存相关的文件信息
        if (type == SERVER)
            saveFileProperties();
        else {
            try {
                cfgWriter.close();
            } catch (IOException e) {
                log.error("", e);
            }
            FileUtils.deleteQuietly(cfgFile);
        }
        // 关闭文件的相关流信息
        this.close();

        // 校验成功，完成文件的重命名
        if (!tmpFile.renameTo(file)) {
            log.error("临时文件 [{}] 转换为正式的文件 [{}] 出错.", tmpFile, file);
            throw new FtpException(FtpErrCode.FILE_RENAME_ERROR);
        }
    }

    public void writeIdx(int idx) {
        try {
            cfgWriter.seek(0);
            cfgWriter.write(("curr_idx=" + idx).getBytes());
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 关闭文件输入流和输出流的处理
     *
     * @throws FtpException
     */
    public void close() throws FtpException {
        try {
            // 关闭文件的输入流
            if (reader != null) {
                reader.close();
                reader = null;
            }

            // 关闭文件的输出流
            if (writer != null) {
                writer.close();
                writer = null;
            }

            // 关闭配置文件的写入对象
            if (cfgWriter != null) {
                cfgWriter.close();
                cfgWriter = null;
            }

            // 关闭配置文件的读取对象
            if (cfgReader != null) {
                cfgReader.close();
                cfgReader = null;
            }
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.FILE_CLOSE_ERROR, e);
        }

        // 关闭远程连接对象
        if (remoteConnect != null) {
            remoteConnect.close();
            remoteConnect = null;
        }

    }

    /**
     * 读取文件第几个分片的内容
     *
     * @param bean 输入输出参数
     * @throws FtpException
     */
    public void read(FileMsgBean bean) throws FtpException {
        log.debug("读取文件[{}]的第[{}]个分片", bean.getFileName(), bean.getFileIndex());

        if (this.isRemoteFile) {
            // 读取远程对象数据，不进行加密和解密处理
            log.info("读取远程文件的数据");
            Boolean scrtFlag = bean.isScrtFlag();
            bean.setScrtFlag(false);
            remoteConnect.writeHead(bean);
            remoteConnect.readHead(bean);
            if (FileMsgType.SUCC.equals(bean.getFileMsgFlag())) {
                log.debug("读取远程文件[{}]" + "第[{}]分片成功", bean.getFileName(), bean.getFileIndex());
                remoteConnect.readCont(bean);
                bean.setScrtFlag(scrtFlag);
            } else {
                throw new FtpException(bean.getFileMsgFlag());
            }
            return;
        }

        // 计算文件的偏移量应该是多少
        int pieceNum = bean.getPieceNum();
        long offset1 = (long) (bean.getFileIndex() - 1) * pieceNum;
        // 判读当前的偏移量与目标偏移量是否相等，如果不等则重新打开文件
        if (offset != offset1) {
            log.info("文件offset错误，需要重新打开#{},{}", offset, offset1);
            this.close();
            this.openForRead(offset1);
        }

        int num = 0;
        try {
            // 读取文件内容
            byte b[] = bean.getFileCont();
            num = reader.read(b, 0, pieceNum);
            if (num < 0) num = 0;
            offset = offset + num;
            bean.setContLen(num);
            bean.setFileSize(size);
            // MD5校验码不存在，需要计算校验码
            if (fileMd5 == null)
                //length 应该为未压缩前的length
                md5Alm.update(bean.getFileCont(), 0, bean.getContLen());
            if (bean.getCompressFlag() != null) {
                byte src[] = new byte[num];
                System.arraycopy(b, 0, src, 0, num);
                byte[] dest = compress(src, bean.getCompressFlag());
                if (dest.length > src.length) {
                    //压缩文件读取出来的数据块，压缩后壁压缩前数据还大
                    log.info("数据块已压缩,无需重新压缩");
                    bean.setCompressFlag(null);
                    dest = src;
                }
                bean.setContLen(dest.length);
                bean.setFileCont(dest);
            }
        } catch (Exception e) {
            FtpException fe = new FtpException(FtpErrCode.FILE_READ_ERROR, e);
            log.error(fe.getMessage(), fe);
            throw fe;
        }


        // 读取的内容少于分配或是文件便宜量大于等于文件的大小时是最后一个分片
        if (num < pieceNum || offset >= size) {
            // 设置最后分片的标志
            bean.setLastPiece(true);

            // 设置文件的MD5校验码
            if (fileMd5 == null) fileMd5 = this.getFileMd5();
            bean.setMd5(fileMd5);
        }
    }

    /**
     * 写入文件第几个分片的内容
     *
     * @param bean 输入输出参数
     * @throws FtpException
     */
    public void write(FileMsgBean bean) throws FtpException {
        log.debug("写入文件[{}]的第[{}]个分片", bean.getFileName(), bean.getFileIndex());
        try {
            byte b[] = bean.getFileCont();

            if (bean.getCompressFlag() != null) {
                byte[] src = new byte[bean.getContLen()];
                System.arraycopy(b, 0, src, 0, bean.getContLen());
                byte dest[] = decompress(src, bean.getCompressFlag());
                writer.write(dest, 0, dest.length);
                bean.setContLen(dest.length);
                bean.setFileCont(dest);
            } else {
                writer.write(b, 0, bean.getContLen());
            }
            md5Alm.update(bean.getFileCont(), 0, bean.getContLen());
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.FILE_WRITE_ERROR, e);
        }
    }

    /**
     * 生成本地文件的Md5校验码信息
     *
     * @return 返回本地文件的校验码信息
     */
    public String getFileMd5() {
        byte[] args = this.md5Alm.digest();
        StringBuilder sb = new StringBuilder();
        for (byte arg : args) {
            sb.append(Integer.toHexString((0x000000ff & arg) | 0xffffff00).substring(6));
        }

        String str = sb.toString();
        log.info("生成文件的Md5校验码：{}", str);
        return str;
    }

    /**
     * 获取文件的大小
     *
     * @return 文件的大小
     */
    public long getSize() {
        return size;
    }

    /**
     * 文件类的toString
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("realFileName=").append(realFileName);
        return sb.toString();
    }

    /**
     * 文件服务器端，获取文件是否是远程文件的标志
     *
     * @return true-是远程文件 false-不是远程文件
     */
    public boolean isRemoteFile() {
        return isRemoteFile;
    }

    /**
     * 文件服务器端，设置文件是否是远程文件
     *
     * @param isRemoteFile true-是远程文件 false-不是远程文件
     */
    public void setRemoteFile(boolean isRemoteFile) {
        this.isRemoteFile = isRemoteFile;
    }

    //getter
    public String getFileName() {
        return fileName;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public String getTmpFileName() {
        return tmpFileName;
    }

    public String getCfgFileName() {
        return cfgFileName;
    }

    public String getRnmFileName() {
        return rnmFileName;
    }

    public String getCfgRnmFileName() {
        return cfgRnmFileName;
    }

    public File getFile() {
        return file;
    }

    public File getCfgFile() {
        return cfgFile;
    }

    public File getTmpFile() {
        return tmpFile;
    }
}
