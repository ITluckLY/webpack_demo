package com.dcfs.esb.ftp.utils;

import com.dcfs.esc.ftp.comm.constant.UnitCons;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * ref org.apache.commons.io.FilenameUtils
 * Created by mocg on 2016/6/22.
 */
public class FileUtil {
    /**
     * The file copy buffer size (30 MB)
     */
    private static final int FILE_COPY_BUFFER_SIZE = UnitCons.ONE_MB * 30;

    private FileUtil() {
    }

    public static String convertToLocalPath(String filePath) {
        String filePath2 = filePath;
        if (File.separatorChar == '\\') {//is windows
            filePath2 = filePath2.replaceAll("/+", "\\\\");
        }
        return filePath2;
    }

    public static String convertToSelfPath(String filePath) {
        return filePath.replaceAll("\\\\+", "/");
    }

    public static String delDuplicateSeparator(String path) {
        String path2 = path;
        path2 = path2.replaceAll("/+", "/");
        path2 = path2.replaceAll("\\\\+", "\\\\");
        return path2;
    }

    public static String concatFilePath(String... filePaths) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < filePaths.length; i++) {
            if (i > 0) builder.append('/');
            builder.append(filePaths[i]);
        }
        String s = builder.toString();
        return delDuplicateSeparator(s);
    }

    public static String concatFilePath2Linux(String... filePaths) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < filePaths.length; i++) {
            if (i > 0) builder.append('/');
            builder.append(filePaths[i]);
        }
        String s = builder.toString();
        return delDuplicateSeparator(convertToSelfPath(s));
    }

    public static String concat(String... fileNameParts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fileNameParts.length; i++) {
            if (i > 0) builder.append('_');
            builder.append(fileNameParts[i]);
        }
        return builder.toString();
    }

    public static String randomTimeFileName(String fileName) {
        String ext = FilenameUtils.getExtension(fileName);
        long rdmlong = RandomUtils.nextLong();
        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + '_' + rdmlong + '.' + ext;
    }

    public static String randomTimeFilePath(String ext) {
        Date now = new Date();
        String yyyyMM = DateFormatUtils.format(now, "yyyyMM");
        String dd = DateFormatUtils.format(now, "dd");
        String hh = DateFormatUtils.format(now, "HH");
        String mm = DateFormatUtils.format(now, "mm");
        String ssSSS = DateFormatUtils.format(now, "ssSSS");
        long rdmlong = RandomUtils.nextLong();
        return concatFilePath(yyyyMM, dd, hh, mm, ssSSS + '_' + rdmlong + '.' + ext);
    }

    public static boolean mkdir(String filePath) throws IOException {
        File parentDir = new File(filePath).getParentFile();
        FileUtils.forceMkdir(parentDir);
        return true;
    }

    public static String startWithSeparator(String filePath) {
        if (filePath.charAt(0) == '/') return filePath;
        else {
            return '/' + filePath;
        }
    }

    public static boolean renameTo(File src, File dest) {
        return renameTo(src, dest, false);
    }

    /**
     * 文件重命名
     *
     * @param src
     * @param dest
     * @param orCopy 直接重命名不成功时，是否把源文件拷贝一份，但不保证源文件一定能被删除
     * @return
     */
    public static boolean renameTo(File src, File dest, boolean orCopy) {
        if (src == null || dest == null || !src.exists()) return false;
        boolean destDel = true;
        if (dest.exists()) destDel = dest.delete();
        if (!destDel) return false;

        boolean rename = src.renameTo(dest);
        return rename || (orCopy && renameByCopy(src, dest));
    }

    private static boolean renameByCopy(File src, File dest) {
        if (src.renameTo(dest)) {
            return true;
        } else {
            //ref org.apache.commons.io.FileUtils.doCopyFile
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream(src);
                fos = new FileOutputStream(dest);
                sourceChannel = fis.getChannel();
                destChannel = fos.getChannel();
                long size = sourceChannel.size();
                long pos = 0;
                long count;
                while (pos < size) {
                    count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                    pos += destChannel.transferFrom(sourceChannel, pos, count);
                }
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                IOUtils.closeQuietly(sourceChannel);
                IOUtils.closeQuietly(destChannel);
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(fos);
                //删除src文件
                FileUtils.deleteQuietly(src);
            }
        }
    }

    public static boolean directoryContains(File dir, String fileName) {
        return !(dir == null || fileName == null) && dir.exists() && new File(dir, fileName).exists();
    }

    public static boolean moveFileToDirectoryQuietly(File srcFile, File destDir, boolean createDestDir) {
        if (srcFile == null || !srcFile.exists()) return false;
        try {
            FileUtils.moveFileToDirectory(srcFile, destDir, createDestDir);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
