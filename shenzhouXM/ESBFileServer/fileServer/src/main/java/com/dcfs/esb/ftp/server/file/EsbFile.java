package com.dcfs.esb.ftp.server.file;

import com.dcfs.esb.ftp.common.compress.CompressFactory;
import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.common.msg.FileMsgBean;
import com.dcfs.esb.ftp.common.scrt.Md5Alg;
import com.dcfs.esb.ftp.common.scrt.ScrtUtil;
import com.dcfs.esb.ftp.common.socket.FtpConnector;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.interfases.context.CachedContext;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
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
    /* EsbFile类型，挂载 */
    public static final int MOUNT = 3;
    private static final Logger log = LoggerFactory.getLogger(EsbFile.class);
    protected final Object lock = new Object();
    /* 文件的Md5计算对象 */
    protected Md5Alg md5Alm = new Md5Alg();
    /* EsbFile类型标志 */
    protected int type = -1;
    /* 文件的名称 */
    protected String fileName;
    /* 文件的绝对路径 */
    protected String realFileName;
    /* 文件的临时文件名 */
    protected String tmpFileName;
    /* 文件的配置文件名 */
    protected String cfgFileName;
    /* 文件的重命名文件名 */
    protected String rnmFileName;
    /* 文件的配置重命名 */
    protected String cfgRnmFileName;
    /* 文件的属性 */
    protected Properties fileProperties = new Properties();
    /* realFileName对应的File对象 */
    protected File file;
    /* tmpFileName对应的File对象 */
    protected File tmpFile;
    /* cfgFileName对应的File对象 */
    protected File cfgFile;
    /* rnmFileName对应的File对象 */
    protected File rnmFile;
    /* cfgRnmFileName对应的File对象 */
    protected File cfgRnmFile;
    /* 文件的Md5值 */
    protected String fileMd5;
    /* 文件的远程属性 */
    protected boolean isRemoteFile = false;
    /* 文件的远程连接 */
    protected FtpConnector remoteConnect;
    /* tmpFile的写入流，文件下载是客户端有效，文件上传服务端有效 */
    protected RandomAccessFile writer;
    /* realFileName的读取流 */
    protected RandomAccessFile reader;
    /* cfgFile的写入流，服务端有效 */
    protected RandomAccessFile cfgWriter;
    /* 文件的大小，读取文件内容时有效 */
    protected long size = 0;
    /* 文件的便移量，读取文件内容时有效 */
    protected long offset = 0;
    protected boolean md5Valid;

    protected FileLock fileLock;
    protected File lockFile;
    protected RandomAccessFile lockFileRaf;
    protected long nano;
    protected long fileVersion;
    /*流水号*/
    protected String flowNo;
    private String propMd5;

    private static final String CLIENT_FILE_MD5 = "ClientFileMd5";

    public EsbFile(String fileName, int type, CachedContext context) throws FtpException {
        this(fileName, type, null, context);
    }

    public EsbFile(String fileName, int type, FileMsgBean bean, CachedContext context) throws FtpException {
        this(fileName, type, bean, (String) context.get(ContextConstants.STR_UPLOAD_TMP_FILE_KEY));
    }

    public EsbFile(final String fileName, int type, FileMsgBean bean, final String lastTmpFileName) throws FtpException {//NOSONAR
        CapabilityDebugHelper.markCurrTime("newEsbFileBegin");
        this.type = type;
        this.fileName = fileName;
        this.realFileName = fileName;
        if (type == SERVER) {
            // 文件服务器构造的方法
            // 服务器的本地文件，通过文件管理工程转换成绝对的路径
            this.realFileName = EsbFileWorker.getInstance().getFileAbsolutePath(this.fileName);
            log.debug("nano:{}#flowNo:{}#本地文件：{}", nano, flowNo, realFileName);
        } else if (type == MOUNT) {
            // 服务器的本地挂载文件，通过文件管理工程转换成绝对的路径 需要去掉 /节点名称
            String localFileName = this.fileName.substring(this.fileName.indexOf('/', 1));
            this.realFileName = new File(FtpConfig.getInstance().getMountFilePath(), localFileName).getPath();
            log.debug("nano:{}#flowNo:{}#本地挂载文件：{}", nano, flowNo, realFileName);
        }

        // 创建真实文件信息
        file = new File(realFileName);
        this.size = file.length();

        // 创建配置文件信息
        this.cfgFileName = realFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT;
        cfgFile = new File(cfgFileName);

        // 创建临时文件信息
        if (lastTmpFileName != null && lastTmpFileName.length() > 0) {
            this.tmpFileName = lastTmpFileName;
        } else {
            this.tmpFileName = realFileName + "." + NanoHelper.nanos() + SvrGlobalCons.DCFS_TMP_FILE_EXT;
        }
        tmpFile = new File(tmpFileName);

        CapabilityDebugHelper.markCurrTime("newEsbFileEnd");
    }
    /**
     * 构造函数
     *
     * @param fileName 文件名称
     * @param type     1-服务器文件，使用相对路径 2-客户端文件，使用文件绝对路径
     * @throws FtpException
     */
    public EsbFile(String fileName, int type) throws FtpException { //NOSONAR
        CapabilityDebugHelper.markCurrTime("newEsbFileBegin");
        this.type = type;
        this.fileName = fileName;
        this.realFileName = fileName;
        if (type == SERVER) {
            // 文件服务器构造的方法
            // 服务器的本地文件，通过文件管理工程转换成绝对的路径
            this.realFileName = EsbFileWorker.getInstance().getFileAbsolutePath(this.fileName);
            log.debug("nano:{}#flowNo:{}#本地文件：{}", nano, flowNo, realFileName);
        } else if (type == MOUNT) {
            // 服务器的本地挂载文件，通过文件管理工程转换成绝对的路径 需要去掉 /节点名称
            String localFileName = this.fileName.substring(this.fileName.indexOf('/', 1));
            this.realFileName = new File(FtpConfig.getInstance().getMountFilePath(), localFileName).getPath();
            log.debug("nano:{}#flowNo:{}#本地挂载文件：{}", nano, flowNo, realFileName);
        }

        // 创建真实文件信息
        file = new File(realFileName);
        this.size = file.length();

        // 创建临时文件信息
        this.tmpFileName = realFileName + "." + NanoHelper.nanos() + SvrGlobalCons.DCFS_TMP_FILE_EXT;
        tmpFile = new File(tmpFileName);

        // 创建配置文件信息
        this.cfgFileName = realFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT;
        cfgFile = new File(cfgFileName);
        CapabilityDebugHelper.markCurrTime("newEsbFileEnd");
    }

    /**
     * 构造函数(文件重命名用)
     *
     * @param fileName   文件名称
     * @param clientName 重命名文件名称
     * @param type       1-服务器文件，使用相对路径 2-客户端文件，使用文件绝对路径
     * @throws FtpException
     */
    public EsbFile(String fileName, String clientName, int type) throws FtpException {//NOSONAR
        this.type = type;
        this.fileName = fileName;
        this.realFileName = fileName;
        if (type == SERVER) {
            // 文件服务器构造的方法
            // 服务器的本地文件，通过文件管理工程转换成绝对的路径
            this.realFileName = EsbFileWorker.getInstance().getFileAbsolutePath(this.fileName);
            log.debug("nano:{}#flowNo:{}#本地文件:{}", nano, flowNo, fileName);
        }

        // 创建真实文件信息
        file = new File(realFileName);
        this.size = file.length();

        // 创建配置文件信息
        this.cfgFileName = realFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT;
        cfgFile = new File(cfgFileName);

        // 创建重命名文件信息
        this.rnmFileName = realFileName.substring(0, realFileName.lastIndexOf('/') + 1) + clientName;
        this.cfgRnmFileName = realFileName.substring(0, realFileName.lastIndexOf('/') + 1) + clientName + "_bak.cfg";
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

    public boolean lock() throws FtpException {
        try {
            synchronized (lock) {
                lockFile = new File(realFileName + SvrGlobalCons.DCFS_LOCK_FILE_EXT);
                FileUtils.forceMkdir(lockFile.getParentFile());
                lockFileRaf = new RandomAccessFile(lockFile, "rw");
                FileChannel fileChannel = lockFileRaf.getChannel();
                fileLock = fileChannel.tryLock(0, 1, false);
                return fileLock != null && fileLock.isValid();
            }
        } catch (OverlappingFileLockException e) {
            log.debug("nano:{}#flowNo:{}#获取lockFile锁失败", nano, flowNo, e);
            return false;
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#锁住lockFile失败", nano, flowNo, e);
            throw new FtpException(FtpErrCode.LOCK_FILE_LOCKED_ERROR, flowNo, nano, e);
        }
    }

    public void unlock() throws FtpException {
        IOException ioException = null;
        if (fileLock != null) {
            try {
                if (fileLock.isValid()) fileLock.release();
                fileLock = null;
            } catch (IOException e) {
                ioException = e;
                log.error("nano:{}#flowNo:{}#unlock err", nano, flowNo, e);
            }
        }
        if (lockFileRaf != null) {
            try {
                lockFileRaf.close();
                lockFileRaf = null;
            } catch (IOException e) {
                ioException = e;
                log.error("nano:{}#flowNo:{}#unlock err", nano, flowNo, e);
            }
        }
        if (lockFile != null) {
            boolean delete = lockFile.delete();
            if (delete) lockFile = null;
        }
        if (ioException != null) throw new FtpException(FtpErrCode.LOCK_FILE_UNOCK_ERROR, flowNo, nano, ioException);
    }

    /**
     * 设置文件的相关信息
     *
     * @param bean 传入参数
     */
    public void setFileProperties(FileMsgBean bean) {
        Date now = new Date();
        setFilePropertie("ClientIp", bean.getClientIp());
        setFilePropertie("FileName", bean.getFileName());
        setFilePropertie("ClientFileName", bean.getClientFileName());
        setFilePropertie("User", bean.getUid());
        setFilePropertie("CreateTime", DateFormatUtils.format(now, ContextConstants.DATE_FORMAT_PATT));
        setFilePropertie("FileSize", Long.toString(bean.getFileSize()));
        setFilePropertie("version", Long.toString(fileVersion == 0 ? now.getTime() : fileVersion));
        setFilePropertie("tmpFile", tmpFileName);
        setFilePropertie("targetSysname", bean.getTarSysName());
        setFilePropertie("targetFileName", bean.getTarFileName());
        //setFilePropertie("orgiFileName", reqDto.getFileName());//NOSONAR
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
    public void flushFileProperties() throws FtpException {
        try (FileOutputStream fos = new FileOutputStream(cfgFile)) {
            if (fileProperties != null) {
                // 保存到文件中
                setFilePropertie(CLIENT_FILE_MD5, ScrtUtil.encryptEsb(fileMd5));
                fileProperties.store(fos, "file properties");
            }
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#保存文件的配置信息出错", nano, flowNo, e);
            throw new FtpException(FtpErrCode.SAVE_CONFIG_FILE_ERROR, flowNo, nano, e);
        }
    }

    /**
     * 保存文件的配置信息
     */
    public void saveFileProperties() throws FtpException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            //保存完成后，去除前面的curr_idx=X的部分
            cfgWriter.seek(0);
            if (fileProperties != null) {
                // 保存到文件中
                fileProperties.setProperty(CLIENT_FILE_MD5, ScrtUtil.encryptEsb(fileMd5));
                fileProperties.store(bos, "file properties");
                cfgWriter.write(bos.toByteArray());
            }
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#保存文件的配置信息出错", nano, flowNo, e);
            throw new FtpException(FtpErrCode.SAVE_CONFIG_FILE_ERROR, flowNo, nano, e);
        } finally {
            try {
                cfgWriter.close();
            } catch (Exception e) {
                log.error("nano:{}#flowNo:{}#", nano, flowNo, e);//NOSONAR
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
                fileProperties = new Properties();
                PropertiesTool.load(fileProperties, cfgFile);
                // 对MD5进行解码处理
                propMd5 = ScrtUtil.decryptEsb(fileProperties.getProperty(CLIENT_FILE_MD5));
                log.debug("nano:{}#flowNo:{}#读取文件的Md5校验码：{}", nano, flowNo, propMd5);
            }
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#加载文件的配置信息出错", e);
            throw new FtpException(FtpErrCode.LOAD_CONFIG_FILE_ERROR, flowNo, nano, e);
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
            log.error("nano:{}#flowNo:{}#打开文件出错", nano, flowNo, e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, flowNo, nano, e);
        }
        fileMd5 = null;
    }

    /**
     * 读取文件，在服务端用于处理文件的下载，在客户端用于处理文件的下载。
     *
     * @throws FtpException
     */
    public void openForRead(long offset) throws FtpException {
        // 服务端的本地文件，加载配置信息
        if (type == SERVER) this.loadFileProperties();

        // 本地文件，打开文件的输入流
        try {
            size = file.length();
            this.offset = offset;
            reader = new RandomAccessFile(realFileName, "r");
            if (offset > 0) reader.seek(offset);
            if (type == SERVER) cfgWriter = new RandomAccessFile(cfgFile, "rw");
        } catch (FileNotFoundException e) {
            throw new FtpException(FtpErrCode.FILE_NOT_FOUND_ERROR, e);
        } catch (IOException e) {
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, e);
        }
        fileMd5 = null;
    }

    /**
     * 重命名时文件的备份
     */
    protected void fileBakupRnm() {
        rnmFile = new File(rnmFileName);
        cfgRnmFile = new File(cfgRnmFileName);
        if (rnmFile.exists()) {
            // 本地文件如果存在，则重命名
            if (rnmFile.isFile()) {
                File newRnmFile = new File(rnmFileName + "_" + this.getDateTime() + ".bak");
                if (rnmFile.renameTo(newRnmFile)) {
                    log.debug("nano:{}#flowNo:{}#重命名本地文件[{}]成功，重命名后文件[{}]", nano, flowNo, rnmFile, newRnmFile);
                }
                // 原本的配置文件如存在则删除
                File oldCfgRnmFile = new File(rnmFileName + SvrGlobalCons.DCFS_CFG_FILE_EXT);
                if (oldCfgRnmFile.exists() && oldCfgRnmFile.isFile() && oldCfgRnmFile.delete()) {
                    log.info("nano:{}#flowNo:{}#删除文件[ {} ]成功", nano, flowNo, oldCfgRnmFile);
                }
                File newCfgRnmFile = new File(rnmFileName + "_bak.cfg_" + this.getDateTime() + ".bak");
                if (cfgRnmFile.renameTo(newCfgRnmFile)) {
                    log.debug("nano:{}#flowNo:{}#重命名本地文件[{}]成功，重命名后文件[{}]", nano, flowNo, cfgRnmFile, newCfgRnmFile);
                }
            } else {
                log.error("nano:{}#flowNo:{}#{}是目录!", nano, flowNo, rnmFile);
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
        log.debug("nano:{}#flowNo:{}#-----------------------开始重命名文件[{}]", nano, flowNo, file);

        if (file.isFile()) {
            if (file.renameTo(rnmFile)) {
                log.debug("nano:{}#flowNo:{}#重命名文件[{}]成功", nano, flowNo, file.getPath());
            } else {
                log.error("nano:{}#flowNo:{}#重命名文件[{}]出错", nano, flowNo, file.getPath());
                throw new FtpException(FtpErrCode.FILE_RENAME_ERROR, flowNo, nano);
            }
            if (cfgFile.renameTo(cfgRnmFile)) {
                log.debug("nano:{}#flowNo:{}#重命名配置文件[{}]成功", nano, flowNo, cfgFile.getPath());
            } else {
                log.error("nano:{}#flowNo:{}#重命名配置文件[{}]出错", nano, flowNo, cfgFile.getPath());
                throw new FtpException(FtpErrCode.CFG_FILE_RENAME_ERROR, flowNo, nano);
            }
        } else {
            log.error("nano:{}#flowNo:{}#{}是目录", nano, flowNo, cfgFile);//NOSONAR
            throw new FtpException(FtpErrCode.NOT_FILE_ERROR, flowNo, nano);
        }
    }

    // 获取当前日期时间
    protected String getDateTime() {
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
                    log.error("nano:{}#flowNo:{}#删除文件[{}]出错", nano, flowNo, file);
                    throw new FtpException(FtpErrCode.FILE_DELETE_ERROR, flowNo, nano);
                }
            } else {
                log.error("nano:{}#flowNo:{}#{}是目录", nano, flowNo, cfgFile);
                throw new FtpException(FtpErrCode.NOT_FILE_ERROR, flowNo, nano);
            }
        }
    }

    public void deleteCfgTmp() throws FtpException {
        // 配置文件如果存在，则备份
        if (cfgFile.exists()) {
            if (cfgFile.isFile()) {
                if (!cfgFile.delete()) {
                    log.error("nano:{}#flowNo:{}#删除配置文件[{}]出错", nano, flowNo, cfgFile);
                    throw new FtpException(FtpErrCode.CFG_FILE_DELETE_ERROR, flowNo, nano);
                }
            } else {
                log.error("nano:{}#flowNo:{}#{}是目录", nano, flowNo, cfgFile);
                throw new FtpException(FtpErrCode.NOT_FILE_ERROR, flowNo, nano);
            }
        }

        // 临时文件如果存在，则删除
        if (tmpFile.exists()) {
            if (tmpFile.isFile()) {
                if (!tmpFile.delete()) {
                    log.error("nano:{}#flowNo:{}#删除临时文件[{}]出错", nano, flowNo, tmpFile);
                    throw new FtpException(FtpErrCode.TMP_FILE_DELETE_ERROR, flowNo, nano);
                }
            } else {
                log.error("nano:{}#flowNo:{}#{}是目录", nano, flowNo, tmpFile);
                throw new FtpException(FtpErrCode.NOT_FILE_ERROR, flowNo, nano);
            }
        }
    }

    /**
     * 异步删除远程服务器的文件
     */
    public void deleteRemoteFile() {
        //EsbFileDel delFile = new EsbFileDel(this.fileName);//NOSONAR
        //new Thread(delFile).start();//NOSONAR
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
        // 完成文件的MD5校验
        String tmpMd5 = digestFileMd5();
        if (!tmpMd5.equals(md5)) {
            md5Valid = false;
            log.error("nano:{}#flowNo:{}#文件内容MD5校验失败,local={},remote={}", nano, flowNo, tmpMd5, md5);
            throw new FtpException(FtpErrCode.FILE_CHECK_ERROR, flowNo, nano);
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
        if (!md5Valid) throw new FtpException(FtpErrCode.FILE_MD5_VALID_FAIL, flowNo, nano);

        // 在服务器上，保存相关的文件信息
        if (type == SERVER)
            saveFileProperties();
        else {
            try {
                cfgWriter.close();
            } catch (IOException e) {
                log.error("nano:{}#flowNo:{}#", nano, flowNo, e);
            }
            FileUtils.deleteQuietly(cfgFile);
        }
        // 关闭文件的相关流信息
        this.close();

        // 校验成功，完成文件的重命名
        if (!FileUtil.renameTo(tmpFile, file, true)) {
            log.error("nano:{}#flowNo:{}#临时文件 [{}] 转换为正式的文件 [{}] 出错.", nano, flowNo, tmpFile, file);
            throw new FtpException(FtpErrCode.FILE_RENAME_ERROR, flowNo, nano);
        }
    }

    public void writeIdx(int idx) {
        try {
            cfgWriter.seek(0);
            cfgWriter.write(("curr_idx=" + idx).getBytes());
        } catch (IOException e) {
            log.error("nano:{}#flowNo:{}#", nano, flowNo, e);
        }
    }

    /**
     * 关闭文件输入流和输出流的处理
     *
     * @throws FtpException
     */
    public void close() throws FtpException {
        IOException ioException = null;
        try {
            // 关闭文件的输入流
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    ioException = e;
                    log.error("nano:{}#flowNo:{}#关闭文件的输入流err", nano, flowNo, e);
                }
            }

            // 关闭文件的输出流
            if (writer != null) {
                try {
                    writer.close();
                    writer = null;
                } catch (IOException e) {
                    ioException = e;
                    log.error("nano:{}#flowNo:{}#关闭文件的输出流err", nano, flowNo, e);
                }
            }

            // 关闭配置文件的写入对象
            if (cfgWriter != null) {
                try {
                    cfgWriter.close();
                    cfgWriter = null;
                } catch (IOException e) {
                    ioException = e;
                    log.error("nano:{}#flowNo:{}#关闭配置文件的写入对象err", nano, flowNo, e);
                }
            }
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.FILE_CLOSE_ERROR, flowNo, nano, e);
        } finally {
            unlock();
        }
        if (ioException != null) {
            throw new FtpException(FtpErrCode.FILE_CLOSE_ERROR, flowNo, nano, ioException);
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
        log.debug("nano:{}#flowNo:{}#读取文件[{}]的第[{}]个分片", nano, flowNo, bean.getFileName(), bean.getFileIndex());

        if (this.isRemoteFile) {
            // 读取远程对象数据，不进行加密和解密处理
            log.debug("nano:{}#flowNo:{}#读取远程文件的数据", nano, flowNo);
            Boolean scrtFlag = bean.isScrtFlag();
            bean.setScrtFlag(false);
            remoteConnect.writeHead(bean);
            remoteConnect.readHead(bean);
            String errCode = bean.getErrCode();
            if (errCode == null || FtpErrCode.SUCC.equals(errCode)) {
                log.debug("nano:{}#flowNo:{}#读取远程文件[{}]" + "第[{}]分片成功", nano, flowNo, bean.getFileName(), bean.getFileIndex());
                remoteConnect.readCont(bean);
                bean.setScrtFlag(scrtFlag);
            } else {
                throw new FtpException(errCode, flowNo, nano);
            }
            return;
        }

        // 计算文件的偏移量应该是多少
        int pieceNum = bean.getPieceNum();
        long offset1 = ((long) pieceNum) * (bean.getFileIndex() - 1);
        // 判读当前的偏移量与目标偏移量是否相等，如果不等则重新打开文件
        if (offset != offset1) {
            log.info("nano:{}#flowNo:{}#文件offset错误，需要重新打开#{},{}", nano, flowNo, offset, offset1);
            this.close();
            this.openForRead(offset1);
        }

        int num;
        try {
            // 读取文件内容
            byte[] fileCont = bean.getFileCont();
            num = reader.read(fileCont, 0, pieceNum);
            if (num < 0) num = 0;
            offset = offset + num;
            bean.setContLen(num);
            bean.setFileSize(size);
            // MD5校验码不存在，需要计算校验码
            if (fileMd5 == null)
                //length 应该为未压缩前的length
                md5Alm.update(bean.getFileCont(), 0, bean.getContLen());
            if (bean.getCompressFlag() != null) {
                byte[] src = new byte[num];
                System.arraycopy(fileCont, 0, src, 0, num);
                byte[] dest = compress(src, bean.getCompressFlag());
                if (dest.length > src.length) {
                    //压缩文件读取出来的数据块，压缩后壁压缩前数据还大
                    log.info("nano:{}#flowNo:{}#数据块已压缩,无需重新压缩", nano, flowNo);
                    bean.setCompressFlag(null);
                    dest = src;
                }
                bean.setContLen(dest.length);
                bean.setFileCont(dest);
            }
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#FileReadError", nano, flowNo, e);
            throw new FtpException(FtpErrCode.FILE_READ_ERROR, flowNo, nano, e);
        }

        // 读取的内容少于分配或是文件便宜量大于等于文件的大小时是最后一个分片
        if (num < pieceNum || offset >= size) {
            // 设置最后分片的标志
            bean.setLastPiece(true);

            // 设置文件的MD5校验码
            fileMd5 = digestFileMd5();
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
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#写入文件[{}]的第[{}]个分片", nano, flowNo, bean.getFileName(), bean.getFileIndex());
        }
        try {
            byte[] b = bean.getFileCont();

            if (bean.getCompressFlag() != null) {
                byte[] src = new byte[bean.getContLen()];
                System.arraycopy(b, 0, src, 0, bean.getContLen());
                byte[] dest = decompress(src, bean.getCompressFlag());
                writer.write(dest, 0, dest.length);
                bean.setContLen(dest.length);
                bean.setFileCont(dest);
            } else {
                writer.write(b, 0, bean.getContLen());
            }
            md5Alm.update(bean.getFileCont(), 0, bean.getContLen());
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.FILE_WRITE_ERROR, flowNo, nano, e);
        }
    }

    public void write(byte[] fileCont) throws FtpException {
        write(fileCont, 0, fileCont.length);
    }

    public void write(byte[] fileCont, int offset, int len) throws FtpException {
        try {
            writer.write(fileCont, offset, len);
            md5Alm.update(fileCont, offset, len);
        } catch (Exception e) {
            throw new FtpException(FtpErrCode.FILE_WRITE_ERROR, flowNo, nano, e);
        }
    }

    /**
     * 生成本地文件的Md5校验码信息
     *
     * @return 返回本地文件的校验码信息
     */
    public String digestFileMd5() {
        if (fileMd5 == null || fileMd5.length() == 0) fileMd5 = md5Alm.digestAndString();
        return fileMd5;
    }

    public long fileLength() {
        return file == null ? -1 : file.length();
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
        return "realFileName=" + realFileName;
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

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }

    public long getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(long fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getPropMd5() {
        return propMd5;
    }
}
