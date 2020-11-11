package com.dc.smarteam.common.msggenerator;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.modules.client.entity.ClientFile;
import com.dc.smarteam.modules.file.entity.FtFile;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/3.
 */
public class FtFileMsgGen {

    public static String getFileMsg() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"select\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getPath() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"allPath\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getVersion() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"version\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getFtsApiCfg() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"ftsApiCfg\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getFtsApiCfgCache() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"ftsApiCfgCache\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getFtsClientConfig() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"ftsClientConfig\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getLog4j2() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"log4j2\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getQuartz() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"quartz\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getRule() {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"config\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"rule\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getConfig(String fileName){
        if(fileName.equals("FtsApiConfig.properties")){
            return getFtsApiCfg();
        }else if(fileName.equals("FtsApiCfgCache.properties")){
            return getFtsApiCfgCache();
        }else if(fileName.equals("FtsClientConfig.properties")){
            return getFtsClientConfig();
        }else if(fileName.equals("quartz.properties")){
            return getQuartz();
        }else if(fileName.equals("log4j2.xml")) {
            return getLog4j2();
        }else if(fileName.equals("rule.xml")) {
            return getRule();
        }
        return "文件不存在";
    }

    public static String getSync(String content,String fileName) {
        JSONObject obj = new JSONObject();
        obj.put("content", content);
        obj.put("fileName", fileName);
        String objStr = obj.toString();
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"sync\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":" + objStr + ",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }



    public static String getSubDir(String path) {
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"subDir\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + path + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}";
        return str;
    }

    public static String getFileContent(String path, Page page) {//NOSONAR
        path = path.replaceAll("\\\\", "/");
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"selByKey\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + path + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    //构建条件查询语句
    public static String getQuery(FtFile ftFile) {
        JSONObject obj = new JSONObject();
        obj.put("fileName", ftFile.getFileName());
        obj.put("systemName", ftFile.getSystemName());
        Date createDate = ftFile.getCreateDate();
        if(createDate!=null){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String createdate= formatter.format(createDate);
        obj.put("createDate", createdate);
        }

        obj.put("parentPath", ftFile.getParentPath());
        String objStr = obj.toString();
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"query\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":" + objStr + ",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getQuery(ClientFile ftFile) {
        JSONObject obj = new JSONObject();
        obj.put("fileName", ftFile.getFileName());
        obj.put("systemName", ftFile.getSystemName());
        obj.put("parentPath", ftFile.getParentPath());
        Date createDate = ftFile.getCreateDate();
        if(createDate!=null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String createdate= formatter.format(createDate);
            obj.put("createDate", createdate);
        }
        String objStr = obj.toString();
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"query\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":" + objStr + ",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String download(String path) {
        path = path.replaceAll("\\\\", "/");
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"download\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":\"" + path + "\",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }

    public static String getRepush(ClientFile clientFile) {
        JSONObject obj = new JSONObject();
        obj.put("fileName", clientFile.getFileName());
        obj.put("parentPath", clientFile.getParentPath());
        String objStr = obj.toString();
        String str = "{\n" +
                "\t\t\t\t\t\t\t\"target\":\"file\",\n" +
                "\t\t\t\t\t\t\t\"operateType\":\"repush\",\n" +
                "\t\t\t\t\t\t\t\"data\":{\n" +
                "\t\t\t\t\t\t\t\t\t\"key\":" + objStr + ",\n" +
                "\t\t\t\t\t\t\t\t\t\"describe\":\"\",\n" +
                "\t\t\t\t\t\t\t\t\t\"text\":\"\"\n" +
                "\t\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t}  ";
        return str;
    }
}
