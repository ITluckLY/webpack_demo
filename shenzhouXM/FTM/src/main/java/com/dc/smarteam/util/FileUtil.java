package com.dc.smarteam.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * ref org.apache.commons.io.FilenameUtils
 * Created by mocg on 2016/6/22.
 */
public class FileUtil {
    public static String convertToLocalPath(String filePath) {
        if (StringUtils.equals(File.separator, "\\")) {
            filePath = filePath.replaceAll("/+", "\\\\");
        }
        return filePath;
    }

    public static String convertToSelfPath(String filePath) {
        return filePath.replaceAll("\\\\+", "/");
    }

    public static String delDuplicateSeparator(String path) {
        path = path.replaceAll("/+", "/");
        path = path.replaceAll("\\\\+", "\\\\");
        return path;
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
        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + "_" + rdmlong + "." + ext;
    }

    public static String randomTimeFilePath(String ext) {
        Date now = new Date();
        String yyyyMM = DateFormatUtils.format(now, "yyyyMM");
        String dd = DateFormatUtils.format(now, "dd");
        String hh = DateFormatUtils.format(now, "HH");
        String mm = DateFormatUtils.format(now, "mm");
        String ssSSS = DateFormatUtils.format(now, "ssSSS");
        long rdmlong = RandomUtils.nextLong();
        return concatFilePath(yyyyMM, dd, hh, mm, ssSSS + "_" + rdmlong + "." + ext);
    }

    public static boolean mkdir(String filePath) throws IOException {
        File parentDir = new File(filePath).getParentFile();
        FileUtils.forceMkdir(parentDir);
        return true;
    }

    public static String startWithSeparator(String filePath) {
        if (filePath.startsWith("/")) return filePath;
        else {
            return "/" + filePath;
        }
    }

    public static void main(String[] args) {
        System.out.println(convertToSelfPath("\\\\\\\\"));
        System.out.println(convertToLocalPath("/////"));
        System.out.println(delDuplicateSeparator("/////\\\\\\\\\\"));
        System.out.println(FileUtil.concatFilePath("a/", "b"));
    }

    public static boolean renameTo(File src, File dest) {
        return !(src == null || dest == null || !src.exists())
                && (src.renameTo(dest) || (!dest.exists() || dest.delete()) && src.renameTo(dest));
    }
}
