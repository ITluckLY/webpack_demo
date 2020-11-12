package com.dcfs.esc.ftp.namenode.redis;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisClient {
    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);
    private static final String SERVER_SET = "FILESERVER_KEY_SET";
    private static final String SERVER_TOKEN_SET = "FILESERVER_TOKEN_SET";

    private RedisClient() {
    }

    /**
     * 添加文件
     *
     * @param file      文件名
     * @param serverid  服务器节点ID
     * @param expireSec 过期时间 毫秒
     * @param first     上传：true，冗余：false
     */
    public static boolean addFile(String file, String serverid, int expireSec, boolean first) {
        log.debug("新增记录到文件目录 File=[{}] Node=[{}],过期时间[{}]秒.", file, serverid, expireSec);
        boolean r = false;
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance().getResource();
            if (first) {
                Set<String> ss = jedis.smembers(file);
                if (!ss.isEmpty()) {
                    //如果文件目录有记录，清除再添加，防止多节点文件不一致的情况，20170417
                    jedis.del(file);
                    log.info("清除文件目录已有文件[{}]的记录。", file);
                }
            }
            Long re = jedis.sadd(file, serverid);
            jedis.expire(file, expireSec);
            r = re > 0;
        } catch (Exception e) {
            log.error("Redis 处理异常。", e);
        } finally {
            if (null != jedis)
                jedis.close();
        }
        return r;
    }

    /**
     * 获取文件的服务器节点ID
     *
     * @param file 文件名
     */
    public static String getFile(String file, Jedis jedis) {
        String serverid = null;
        serverid = jedis.srandmember(file);
        return serverid;
    }

    /**
     * 获取文件的服务器节点ID
     *
     * @param file 文件名
     */
    public static Set<String> getALLFile(String file) {
        Set<String> serverid = null;
        Jedis jedis = RedisFactory.getInstance().getResource();
        serverid = jedis.smembers(file);
        jedis.close();
        return serverid;
    }

    /**
     * 删除文件
     *
     * @param file     文件名
     * @param serverid 服务器节点ID
     */
    public static boolean delFile(String file, String serverid) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        Long re = jedis.srem(file, serverid);
        jedis.close();
        return re > 0;
    }

    /**
     * 恢复节点
     *
     * @param serverid 服务器节点ID
     */
    public static boolean addServer(String serverid) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        if (getAllGoodServer(jedis).contains(serverid)) {
            return true;
        }
        Long re = jedis.sadd(SERVER_SET, serverid);

        String group = FtpConfig.getInstance().getProp().getProperty("SERVER_GROUP", "");//NOSONAR
        jedis.sadd(SERVER_SET + group, serverid);

        jedis.close();
        return re != null && re > 0;
    }

    /**
     * 获取随机可用节点
     *
     * @return String 服务器节点ID
     */
    public static String getServer() {
        long st = System.currentTimeMillis();
        String server = null;
        Jedis jedis = RedisFactory.getInstance().getResource();
        server = jedis.srandmember(SERVER_SET);
        jedis.close();
        log.debug("time:{}", System.currentTimeMillis() - st);
        return server;
    }

    /**
     * 获取所有节点
     *
     * @return Set<String> 服务器节点set
     */
    public static Set<String> getAllGoodServer(Jedis jedis) {
        Set<String> server = null;
        server = jedis.smembers(SERVER_SET);
        //过滤掉探测异常的节点
        Map<String, String> diedServer = AliveTestTask.getDiedServer();
        for (String key : diedServer.keySet()) {
            server.remove(key);
            log.info("移除探测异常节点:{}", diedServer);
        }
        return server;
    }

    /**
     * 根据分组，获取所有节点
     *
     * @return Set<String> 服务器节点set
     */
    public static Set<String> getAllGoodServerbyGroup(Jedis jedis) {
        Set<String> server = null;
        String group = FtpConfig.getInstance().getProp().getProperty("SERVER_GROUP", "");
        server = jedis.smembers(SERVER_SET + group);

        //过滤掉探测异常的节点
        Map<String, String> diedServer = AliveTestTask.getDiedServer();
        for (String key : diedServer.keySet()) {
            server.remove(key);
            log.info("移除探测异常节点:{}", diedServer);
        }
        return server;
    }

    /**
     * 获取所有节点
     *
     * @return Set<String> 服务器节点set
     */
    public static Set<String> getAllServer(Jedis jedis) {
        Set<String> server = null;
        server = jedis.smembers(SERVER_SET);
        return server;
    }

    /**
     * 注册节点地址
     *
     * @param serverid 节点ID
     * @param ipport   节点地址
     */
    public static boolean addNode(String serverid, String ipport) {
        boolean r = false;
        Jedis jedis = RedisFactory.getInstance().getResource();
        String re = jedis.set(serverid, ipport);
        jedis.hset(SERVER_TOKEN_SET, serverid, "0");
        jedis.close();
        if ("OK".equals(re)) {
            r = true;
        }
        return r;
    }

    /**
     * 获取节点 地址
     *
     * @param serverid 节点ID
     * @return String 节点地址
     */
    public static String getNode(String serverid, Jedis jedis) {
        return jedis.get(serverid);
    }

    /**
     * 隔离节点
     *
     * @param serverid 服务器节点ID
     */
    public static boolean delServer(String serverid) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        if (!getAllGoodServer(jedis).contains(serverid)) {
            return true;
        }
        Long re = jedis.srem(SERVER_SET, serverid);

        String group = FtpConfig.getInstance().getProp().getProperty("SERVER_GROUP", "");
        jedis.srem(SERVER_SET + group, serverid);

        jedis.close();
        return re != null && re > 0;
    }

    /**
     * 获取文件的节点地址
     *
     * @param file 文件名
     * @return String 节点地址
     */
    public static String getFileNode(String file) {
        String nodeAddr = null;
        try {
            Jedis jedis = RedisFactory.getInstance().getResource();
            String node = getFile(file, jedis);
            nodeAddr = node;
            jedis.close();
        } catch (Exception e) {
            log.error("查询文件目录出错！", e);
        }
        return nodeAddr;
    }

    /**
     * 节点并发数+1
     */
    public static void addToken(String serverid) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        jedis.hincrBy(SERVER_TOKEN_SET, serverid, 1);
        jedis.close();
    }

    /**
     * 节点并发数-1
     */
    public static void subToken(String serverid) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        jedis.hincrBy(SERVER_TOKEN_SET, serverid, -1);
        jedis.close();
    }

    /**
     * 节点并发数-x
     */
    public static void subToken(String serverid, int count) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        jedis.hincrBy(SERVER_TOKEN_SET, serverid, -count);
        jedis.close();
    }

    /**
     * 节点并发数重置
     */
    public static void zeroToken(String serverid) {
        Jedis jedis = RedisFactory.getInstance().getResource();
        jedis.hset(SERVER_TOKEN_SET, serverid, "0");
        jedis.close();
    }

    /**
     * 获取节点并发数
     */
    public static int getToken(String serverid) {
        int t = -1;
        Jedis jedis = RedisFactory.getInstance().getResource();
        Map<String, String> nodeToken = jedis.hgetAll(SERVER_TOKEN_SET);
        String nt = nodeToken.get(serverid);
        t = Integer.parseInt(nt);
        jedis.close();
        return t;
    }

    /**
     * 获取下载负载节点地址
     *
     * @param file 文件名
     * @return String 节点地址
     */
    public static String getBalanceNodeGet(String file) {
        String ipport = null;
        Jedis jedis = RedisFactory.getInstance().getResource();
        //获取可用节点
        Set<String> serverids = getAllGoodServer(jedis);
        //获取文件节点
        Set<String> serverid2 = jedis.smembers(file);

        Set<String> set = new HashSet<>();
        for (String server : serverid2) {
            set.add(server);
        }
        for (String server : set) {
            if (!serverids.contains(server)) {
                //如果该节点不可用，剔除
                serverid2.remove(server);
            }
        }
        String id = getServer(serverid2);
        if (null != id) {
            ipport = jedis.get(id);
        }
        jedis.close();
        log.info("将使用传输节点：name[{}],addr[{}].", id, ipport);
        return ipport;
    }

    /**
     * 获取上传负载节点地址
     *
     * @return String 节点地址
     */
    public static String getBalanceNodePut() {
        String ipport = null;
        Jedis jedis = RedisFactory.getInstance().getResource();
        String id = null;
        try {
            //获取可用节点
            Set<String> serverids = getAllGoodServer(jedis);
            id = getServer(serverids);
            if (null != id) {
                ipport = jedis.get(id);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Redis 处理异常。", e);
            }
        } finally {
            if (null != jedis)
                jedis.close();
        }
        if (log.isInfoEnabled()) {
            log.info("将使用传输节点：name[" + id + "],addr[" + ipport + "].");
        }
        return ipport;
    }

    /**
     * 获取当前节点的冗余节点组和传输节点组
     */
    public static String getNodeGroup() {
        String nodes = "";
        Jedis jedis = RedisFactory.getInstance().getResource();
        Set<String> serverids = getAllGoodServer(jedis);
        Set<String> server = getAllGoodServerbyGroup(jedis);
        jedis.close();

        nodes = "当前节点可用传输节点为:" + (serverids == null ? "[]" : serverids.toString())
                + "*当前节点可用冗余节点为:" + (server == null ? "[]" : server.toString());
        return nodes;
    }

    /**
     * 根据节点分组获取可冗余节点
     */
    public static String getRedundantNodebyGroup() {
        String servName = FtpConfig.getInstance().getNodeName();
        String node = null;
        Set<String> server = null;
        Jedis jedis = RedisFactory.getInstance().getResource();
        server = getAllGoodServerbyGroup(jedis);
        server.remove(servName);
        int avilableNodes = server.size();
        if (avilableNodes > 0) {
            int r = new Random().nextInt(avilableNodes);
            Object[] oo = server.toArray();
            String id = (String) oo[r];
            node = jedis.get(id);
            if (log.isInfoEnabled()) {
                log.info("将冗余文件到节点：name[" + id + "],addr[" + node + "].");
            }
        }
        jedis.close();
        return node;
    }


    /**
     * 获取其他节点IP
     */
    public static Map<String, String> getAllOtherNode(String servName) {
        Map<String, String> node = null;
        Set<String> server = null;
        Jedis jedis = RedisFactory.getInstance().getResource();
        server = getAllServer(jedis);
        server.remove(servName);
        int avilableNodes = server.size();
        if (avilableNodes > 0) {
            node = new HashMap<>();
            for (String sid : server) {
                node.put(sid, jedis.get(sid).split(":")[0]);
            }
        }
        jedis.close();
        return node;
    }

    private static HashMap<String, String> countMap = new HashMap<>();

    public static synchronized String getServer(Set<String> serverids) {
        String id = null;
        int c = -1;
        for (String server : serverids) {
            String nt = countMap.get(server);
            int c1 = (null == nt ? 0 : Integer.parseInt(nt));
            if (c == -1) {
                //第一次不比较
                c = c1;
                id = server;
                continue;
            }
            if (c1 < c) {
                id = server;
                c = c1;
            }
        }
        c++;
        countMap.put(id, Integer.toString(c));

        if (c >= 500) {
            //计数过大清零
            countMap.clear();
        }
        return id;
    }
}
