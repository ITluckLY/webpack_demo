package com.dcfs.esb.ftp.spring.service;

import com.dcfs.esb.ftp.server.model.*;
import com.dcfs.esb.ftp.utils.FileNameUtils;
import com.dcfs.esb.ftp.utils.GsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mocg on 2016/7/21.
 */
@Service
public class FileRecord2FileService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${fileRecordDir}")
    private String fileRecordDir;

    private static final int TIME_MINUTE_INTERVAL = 5;

    public boolean saveLog(FileSaveRecord record) {
        log.debug("开始记录文件成功上传...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "savefile", "s" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");//NOSONAR
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);//NOSONAR
            return false;
        }
        return true;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("开始创建文件记录保存目录#{}", fileRecordDir);
        File dir = new File(fileRecordDir);
        FileUtils.forceMkdir(dir);
        log.info("创建文件记录保存目录成功#{}", dir.getAbsolutePath());
    }

    public boolean saveLog(FileDownloadRecord record) {
        log.debug("开始记录文件下载...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "downfile", "d" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean saveLog(FileMsgDownloadResultRecord record) {
        log.debug("开始记录文件推送文件下载结果...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "filemsgdownloadresult", "d" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean saveLog(FileUploadRecord record) {
        log.debug("开始记录文件上传...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "upfile", "u" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean saveLog(FileDeleteRecord record) {
        log.debug("开始记录文件删除...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "delfile", "del" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean saveLog(FileDistributeRecord record) {
        log.debug("开始记录文件分发...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "distribute", "distr" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean send(SameFileDeleteRecord record) {
        log.debug("开始记录删除同名文件...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "delsame", "delsame" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存文件记录到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean saveLog(PushFileMsg2CliRecord record) {
        log.debug("开始记录PushFileMsg2Cli...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "pushMsg2Cli", "msg2Cli" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存PushFileMsg2CliRecord到文件时出错", e);
            return false;
        }
        return true;
    }

    public boolean saveLog(NodeListGetRecord record) {
        log.debug("开始记录NodeListGetRecord...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "nodeListGet", "nodeListGet" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存NodeListGetRecord到文件时出错", e);
            return false;
        }
        return true;
    }
	/**
     * 保存客户端状态信息
     * @param record
     * @return
     */
	
    public boolean saveLog(ClientRegisterRecord record) {
        log.debug("开始记录ClientRegisterRecord...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "clientRegister", "clientRegister" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存ClientRegisterRecord到文件时出错", e);
            return false;
        }
        return true;
    }

    /**
     * 保存用户信息
     * @param record
     * @return
     */
    public boolean saveLog(UserRegisterRecord record) {
        log.debug("开始记录UserRegisterRecord...");
        try {
            String json = GsonUtil.toJson(record);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int minute = calendar.get(Calendar.MINUTE);
            //每5分钟生成一个新的文件
            minute = minute / TIME_MINUTE_INTERVAL;
            String pathname = FileNameUtils.concat(fileRecordDir, "userRegister", "userRegister" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
            File file = new File(pathname);
            FileUtils.writeStringToFile(file, json + "\r\n", true);
        } catch (Exception e) {
            log.error("保存UserRegisterRecord到文件时出错", e);
            return false;
        }
        return true;
    }


    /**
     * 文件归档
     * @param record
     * @return
     */
//    public boolean saveLog(FileUploadClearRecord record) {
//        log.debug("开始记录FileUploadClearRecord...");
//        try {
//            String json = GsonUtil.toJson(record);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            int minute = calendar.get(Calendar.MINUTE);
//            //每5分钟生成一个新的文件
//            minute = minute / TIME_MINUTE_INTERVAL;
//            String pathname = FileNameUtils.concat(fileRecordDir, "userRegister", "userRegister" + DateFormatUtils.format(calendar.getTime(), "yyyyMMddHH") + minute + ".json");
//            File file = new File(pathname);
//            FileUtils.writeStringToFile(file, json + "\r\n", true);
//        } catch (Exception e) {
//            log.error("保存FileUploadClearRecord到文件时出错", e);
//            return false;
//        }
//        return true;
//    }
}
