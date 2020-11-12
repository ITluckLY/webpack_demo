package com.dcfs.esc.ftp.datanode.process.handler;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.network.Channel;
import com.dcfs.esb.ftp.network.ControlUtil;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.route.Route;
import com.dcfs.esb.ftp.server.route.RouteManager;
import com.dcfs.esb.ftp.server.system.*;
import com.dcfs.esc.ftp.comm.constant.CommGlobalCons;
import com.dcfs.esc.ftp.comm.dto.FileUploadDataRspDto;
import com.dcfs.esc.ftp.datanode.context.ChannelContext;
import com.dcfs.esc.ftp.datanode.context.UploadContextBean;
import com.dcfs.esc.ftp.datanode.pool.ThreadExecutorFactory;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.UploadProcessHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by mocg on 2017/6/6.
 */
public class FileRouteHandler extends UploadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(FileRouteHandler.class);
    private static Executor exe = ThreadExecutorFactory.getExecutorForFileRoute();

    @Override
    public void finish(ProcessHandlerContext ctx) throws Exception {
        if (!ctx.isFileloadSucc()) return;
        FileUploadDataRspDto rspDto = ctx.responseObj(FileUploadDataRspDto.class);
        ChannelContext channelContext = ctx.getChannelContext();
        UploadContextBean cxtBean = channelContext.cxtBean();
        final long nano = channelContext.getNano();
        final String flowNo = channelContext.getFlowNo();
        String targetFileName = cxtBean.getTargetFileName();
        //获取打包文件里的文件名
        if (StringUtils.isNotEmpty(targetFileName) && targetFileName.endsWith("dcfs.zip") && targetFileName.contains("dcfsing")) {
            File zipFile = cxtBean.getEsbFile().getFile();
            cxtBean.setOriFilename(getZipniname(zipFile, channelContext));
        }
        //请求指定不进行文件路由
        if (cxtBean.isDontRoute()) {
            log.debug("nano:{}#flowNo:{}#dontFileRoute.return", nano, flowNo);
            return;
        }
        log.debug("nano:{}#flowNo:{}#FileRoute...", nano, flowNo);
        doFileRoute(cxtBean);
        rspDto.setFileRouteResult(cxtBean.getFileRouteResult());
    }

    protected void doFileRoute(UploadContextBean cxtBean) throws FtpException {//NOSONAR
        final long nano = cxtBean.getNano();
        final String flowNo = cxtBean.getFlowNo();
        String targetFileName = cxtBean.getTargetFileName();
        if (StringUtils.isEmpty(targetFileName)) {
            log.debug("nano:{}#flowNo:{}#文件路由#目标文件为空,返回", nano, flowNo);
            return;
        }
        String uid = cxtBean.getUid();
        String tranCode = cxtBean.getTranCode();
        String targetSysname = cxtBean.getTargetSysname();//路由目标
        String svrFilePath = cxtBean.getSvrFilePath();
        String userIp = cxtBean.getUserIp();
        log.debug("nano:{}#flowNo:{}#开始路由文件#uid:{},tranCode:{},fileName:{},targetSysname:{},tarFileName:{},userIp:{}"
                , nano, flowNo, uid, tranCode, svrFilePath, targetSysname, targetFileName, userIp);

        Route route = RouteManager.getInstance().serachRoute(uid, tranCode);
        if (route == null) {
            log.debug("nano:{}#flowNo:{}#路由表中未查找匹配的路由规则#User:{},tranCode:{}", nano, flowNo, uid, tranCode);
            return;
        }
        //targetSysname 也是一个目标路由名称 ref system.xml#systems.system.name
        String[] dests = route.getDestination();
        if (dests == null && StringUtils.isEmpty(targetSysname)) {
            log.warn("nano:{}#flowNo:{}#该路由没有配置目的地#User:{},tranCode:{}", nano, flowNo, uid, tranCode);
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
                    log.debug("nano:{}#flowNo:{}#路由信息#name:{},ip:port:{}:{},userName:{},protocol:{}"
                            , nano, flowNo, systemInfo.getName(), systemInfo.getIp(), systemInfo.getPort(), systemInfo.getUsername(), systemInfo.getProtocol());
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
                        routeArgs.setNano(nano);
                        routeArgs.setFlowNo(flowNo);
                        final IProtocol protocol = ProtocolFactory.getProtocol(systemInfo, localFileName, targetFileName, channelName, routeArgs);
                        log.info("nano:{}#flowNo:{}#文件路由#localFileName:{},routeName:{},targetFileName:{}", nano, flowNo, localFileName, systemInfo.getName(), targetFileName);
                        if (mode.equals("syn")) {
                            //FtsFileMsgProtocol::消息接收与文件下载同步
                            routeArgs.setSync(true);
                            log.debug("nano:{}#flowNo:{}#开始文件同步路由", nano, flowNo);
                            long t1 = System.currentTimeMillis();
                            try {
                                if (protocol == null) {
                                    log.error("nano:{}#flowNo:{}#登录系统错误:{}", nano, flowNo, dest);
                                    continue;
                                }
                                boolean upload = protocol.uploadBySync();
                                cxtBean.setFileRouteResult(upload ? CommGlobalCons.RESULT_STATE_SUCCESS : CommGlobalCons.RESULT_STATE_FAIL);
                            } catch (Exception e) {
                                cxtBean.setFileRouteResult(CommGlobalCons.RESULT_STATE_EXCEPTION);
                                log.error("nano:{}#flowNo:{}#系统文件路由失败#dest:{}", nano, flowNo, dest, e);
                            }
                            long t2 = System.currentTimeMillis();
                            log.debug("nano:{}#flowNo:{}#文件同步路由文件结束,耗费时间:{}", nano, flowNo, (t2 - t1));
                        } else {
                            routeArgs.setSync(false);
                            cxtBean.setFileRouteResult(CommGlobalCons.RESULT_STATE_NOTSURE);
                            exe.execute(new Runnable() {
                                public void run() {
                                    try {
                                        if (protocol != null) {
                                            protocol.uploadByAsync();
                                        }
                                    } catch (FtpException e) {
                                        log.error("nano:{}#flowNo:{}#文件异步路由错误", nano, flowNo, e);
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
                    log.error("nano:{}#flowNo:{}#未获取到{}对应的系统信息", nano, flowNo, dest);
                }
            }
        } else {
            log.warn("nano:{}#flowNo:{}#不支持除s之外的类型", nano, flowNo);
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

    private String getZipniname(File zipFile, ChannelContext channelContext) {
        final long nano = channelContext.getNano();
        final String flowNo = channelContext.getFlowNo();
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zin = new ZipInputStream(fis, Charset.forName("GB18030"))) {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                sb.append(ze.getName()).append(",");
                log.debug("nano:{}#flowNo:{}#压缩包文件名为{}", nano, flowNo, ze.getName());
            }
        } catch (Exception e) {
            log.error("nano:{}#flowNo:{}#压缩包内文件名获取失败", nano, flowNo, e);
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
