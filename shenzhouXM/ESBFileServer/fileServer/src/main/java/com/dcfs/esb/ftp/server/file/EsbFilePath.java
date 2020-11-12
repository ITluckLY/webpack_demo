package com.dcfs.esb.ftp.server.file;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件服务器文件系统类
 */
public class EsbFilePath {
    private String fileName = null;
    private String fileAbsoluteName = null;
    private HashMap<String, String> fileAuth = new HashMap<>();

    /**
     * 构造函数
     *
     * @param fileName
     * @param fileAbsoluteName
     */
    public EsbFilePath(String fileName, String fileAbsoluteName) {
        this.fileName = fileName;
        this.fileAbsoluteName = fileAbsoluteName;
    }

    /**
     * 功能：获取文件的名称
     *
     * @return 文件名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 功能：获取文件的绝对路径
     *
     * @return 文件的绝对路径
     */
    public String getFileAbsoluteName() {
        return fileAbsoluteName;
    }

    /**
     * 添加用户权限
     *
     * @param user 用户名称
     * @param op   操作标志R-读取 W-写入 A-全部
     * @return 操作状态 true - 成功 false - 失败
     */
    public boolean addFileAuth(String user, String op) {
        fileAuth.put(user, op);
        return true;
    }

    /**
     * 检查用户权限
     *
     * @param user 用户名称
     * @param op   操作标志R-读取 W-写入 A-全部
     * @return 操作状态 true - 成功 false - 失败
     */
    public boolean checkFileAuth(String user, String op) {
        if (op == null || user == null)
            return false;

        String op1 = fileAuth.get(user);
        return op1 != null && (op1.equalsIgnoreCase(op) || op1.equals(EsbFileManager.OP_A));
    }

    public Map<String, String> getFileAuth() {
        return fileAuth;
    }
}
