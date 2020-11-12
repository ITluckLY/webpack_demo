package com.dcfs.esb.ftp.utils;

import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by mocg on 2016/7/26.
 */
public class FileNameUtils extends FilenameUtils {

    public static String concat(String... parts) {
        String path = parts[0];
        for (int i = 1; i < parts.length; i++) {
            path = FilenameUtils.concat(path, parts[i]);
        }
        return path;
    }

    /**
     * 添加文件结束符name-separator character
     *
     * @param path
     * @return
     */
    public static String addDirectorySepartor(String path) {
        if (!(path.endsWith("/") || path.endsWith("\\"))) {
            return path + File.separator;
        }
        return path;
    }

    /**
     * 检查path是否有 ../ 或 ..\ 等跳转符号,防止防止跳转到其他位置
     *
     * @param path
     */
    public static void checkJumpSymbol(String path) {
        if (path == null) return;
        //不允许路径有 ../ 或 ..\ ,防止防止跳转到其他位置
        if (containsJumpSymbol(path)) {
            throw new NestedRuntimeException("路径不允许有../或..\\等跳转符号#" + path);
        }
    }

    /**
     * 路径是否包含跳转符号
     *
     * @param path
     * @return
     */
    public static boolean containsJumpSymbol(String path) {
        return path != null && (path.contains("../") || path.contains("..\\") || path.contains("~/"));
    }
}
