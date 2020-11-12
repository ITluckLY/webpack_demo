package com.dcfs.esb.ftp.server.tool;

import com.dcfs.esc.ftp.comm.constant.UnitCons;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIP文件工具箱
 */
public class ZipUtil {
    private ZipUtil() {
    }

    /**
     * 日志
     */
    protected static Logger log = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * @param filepath       要解压的zip文件路径
     * @param outputfilepath 解压文件后存放的路径
     * @throws Exception
     */
    public static void extractFile(String filepath, String outputfilepath) throws Exception { //NOSONAR
        File outputfile = new File(outputfilepath);
        if (!outputfile.isDirectory()) {
            outputfile.mkdir();
        }
        File srcfile = new File(filepath);
        if (!srcfile.getName().endsWith(".zip")) {
            // throw new Exception(String.format("%s不是zip文件", filepath))
        }
        ZipFile zipFile = new ZipFile(filepath);// 加载zip文件
        // System.out.println(zipFile.getName()+"共有文件数"+zipFile.size());//打印zip文件包含的文件数
        // 文件夹也包括在内
        try {
            ZipEntry zipentry;// 声明一个zip文件包含文件单一的实体对象
            Enumeration<?> e = zipFile.entries();// 返回 ZIP文件条目的枚举。
            while (e.hasMoreElements()) {// 测试此枚举是否包含更多的元素。
                zipentry = (ZipEntry) e.nextElement();
                if (zipentry.isDirectory()) {// 是否为文件夹而非文件
                    File file = new File(outputfilepath + zipentry.getName());
                    file.mkdir();// 创建文件夹
                } else {
                    InputStream input = zipFile.getInputStream(zipentry);// 得到当前文件的文件流
                    File f = new File(outputfilepath + zipentry.getName());// 创建当前文件
                    // 声明一个输出流
                    try (FileOutputStream fos = new FileOutputStream(f)) {
                        final int bufLen = UnitCons.ONE_KB * 8;
                        byte[] bytes = new byte[bufLen];// 每次读1kb
                        while (input.read(bytes) != -1) {
                            fos.write(bytes);// 将读到的流输出生成一个新的文件
                        }
                        input.close();
                    }
                }
            }
        } finally {
            zipFile.close();
        }
    }

    /**
     * 对文件或文件夹进行压缩
     *
     * @param srcPathName 源文件名
     * @param pathName    压缩后的文件名
     */
    public static void compress(String srcPathName, String pathName, String includeFile) {
        File zipFile = new File(pathName);
        File srcdir = new File(srcPathName);
        if (!srcdir.exists())
            throw new NestedRuntimeException(srcPathName + "不存在！");

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        if (!"".equals(includeFile))
            fileSet.setIncludes(includeFile + "/**"); // 包括哪些文件或文件夹
        // eg:zip.setIncludes("*.java")
        // fileSet.setExcludes(...); 排除哪些文件或文件夹
        zip.addFileset(fileSet);

        zip.execute();
    }

    /**
     * 对文件或文件夹进行压缩
     *
     * @param srcPathName 源文件名
     * @param pathName    压缩后的文件名
     */
    public static void compressZip(String srcPathName, String pathName, String excludeFile) {
        File zipFile = new File(pathName);
        File srcdir = new File(srcPathName);
        if (!srcdir.exists())
            throw new NestedRuntimeException(srcPathName + "不存在！");

        Project prj = new Project();

        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        fileSet.setExcludes(excludeFile); // 排除哪些文件或文件夹
        zip.addFileset(fileSet);

        zip.execute();
    }

    /**
     * 对文件或文件夹进行压缩
     *
     * @param srcPathName 源文件名
     * @param pathName    压缩后的文件名
     */
    public static void compressZip2(String srcPathName, String pathName, String includeFile) {
        File zipFile = new File(pathName);
        File srcdir = new File(srcPathName).getAbsoluteFile();
        if (!srcdir.exists())
            throw new NestedRuntimeException(srcPathName + "不存在！");

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        fileSet.setIncludes(includeFile);
        // fileSet.setExcludes(...); 排除哪些文件或文件夹
        zip.addFileset(fileSet);

        zip.execute();
    }

    /**
     * 以文件流的方式复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @throws Exception 系统异常
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {//NOSONAR
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(targetFile);
            int c;
            byte[] buffer = new byte[1024];//NOSONAR
            while ((c = in.read(buffer)) != -1) {
                out.write(buffer, 0, c);
            }
            out.flush();
        } finally {
            IOUtil.closeQuietly(in, out);
        }
    }
}
