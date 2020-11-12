package com.dcfs.esb.ftp.server.invoke.file;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.impls.context.ContextConstants;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.server.invoke.node.NodeManager;
import com.dcfs.esb.ftp.server.model.FileDeleteRecord;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.spring.outservice.EsbFileService;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esb.ftp.utils.PropertiesTool;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import com.dcfs.esc.ftp.svr.comm.cons.SvrGlobalCons;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private Element root;

    private String key; // is path
    private String rename;

    private int startIndex = 0;
    private int endIndex = 15;//NOSONAR

    private String rootPath;

    private String str = null;

    private FileManager() {
        Document doc;
        try {
            doc = CachedCfgDoc.getInstance().loadFile();
        } catch (FtpException e) {
            throw new NestedRuntimeException("加载配置文件出错", e);
        }
        root = XMLDealTool.getRoot(doc);
        rootPath = FtpConfig.getInstance().getFileRootPath();
    }

    public static FileManager getInstance() {
        return new FileManager();
    }

    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        key = MessDealTool.getString(data, "key");
        rename = MessDealTool.getString(data, "rename");
        String startIndexStr = MessDealTool.getString(data, "startIndex");
        if (startIndexStr != null) {
            this.startIndex = Integer.parseInt(startIndexStr);
        }
        String endIndexStr = MessDealTool.getString(data, "endIndex");
        if (endIndexStr != null) {
            this.endIndex = Integer.parseInt(endIndexStr);
        }
    }

    public ResultDto<String> getFileTree(JSONObject jsonObject) {//NOSONAR
        File file = new File(rootPath);
        File[] list = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (list == null) return ResultDtoTool.buildError("根目录不存在");

        List<Map<String, String>> fileMapList = new ArrayList<>();
        for (File aList : list) {
            Map<String, String> map = new HashMap<>();
            map.put("path", aList.getName());
            fileMapList.add(map);
        }
        str = GsonUtil.toJson(fileMapList);
        //[path:val] eg:[{"@path":"aa"},{"@path":"esb"}]
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByKey(JSONObject jsonObject) {//NOSONAR
        // 通过传入路径获取文件
        load(jsonObject);
        File file = new File(rootPath + File.separator + key);
        if (file.isDirectory()) {
            List<JSONObject> children = new ArrayList<>();
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile()
                            && !pathname.getName().endsWith(SvrGlobalCons.DCFS_CFG_FILE_EXT)
                            && new File(pathname.getAbsolutePath() + SvrGlobalCons.DCFS_CFG_FILE_EXT).exists();
                }
            });
            if (files != null) {
                for (File child : files) {
                    try {
                        JSONObject o = getContent(child);
                        if (o != null) children.add(o);
                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
            }
            startIndex = Math.min(startIndex, endIndex);
            if (endIndex < children.size()) {
                endIndex = children.size();
                List<JSONObject> subChildren = children.subList(Math.min(children.size(), startIndex), endIndex);
                str = JSONArray.fromObject(subChildren).toString();
            } else {
                List<JSONObject> subChildren = children.subList(Math.min(children.size(), startIndex), Math.min(children.size(), endIndex));
                str = JSONArray.fromObject(subChildren).toString();
            }
            //if(children.size()>endIndex){endIndex=children.size();}//NOSONAR
            //List<JSONObject> subChildren = children.subList(Math.min(children.size(), startIndex), Math.min(children.size(), endIndex))
            //str = JSONArray.fromObject(subChildren).toString()
        } else if (file.isFile()) {
            JSONObject obj = null;
            try {
                obj = getContent(file);
                if (obj != null) str = obj.toString();
            } catch (IOException e) {
                return ResultDtoTool.buildError("操作文件异常");
            }
        } else {
            return ResultDtoTool.buildError("没有找到指定文件或目录");
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> subDir(JSONObject jsonObject) {

        load(jsonObject);

        File file = new File(rootPath + File.separator + key);
        if (file.isDirectory()) {
            List<JSONObject> children = new ArrayList<JSONObject>();
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (File child : files) {
                JSONObject obj = new JSONObject();
                obj.put("id", key + "/" + child.getName());
                obj.put("name", child.getName());
                obj.put("path", file.getPath());
                obj.put("pid", key);
                obj.put("isParent", "true");
                obj.put("open", "false");
                children.add(obj);
            }

            str = JSONArray.fromObject(children).toString();
        } else {
            return ResultDtoTool.buildError("1001", "没有子节点");
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query(JSONObject jsonObject) {//NOSONAR
        load(jsonObject);

        JSONObject conditions = JSONObject.fromObject(key);
        String fileName = MessDealTool.getString(conditions, "fileName");
        if (StringUtils.isEmpty(fileName)) {
            fileName = "*";
        } else {
            fileName = "*" + fileName + "*";
        }
        String systemName = MessDealTool.getString(conditions, "systemName");
        String createDate = MessDealTool.getString(conditions, "createDate");
        // String fileSize = MessDealTool.getString(conditions, "fileSize")
        // String remarks = MessDealTool.getString(conditions, "remarks")
        String parentPath = MessDealTool.getString(conditions, "parentPath");

        String path = FileUtil.convertToLocalPath(FileUtil.concatFilePath(rootPath, parentPath));
        List<File> list = new ArrayList<>();
        FileSearcher.findFiles(path, fileName, list);

        List<JSONObject> children = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            File child = list.get(i);
            if (child.isFile()
                    && !(child.getName()).endsWith(SvrGlobalCons.DCFS_CFG_FILE_EXT)
                    && (new File(child.getAbsolutePath() + SvrGlobalCons.DCFS_CFG_FILE_EXT)).exists()) {
                String filePath = child.getAbsolutePath();
                if (StringUtils.isNotEmpty(systemName) && filePath.contains(systemName)) {
                    // 暂时认为路径中包含节点组名称
                    continue;
                }
                if (StringUtils.isNotEmpty(parentPath) && !FilenameUtils.normalize(filePath, true).contains(parentPath)) {
                    continue;
                }
                // 根据日期判断
                if (StringUtils.isNotEmpty(createDate)) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(child.lastModified());
                    //createDate=sdf.format(createDate)
                    String lastModifiedDate = sdf.format(cal.getTime());
                    if (!createDate.equals(lastModifiedDate)) {
                        continue;
                    }
                }
                try {
                    JSONObject o = getContent(child);
                    if (o != null) children.add(o);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        startIndex = Math.min(startIndex, endIndex);
        if (endIndex < children.size()) {
            endIndex = children.size();
            List<JSONObject> subChildren = children.subList(Math.min(children.size(), startIndex), endIndex);
            str = JSONArray.fromObject(subChildren).toString();
        } else {
            List<JSONObject> subChildren = children.subList(Math.min(children.size(), startIndex), Math.min(children.size(), endIndex));
            str = JSONArray.fromObject(subChildren).toString();
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> downLoad(JSONObject jsonObject, Socket socket) {
        load(jsonObject);

        String filePath = rootPath + key;
        filePath = FileUtil.convertToLocalPath(filePath);
        File file = new File(filePath);
        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                byte[] sendBytes = new byte[1024];
                int length;
                while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                    dos.write(sendBytes, 0, length);
                    dos.flush();
                }
                //dos.close();//这里会关闭socket
                str = "下载文件成功";
            } catch (Exception e) {
                str = "读取文件出错";
                return ResultDtoTool.buildError(str);
            }

        } else {
            str = "文件[" + key + "]不存在";
            return ResultDtoTool.buildError(str);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> rename(JSONObject jsonObject) throws FtpException {
        load(jsonObject);

        String filePath = rootPath + key;
        filePath = FileUtil.convertToLocalPath(filePath);

        File file = new File(filePath);
        File cfgFile = new File(filePath + SvrGlobalCons.DCFS_CFG_FILE_EXT);
        rename = file.getParent() + rename;
        File newFile = new File(rename);
        File newCfgFile = new File(rename + SvrGlobalCons.DCFS_CFG_FILE_EXT);

        if (log.isInfoEnabled())
            log.info("-----------------------开始重命名文件[" + file + "]");

        if (file.isFile()) {
            if (file.renameTo(newFile)) {
                log.debug("文件重命名[{}]成功", file);
            } else {
                log.debug("文件重命名[{}]出错", file);
                str = "文件[" + file + "]重命名出错";
                return ResultDtoTool.buildError(str);
            }
            if (cfgFile.renameTo(newCfgFile)) {
                log.debug("配置文件重命名[{}]成功", cfgFile);
                str = "文件[" + file + "]重命名成功";
            } else {
                log.debug("配置文件重命名[{}]出错", cfgFile);
                str = "文件[" + file + "]重命名出错}";
                return ResultDtoTool.buildError(str);
            }
        } else {
            log.error("{}是目录！", file);
            str = "文件[" + file + "]是目录";
            return ResultDtoTool.buildError(str);
        }
        return ResultDtoTool.buildSucceed(str);
    }

    public void convert(JSONObject jsonObject) {
        load(jsonObject);
    }

    private JSONObject getContent(File file) throws IOException {
        JSONObject obj = new JSONObject();
        String selfRootPath = FileUtil.convertToSelfPath(new File(rootPath).getAbsolutePath());
        obj.put("id", FileUtil.convertToSelfPath(file.getAbsolutePath()).replace(selfRootPath, ""));
        obj.put("name", file.getName());
        obj.put("path", FileUtil.convertToSelfPath(file.getParentFile().getPath()).replace(selfRootPath, ""));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        obj.put("lastModifiedDate", DateFormatUtils.format(cal, ContextConstants.DATE_FORMAT_PATT));

        Properties fileProperties = new Properties();

        try (FileInputStream in = new FileInputStream(file.getAbsoluteFile() + SvrGlobalCons.DCFS_CFG_FILE_EXT)) {
            fileProperties.load(in);
        } catch (IOException e1) {
            log.error("", e1);
            str = "{\"message\":\"读取Properties文件出错\"}";
            return null;
        }
        obj.put("clientIp", fileProperties.getProperty("ClientIp"));
        obj.put("size", fileProperties.getProperty("FileSize"));
        obj.put("clientFileName", fileProperties.getProperty("ClientFileName"));
        obj.put("clientFileMd5", fileProperties.getProperty("ClientFileMd5"));
        obj.put("createTime", fileProperties.getProperty("CreateTime"));
        obj.put("systemName", fileProperties.getProperty("User"));

        return obj;
    }

    /**
     * 创建目录
     *
     * @param jsonObject key
     * @return
     */
    public ResultDto<String> mkdir(JSONObject jsonObject) {
        load(jsonObject);

        String filePath = rootPath + key;
        filePath = FileUtil.convertToLocalPath(filePath);
        File dir = new File(filePath);
        int result;
        if (dir.exists()) {
            result = -1;
            if (log.isDebugEnabled()) log.debug("创建目录#" + filePath + "#已存在");
        } else {
            boolean mkdirs = dir.mkdirs();
            result = mkdirs ? 1 : 0;
            if (log.isDebugEnabled()) log.debug("创建目录#" + filePath + "#" + mkdirs);
        }

        if (result > 0) {
            str = "目录创建成功,result:" + result;
            return ResultDtoTool.buildSucceed(str);
        } else {
            str = "目录创建失败,result:" + result;
        }
        return ResultDtoTool.buildError(str);
    }

    /**
     * 先做逻辑删除（文件名称后加.del），后面文件定时清理时一并物理删除掉
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> delFile(JSONObject jsonObject) {
        load(jsonObject);
        String fileName = key;
        String result = delFile(fileName, 0);
        return ResultDtoTool.buildSucceed(result);
    }

    public String delFile(String fileName, long newVersion) {//NOSONAR
        String result = "-";
        String unixPath = fileName;
        String filePath = rootPath + fileName;
        filePath = FileUtil.convertToLocalPath(filePath);
        File file = new File(filePath);
        if (!file.exists()) result = "notExists";
        else if (!file.isFile()) result = "notIsFile";
        else {
            //todo 删除文件前判断权限
            /*boolean pass = true || FileRenameManager.getInstance().compare(fileName);//NOSONAR
            if (!pass) result = "refuseDel";//NOSONAR
            else */
            if (file.isFile()) {
                long timestamp = System.currentTimeMillis();
                File cfgFile = new File(filePath + SvrGlobalCons.DCFS_CFG_FILE_EXT);
                long fileVersion = getFileVersion(cfgFile);
                //文件版本号相同不删除
                if (fileVersion > 0 && fileVersion == newVersion) {
                    result = "sameVersion";
                } else {
                    boolean cfgRenameTo = FileUtil.renameTo(cfgFile, new File(cfgFile.getPath() + timestamp + SvrGlobalCons.DCFS_DEL_FILE_EXT));
                    boolean renameTo = FileUtil.renameTo(file, new File(filePath + "." + timestamp + SvrGlobalCons.DCFS_DEL_FILE_EXT));
                    boolean delSucc = cfgRenameTo || renameTo;
                    result = delSucc ? "delSuss" : "delFail";
                    if (delSucc) {
                        FileDeleteRecord record = new FileDeleteRecord();
                        record.setFilePath(unixPath);
                        record.setNodeName(FtpConfig.getInstance().getNodeName());
                        record.setSysname(NodeManager.getInstance().getSysName());
                        record.setFileVersion(fileVersion);
                        record.setDelTime(new Date());
                        EsbFileService.getInstance().logFileDelete(record);
                    }
                }
            }
        }
        log.info("delFile:{}#{}", fileName, result);
        return result;
    }

    private long getFileVersion(File cfgFile) {
        try {
            Properties properties = PropertiesTool.load(new Properties(), cfgFile);
            String version = properties.getProperty("version", "0");
            return Long.parseLong(version);
        } catch (IOException e) {
            log.warn("", e);
        }
        return 0;
    }

    public String getVersion() {
        return root.attributeValue("timestamp", null);
    }

    public ResultDto<String> search(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        String fileName = (String) data.get("filename");
        String absFile = rootPath+fileName;
        if(new File(absFile).exists()&&new File(absFile).isFile()&&new File(absFile+SvrGlobalCons.DCFS_CFG_FILE_EXT).exists()){
            log.info("文件存在");
            return ResultDtoTool.buildSucceed(Boolean.toString(true));
        }
        log.error("文件不存在");
        return ResultDtoTool.buildError(Boolean.toString(false));
    }
}
