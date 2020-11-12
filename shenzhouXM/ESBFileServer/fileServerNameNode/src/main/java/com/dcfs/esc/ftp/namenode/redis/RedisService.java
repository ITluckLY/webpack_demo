package com.dcfs.esc.ftp.namenode.redis;

import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.svr.comm.model.FilePathValue;
import com.dcfs.esc.ftp.svr.comm.model.NodeNameValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Set;

/**
 * Created by huangzbb on 2017/7/20.
 */
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private static final String SYSTEM_SEPARATOR = "_";

    public static RedisService getInstance() {
        return new RedisService();
    }

    /**
     * redis同时添加两条记录
     * <p>
     * systemName_filePath {nodeName fileVersion fileSize}
     * nodeName {filePath fileVersion fileSize time}
     */
    public boolean addRecord(String systemName, String filePath, String filePathValue, String nodeName, String nodeNameValue, Long nano) {
        String systemNameFilePath = systemName + SYSTEM_SEPARATOR + filePath;
        log.debug("nano:{}#新增记录到目录服务器#systemNameFilePath:{}#value:{}", nano, systemNameFilePath, filePathValue);
        log.debug("nano:{}#新增记录到目录服务器#nodeName:{}#value:{}", nano, nodeName, nodeNameValue);
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance().getResource();
            Set<String> smembers1 = jedis.smembers(systemNameFilePath);
            Set<String> smembers2 = jedis.smembers(nodeName);
            Transaction trans = jedis.multi();
            // TODO 保留相同系统下同名文件不同版本信息
            for (String str : smembers1) {
                FilePathValue filePathValueObj = GsonUtil.fromJson(str, FilePathValue.class);
                if (StringUtils.equals(nodeName, filePathValueObj.getNodeName())) {
                    trans.srem(systemNameFilePath, str);
                    break;
                }
            }
            for (String str : smembers2) {
                NodeNameValue nodeNameValueObj = GsonUtil.fromJson(str, NodeNameValue.class);
                if (StringUtils.equals(filePath, nodeNameValueObj.getFilePath())) {
                    trans.srem(nodeName, str);
                    break;
                }
            }
            trans.sadd(systemNameFilePath, filePathValue);
            trans.sadd(nodeName, nodeNameValue);
            if (trans.exec() != null) {
                return true;
            }
        } catch (JedisException e) {
            log.error("nano:{}#redis处理异常。", nano, e);//NOSONAR
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 查询文件位于哪些节点
     */
    public Set<String> getFilePathValue(String systemName, String filePath, Long nano) {
        String systemNameFilePath = systemName + SYSTEM_SEPARATOR + filePath;
        Jedis jedis = null;
        Set<String> filePathValue = null;
        try {
            jedis = RedisFactory.getInstance().getResource();
            filePathValue = jedis.smembers(systemNameFilePath);
        } catch (JedisException e) {
            log.error("nano:{}#redis处理异常。", nano, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return filePathValue;
    }

    public boolean delRecord(String systemName, String filePath, String nodeName, Long nano) {
        // TODO 根据文件版本删除同名文件
        String systemNameFilePath = systemName + SYSTEM_SEPARATOR + filePath;
        log.debug("nano:{}#目录服务器删除记录#systemNameFilePath:{}#nodeName:{}", nano, systemNameFilePath, nodeName);
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance().getResource();
            Set<String> smembers1 = jedis.smembers(systemNameFilePath);
            Set<String> smembers2 = jedis.smembers(nodeName);
            Transaction trans = jedis.multi();
            for (String str : smembers1) {
                FilePathValue filePathValueObj = GsonUtil.fromJson(str, FilePathValue.class);
                if (StringUtils.equals(nodeName, filePathValueObj.getNodeName())) {
                    trans.srem(systemNameFilePath, str);
                    break;
                }
            }
            for (String str : smembers2) {
                NodeNameValue nodeNameValueObj = GsonUtil.fromJson(str, NodeNameValue.class);
                if (StringUtils.equals(filePath, nodeNameValueObj.getFilePath())) {
                    trans.srem(nodeName, str);
                    break;
                }
            }
            if (trans.exec() != null) {
                return true;
            }
        } catch (JedisException e) {
            log.error("nano:{}#redis处理异常。", nano, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

}
