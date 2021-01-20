package com.dcfs.esc.ftp.datanode.nework.httphandler;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.network.Channel;
import com.dcfs.esb.ftp.network.ControlUtil;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.route.Route;
import com.dcfs.esb.ftp.server.route.RouteManager;
import com.dcfs.esb.ftp.server.system.*;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.scrt.bean.SDKRequestHead;
import com.dcfs.esc.ftp.datanode.pool.ThreadExecutorFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;

public class HttpFileRouteHandler {
    private static final Logger log = LoggerFactory.getLogger(HttpFileRouteHandler.class);
    private static Executor exe = ThreadExecutorFactory.getExecutorForFileRoute();


    public void doFileRoute(SDKRequestHead sdkRequestHead) throws FtpException {//NOSONAR
        final String flowNo = sdkRequestHead.getRqsSrlNo();
        String targetFileName = sdkRequestHead.getFileName();
        if (StringUtils.isEmpty(targetFileName)) {
            log.debug("#flowNo:{}#文件路由#目标文件为空,返回", flowNo);
            return;
        }

        String uid = sdkRequestHead.getUid();
        String tranCode = sdkRequestHead.getTranCode();
        String targetSysname = null;//路由目标
        String svrFilePath = sdkRequestHead.getServFileName();
        String userIp = sdkRequestHead.getUserIp();
        log.debug("#flowNo:{}#开始路由文件#uid:{},tranCode:{},fileName:{},targetSysname:{},tarFileName:{}"
                , flowNo, uid, tranCode, svrFilePath, targetSysname, targetFileName);

        Route route = RouteManager.getInstance().serachRoute(uid, tranCode);
        if (route == null) {
            log.debug("#flowNo:{}#路由表中未查找匹配的路由规则#User:{},tranCode:{}", flowNo, uid, tranCode);
            return;
        }
        //targetSysname 也是一个目标路由名称 ref system.xml#systems.system.name
        String[] dests = route.getDestination();
        if (dests == null && StringUtils.isEmpty(targetSysname)) {
            log.warn("#flowNo:{}#该路由没有配置目的地#User:{},tranCode:{}", flowNo, uid, tranCode);
            return;
        } else if (dests == null) {
            dests = new String[]{targetSysname};
        } else if (StringUtils.isNoneEmpty(targetSysname)) {
            String[] newdests = new String[dests.length + 1];
            newdests[0] = targetFileName;
            System.arraycopy(dests, 0, newdests, 1, dests.length);
            dests = newdests;
        }
        String type = route.getType();
        String mode = route.getMode();
        if (type.equals("s")) {//s为系统路由，d为目录路由
            dests = filtSys(dests);
            for (String dest : dests) {
                SystemInfo systemInfo = SystemManage.getInstance().getSystemInfo(dest);
                if (systemInfo != null) {
                    log.debug("#flowNo:{}#路由信息#name:{},ip:port:{}:{},userName:{},protocol:{}"
                            , flowNo, systemInfo.getName(), systemInfo.getIp(), systemInfo.getPort(), systemInfo.getUsername(), systemInfo.getProtocol());
                    //获取渠道名称与要上传的目标系统
                    String channelName = uid + "-" + userIp + "->" + systemInfo.getName();
                    Channel channel = new Channel(channelName);
                    Map<String, Channel> map = ControlUtil.getInstance().getChannelCollMap();
                    synchronized (ControlUtil.CHANNEL_LOCK) {
                        map.put(channelName, channel);
                    }
                    //取远程文件路径
                    try {
                        String localFileName = EsbFileManager.getInstance().getFileAbsolutePath(svrFilePath);
                        FileRouteArgs routeArgs = new FileRouteArgs();
                        routeArgs.setTranCode(tranCode);
                        routeArgs.setSvrFilePath(svrFilePath);
                        routeArgs.setUploadUid(uid);
                        routeArgs.setFlowNo(flowNo);
                        final IProtocol protocol = ProtocolFactory.getProtocol(systemInfo, localFileName, targetFileName, channelName, routeArgs);
                        log.info("#flowNo:{}#文件路由#localFileName:{},routeName:{},targetFileName:{}",  flowNo, localFileName, systemInfo.getName(), targetFileName);
                        if (mode.equals("syn")) {
                            //FtsFileMsgProtocol::消息接收与文件下载同步
                            routeArgs.setSync(true);
                            log.debug("#flowNo:{}#开始文件同步路由", flowNo);
                            long t1 = System.currentTimeMillis();
                            try {
                                if (protocol == null) {
                                    log.error("#flowNo:{}#登录系统错误:{}",  flowNo, dest);
                                    continue;
                                }
                                boolean upload = protocol.uploadBySync();
                                log.info("路由结果{}",upload ? CommGlobalCons.RESULT_STATE_SUCCESS : CommGlobalCons.RESULT_STATE_FAIL);
                            } catch (Exception e) {
                                log.error("#flowNo:{}#系统文件路由失败#dest:{}", flowNo, dest, e);
                            }
                            long t2 = System.currentTimeMillis();
                            log.debug("#flowNo:{}#文件同步路由文件结束,耗费时间:{}",  flowNo, (t2 - t1));
                        } else {
                            routeArgs.setSync(false);
                            log.error("#flowNo:{}#系统文件路由状态:{}", flowNo, CommGlobalCons.RESULT_STATE_NOTSURE);
                            exe.execute(new Runnable() {
                                public void run() {
                                    try {
                                        if (protocol != null) {
                                            protocol.uploadByAsync();
                                        }
                                    } catch (FtpException e) {
                                        log.error("#flowNo:{}#文件异步路由错误", flowNo, e);
                                    }
                                }
                            });
                        }
                    } finally {
                        synchronized (ControlUtil.CHANNEL_LOCK) {
                            map.remove(channelName);
                        }
                    }
                } else {
                    log.error("#flowNo:{}#未获取到{}对应的系统信息",  flowNo, dest);
                }
            }
        } else {
            log.warn("#flowNo:{}#不支持除s之外的类型", flowNo);
        }
    }

    private String[] filtSys(String[] dir) {
        String[] tmp = new String[dir.length];
        int c = 0;
        for (String d : dir) {
            SystemInfo info = SystemManage.getInstance().getSystemInfo(d);
            if (info != null) {
                tmp[c++] = d;
            }
        }
        String[] dirs = new String[c];
        System.arraycopy(tmp, 0, dirs, 0, c);
        return dirs;
    }


}
