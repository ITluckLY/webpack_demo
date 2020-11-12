package com.dcfs.esc.ftp.comm.util;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

public class ZipFileUtils {
    private static final Logger log = LoggerFactory.getLogger(ZipFileUtils.class);
    private static final int BUF_LEN = 4096;

    private ZipFileUtils() {
    }

    /**
     * 解压缩ZIP文件，将ZIP文件里的内容解压到descFileName目录下
     *
     * @param zipFileName  需要解压的ZIP文件
     * @param descFileName 目标文件
     */
    public static boolean unZipFiles(String zipFileName, String descFileName) {
        String descFileNames = descFileName;
        if (!descFileNames.endsWith(File.separator)) {
            descFileNames = descFileNames + File.separator;
        }
        ZipFile zipFile = null;
        try {
            // 根据ZIP文件创建ZipFile对象
            zipFile = new ZipFile(zipFileName, "GB18030");
            List<ZipEntry> zipEntryList = new ArrayList<ZipEntry>();// 排序
            ZipEntry entry = null;
            String entryName = null;
            String descFileDir = null;
            byte[] buf = new byte[BUF_LEN];
            int readByte = 0;
            // 获取ZIP文件里所有的entry
            @SuppressWarnings("rawtypes")
            Enumeration enums = zipFile.getEntries();
            // 遍历所有entry
            while (enums.hasMoreElements()) {
                entry = (ZipEntry) enums.nextElement();
                zipEntryList.add(entry);

            }
            //SortList<ZipEntry> sl = new SortList<ZipEntry>();//NOSONAR
            //sl.SortLong(zipEntryList, "getTime", "desc");//NOSONAR

            for (ZipEntry zentry : zipEntryList) {
                // 获得entry的名字
                entryName = zentry.getName();
                descFileDir = replaceSeparator(descFileNames + entryName);
                if (zentry.isDirectory()) {
                    // 如果entry是一个目录，则创建目录
                    new File(descFileDir).mkdirs();
                    continue;
                } else {
                    // 如果entry是一个文件，则创建父目录
                    new File(descFileDir).getParentFile().mkdirs();
                }

                File file = new File(descFileDir);
                log.debug("解压文件:{}", file.getAbsolutePath());
                // 打开文件输出流
                OutputStream os = null;
                InputStream is = null;
                try {
                    os = new FileOutputStream(file);
                    // 从ZipFile对象中打开entry的输入流
                    is = zipFile.getInputStream(zentry);
                    while ((readByte = is.read(buf)) != -1) {
                        os.write(buf, 0, readByte);
                    }
                } finally {
                    IOUtil.closeQuietly(is, os);
                }
            }
            log.debug("文件解压成功!{}", zipFileName);
            return true;
        } catch (Exception e) {
            log.debug("文件解压失败#{}", zipFileName, e);
            return false;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e) {
                    log.debug("关闭zipFile失败#{}", zipFileName, e);
                }
            }
        }
    }


    /**
     * zip文件夹 不包括第0层中的文件名含有.dcfsok的文件或文件夹
     *
     * @param srcDirName
     * @param descFileName
     * @return
     */
    public static File zipDcfsingDirectory(String srcDirName, String descFileName) {
        String excludeFileNameRegex = ".+\\.dcfsok.*";
        Pattern excludeFileNamePattern = Pattern.compile(excludeFileNameRegex);
        return zipDirectory(srcDirName, descFileName, excludeFileNamePattern, 0);
    }

    /**
     * 压缩文件或目录
     *
     * @param srcDirName             压缩的根目录
     * @param descFileName           目标zip文件
     * @param excludeFileNamePattern 排除的文件名正则
     * @param regexMaxDirLevel       从0开始，包含
     */
    public static File zipDirectory(String srcDirName, String descFileName, Pattern excludeFileNamePattern, int regexMaxDirLevel) {
        // 判断目录是否存在
        if (srcDirName == null) {
            log.debug("文件压缩失败，目录为空!");
            return null;
        }
        File fileDir = new File(srcDirName);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            log.debug("文件压缩失败，目录不存在!{}", srcDirName);
            return null;
        }
        String dirPath = fileDir.getAbsolutePath();
        File descFile = new File(descFileName);
        FileOutputStream fos = null;
        ZipOutputStream zouts = null;
        try {
            fos = new FileOutputStream(descFile);
            zouts = new ZipOutputStream(fos);
            //zouts.setEncoding("UTF-8"); // linux乱码

            zipDirectoryToZipFile(dirPath, fileDir, zouts, 0, excludeFileNamePattern, regexMaxDirLevel);
            log.debug(" 文件压缩成功!{}", descFileName);
        } catch (Exception e) {
            log.error("文件压缩失败#{}", srcDirName, e);
        } finally {
            IOUtil.closeQuietly(zouts, fos);
        }
        return descFile;

    }

    /**
     * @param dirPath                目录路径
     * @param fileDir                文件信息
     * @param zouts                  输出流
     * @param currDirLevel           从0开始
     * @param excludeFileNamePattern 排除的文件名正则
     * @param regexMaxDirLevel       从0开始，包含
     * @throws IOException
     */
    private static void zipDirectoryToZipFile(String dirPath, File fileDir, ZipOutputStream zouts, int currDirLevel, Pattern excludeFileNamePattern, int regexMaxDirLevel) throws IOException {//NOSONAR
        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            //fileDir不存在
            if (files == null) return;
            // 空的文件夹
            if (files.length == 0) {
                // 目录信息
                ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
                try {
                    //entry.setUnixMode(755);// 解决Linux乱码 目录755
                    zouts.putNextEntry(entry);
                    zouts.closeEntry();
                } catch (Exception e) {
                    log.error("", e);
                }
                return;
            }

            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    //文件层级 文件名正则
                    if (regexMaxDirLevel >= 0 && regexMaxDirLevel >= currDirLevel
                            && excludeFileNamePattern != null && excludeFileNamePattern.matcher(fileName).matches())
                        continue;
                    // 如果是文件，则调用文件压缩方法
                    zipFilesToZipFile(dirPath, file, zouts);
                } else {
                    // 如果是目录，则递归调用
                    zipDirectoryToZipFile(dirPath, file, zouts, currDirLevel + 1, excludeFileNamePattern, regexMaxDirLevel);
                }
            }
        }
    }

    /**
     * 将dirPath下的file文件压缩到ZIP输出流
     *
     * @param dirPath 目录路径
     * @param file    文件
     * @param zouts   输出流
     */
    private static void zipFilesToZipFile(String dirPath, File file, ZipOutputStream zouts) throws IOException {
        FileInputStream fin = null;
        ZipEntry entry;
        if (file.isFile()) {
            try {
                // 创建一个文件输入流
                fin = new FileInputStream(file);
                // 创建一个ZipEntry
                entry = new ZipEntry(getEntryName(dirPath, file));
                entry.setTime(file.lastModified());
                //entry.setUnixMode(644);// Linux中文乱码 文件644
                //存储信息到压缩文件
                zouts.setEncoding("GB18030");
                zouts.putNextEntry(entry);
                // 创建复制缓冲区
                byte[] buf = new byte[BUF_LEN];
                // 复制字节到压缩文件
                int readByte;
                while ((readByte = fin.read(buf)) != -1) {
                    zouts.write(buf, 0, readByte);
                }
                zouts.closeEntry();
            } catch (IOException e) {
                log.error("", e);
                throw e;
            } finally {
                IOUtil.closeQuietly(fin);
            }
        }
    }

    /**
     * 获取待压缩文件在ZIP文件中entry的名字，即相对于跟目录的相对路径名
     *
     * @param dirPath 目录名
     * @param file    entry文件名
     * @return
     */
    private static String getEntryName(String dirPath, File file) {
        String dirPaths = dirPath;
        if (!dirPaths.endsWith(File.separator)) {
            dirPaths = dirPaths + File.separator;
        }
        String filePath = file.getAbsolutePath();
        // 对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储
        if (file.isDirectory()) {
            filePath += "/";
        }
        int index = filePath.indexOf(dirPaths);
        return filePath.substring(index + dirPaths.length());
    }

    /**
     * 根据操作系统不同替换成响应的name-separator character
     *
     * @param path
     * @return
     */
    private static String replaceSeparator(String path) {
        String path2 = path;
        if (File.separator.equals("/")) {
            path2 = path2.replaceAll("\\\\", File.separator);
        } else if (File.separator.equals("\\")) {
            path2 = path2.replaceAll("//", File.separator);
        }
        return path2;
    }

}
