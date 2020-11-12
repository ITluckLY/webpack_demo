package com.dcfs.esb.ftp.server.invoke.service;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.helper.FileVersionHelper;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodeWorker;
import com.dcfs.esb.ftp.server.model.FileDeleteRecord;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.StringTool;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by huangzbb on 2016/10/27.
 */
public class ServiceCleanManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceCleanManager.class);

    private Element root;

    private ServiceCleanManager() {
        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().loadServicesInfo();
        } catch (FtpException e) {
            throw new NestedRuntimeException("加载配置文件出错", e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static ServiceCleanManager getInstance() {
        return new ServiceCleanManager();
    }

    public void clean() {
        String xpath = "/services/service";
        List<Element> serviceList = root.selectNodes(xpath);
        for (Element service : serviceList) {
            long filePeriod = StringTool.toLong(service.attribute("filePeriod").getValue(), 0L);
            String trancode = service.attribute("trancode").getValue();
            String sysname = service.attribute("sysname").getValue();
            if (filePeriod < 1) continue;
            xpath = String.format("/services/service[@sysname='%s'][@trancode='%s']/putAuth/user", sysname, trancode);
            List<Element> userList = service.selectNodes(xpath);
            for (Element user : userList) {
                String directoy = user.attribute("directoy").getValue();
                cleanFile(directoy, filePeriod);
            }
        }
    }

    public void cleanFile(String directoy, long filePeriod) {
        File file = getAbsSrcFile(directoy);
        if (file.exists()) {
            cleanFile(file, filePeriod);
        }
    }

    private void cleanFile(File srcFile, long filePeriod) {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    cleanFile(subFile, filePeriod);
                }
            }
        } else if (srcFile.isFile()) {
            String srcFilePath = srcFile.getPath();
            if (srcFilePath.endsWith(CommGlobalCons.DCFS_DEL_FILE_EXT)) {
                boolean delete = srcFile.delete();
                log.debug("删除dcfsdel文件#{}#{}", srcFilePath, delete);
            } else if ((new Date().getTime() - srcFile.lastModified() > 60000L * filePeriod)) {//NOSONAR
                //带点的文件后缀
                String extension = '.' + FilenameUtils.getExtension(srcFilePath);
                if (isDcfsFileToDirectDel(extension)) {
                    boolean delete = srcFile.delete();
                    log.debug("清理dcfs文件#{}#{}", srcFilePath, delete);
                } else if (!extension.equals(CommGlobalCons.DCFS_CFG_FILE_EXT)) {//不是配置文件
                    boolean delete = srcFile.delete();
                    log.debug("清理文件#{}#{}", srcFilePath, delete);
                    if (delete) {
                        String rootPath = FtpConfig.getInstance().getFileRootPath();
                        String selfRootPath = new File(rootPath).getAbsolutePath();
                        String subpath = srcFilePath.substring(selfRootPath.length(), srcFilePath.length());
                        String unixPath = FilenameUtils.separatorsToUnix(subpath);
                        if (!unixPath.startsWith("/")) unixPath = '/' + unixPath;
                        FileDeleteRecord record = new FileDeleteRecord();
                        record.setFilePath(unixPath);
                        record.setNodeName(FtpConfig.getInstance().getNodeName());
                        record.setSysname(NodeWorker.getInstance().getSysName());
                        record.setFileVersion(FileVersionHelper.getFileVersion(srcFilePath));
                        record.setDelTime(new Date());
                        EsbFileService.getInstance().logFileDelete(record);

                        //处理对应的配置文件
                        File cfgFile = new File(srcFilePath + CommGlobalCons.DCFS_CFG_FILE_EXT);
                        if (cfgFile.exists() && cfgFile.isFile()) {
                            boolean delete2 = cfgFile.delete();
                            log.debug("清理文件配置文件#{}#{}", cfgFile.getPath(), delete2);
                        }
                    }
                }
            }
        }
    }

    private File getAbsSrcFile(String srcPath) {
        String rootPath = FtpConfig.getInstance().getFileRootPath();
        String path = FileUtil.concatFilePath(rootPath, srcPath);
        return new File(FileUtil.convertToLocalPath(path));
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

}
