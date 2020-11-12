package com.dcfs.esc.ftp.namenode.redis;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RedisFactory {
    private static final Logger log = LoggerFactory.getLogger(RedisFactory.class);

    private static final String REDIS_SENTINEL = "REDIS_SENTINEL";
    private static final String REDIS_MASTER = "REDIS_MASTER";
    private static final String REDIS_POOLSIZE = "REDIS_POOLSIZE";
    private static final String REDIS_TIMEOUT = "REDIS_TIMEOUT";
    private static final String REDIS_PASSWD = "REDIS_PASSWD";//NOSONAR
    private String redisSentinel;
    private String redisMaster;
    private int redisPoolsize;
    private int redisTimeout;
    private String redisPasswd;

    private static RedisFactory factory = null;
    private JedisSentinelPool pool = null;
    private boolean clustered = false;
    private Jedis jedis = null;

    public static RedisFactory getInstance() {
        if (null == factory) {
            factory = new RedisFactory();
        }
        return factory;
    }

    private RedisFactory() {
        redisSentinel = FtpConfig.getInstance().getProp().getProperty(REDIS_SENTINEL);
        redisMaster = FtpConfig.getInstance().getProp().getProperty(REDIS_MASTER);
        redisPoolsize = Integer.parseInt(FtpConfig.getInstance().getProp().getProperty(REDIS_POOLSIZE, "100"));
        redisTimeout = Integer.parseInt(FtpConfig.getInstance().getProp().getProperty(REDIS_TIMEOUT, "10000"));
        redisPasswd = FtpConfig.getInstance().getProp().getProperty(REDIS_PASSWD);
        if (StringUtils.contains(redisSentinel, ",")) clustered = true;
        if (clustered) init();
        else init4Standalone();
    }

    private void init() {
        try {
            Set<String> sentinels = new HashSet<>();
            String[] addra = redisSentinel.split(",");
            Collections.addAll(sentinels, addra);
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(redisPoolsize);
            pool = new JedisSentinelPool(redisMaster, sentinels, poolConfig, redisTimeout, redisPasswd);
            if (log.isInfoEnabled()) {
                log.info("创建Redis连接池成功：masterName=[" + redisMaster + "],sentinels=" + sentinels + "," +
                        "poolsize=[" + redisPoolsize + "],timeout=[" + redisTimeout + "]password=[" + redisPasswd + "].");//NOSONAR
            }
        } catch (Exception e) {
            log.error("创建Redis连接池出错！", e);
        }
    }

    private void init4Standalone() {
        String[] ipPortArr = redisSentinel.split(":");
        String jedisIp = ipPortArr[0];
        int jedisPort = Integer.parseInt(ipPortArr[1]);
        jedis = new Jedis(jedisIp, jedisPort);
        jedis.auth(redisPasswd);
    }

    public void getMaster() {
        if (log.isDebugEnabled()) log.debug(pool.getCurrentHostMaster().toString());
    }

    public Jedis getResource() {
        if (!clustered) return jedis;
        return pool.getResource();
    }

}
