package com.dcfs.esb.ftp.server.invoke.fileclean;

import com.dcfs.esb.ftp.common.cons.NodeType;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.helper.FileVersionHelper;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodeManager;
import com.dcfs.esb.ftp.server.invoke.service.ServiceCleanManager;
import com.dcfs.esb.ftp.server.model.FileDeleteRecord;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import com.dcfs.esb.ftp.server.tool.ZipUtil;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.BooleanTool;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.dcfs.esc.ftp.comm.helper.NanoHelper;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/6/6.
 */
public class FileCleanManager {
    private static final Logger log = LoggerFactory.getLogger(FileCleanManager.class);
    private static final Object lock = new Object();
    private static FileCleanManager instance;
    private Document doc;
    private Element root;
    private String str = null;
    private CleanThread cleanThread;

    public FileCleanManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadFileClean();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);

        if (NodeType.DATANODE.equals(Cfg.getNodeType())) {
            cleanThread = new CleanThread();
            cleanThread.setDaemon(true);
            cleanThread.start();
        }
    }

    public static FileCleanManager getInstance() {
        if (instance != null) return instance;
        synchronized (lock) {
            if (instance == null) {
                instance = new FileCleanManager();
            }
        }
        return instance;
    }

    public synchronized void reload() throws FtpException {
        if (instance.cleanThread != null) {
            instance.cleanThread.stopThread();
            instance.cleanThread = null;
        }
        CachedCfgDoc.getInstance().reloadFileClean();
        instance = new FileCleanManager();
    }

    public ResultDto<String> add(JSONObject json) {
        JSONObject data = json.getJSONObject("data");
        String id = data.getString("id");
        String srcPath = data.getString("srcPath");//NOSONAR
        String keepTime = data.getString("keepTime");//NOSONAR
        String isBackup = data.getString("isBackup");//NOSONAR
        String backupPath = data.getString("backupPath");//NOSONAR
        String desc = data.getString("desc");
        String state = data.getString("state");//NOSONAR
        String system = data.getString("system");//NOSONAR
        try {
            String cfgPath = Cfg.getFileCleanCfg();
            Element element = root.addElement("fileClean");
            element.addAttribute("id", id);
            element.addAttribute("srcPath", srcPath);
            element.addAttribute("keepTime", keepTime);
            element.addAttribute("isBackup", isBackup);
            element.addAttribute("backupPath", backupPath);
            element.addAttribute("desc", desc);
            element.addAttribute("state", state);
            element.addAttribute("system", system);
            //createFile(doc, cfgPath)
            str = XMLDealTool.xmlWriter(doc, cfgPath);
        } catch (Exception e) {
            log.error("add file clean fail!", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> update(JSONObject json) {
        JSONObject data = json.getJSONObject("data");
        String id = data.getString("id");
        String srcPath = data.getString("srcPath");
        String keepTime = data.getString("keepTime");
        String isBackup = data.getString("isBackup");
        String backupPath = data.getString("backupPath");
        String desc = data.getString("desc");
        String state = data.getString("state");
        try {
            String cfgPath = Cfg.getFileCleanCfg();
            for (Object obj : root.elements()) {
                Element element = (Element) obj;
                if (id.equals(element.attributeValue("id"))) {
                    element.addAttribute("id", id);
                    element.addAttribute("srcPath", srcPath);
                    element.addAttribute("keepTime", keepTime);
                    element.addAttribute("isBackup", isBackup);
                    element.addAttribute("backupPath", backupPath);
                    element.addAttribute("desc", desc);
                    element.addAttribute("state", state);
                    //createFile(doc, cfgPath)
                    str = XMLDealTool.xmlWriter(doc, cfgPath);
                }
            }
        } catch (Exception e) {
            log.error("add file clean fail!", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> delete(JSONObject json) {
        String id = json.getJSONObject("data").getString("id");
        try {
            String cfgPath = Cfg.getFileCleanCfg();
            for (Object obj : root.elements()) {
                Element element = (Element) obj;
                if (id.equals(element.attributeValue("id"))) {
                    root.remove(element);
                }
            }
            //createFile(doc, cfgPath)
            str = XMLDealTool.xmlWriter(doc, cfgPath);
        } catch (Exception e) {
            log.error("delete file clean fail!", e);//NOSONAR
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> select() {
        try {
            str = XMLtoJSON.getJSONFromXMLEle(root);
        } catch (Exception e) {
            log.error("delete file clean fail!", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query(JSONObject json) {
        try {
            str = XMLtoJSON.getJSONFromXMLEle(root);
        } catch (Exception e) {
            log.error("delete file clean fail!", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> clean(JSONObject json) {
        try {
            List<Element> children = root.elements();
            String ids = json.getJSONObject("data").getString("id");
            String[] idA = ids.split(",");
            for (Element child : children) {
                for (String id : idA) {
                    String childId = child.attributeValue("id");
                    if (id.equals(childId)) {
                        String srcPath = child.attributeValue("srcPath");
                        long keepTime = StringTool.toLong(child.attributeValue("keepTime"), 3600L);//NOSONAR
                        boolean isBackup = BooleanTool.toBoolean(child.attributeValue("isBackup"));
                        String backupPath = child.attributeValue("backupPath");
                        String desc = child.attributeValue("desc");
                        String system = child.attributeValue("system");
                        FileClean fileClean = new FileClean(childId, srcPath, keepTime, isBackup, backupPath, desc, system);
                        clean(fileClean, true);
                    }
                }
            }
            str = XMLtoJSON.getJSONFromXMLEle(root);
        } catch (Exception e) {
            log.error("delete file clean fail!", e);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public void createFile(Document doc, String fileName) {
        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");
            FileOutputStream fos = new FileOutputStream(fileName);
            XMLWriter writer = new XMLWriter(fos, format);
            writer.write(doc);
            writer.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public void clean(FileClean fileClean, boolean force) {
        String srcPath = fileClean.getSrcPath();
        File file = getAbsSrcFile(srcPath);
        if (file.exists()) {
            boolean isBackup = fileClean.isBackup();
            long keepTime = fileClean.getKeepTime();
            String backupPath = fileClean.getBackupPath();
            File tmpDir = new File(FileUtils.getTempDirectory() + File.separator + "DCFStmp" + NanoHelper.nanos());
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
            if (backupPath != null) {
                backupPath = getAbsBackupPath(backupPath);
                File destFile = new File(backupPath);
                if (!destFile.exists()) {
                    destFile.mkdirs();
                }
                cleanFile(file, isBackup, keepTime, force, tmpDir);

                File[] files = tmpDir.listFiles();
                if (files != null && files.length > 0) {
                    String zipFilePath = backupPath + File.separator + file.getName() + "_" + new Date().getTime() + ".zip";
                    ZipUtil.compressZip(tmpDir.getAbsolutePath(), zipFilePath, null);
                }
            }
            try {
                FileUtils.forceDelete(tmpDir);
            } catch (IOException e) {
                log.warn("fileClean error#" + tmpDir.getPath(), e);
            }
        }
    }

    private void cleanFile(File srcFile, boolean isBackup, long keepTime, boolean force, File tmpDir) {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    cleanFile(subFile, isBackup, keepTime, force, tmpDir);
                }
            }
        } else if (srcFile.isFile()) {
            String srcFilePath = srcFile.getPath();
            if (srcFilePath.endsWith(SvrGlobalCons.DCFS_DEL_FILE_EXT)) {
                boolean delete = srcFile.delete();
                log.debug("删除dcdel文件#{}#{}", srcFilePath, delete);
            } else if (force || (new Date().getTime() - srcFile.lastModified() > 60000L * keepTime)) {//NOSONAR
                //带点的文件后缀
                String extension = '.' + FilenameUtils.getExtension(srcFilePath);
                if (isDcfsFileToDirectDel(extension)) {
                    boolean delete = srcFile.delete();
                    log.debug("清理dcfs文件#{}#{}", srcFilePath, delete);
                } else if (!extension.equals(SvrGlobalCons.DCFS_CFG_FILE_EXT)) {//不是配置文件
                    boolean delete;
                    if (isBackup) {
                        File tmpFile = new File(tmpDir, srcFile.getName());
                        delete = srcFile.renameTo(tmpFile);
                    } else {
                        delete = srcFile.delete();
                    }
                    log.debug("清理文件#{}#{}", srcFilePath, delete);
                    //发送删除文件消息
                    if (delete) {
                        String rootPath = FtpConfig.getInstance().getFileRootPath();
                        String selfRootPath = new File(rootPath).getAbsolutePath();
                        String subpath = srcFilePath.substring(selfRootPath.length(), srcFilePath.length());
                        String unixPath = FilenameUtils.separatorsToUnix(subpath);
                        if (!unixPath.startsWith("/")) unixPath = '/' + unixPath;
                        FileDeleteRecord record = new FileDeleteRecord();
                        record.setFilePath(unixPath);
                        record.setNodeName(FtpConfig.getInstance().getNodeName());
                        record.setSysname(NodeManager.getInstance().getSysName());
                        record.setFileVersion(FileVersionHelper.getFileVersion(srcFilePath));
                        record.setDelTime(new Date());
                        EsbFileService.getInstance().logFileDelete(record);
                    }
                    //处理对应的配置文件
                    File cfgFile = new File(srcFilePath + SvrGlobalCons.DCFS_CFG_FILE_EXT);
                    if (cfgFile.exists() && cfgFile.isFile()) {
                        boolean delete2;
                        if (isBackup) {
                            File tmpCfgFile = new File(tmpDir, srcFile.getName() + SvrGlobalCons.DCFS_CFG_FILE_EXT);
                            delete2 = cfgFile.renameTo(tmpCfgFile);
                        } else {
                            delete2 = cfgFile.delete();
                        }
                        log.debug("清理文件配置文件#{}#{}", srcFilePath, delete2);
                    }
                }
            }
        }
    }

    /**
     * 不包括 DCFS_CFG_FILE_EXT 文件
     *
     * @param fileExt 带点的文件后缀
     * @return
     */
    private boolean isDcfsFileToDirectDel(String fileExt) {
        return fileExt.equals(CommGlobalCons.DCFS_TMP_FILE_EXT)
                || fileExt.equals(CommGlobalCons.DCFS_LOCK_FILE_EXT)
                || fileExt.equals(CommGlobalCons.DCFS_FILE_UPLOADED_FILE_EXT)
                || fileExt.equals(CommGlobalCons.DCFS_FILE_UPLOADING_FILE_EXT);
    }

    private File getAbsSrcFile(String srcPath) {
        String rootPath = FtpConfig.getInstance().getFileRootPath();
        String path = FileUtil.concatFilePath(rootPath, srcPath);
        return new File(FileUtil.convertToLocalPath(path));
    }

    private String getAbsBackupPath(String backupPath) {
        String backupRootpath = FtpConfig.getInstance().getFileBackupRootPath();
        String path = FileUtil.concatFilePath(backupRootpath, backupPath);
        return FileUtil.convertToLocalPath(path);
    }

    class CleanThread extends Thread {
        private boolean cleanThreadRunning = true;

        @Override
        public void run() {
            log.debug("开始运行文件清理...");
            while (cleanThreadRunning) {
                try {
                    if (root != null) {
                        List<Element> children = root.elements();
                        for (Element child : children) {
                            if (!cleanThreadRunning) break;
                            if (!"1".equals(child.attributeValue("state"))) continue;
                            String id = child.attributeValue("id");
                            String srcPath = child.attributeValue("srcPath");
                            long keepTime = StringTool.toLong(child.attributeValue("keepTime"), 3600L);
                            boolean isBackup = BooleanTool.toBoolean(child.attributeValue("isBackup"));
                            String backupPath = child.attributeValue("backupPath");
                            String desc = child.attributeValue("desc");
                            String system = child.attributeValue("system");
                            FileClean fileClean = new FileClean(id, srcPath, keepTime, isBackup, backupPath, desc, system);
                            clean(fileClean, false);
                        }
                    }
                    if (!cleanThreadRunning) break;
                    ServiceCleanManager.getInstance().clean();
                } catch (Exception e) {
                    log.error("清理文件err", e);
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e1) {
                    log.debug("CleanThread sleep interrupted", e1);
                    Thread.currentThread().interrupt();
                }
            }
            log.debug("文件清理运行结束.");
        }

        public void stopThread() {
            cleanThreadRunning = false;
            try {
                this.interrupt();
            } catch (Exception e) {
                log.error("中断文件清理线程出错", e);
            }
        }
    }
}
