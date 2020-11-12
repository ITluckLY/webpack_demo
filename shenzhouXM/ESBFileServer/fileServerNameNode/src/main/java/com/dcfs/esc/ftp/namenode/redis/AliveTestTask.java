package com.dcfs.esc.ftp.namenode.redis;

import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class AliveTestTask extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(AliveTestTask.class);
    private static Map<String, String> diedServer = new HashMap<>();

    public static void start() {
        Timer timer = new Timer();
        timer.schedule(new AliveTestTask(), 5000, 30000);
        log.info("启动节点运行探测任务，执行间隔[30000]");
    }

    @Override
    public void run() {
        try {
            Map<String, String> ips = RedisClient.getAllOtherNode(FtpConfig.getInstance().getNodeName());
            Map<String, String> diedServerTmp = new HashMap<>();
            if (null != ips) {
                diedServerTmp.clear();
                for (Map.Entry<String, String> entry : ips.entrySet()) {
                    diedServerTmp.put(entry.getKey(), entry.getValue());
                }
            } else {
                log.warn("没有可探测节点，请检查其他节点状态！");
                return;
            }
            if (log.isInfoEnabled()) {
                log.info("开始探测节点：{}", diedServerTmp);
            }
            int cmdport = FtpConfig.getInstance().getCommandPort();
            // 探测
            Set<String> set = ips.keySet();
            for (String id : set) {
                String ip = ips.get(id);
                one(diedServerTmp, ip, cmdport, id);
            }
            setDiedServer(diedServerTmp);
            if (!diedServer.isEmpty())
                if (log.isErrorEnabled()) {//NOSONAR
                    log.error("以下节点探测出现问题：" + diedServer);
                }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("探测任务运行异常！", e);
            }
        }
    }

    private void one(Map<String, String> diedServerTmp, String ip, int cmdport, String id) throws IOException {
        BufferedOutputStream out = null;
        DataInputStream in = null;
        try (Socket client = new Socket(ip, cmdport)) {
            client.setSoTimeout(5000);// 超时时间
            byte[] msg = "{\"target\":\"f5\",\"operateType\":\"check\"} ".getBytes();
            out = new BufferedOutputStream(client.getOutputStream());
            int length = msg.length;
            StringBuilder lengthStr = new StringBuilder(String.valueOf(length));
            int len = lengthStr.length();
            for (int i = 0; i < 6 - len; i++) {
                lengthStr.insert(0, "0");
            }
            out.write(lengthStr.toString().getBytes());
            out.write(msg);
            out.flush();
            client.shutdownOutput();
            byte[] lengthHeaderByte = new byte[6];
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            in.readFully(lengthHeaderByte);
            String lengthHeaderStr = new String(lengthHeaderByte);
            int lengthHeader = Integer.parseInt(lengthHeaderStr);
            byte[] returnMsg = new byte[lengthHeader];
            in.readFully(returnMsg);
            String rs = new String(returnMsg, "utf-8");
            JSONObject json = JSONObject.fromObject(rs);
            String code = json.getString("returncode");
            if ("0000".equals(code)) {
                diedServerTmp.remove(id);
            }
        } catch (Exception e) {
            log.error("探测节点[{}-{}:{}]出错", id, ip, cmdport, e);
        } finally {
            IOUtil.closeQuietly(in, out);
        }
    }

    public static Map<String, String> getDiedServer() {
        return diedServer;
    }

    public static void setDiedServer(Map<String, String> diedServer) {
        AliveTestTask.diedServer = diedServer;
    }
}
