package com.dcfs.esb.ftp.server.invoke.file;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import net.sf.json.JSONArray;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FileInfoManager {
    private static final Object lock = new Object();
    private static FileInfoManager instance;

    private FileInfoManager() {
    }

    public static FileInfoManager getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (lock) {
                if (instance == null)
                    instance = new FileInfoManager();
            }
            return instance;
        }
    }

    public void downLoad(String path, Socket socket) throws IOException {
        File f = new File(path);
        try (FileInputStream fis = new FileInputStream(f); OutputStream os = socket.getOutputStream()) {
            String l = Long.toString(f.length());
            String length = "0000000000" + l;
            length = length.substring(l.length());
            String messHeader = "000043{\"code\":\"0000\",\"length\":\"" + length + "\"}";
            os.write(messHeader.getBytes("utf-8"));
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        }
    }

    public String selectDir() {
        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().loadFile();
        } catch (FtpException e) {
            throw new NestedRuntimeException("加载配置文件出错", e);
        }
        Element root = XMLDealTool.getRoot(doc);
        List<String> l = new ArrayList<String>();
        List<Element> eleList = root.selectNodes("/FileRoot/BaseFile");
        for (Element e : eleList) {
            l.add(e.attributeValue("path"));
        }
        JSONArray jsonArray = JSONArray.fromObject(l);
        return jsonArray.toString();
    }

    public String getFiles(String path) throws IOException {
        List<FileInfo> list = new ArrayList<FileInfo>();
        File f = new File(path);
        File[] files = f.listFiles();
        if (files != null) {
            for (File item : files) {
                if (item.isFile() && (!(item.getName()).endsWith(SvrGlobalCons.DCFS_CFG_FILE_EXT)
                        && (new File(item.getAbsolutePath() + SvrGlobalCons.DCFS_CFG_FILE_EXT)).exists())) {
                    FileInfo info = getFileInfo(item);
                    list.add(info);
                }
            }
        }
        JSONArray jsonArray = JSONArray.fromObject(list);
        return jsonArray.toString();
    }

    private FileInfo getFileInfo(File file) throws IOException {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getName());
        fileInfo.setPath(file.getAbsolutePath());
        fileInfo.setFileCfgInfo(getFileCfgInfo(new File(file.getAbsolutePath() + SvrGlobalCons.DCFS_CFG_FILE_EXT)));
        return fileInfo;
    }

    private FileCfgInfo getFileCfgInfo(File file) throws IOException {
        FileCfgInfo fileCfgInfo = new FileCfgInfo();
        byte[] buffer;
        try (FileInputStream in = new FileInputStream(file)) {
            int size = in.available();
            buffer = new byte[size];
            in.read(buffer);//NOSONAR
        }

        String str = new String(buffer, "utf-8");
        str = str.substring(str.indexOf('\n', 20) + 1);

        String[] ps = str.split("\r\n");
        String[] s = new String[7];
        for (int j = 0; j < 7; j++) {
            String param = ps[j];
            int idx = param.indexOf('=');
            if (idx < 1) {
                continue;
            }
            s[j] = param.substring(idx + 1);
        }
        fileCfgInfo.setFileName(s[0]);
        fileCfgInfo.setClientIp(s[1]);
        fileCfgInfo.setFileSize(s[2]);
        fileCfgInfo.setClientFileName(s[3]);
        fileCfgInfo.setClientFileMd5(s[4]);
        fileCfgInfo.setCreateTime(s[5]);
        fileCfgInfo.setUser(s[6]);

        return fileCfgInfo;
    }
}
